plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.spring.boot)
    implementation(libs.git.properties)
    implementation(libs.diffplug.spotless)
    implementation(libs.sonarqube)
    implementation(libs.cyclonedx.sbom)
}

tasks
    .withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>()
    .configureEach {
        compilerOptions.languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
    }

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_19.toString()
}

tasks.withType<JavaCompile> {
    java.targetCompatibility = JavaVersion.VERSION_19
}
