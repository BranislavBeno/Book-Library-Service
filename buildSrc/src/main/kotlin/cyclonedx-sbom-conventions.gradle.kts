plugins {
    id("org.cyclonedx.bom")
}

tasks.cyclonedxDirectBom {
    includeConfigs = listOf("runtimeClasspath")
    skipConfigs = listOf("compileClasspath", "testCompileClasspath")
    projectType = org.cyclonedx.model.Component.Type.APPLICATION
    includeBomSerialNumber = false
    includeLicenseText = true
}
