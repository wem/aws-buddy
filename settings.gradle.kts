rootProject.name = "aws-buddy"

include("aws-buddy-frontend")
include("aws-buddy-backend")

pluginManagement {
    val quarkusVersion: String by settings
    val quarkusPluginId: String by settings
    val kotlinVVersion: String by settings

    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id(quarkusPluginId) version quarkusVersion
        kotlin("jvm") version kotlinVVersion
        kotlin("multiplatform") version kotlinVVersion
        kotlin("plugin.allopen") version kotlinVVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVVersion
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
