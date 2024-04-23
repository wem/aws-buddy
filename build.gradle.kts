plugins {
    kotlin("multiplatform") version "1.9.23" apply false
    kotlin("jvm") version "1.9.23" apply false
    kotlin("plugin.allopen") version "1.9.23" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23" apply false
    id("com.ncorti.ktfmt.gradle") version "0.17.0" apply false
}

subprojects {
    repositories {
        mavenCentral()
    }
}