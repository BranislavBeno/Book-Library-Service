import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    application
    id("java-library-conventions")
    id("spotless-conventions")
    id("sonarqube-conventions")
    id("cyclonedx-sbom-conventions")
    id("org.springframework.boot") version "3.0.6"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
}

apply(plugin = "io.spring.dependency-management")

springBoot {
    buildInfo()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(platform(libs.jackson.bom))
    implementation(libs.jackson.dataformat.xml)
    implementation(libs.thymeleaf.layout.dialect)
    implementation(libs.webjars.locator.core)
    implementation(libs.webjars.bootstrap)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.micrometer.observation.test)
    runtimeOnly(libs.database.h2)
}

val versionMajor = 0
val versionMinor = 1
val versionPatch = 0
version = "v$versionMajor.$versionMinor.$versionPatch"

tasks.getByName<BootJar>("bootJar") {
    this.archiveFileName.set("book-library-service.jar")
}
