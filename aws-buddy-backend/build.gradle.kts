import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("io.quarkus")
    id("org.jetbrains.kotlin.plugin.serialization")
}

repositories {
    mavenCentral()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusVersion: String by project

val neo4JVersion: String by project

val awsSdkVersion: String by project

val kotlinLoggingVersion: String by project

// Testing
val testcontainersVersion: String by project
val localstackVersion: String by project
val neo4jContainerVersion: String by project
val kotestVersion: String by project

dependencies {
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:${quarkusVersion}"))
    implementation(platform("software.amazon.awssdk:bom:$awsSdkVersion"))
    testImplementation(platform("org.testcontainers:testcontainers-bom:$testcontainersVersion"))

    implementation(project(":aws-buddy-frontend"))

    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-resteasy-reactive-kotlin-serialization")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-smallrye-context-propagation")
    implementation("org.neo4j.driver:neo4j-java-driver:$neo4JVersion")

    implementation("io.github.oshai:kotlin-logging:$kotlinLoggingVersion")

    // AWS
    implementation("software.amazon.awssdk:cloudformation")
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:sso")
    implementation("software.amazon.awssdk:sts")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.rest-assured:kotlin-extensions")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")

    testImplementation("org.testcontainers:localstack")
    testImplementation("org.testcontainers:neo4j")
    testImplementation("org.testcontainers:junit-jupiter")
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.GRAAL_VM
    }
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
        javaParameters = true
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.GRAAL_VM
    }
}

//ktfmt {
//    kotlinLangStyle()
//}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    environment("LOCALSTACK_DOCKER_IMAGE" to "localstack/localstack:$localstackVersion")
    environment("NEO4J_DOCKER_IMAGE" to "neo4j:$neo4jContainerVersion")
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}