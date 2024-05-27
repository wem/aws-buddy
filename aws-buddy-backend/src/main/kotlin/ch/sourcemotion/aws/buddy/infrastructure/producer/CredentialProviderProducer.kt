package ch.sourcemotion.aws.buddy.infrastructure.producer

import ch.sourcemotion.aws.buddy.infrastructure.ConfigurationProperties.AWS.PROFILE
import io.quarkus.arc.DefaultBean
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import java.util.*
import org.eclipse.microprofile.config.inject.ConfigProperty
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider

@ApplicationScoped
class CredentialProviderProducer(@ConfigProperty(name = PROFILE) val profile: Optional<String>) {

    @ApplicationScoped
    @Produces
    @DefaultBean
    fun createCredentialsProvider(): AwsCredentialsProvider {
        return profile.orElse(null)?.let { profile ->
            ProfileCredentialsProvider.builder().profileName(profile).build()
        } ?: AwsCredentialsProviderChain.builder().reuseLastProviderEnabled(true).build()
    }
}
