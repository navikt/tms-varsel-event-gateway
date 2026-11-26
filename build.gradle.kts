import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    alias(managed.plugins.kotlin)
    alias(managed.plugins.tms.jarBundling)
    application
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
    mavenLocal()
}

dependencies {
    implementation(managed.jackson.datatypeJsr310)
    implementation(managed.jackson.moduleKotlin)
    implementation(managed.kafka.clients)
    implementation(managed.kotlinLogging)
    implementation(managed.prometheus.metricsCore)
    implementation(managed.tms.kafka.application)
    implementation(managed.tms.kafka.producerUtils)
    implementation(managed.tms.commonLib.utils)

    runtimeOnly(managed.logback.encoder)

    testImplementation(managed.junit.api)
    testImplementation(managed.junit.params)
    testImplementation(managed.kotest.assertion)

    testRuntimeOnly(managed.junit.engine)
    testRuntimeOnly(managed.junit.launcher)
}

application {
    mainClass.set("no.nav.tms.varsel.hendelse.gateway.ApplicationKt")
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            events("passed", "skipped", "failed")
        }
    }
}
