package ch.sourcemotion.aws.buddy.application.cloudformation.interfaces

import ch.sourcemotion.aws.buddy.domain.cloudformation.entity.StackEntity
import ch.sourcemotion.aws.buddy.infrastructure.neo4j.Neo4JRepository

interface StackRepository : Neo4JRepository {
    suspend fun clearRepository()

    suspend fun save(stacks: List<StackEntity>)

    suspend fun findAll() : List<StackEntity>
}
