package ch.sourcemotion.aws.buddy.domain.cloudformation.entity

import ch.sourcemotion.aws.buddy.shared.StackId

class StackEntity(
    val id: StackId,
    val name: String,
) {
    companion object {
        const val LABEL = "CF_STACK"
        const val ID_PROP = "id"
        const val NAME_PROP = "name"
        const val STACK_CONTAINS_RELATION = "CF_STACK_CONTAINS"
    }

    private val dependents: MutableSet<StackEntity> = HashSet()

    private val parameters: MutableSet<StackParameter> = HashSet()
    private val outputs: MutableSet<StackOutput> = HashSet()
    private val resources: MutableSet<StackResource> = HashSet()

    fun addDependent(otherStack: StackEntity) = dependents.add(otherStack)

    fun addParameter(parameter: StackParameter) = parameters.add(parameter)

    fun addOutput(output: StackOutput) = outputs.add(output)

    fun addResource(resource: StackResource) = resources.add(resource)

    fun dependents(): List<StackEntity> = dependents.toList()

    fun parameters(): List<StackParameter> = parameters.toList()

    fun outputs(): List<StackOutput> = outputs.toList()

    fun resources(): List<StackResource> = resources.toList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StackEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
