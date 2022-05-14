import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    kotlin("jvm") version "1.6.21"
}

group = "com.kamelia"
version = "0.0.1"

project(":server")

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

val isWindows = Os.isFamily(Os.FAMILY_WINDOWS)

val npmCmd = "npm${if (isWindows) ".cmd" else ""}"
val exportCmd = if (isWindows) "set" else "export"

tasks.register<Delete>("npm-clean") {
    delete(file("client/dist"))
    delete(file("server/src/main/resources/static/"))
}

tasks.register<Exec>("npm-install") {
    workingDir("client")
    commandLine(npmCmd, "install")
}

tasks.register<Exec>("npm-generate") {
    workingDir("client")
    commandLine(exportCmd, "NODE_OPTIONS=--openssl-legacy-provider")
    commandLine(npmCmd, "run", "generate")
}

tasks.register<Copy>("bundle-client") {
    from("client/dist")
    into("server/src/main/resources/static")
}

tasks.findByName("clean")?.dependsOn("npm-clean")
tasks.findByName("bundle-client")?.dependsOn("npm-generate")
tasks.findByName("build")?.dependsOn("bundle-client")
