plugins {
    kotlin("jvm") version "1.8.10"
}

group = "com.kamelia"
version = project.properties["project.version"] as String

repositories {
    mavenCentral()
}

tasks {
    clean {
        dependsOn(":client:pnpmClean")
        delete(file("${project.projectDir}/executables"))
        delete(file("${project.projectDir}/upload"))
        delete(file("${project.projectDir}/server/upload"))
    }

    register<GradleBuild>("package") {
        dependsOn(":client:bundleClient")
        dependsOn(":server:shadowJar")
    }
}
