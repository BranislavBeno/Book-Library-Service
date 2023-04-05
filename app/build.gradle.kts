import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    application
    jacoco
    id("org.springframework.boot") version "3.0.5"
    id("org.sonarqube") version "4.0.0.2929"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
    id("org.cyclonedx.bom") version "1.7.4"
}

apply(plugin = "io.spring.dependency-management")

jacoco {
    toolVersion = "0.8.9"
}

sonarqube {
    properties {
        property("sonar.projectKey", "BranislavBeno_BookLibraryService")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
        vendor.set(JvmVendorSpec.AZUL)
    }
}

springBoot {
    buildInfo()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.1.0")
    implementation("org.webjars:webjars-locator-core")
    implementation("org.webjars:bootstrap:5.2.3")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.micrometer:micrometer-observation-test")
}

val versionMajor = 0
val versionMinor = 1
val versionPatch = 0
version = "v${versionMajor}.${versionMinor}.${versionPatch}"

tasks.getByName<BootJar>("bootJar") {
    this.archiveFileName.set("book-library-service.jar")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    afterSuite(KotlinClosure2<TestDescriptor, TestResult, Unit>({ descriptor, result ->
        if (descriptor.parent == null) {
            logger.lifecycle(
                "\nTest result: ${result.resultType}"
            )
            logger.lifecycle(
                "Test summary: " +
                        "${result.testCount} tests, " +
                        "${result.successfulTestCount} succeeded, " +
                        "${result.failedTestCount} failed, " +
                        "${result.skippedTestCount} skipped"
            )
        }
    }))
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}

tasks.cyclonedxBom {
    setIncludeConfigs(listOf("runtimeClasspath"))
    setSkipConfigs(listOf("compileClasspath", "testCompileClasspath"))
    setProjectType("application")
    setDestination(project.file("build/reports/sbom"))
    setOutputName("CycloneDX-SBOM")
    setOutputFormat("all")
    setIncludeBomSerialNumber(false)
    setIncludeLicenseText(true)
    setComponentVersion("2.0.0")
}
