package ch.sourcemotion.aws.buddy.infrastructure.cloudformation.repository

import ch.sourcemotion.aws.buddy.application.cloudformation.interfaces.StackRepository
import ch.sourcemotion.aws.buddy.domain.cloudformation.entity.StackEntity
import ch.sourcemotion.aws.buddy.domain.cloudformation.entity.StackEntity.Companion.STACK_CONTAINS_RELATION
import ch.sourcemotion.aws.buddy.domain.cloudformation.entity.StackOutput
import ch.sourcemotion.aws.buddy.domain.cloudformation.entity.StackParameter
import ch.sourcemotion.aws.buddy.domain.cloudformation.entity.StackResource
import ch.sourcemotion.aws.buddy.shared.StackId
import ch.sourcemotion.aws.buddy.shared.toResourceType
import ch.sourcemotion.aws.buddy.shared.toStackId
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Instance
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.neo4j.driver.Driver
import org.neo4j.driver.Query
import org.neo4j.driver.internal.value.NodeValue
import org.neo4j.driver.types.Node

@ApplicationScoped
class Neo4jStackRepository(private val driver: Instance<Driver>) : StackRepository {

    private companion object {
        val logger = KotlinLogging.logger {}

        val deleteAllNodesAndRelations = Query("MATCH(n) OPTIONAL MATCH(n)-[r]->(m) DELETE n, r,m")

        val findAllStacksQuery =
            Query(
                "MATCH(s:${StackEntity.LABEL}) " +
                    "OPTIONAL MATCH(s)-[:${STACK_CONTAINS_RELATION}]->(r) RETURN s, r"
            )

        val saveStackQuery =
            Query(
                "CREATE(s:${StackEntity.LABEL} \$stack) WITH s " +
                    "UNWIND \$outputs as output MERGE(s)-[:${STACK_CONTAINS_RELATION}]->(o:${StackOutput.LABEL}) SET o = output WITH s " +
                    "UNWIND \$parameters as parameter MERGE(s)-[:${STACK_CONTAINS_RELATION}]->(p:${StackParameter.LABEL}) SET p = parameter WITH s " +
                    "UNWIND \$resources as resource MERGE(s)-[:${STACK_CONTAINS_RELATION}]->(r:${StackResource.LABEL}) SET r = resource"
            )
    }

    override suspend fun clearRepository() {
        driver.get().useSession { session ->
            session.useTx { tx -> tx.runAsync(deleteAllNodesAndRelations).await() }
        }
    }

    override suspend fun findAll(): List<StackEntity> {
        return driver.get().useReadOnlySession { session ->
            session.useTx { tx ->
                val cursor = tx.runAsync(findAllStacksQuery).await()
                val stackEntities = HashMap<StackId, StackEntity>()
                cursor
                    .forEachAsync { record ->
                        val stackNode = (record.get("s") as NodeValue).asEntity()
                        val stackId = stackNode.get(StackEntity.ID_PROP).asString().toStackId()
                        stackEntities
                            .getOrPut(stackId) { stackNode.mapToStackEntity() }
                            .apply {
                                record.ifNotNull<NodeValue>("r") {
                                    val node = asEntity()
                                    when (node.labels().first()) {
                                        StackOutput.LABEL -> addOutput(node.mapToStackOutput())
                                        StackParameter.LABEL ->
                                            addParameter(node.mapToStackParameter())
                                        StackResource.LABEL ->
                                            addResource(node.mapToStackResource())
                                    }
                                }
                            }
                    }
                    .await()
                stackEntities.values.toList()
            }
        }
    }

    override suspend fun save(stacks: List<StackEntity>) {
        driver.get().useSession { session ->
            stacks.chunked(10).forEach { stackChunk ->
                session.useTx { tx ->
                    supervisorScope {
                        stackChunk.forEach { stack ->
                            launch {
                                tx.runAsync(
                                        saveStackQuery.withParameters(stack.mapToQueryParameters())
                                    )
                                    .await()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Node.mapToStackEntity(): StackEntity {
        val stackId = get(StackEntity.ID_PROP).asString().toStackId()
        val stackName = get(StackEntity.NAME_PROP).asString()
        return StackEntity(stackId, stackName)
    }

    private fun Node.mapToStackOutput(): StackOutput {
        val key = get(StackOutput.KEY_PROP).asString()
        val value = get(StackOutput.VALUE_PROP).asString()
        val exportName = get(StackOutput.EXPORT_NAME_PROP).asString()
        val description = get(StackOutput.DESCRIPTION_PROP).asString()
        return StackOutput(key, value, description, exportName)
    }

    private fun Node.mapToStackParameter(): StackParameter {
        val key = get(StackParameter.KEY_PROP).asString()
        val value = get(StackParameter.VALUE_PROP).asString()
        return StackParameter(key, value)
    }

    private fun Node.mapToStackResource(): StackResource {
        val logicalId = get(StackResource.LOGICAL_ID_PROP).asString()
        val physicalId = get(StackResource.PHYSICAL_ID_PROP).asString()
        val plainType = get(StackResource.TYPE_PROP).asString()
        return StackResource(logicalId, physicalId, plainType.toResourceType())
    }

    private fun StackEntity.mapToQueryParameters() =
        mapOf(
            "stack" to mapOf("id" to "$id", "name" to name),
            "outputs" to
                outputs().map { output ->
                    mapOf(
                        StackOutput.KEY_PROP to output.key,
                        StackOutput.VALUE_PROP to output.value,
                        StackOutput.EXPORT_NAME_PROP to output.exportName,
                        StackOutput.DESCRIPTION_PROP to output.description,
                    )
                },
            "parameters" to
                parameters().map { parameter ->
                    mapOf(
                        StackParameter.KEY_PROP to parameter.key,
                        StackParameter.VALUE_PROP to parameter.value,
                    )
                },
            "resources" to
                resources().map { resource ->
                    mapOf(
                        StackResource.LOGICAL_ID_PROP to resource.logicalId,
                        StackResource.PHYSICAL_ID_PROP to resource.physicalId,
                        StackResource.TYPE_PROP to resource.type.plainType,
                    )
                },
        )
}
