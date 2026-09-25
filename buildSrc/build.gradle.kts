import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    maven {
        url = uri("https://artifacts.codegenomeproject.org/maven")
        credentials {
            username = providers.gradleProperty("codegenome.project.user").getOrElse("")
            password = providers.gradleProperty("codegenome.project.token").getOrElse("")
        }
    }
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.diffplug.spotless)
    implementation(libs.sonarqube)
    implementation(libs.cyclonedx.sbom)
    implementation(libs.open.rewrite)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_25)
        languageVersion.set(KotlinVersion.KOTLIN_2_3)
    }
}
