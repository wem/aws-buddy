package ch.sourcemotion.aws.buddy.infrastructure.cloudformation

import ch.sourcemotion.aws.buddy.domain.cloudformation.entity.StackEntity
import ch.sourcemotion.aws.buddy.domain.cloudformation.entity.StackOutput
import ch.sourcemotion.aws.buddy.domain.cloudformation.entity.StackParameter
import ch.sourcemotion.aws.buddy.domain.cloudformation.entity.StackResource
import ch.sourcemotion.aws.buddy.infrastructure.cloudformation.repository.Neo4jStackRepository
import ch.sourcemotion.aws.buddy.shared.ResourceType
import ch.sourcemotion.aws.buddy.shared.toStackId
import ch.sourcemotion.aws.buddy.testcontainers.BaseNeo4JRepositoryTest
import ch.sourcemotion.aws.buddy.testcontainers.Neo4jTestResource
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.neo4j.driver.Driver

@QuarkusTest
@QuarkusTestResource(Neo4jTestResource::class, restrictToAnnotatedClass = true)
class Neo4jStackRepositoryTest : BaseNeo4JRepositoryTest() {

    @Inject private lateinit var sut: Neo4jStackRepository
    @Inject override lateinit var driver: Driver

    @Test
    fun `save and findAll one stack - without outputs, parameters or resources`(): Unit =
        runBlocking {
            val stackEntity = StackEntity("stack-id".toStackId(), "stack-name")
            sut.save(listOf(stackEntity))
            sut.findAll().shouldHaveSize(1).first().shouldBe(stackEntity)
        }

    @Test
    fun `save and findAll one stack - with output`(): Unit = runBlocking {
        val stackEntity =
            StackEntity("stack-id".toStackId(), "stack-name").apply {
                addOutput(StackOutput("key", "value", "description", "export-name"))
            }
        sut.save(listOf(stackEntity))
        sut.findAll().shouldHaveSize(1).first().shouldBe(stackEntity)
    }

    @Test
    fun `save and findAll one stack - with parameter`(): Unit = runBlocking {
        val stackEntity =
            StackEntity("stack-id".toStackId(), "stack-name").apply {
                addParameter(StackParameter("key", "value"))
            }
        sut.save(listOf(stackEntity))
        sut.findAll().shouldHaveSize(1).first().shouldBe(stackEntity)
    }

    @Test
    fun `save and findAll one stack - with resource`(): Unit = runBlocking {
        val stackEntity =
            StackEntity("stack-id".toStackId(), "stack-name").apply {
                addResource(StackResource("logicalId", "physicalId", ResourceType.S3_BUCKET))
            }
        sut.save(listOf(stackEntity))
        sut.findAll().shouldHaveSize(1).first().shouldBe(stackEntity)
    }

    @Test
    fun `save and findAll one stack - with parameter, output and resource`(): Unit = runBlocking {
        val stackEntity =
            StackEntity("stack-id".toStackId(), "stack-name").apply {
                addOutput(StackOutput("key", "value", "description", "export-name"))
                addParameter(StackParameter("key", "value"))
                addResource(StackResource("logicalId", "physicalId", ResourceType.S3_BUCKET))
            }
        sut.save(listOf(stackEntity))
        sut.findAll().shouldHaveSize(1).first().shouldBe(stackEntity)
    }

    @Test
    fun `save and findAll 100 stacks - with parameters, outputs and resources`(): Unit =
        runBlocking {
            val stackEntities =
                (1..100).map { stackNbr ->
                    StackEntity(
                            "stack-id-$stackNbr".toStackId(),
                            "stack-$stackNbr-name"
                        )
                        .apply {
                            (1..20).map { edgeNbr ->
                                addOutput(
                                    StackOutput(
                                        "key-output-$edgeNbr",
                                        "value-output-$edgeNbr",
                                        "description-output-$edgeNbr",
                                        "export-name-output-$edgeNbr"
                                    )
                                )
                                addParameter(
                                    StackParameter(
                                        "key-parameter-$edgeNbr",
                                        "value-parameter-$edgeNbr"
                                    )
                                )
                                addResource(
                                    StackResource(
                                        "logicalId-resource-$edgeNbr",
                                        "physicalId-resource-$edgeNbr",
                                        ResourceType.S3_BUCKET
                                    )
                                )
                            }
                        }
                }
            sut.save(stackEntities)
            sut.findAll().shouldHaveSize(100).shouldContainExactlyInAnyOrder(stackEntities)
        }
}
