package ch.sourcemotion.aws.buddy.infrastructure.producer

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.neo4j.driver.AuthToken
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import java.util.*

@ApplicationScoped
class Neo4JDriverProducer(
    @ConfigProperty(name = NEO4J_URI) val uri: Optional<String>,
    @ConfigProperty(name = NEO4J_USERNAME) val username: Optional<String>,
    @ConfigProperty(name = NEO4J_PASSWORD) val password: Optional<String>,
) {

    companion object {
        val logger = KotlinLogging.logger {}

        const val NEO4J_URI = "neo4j.uri"
        const val NEO4J_USERNAME = "neo4j.username"
        const val NEO4J_PASSWORD = "neo4j.password"
    }

    @ApplicationScoped
    @Produces
    fun createNeo4JDriver() : Driver? {
        return if (uri.isPresent){
            GraphDatabase.driver(uri.get(), createAuth())
        } else null
    }

    private fun createAuth() : AuthToken {
        return if (username.isPresent && password.isPresent) {
            logger.info { "Using Neo4J basic auth" }
            AuthTokens.basic(username.get(), password.get())
        } else AuthTokens.none().also { logger.info { "Using Neo4J none auth" } }
    }
}