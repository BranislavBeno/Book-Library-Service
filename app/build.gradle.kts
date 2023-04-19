import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    application
    jacoco
    id("org.springframework.boot") version "3.0.5"
    id("org.sonarqube") version "4.0.0.2929"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
    id("org.cyclonedx.bom") version "1.7.4"
    id("com.diffplug.spotless") version "6.18.0"
}

apply(plugin = "io.spring.dependency-management")

spotless {
    java {
        palantirJavaFormat(libs.palantir.javaformat.get().versionConstraint.requiredVersion)
        importOrder()
        removeUnusedImports()
        target("app/**/*.java")
        targetExclude("app/build/**/*.*")
    }
    kotlinGradle {
        ktlint(libs.pinterest.ktlint.get().versionConstraint.requiredVersion)
        target("*.gradle.kts")
        targetExclude("app/build/**/*.*")
    }
}

jacoco {
    toolVersion = "0.8.9"
}

sonarqube {
    properties {
        property("sonar.projectKey", "BranislavBeno_BookLibraryService")
        property("sonar.projectName", "book-library-service")
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

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    afterSuite(
        KotlinClosure2<TestDescriptor, TestResult, Unit>({ descriptor, result ->
            if (descriptor.parent == null) {
                logger.lifecycle(
                    "\nTest result: ${result.resultType}",
                )
                logger.lifecycle(
                    "Test summary: " +
                        "${result.testCount} tests, " +
                        "${result.successfulTestCount} succeeded, " +
                        "${result.failedTestCount} failed, " +
                        "${result.skippedTestCount} skipped",
                )
            }
        }),
    )
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
