package ch.sourcemotion.aws.buddy.application.cloudformation.interfaces

import ch.sourcemotion.aws.buddy.domain.cloudformation.entity.StackEntity

interface AwsStackRepository {
    suspend fun readAll(): List<StackEntity>
}
