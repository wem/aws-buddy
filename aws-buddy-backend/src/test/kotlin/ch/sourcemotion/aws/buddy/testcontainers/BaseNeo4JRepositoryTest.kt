package ch.sourcemotion.aws.buddy.testcontainers

import org.junit.jupiter.api.BeforeEach
import org.neo4j.driver.Driver

abstract class BaseNeo4JRepositoryTest {
    protected abstract var driver: Driver


    @BeforeEach
    fun clearDatabase() {
        driver.executableQuery("MATCH(n) OPTIONAL MATCH(n)-[r]-(m) DELETE n,r,m").execute()
    }
}