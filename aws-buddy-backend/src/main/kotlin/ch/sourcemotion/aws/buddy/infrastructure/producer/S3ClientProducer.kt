package ch.sourcemotion.aws.buddy.infrastructure.producer

import io.quarkus.arc.DefaultBean
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import java.net.URI
import java.util.*
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.context.ManagedExecutor
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.core.client.config.SdkAdvancedAsyncClientOption.FUTURE_COMPLETION_EXECUTOR
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.internal.crt.S3CrtAsyncClient

@ApplicationScoped
class S3ClientProducer(
    @ConfigProperty(name = "aws.endpoint.url.s3")
    private val endpoint: Optional<String>,
    @ConfigProperty(name = "aws.region", defaultValue = "eu-west-1") private val region: String,
    private val credentialProvider: AwsCredentialsProvider,
    private val managedExecutor: ManagedExecutor
) {

    @ApplicationScoped
    @Produces
    @DefaultBean
    fun createCloudFormationClient(): S3AsyncClient {
        val clientBuilder =
            S3AsyncClient.builder().credentialsProvider(credentialProvider)
        endpoint.ifPresent { endpoint -> clientBuilder.endpointOverride(URI.create(endpoint)) }
        clientBuilder.asyncConfiguration {
            it.advancedOptions(mapOf(FUTURE_COMPLETION_EXECUTOR to managedExecutor))
        }
        clientBuilder.region(Region.of(region))
        return clientBuilder.build()
    }
}
