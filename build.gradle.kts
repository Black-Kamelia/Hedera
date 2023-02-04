import com.github.gradle.node.pnpm.task.PnpmTask

plugins {
    kotlin("jvm") version "1.7.10"
    id("com.github.node-gradle.node") version "3.5.1"
}

group = "com.kamelia"
version = project.properties["project.version"] as String

repositories {
    mavenCentral()
}

node {
    version.set("18.13.0") // lts version
    distBaseUrl.set("https://nodejs.org/dist")
    download.set(true)
    nodeProjectDir.set(file("${project.projectDir}/client"))
    workDir.set(file("${project.projectDir}/.gradle/nodejs"))
}

val pnpmClean = tasks.register<Delete>("pnpmClean") {
    delete(file("${project.projectDir}/client/node_modules"))
    delete(file("${project.projectDir}/client/.output"))
    delete(file("${project.projectDir}/client/.nuxt"))
    delete(file("${project.projectDir}/client/dist"))
    delete(file("${project.projectDir}/server/src/main/resources/static"))
}

val pnpmBuild = tasks.register<PnpmTask>("pnpmBuild") {
    dependsOn(tasks.pnpmInstall)
    pnpmCommand.set(listOf("generate"))
    ignoreExitValue.set(false)
}

val bundleClient = tasks.register<Copy>("bundleClient") {
    dependsOn(pnpmBuild)
    from("client/.output/public")
    into("server/src/main/resources/static")
}

tasks.clean {
    dependsOn(pnpmClean)
    delete(file("${project.projectDir}/executables"))
    delete(file("${project.projectDir}/upload"))
    delete(file("${project.projectDir}/server/upload"))
}

tasks.build {
    dependsOn(bundleClient)
}
