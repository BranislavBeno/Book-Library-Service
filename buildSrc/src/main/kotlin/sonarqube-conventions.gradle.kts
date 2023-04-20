plugins {
    id("org.sonarqube")
}

sonarqube {
    properties {
        property("sonar.projectName", "Book Library Service")
        property("sonar.projectKey", "BranislavBeno_BookLibraryService")
        property("sonar.qualitygate.wait", true)
    }
}
