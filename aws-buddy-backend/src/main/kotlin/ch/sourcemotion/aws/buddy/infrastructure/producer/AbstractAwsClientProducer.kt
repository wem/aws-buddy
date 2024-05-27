package ch.sourcemotion.aws.buddy.infrastructure.producer

import java.net.URI
import org.eclipse.microprofile.context.ManagedExecutor
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.awscore.client.builder.AwsAsyncClientBuilder
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder
import software.amazon.awssdk.core.SdkClient
import software.amazon.awssdk.core.client.config.SdkAdvancedAsyncClientOption.FUTURE_COMPLETION_EXECUTOR
import software.amazon.awssdk.regions.Region

abstract class AbstractAwsClientProducer {

    protected fun <C : SdkClient, B> B.configureAwsClient(
        endpoint: String?,
        region: String,
        credentialProvider: AwsCredentialsProvider,
        managedExecutor: ManagedExecutor
    ): B where B : AwsAsyncClientBuilder<B, C>, B : AwsClientBuilder<B, C> {
        credentialsProvider(credentialProvider)
        endpoint?.let { endpointOverride(URI.create(it)) }
        asyncConfiguration {
            it.advancedOptions(mapOf(FUTURE_COMPLETION_EXECUTOR to managedExecutor))
        }
        return region(Region.of(region))
    }
}
