package ch.sourcemotion.aws.buddy.testcontainers

import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName

class LocalstackTestResource : AbstractContainerTestResource() {

    companion object {
        val localstackContainer: LocalStackContainer =
            LocalStackContainer(DockerImageName.parse(System.getenv("LOCALSTACK_DOCKER_IMAGE")))
                .withServices(LocalStackContainer.Service.CLOUDFORMATION, LocalStackContainer.Service.S3)
    }

    override fun start(): Map<String, String> {
        localstackContainer.startContainer()
        val endpointUrl = "${localstackContainer.endpoint.toURL()}"
        return buildMap {
            put("aws.endpoint.url.cloudformation", endpointUrl)
            put("aws.endpoint.url.s3", endpointUrl)

            put("aws.region", localstackContainer.region)
        }
    }


    override fun stop() {
        localstackContainer.stopContainer()
    }
}
