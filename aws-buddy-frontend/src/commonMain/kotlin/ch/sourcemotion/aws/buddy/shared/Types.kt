package ch.sourcemotion.aws.buddy.shared

import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class StackId(override val id: String) : ARN {
    override fun toString() = id
}

fun String.toStackId() = StackId(this)

@Serializable
@JvmInline
value class S3ARN(override val id: String) : ARN {
    override fun toString() = id
}

interface ARN {
    val id: String
}

@Serializable
enum class ResourceType(val plainType: String) {
    S3_BUCKET("AWS::S3::Bucket")
}

fun String.toResourceType() =
    ResourceType.entries.firstOrNull { type -> this == type.plainType }
        ?: throw IllegalArgumentException("Unknown plain resource type '$this'")
