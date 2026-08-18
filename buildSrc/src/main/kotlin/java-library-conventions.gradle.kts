import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec

val libs = the<LibrariesForLibs>()

plugins {
    `java-library`
    jacoco
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
        vendor.set(JvmVendorSpec.AZUL)
    }
}

jacoco {
    toolVersion = "0.8.15"
}

dependencies {
    implementation(libs.org.jspecify)
}

val testSummaryListener = object : TestListener {
    override fun beforeSuite(suite: TestDescriptor) = Unit

    override fun afterSuite(suite: TestDescriptor, result: TestResult) {
        if (suite.parent == null) {
            logger.lifecycle("\nTest result: ${result.resultType}")
            logger.lifecycle(
                "Test summary: " +
                        "${result.testCount} tests, " +
                        "${result.successfulTestCount} succeeded, " +
                        "${result.failedTestCount} failed, " +
                        "${result.skippedTestCount} skipped"
            )
        }
    }

    override fun beforeTest(testDescriptor: TestDescriptor) = Unit

    override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) = Unit
}

tasks.test {
    this.jvmArgs = listOf("-Dspring.profiles.active=dev")
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    addTestListener(testSummaryListener)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}
