package ch.sourcemotion.aws.buddy.domain.cloudformation.entity

import ch.sourcemotion.aws.buddy.shared.ResourceType

data class StackResource(val logicalId: String, val physicalId: String, val type: ResourceType) {
    companion object {
        const val LABEL = "CF_STACK_RESOURCE"
        const val LOGICAL_ID_PROP = "logicalId"
        const val PHYSICAL_ID_PROP = "physicalId"
        const val TYPE_PROP = "type"
    }
}
