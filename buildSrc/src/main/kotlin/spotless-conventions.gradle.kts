import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("com.diffplug.spotless")
}

val libs = the<LibrariesForLibs>()

spotless {
    java {
        palantirJavaFormat(libs.palantir.javaformat.get().versionConstraint.requiredVersion)
        importOrder()
        removeUnusedImports()
        target("*/**/*.java")
        targetExclude("*/build/**/*.*")
    }
    kotlinGradle {
        ktlint(libs.pinterest.ktlint.get().versionConstraint.requiredVersion)
        target("*.gradle.kts")
        targetExclude("*/build/**/*.*")
    }
}
