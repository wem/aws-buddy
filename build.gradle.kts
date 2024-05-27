plugins {
    kotlin("multiplatform") apply false
    kotlin("jvm") apply false
    kotlin("plugin.allopen") apply false
    id("org.jetbrains.kotlin.plugin.serialization") apply false
    id("com.ncorti.ktfmt.gradle") version "0.18.0" apply false
}

subprojects {
    repositories {
        mavenCentral()
    }
}