import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Dependencies.Kotlin.version
}

group = "com.marcguilera"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(Dependencies.Kotlin.stdlib)
    implementation(Dependencies.Kotlin.reflect)
    implementation(Dependencies.Kotlin.logging)
    implementation(Dependencies.Gatling.core)
    implementation(Dependencies.Gatling.app)
    implementation(Dependencies.Gatling.http)
    implementation(Dependencies.Kotlin.time)
    implementation("org.scalaj:scalaj-collection_2.8.1:1.2")

    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.assertk)
    testImplementation(Dependencies.Test.mockito)
    testImplementation(Dependencies.Test.Spek.dsl)
    testImplementation(Dependencies.Test.Spek.junit)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.suppressWarnings = true
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines = setOf("spek2")
    }
}