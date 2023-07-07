import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    application
    id("java-library-conventions")
    id("spotless-conventions")
    id("sonarqube-conventions")
    id("cyclonedx-sbom-conventions")
}

apply(plugin = "io.spring.dependency-management")

springBoot { buildInfo() }

repositories { mavenCentral() }

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.client)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.thymeleaf.spring.security)
    implementation(platform(libs.jackson.bom))
    implementation(libs.jackson.dataformat.xml)
    implementation(libs.thymeleaf.layout.dialect)
    implementation(libs.webjars.locator.core)
    implementation(libs.webjars.bootstrap)
    implementation(libs.webjars.font.awesome)
    implementation(libs.problem.spring.web)
    implementation(platform(libs.spring.cloud.aws.bom))
    implementation(libs.spring.cloud.aws.starter)
    implementation(libs.aws.sdk.cognito.idp)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.spring.addons.oauth2.test)
    testImplementation(libs.micrometer.observation.test)
    runtimeOnly(libs.database.h2)
}

val versionMajor = 0
val versionMinor = 1
val versionPatch = 0

version = "v$versionMajor.$versionMinor.$versionPatch"

tasks.getByName<BootJar>("bootJar") { this.archiveFileName.set("book-library-service.jar") }

tasks.getByName<BootRun>("bootRun") { this.jvmArgs = listOf("-Dspring.profiles.active=dev") }
