import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    application
    id("java-library-conventions")
    id("spotless-conventions")
    id("sonarqube-conventions")
    id("cyclonedx-sbom-conventions")
    id("openrewrite-conventions")
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
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.thymeleaf.spring.security)
    implementation(libs.thymeleaf.layout.dialect)
    implementation(libs.webjars.locator.core)
    implementation(libs.webjars.bootstrap)
    implementation(libs.webjars.font.awesome)
    implementation(libs.problem.spring.web)
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)
    implementation(libs.micrometer.registry.cloudwatch)
    implementation(platform(libs.spring.cloud.aws.bom))
    implementation(libs.spring.cloud.aws.dynamodb)
    implementation(libs.spring.cloud.aws.sqs)
    implementation(libs.spring.cloud.aws.ses)
    implementation(libs.aws.sdk.cognito.idp)
    implementation(libs.logback.awslogs.json)
    runtimeOnly(libs.database.postgresql)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.micrometer.observation.test)
    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.testcontainers.common)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.localstack)
    testImplementation(libs.testcontainers.keycloak)
    testImplementation(libs.org.awaitility)
    testCompileOnly(libs.spring.boot.devtools)
    testRuntimeOnly(libs.junit.platform.launcher)
}

val versionMajor = 0
val versionMinor = 1
val versionPatch = 0

version = "v$versionMajor.$versionMinor.$versionPatch"

tasks.getByName<BootJar>("bootJar") { this.archiveFileName.set("book-library-service.jar") }

tasks.getByName<BootRun>("bootRun") { this.jvmArgs = listOf("-Dspring.profiles.active=aws") }

tasks.getByName<BootRun>("bootTestRun") { this.jvmArgs = listOf("-Dspring.profiles.active=dev") }
