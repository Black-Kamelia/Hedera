plugins {
    kotlin("jvm") version "1.6.21"
}

group = "com.kamelia"
version = "1.0-SNAPSHOT"

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

tasks.register<Delete>("npm-clean") {
    delete(file("client/dist"))
    delete(file("server/src/main/resources/static/"))
}

tasks.register<Exec>("npm-generate") {
    workingDir("client")
    commandLine("npm", "run", "generate")
}

tasks.register<Copy>("bundle-client") {
    from("client/dist")
    into("server/src/main/resources/static")
}

tasks.findByName("clean")?.dependsOn("npm-clean")
tasks.findByName("bundle-client")?.dependsOn("npm-generate")
tasks.findByName("build")?.dependsOn("bundle-client")
