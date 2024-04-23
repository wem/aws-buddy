package ch.sourcemotion.aws.buddy.application.cloudformation

import ch.sourcemotion.aws.buddy.application.cloudformation.interfaces.AwsStackRepository
import ch.sourcemotion.aws.buddy.application.cloudformation.interfaces.CloudFormationApplicationService
import io.quarkus.arc.DefaultBean
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
@DefaultBean
class DefaultCloudFormationApplicationService(private val awsStackRepository: AwsStackRepository) :
    CloudFormationApplicationService {
    override suspend fun refreshStacks() {
        //        val stackEntities = AWSStackRepository.readStacks()
        println()
    }
}
