package ch.sourcemotion.aws.buddy.infrastructure.cloudformation.fixture

import java.io.InputStream

object FixtureFileContentReader {

    val cloudformation = Cloudformation()

    class Cloudformation(
        private val baseFolder: String =
            "/ch/sourcemotion/aws/buddy/infrastructure/cloudformation/fixture"
    ) {
        fun simpleStackWithDependency(): String {
            return Cloudformation::class
                .java
                .getResourceAsStream("$baseFolder/simple-stack-with-dependency.yaml")!!
                .readContent()
        }

        fun simpleStackWithTwoDependencies(): String {
            return Cloudformation::class
                .java
                .getResourceAsStream("$baseFolder/simple-stack-with-two-dependencies.yaml")!!
                .readContent()
        }

        fun simpleStackWithoutDependency(): String {
            return Cloudformation::class
                .java
                .getResourceAsStream("$baseFolder/simple-stack-without-dependency.yaml")!!
                .readContent()
        }
    }

    private fun InputStream.readContent(): String =
        bufferedReader(Charsets.UTF_8).use { it.readText() }
}
