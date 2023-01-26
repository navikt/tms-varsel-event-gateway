import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm").version(Kotlin.version)
    kotlin("plugin.allopen").version(Kotlin.version)

    id(Shadow.pluginId) version (Shadow.version)

    application
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

repositories {
    maven("https://jitpack.io")
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(DittNAVCommonLib.utils)
    implementation(KotlinLogging.logging)
    implementation(Ktor2.Serialization.jackson)
    implementation(Ktor2.Serialization.kotlinX)
    implementation(Prometheus.common)
    implementation(Prometheus.hotspot)
    implementation(RapidsAndRivers.rapidsAndRivers)

    testImplementation(Junit.api)
    testImplementation(Junit.engine)
    testImplementation(Junit.params)
    testImplementation(Mockk.mockk)
    testImplementation(Kotest.assertionsCore)
}

application {
    mainClass.set("no.nav.tms.utkast.ApplicationKt")
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

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

project.setProperty("mainClassName", application.mainClass.get())
apply(plugin = Shadow.pluginId)
