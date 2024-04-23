package ch.sourcemotion.aws.buddy.infrastructure.cloudformation

import ch.sourcemotion.aws.buddy.infrastructure.cloudformation.fixture.CloudFormationFixture
import ch.sourcemotion.aws.buddy.infrastructure.cloudformation.repository.DefaultAwsStackRepository
import ch.sourcemotion.aws.buddy.testcontainers.LocalstackTestResource
import io.kotest.assertions.asClue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

@QuarkusTest
@QuarkusTestResource(LocalstackTestResource::class, restrictToAnnotatedClass = true)
class DefaultAwsStackRepositoryTest {

    @Inject lateinit var sut: DefaultAwsStackRepository
    @Inject lateinit var fixture: CloudFormationFixture

    @Test
    fun `read stacks with dependencies`(): Unit = runBlocking {
        val stackOneName = "stack-1"
        val stackTwoName = "stack-2"
        val stackThreeName = "stack-3"
        fixture.createSimpleAwsStackWithoutDependency(stackOneName)
        fixture.createSimpleAwsStackWithDependency(stackTwoName, stackOneName)
        fixture.createSimpleAwsStackWithTwoDependencies(stackThreeName, stackOneName, stackTwoName)

        val stacks = sut.readAll()
        stacks.shouldHaveSize(3)

        stacks
            .first { it.name == stackOneName }
            .asClue { stackOne ->
                stackOne
                    .dependents()
                    .shouldHaveSize(2)
                    .map { it.name }
                    .shouldContainExactlyInAnyOrder(stackTwoName, stackThreeName)
            }

        stacks
            .first { it.name == stackTwoName }
            .asClue { stackTwo ->
                stackTwo
                    .dependents()
                    .shouldHaveSize(1)
                    .map { it.name }
                    .shouldContainExactlyInAnyOrder(stackThreeName)
            }
    }

    //    @Test
    //    fun `read a lot of stacks`(): Unit = runBlocking {
    //        val stackCount = 1000000
    //        val stackNames = LinkedList((1..stackCount).map { "stack-$it".toStackName() })
    //        supervisorScope {
    //            repeat(5) {
    //                launch {
    //                    while (stackNames.isNotEmpty()) {
    //                        fixture.createSimpleAwsStackWithoutDependency(stackNames.poll())
    //                        if (stackNames.size % 1000 == 0) {
    //                            println("${stackNames.size} stacks pending")
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //        val stacks = sut.readStacks()
    //        stacks.shouldHaveSize(stackCount)
    //    }
}
