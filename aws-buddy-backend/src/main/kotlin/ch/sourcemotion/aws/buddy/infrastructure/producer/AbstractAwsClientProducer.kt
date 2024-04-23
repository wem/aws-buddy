package ch.sourcemotion.aws.buddy.infrastructure.producer

import org.eclipse.microprofile.context.ManagedExecutor

abstract class AbstractAwsClientProducer(private val managedExecutor: ManagedExecutor) {}
