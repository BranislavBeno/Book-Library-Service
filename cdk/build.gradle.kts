plugins {
    java
    application
    id("com.diffplug.spotless") version "6.18.0"
}

spotless {
    java {
        palantirJavaFormat(libs.palantir.javaformat.get().versionConstraint.requiredVersion)
        importOrder()
        removeUnusedImports()
        target("cdk/**/*.java")
        targetExclude("cdk/build/**/*.*")
    }
    kotlinGradle {
        ktlint(libs.pinterest.ktlint.get().versionConstraint.requiredVersion)
        target("*.gradle.kts")
        targetExclude("cdk/build/**/*.*")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
        vendor.set(JvmVendorSpec.AZUL)
    }
}

application {
    mainClass.set(
        if (project.hasProperty("mainClass")) {
            project.properties["mainClass"].toString()
        } else {
            "Main class not defined!"
        },
    )
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.aws.cdk)
}

version = "0.1.0-SNAPSHOT"
