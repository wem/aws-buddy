package ch.sourcemotion.aws.buddy.application.cloudformation.interfaces

interface CloudFormationApplicationService {
    suspend fun refreshStacks()
}
