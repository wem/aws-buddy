package ch.sourcemotion.aws.buddy.testcontainers

import ch.sourcemotion.aws.buddy.infrastructure.ConfigurationProperties
import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.utility.DockerImageName

class Neo4jTestResource : AbstractContainerTestResource() {

    companion object {
        val neo4jContainer: Neo4jContainer<*> =
            Neo4jContainer(DockerImageName.parse(System.getenv("NEO4J_DOCKER_IMAGE")))
                .withoutAuthentication()
    }

    override fun start(): Map<String, String> {
        neo4jContainer.startContainer()
        return mapOf(ConfigurationProperties.Neo4j.NEO4J_URI to neo4jContainer.boltUrl)
    }

    override fun stop() {
        neo4jContainer.stopContainer()
    }
}
