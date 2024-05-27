package ch.sourcemotion.aws.buddy.infrastructure

object ConfigurationProperties {
    object AWS {
        const val REGION = "aws.region"
        const val DEFAULT_AWS_REGION = "eu-west-1"
        const val PROFILE = "aws.profile"
        const val CF_ENDPOINT = "aws.endpoint.url.cloudformation"
        const val S3_ENDPOINT = "aws.endpoint.url.s3"
    }

    object Neo4j {
        const val NEO4J_URI = "neo4j.uri"
        const val NEO4J_USERNAME = "neo4j.username"
        const val NEO4J_PASSWORD = "neo4j.password"
    }
}
