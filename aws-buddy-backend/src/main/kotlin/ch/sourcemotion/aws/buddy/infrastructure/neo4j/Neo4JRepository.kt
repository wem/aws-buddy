package ch.sourcemotion.aws.buddy.infrastructure.neo4j

import kotlinx.coroutines.future.await
import org.neo4j.driver.*
import org.neo4j.driver.async.AsyncSession
import org.neo4j.driver.async.AsyncTransaction
import org.neo4j.driver.internal.value.NullValue

interface Neo4JRepository {

    companion object {
        val readOnlySessionOptions: SessionConfig =
            SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).build()
    }

    suspend fun <T> Driver.useReadOnlySession(block: suspend (AsyncSession) -> T) =
        useSession(readOnlySessionOptions, block)

    suspend fun <T> Driver.useSession(
        sessionConfig: SessionConfig? = null,
        block: suspend (AsyncSession) -> T
    ): T {
        val session =
            if (sessionConfig != null) {
                session(AsyncSession::class.java, sessionConfig)
            } else session(AsyncSession::class.java)
        return try {
            block(session)
        } finally {
            session.closeAsync().await()
        }
    }

    suspend fun <T> AsyncSession.useTx(block: suspend (AsyncTransaction) -> T): T {
        val tx = beginTransactionAsync().await()
        return try {
            block(tx).also { tx.commitAsync().await() }
        } catch (cause: Throwable) {
            tx.rollbackAsync().await()
            throw cause
        } finally {
            if (tx.isOpenAsync.await()) {
                tx.closeAsync().await()
            }
        }
    }

    fun <T : Value> Record.ifNotNull(name: String, block: T.() -> Unit) {
        val value = get(name)
        if (value != null && value !is NullValue) {
            block(value as T)
        }
    }
}
