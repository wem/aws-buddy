package ch.sourcemotion.aws.buddy.testcontainers

import ch.sourcemotion.aws.buddy.infrastructure.ConfigurationProperties.AWS.CF_ENDPOINT
import ch.sourcemotion.aws.buddy.infrastructure.ConfigurationProperties.AWS.REGION
import ch.sourcemotion.aws.buddy.infrastructure.ConfigurationProperties.AWS.S3_ENDPOINT
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.localstack.LocalStackContainer.Service.CLOUDFORMATION
import org.testcontainers.containers.localstack.LocalStackContainer.Service.S3
import org.testcontainers.utility.DockerImageName

class LocalstackTestResource : AbstractContainerTestResource() {

    companion object {
        val localstackContainer: LocalStackContainer =
            LocalStackContainer(DockerImageName.parse(System.getenv("LOCALSTACK_DOCKER_IMAGE")))
                .withServices(CLOUDFORMATION, S3)
    }

    override fun start(): Map<String, String> {
        localstackContainer.startContainer()
        val endpointUrl = "${localstackContainer.endpoint.toURL()}"
        return buildMap {
            put(CF_ENDPOINT, endpointUrl)
            put(S3_ENDPOINT, endpointUrl)

            put(REGION, localstackContainer.region)
        }
    }

    override fun stop() {
        localstackContainer.stopContainer()
    }
}
