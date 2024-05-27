package ch.sourcemotion.aws.buddy.infrastructure.producer

import ch.sourcemotion.aws.buddy.infrastructure.ConfigurationProperties.AWS.CF_ENDPOINT
import ch.sourcemotion.aws.buddy.infrastructure.ConfigurationProperties.AWS.DEFAULT_AWS_REGION
import ch.sourcemotion.aws.buddy.infrastructure.ConfigurationProperties.AWS.REGION
import io.quarkus.arc.DefaultBean
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import java.util.*
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.context.ManagedExecutor
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient

@ApplicationScoped
class CloudFormationClientProducer(
    @ConfigProperty(name = CF_ENDPOINT) private val endpoint: Optional<String>,
    @ConfigProperty(name = REGION, defaultValue = DEFAULT_AWS_REGION) private val region: String,
    private val credentialProvider: AwsCredentialsProvider,
    private val managedExecutor: ManagedExecutor
) : AbstractAwsClientProducer() {

    @ApplicationScoped
    @Produces
    @DefaultBean
    fun createCloudFormationClient(): CloudFormationAsyncClient {
        val clientBuilder =
            CloudFormationAsyncClient.builder()
                .configureAwsClient(
                    endpoint.orElse(null),
                    region,
                    credentialProvider,
                    managedExecutor
                )
        return clientBuilder.build()
    }
}
