plugins {
    java
    application
    alias(libs.plugins.git.properties)
    id("java-library-conventions")
    id("spotless-conventions")
    id("openrewrite-conventions")
}

application {
    mainClass.set(providers.gradleProperty("mainClass").orElse("Main class not defined!"))
}

repositories { mavenCentral() }

dependencies { implementation(libs.aws.cdk.lib) }

gitProperties { dotGitDirectory.set(File("${project.rootDir}/.git")) }

version = "0.1.0-SNAPSHOT"
