package ch.sourcemotion.aws.buddy.domain.cloudformation.entity

data class StackParameter(val key: String, val value: String) {
    companion object {
        const val LABEL = "CF_STACK_PARAMETER"
        const val KEY_PROP = "key"
        const val VALUE_PROP = "value"
    }
}
