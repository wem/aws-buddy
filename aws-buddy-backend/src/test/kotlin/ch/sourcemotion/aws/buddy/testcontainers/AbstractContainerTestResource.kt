package ch.sourcemotion.aws.buddy.testcontainers

import io.github.oshai.kotlinlogging.KotlinLogging
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.testcontainers.containers.GenericContainer

abstract class AbstractContainerTestResource : QuarkusTestResourceLifecycleManager {

    private companion object {
        val logger = KotlinLogging.logger {}
    }

    protected fun GenericContainer<*>.startContainer() {
        if (!isRunning) {
            logger.info { "Starting container $image" }
            start()
            logger.info { "Container $image stared" }
        } else logger.info { "Container $image not started because is already running" }
    }

    protected fun GenericContainer<*>.stopContainer() {
        if (isRunning) {
            logger.info { "Stopping container $image" }
            stop()
            logger.info { "Container $image stopped" }
        } else logger.info { "Container $image not stopped because is not running" }
    }
}
