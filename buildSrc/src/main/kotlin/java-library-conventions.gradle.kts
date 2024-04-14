import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec

plugins {
    `java-library`
    jacoco
    id("org.springframework.boot")
    id("com.gorylenko.gradle-git-properties")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.AZUL)
    }
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.test {
    this.jvmArgs = listOf("-Dspring.profiles.active=dev")
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
