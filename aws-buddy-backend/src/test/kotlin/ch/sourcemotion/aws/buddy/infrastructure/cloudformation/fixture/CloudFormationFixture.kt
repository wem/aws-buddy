package ch.sourcemotion.aws.buddy.infrastructure.cloudformation.fixture

import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient
import software.amazon.awssdk.services.cloudformation.model.StackStatus

@ApplicationScoped
class CloudFormationFixture(private val client: CloudFormationAsyncClient) {

    suspend fun createSimpleAwsStackWithoutDependency(stackName: String) {
        client
            .createStack {
                it.stackName(stackName)
                it.templateBody(
                    FixtureFileContentReader.cloudformation.simpleStackWithoutDependency()
                )
            }
            .await()
        awaitStackCreatedOrUpdated(stackName)
    }

    suspend fun createSimpleAwsStackWithDependency(
        stackName: String,
        otherStackName: String
    ) {
        client
            .createStack {
                it.stackName(stackName)
                it.parameters(parameterOf("OtherStackName", otherStackName))
                it.templateBody(FixtureFileContentReader.cloudformation.simpleStackWithDependency())
            }
            .await()
        awaitStackCreatedOrUpdated(stackName)
    }

    suspend fun createSimpleAwsStackWithTwoDependencies(
        stackName: String,
        otherStackName: String,
        anotherStackName: String
    ) {
        client
            .createStack {
                it.stackName(stackName)
                it.parameters(
                    parameterOf("OtherStackName", otherStackName),
                    parameterOf("AnotherStackName", anotherStackName),
                )
                it.templateBody(
                    FixtureFileContentReader.cloudformation.simpleStackWithTwoDependencies()
                )
            }
            .await()
        awaitStackCreatedOrUpdated(stackName)
    }

    private suspend fun awaitStackCreatedOrUpdated(stackName: String) {
        val status = client.describeStacks {
            it.stackName(stackName)
        }.await().stacks().first().stackStatus()
        if (status != StackStatus.CREATE_COMPLETE && status != StackStatus.UPDATE_COMPLETE){
            delay(100)
            awaitStackCreatedOrUpdated(stackName)
        }
    }
}
