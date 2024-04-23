package ch.sourcemotion.aws.buddy.domain.cloudformation.entity

data class StackOutput(
    val key: String,
    val value: String,
    val description: String,
    val exportName: String,
) {
    companion object {
        const val LABEL = "CF_STACK_OUTPUT"
        const val KEY_PROP = "key"
        const val VALUE_PROP = "value"
        const val DESCRIPTION_PROP = "description"
        const val EXPORT_NAME_PROP = "exportName"
    }
}
