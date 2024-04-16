import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("jvm").version(Kotlin.version)

    id(Shadow.pluginId) version (Shadow.version)

    application
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
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
    implementation(KotlinLogging.logging)
    implementation(Ktor.Serialization.jackson)
    implementation(Ktor.Serialization.kotlinX)
    implementation(Prometheus.common)
    implementation(Prometheus.hotspot)
    implementation(RapidsAndRivers.rapidsAndRivers)
    implementation(TmsCommonLib.utils)

    testImplementation(Junit.api)
    testImplementation(Junit.engine)
    testImplementation(Junit.params)
    testImplementation(Mockk.mockk)
    testImplementation(Kotest.assertionsCore)
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
