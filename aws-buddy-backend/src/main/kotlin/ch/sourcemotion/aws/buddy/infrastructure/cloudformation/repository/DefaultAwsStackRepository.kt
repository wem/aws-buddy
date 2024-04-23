package ch.sourcemotion.aws.buddy.infrastructure.cloudformation.repository

import ch.sourcemotion.aws.buddy.application.cloudformation.interfaces.AwsStackRepository
import ch.sourcemotion.aws.buddy.domain.cloudformation.entity.StackEntity
import ch.sourcemotion.aws.buddy.domain.cloudformation.entity.StackOutput
import ch.sourcemotion.aws.buddy.domain.cloudformation.entity.StackParameter
import ch.sourcemotion.aws.buddy.domain.cloudformation.entity.StackResource
import ch.sourcemotion.aws.buddy.shared.StackId
import ch.sourcemotion.aws.buddy.shared.toResourceType
import io.quarkus.arc.DefaultBean
import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.await
import kotlinx.coroutines.supervisorScope
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient
import software.amazon.awssdk.services.cloudformation.model.Stack
import software.amazon.awssdk.services.cloudformation.model.StackResourceSummary

@ApplicationScoped
@DefaultBean
class DefaultAwsStackRepository(
    private val cloudFormationClient: CloudFormationAsyncClient,
) : AwsStackRepository {

    override suspend fun readAll(): List<StackEntity> {
        val stacks = readStacksFromAwsRecursively()
        val stacksWithResources =
            supervisorScope {
                    stacks
                        .chunked(10)
                        .map { stackChunk ->
                            async { stackChunk.map { readStackResourcesFromAwsRecursively(it) } }
                        }
                        .awaitAll()
                }
                .flatten()

        val stackEntities =
            stacksWithResources.map { stackWithResources ->
                val stack = stackWithResources.first
                val stackId = StackId(stack.stackId())
                StackEntity(
                        stackId,
                        stack.stackName(),
                    )
                    .apply {
                        stack.mapOutputs().forEach { addOutput(it) }
                        stack.mapParameters().forEach { addParameter(it) }
                        stackWithResources.second.mapResources().forEach { addResource(it) }
                    }
            }

        stackEntities.forEach { stackEntity ->
            stackEntities.findDependents(stackEntity.name).forEach { stackEntity.addDependent(it) }
        }
        return stackEntities
    }

    private fun Stack.mapParameters() =
        parameters().map { parameter ->
            StackParameter(parameter.parameterKey(), parameter.parameterValue())
        }

    private fun Stack.mapOutputs() =
        outputs().map { output ->
            StackOutput(
                output.outputKey(),
                output.outputValue(),
                output.exportName(),
                output.description()
            )
        }

    private fun List<StackResourceSummary>.mapResources() = map { summary ->
        StackResource(
            summary.logicalResourceId(),
            summary.physicalResourceId(),
            summary.resourceType().toResourceType()
        )
    }

    private fun List<StackEntity>.findDependents(stackName: String): List<StackEntity> =
        filter { entity ->
            entity.parameters().any { it.value == stackName }
        }

    private suspend fun readStacksFromAwsRecursively(token: String? = null): List<Stack> {
        val response =
            cloudFormationClient
                .describeStacks {
                    if (token != null) {
                        it.nextToken(token)
                    }
                }
                .await()

        val stacks = response.stacks()
        val nextToken = response.nextToken()
        return if (nextToken != null) {
            stacks + readStacksFromAwsRecursively(nextToken)
        } else stacks
    }

    private suspend fun readStackResourcesFromAwsRecursively(
        stack: Stack,
        token: String? = null
    ): Pair<Stack, List<StackResourceSummary>> {
        val response =
            cloudFormationClient
                .listStackResources {
                    if (token != null) {
                        it.nextToken(token)
                    }
                    it.stackName(stack.stackName())
                }
                .await()

        val stackWithResourceSummaries = stack to response.stackResourceSummaries()
        val nextToken = response.nextToken()
        return if (nextToken != null) {
            stackWithResourceSummaries.apply {
                second.addAll(readStackResourcesFromAwsRecursively(stack, nextToken).second)
            }
        } else stackWithResourceSummaries
    }
}
