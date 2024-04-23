plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.ncorti.ktfmt.gradle")
}


kotlin {
    jvm {
        withJava()
    }

    js(IR) {
        useEsModules()
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            val kotlinSerializationVersion: String by project

            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
        }
    }
}

ktfmt {
    kotlinLangStyle()
}