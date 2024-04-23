rootProject.name = "aws-buddy"

include("aws-buddy-frontend")
include("aws-buddy-backend")

pluginManagement {
    val quarkusVersion: String by settings
    val quarkusPluginId: String by settings

    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id(quarkusPluginId) version quarkusVersion
        kotlin("jvm") version "1.9.23"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
