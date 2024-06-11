plugins {
    java
    application
    id("java-library-conventions")
    id("spotless-conventions")
    id("openrewrite-conventions")
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

repositories { mavenCentral() }

dependencies { implementation(libs.aws.cdk.lib) }

version = "0.1.0-SNAPSHOT"
