package ch.sourcemotion.aws.buddy.testcontainers

import ch.sourcemotion.aws.buddy.testcontainers.LocalstackTestResource.Companion.localstackContainer
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider

@ApplicationScoped
class LocalstackAwsCredentialsProviderProducer {

    @ApplicationScoped
    @Produces
    fun produceAwsCredentialsProviderProducer(): AwsCredentialsProvider {
        return StaticCredentialsProvider.create(
            AwsBasicCredentials.create(localstackContainer.accessKey, localstackContainer.secretKey)
        )
    }
}
