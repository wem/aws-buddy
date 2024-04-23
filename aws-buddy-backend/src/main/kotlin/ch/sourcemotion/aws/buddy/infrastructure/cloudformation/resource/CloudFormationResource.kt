package ch.sourcemotion.aws.buddy.infrastructure.cloudformation.resource

import ch.sourcemotion.aws.buddy.application.cloudformation.interfaces.CloudFormationApplicationService
import io.smallrye.common.annotation.NonBlocking
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path

@Path("/cloudformation")
class CloudFormationResource(private val cfApplication: CloudFormationApplicationService) {

    @Path("/refresh")
    @PUT
    @NonBlocking
    suspend fun refreshStacks() {
        cfApplication.refreshStacks()
    }
}
