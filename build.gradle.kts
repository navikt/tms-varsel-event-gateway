import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("jvm").version(Kotlin.version)

    id(TmsJarBundling.plugin)

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
    implementation(JacksonDatatype.datatypeJsr310)
    implementation(JacksonDatatype.moduleKotlin)
    implementation(Kafka.kafka_2_12)
    implementation(KotlinLogging.logging)
    implementation(Ktor.Serialization.jackson)
    implementation(Ktor.Serialization.kotlinX)
    implementation(Logstash.logbackEncoder)
    implementation(Prometheus.metricsCore)
    implementation(TmsKafkaTools.kafkaApplication)
    implementation(TmsCommonLib.utils)
    implementation(TmsCommonLib.observability)

    testImplementation(JunitPlatform.launcher)
    testImplementation(JunitJupiter.api)
    testImplementation(JunitJupiter.params)
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
