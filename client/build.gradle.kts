import com.github.gradle.node.pnpm.task.PnpmTask

plugins {
  id("com.github.node-gradle.node") version "3.5.1"
}

group = "com.kamelia"
version = project.properties["project.version"] as String

node {
  version.set("18.16.0") // lts version
  distBaseUrl.set("https://nodejs.org/dist")
  download.set(true)
  nodeProjectDir.set(file("${rootProject.projectDir}/client"))
  workDir.set(file("${rootProject.projectDir}/.gradle/nodejs"))
}

tasks {
  register<Delete>("pnpmClean") {
    delete(file("${rootProject.projectDir}/client/node_modules"))
    delete(file("${rootProject.projectDir}/client/.output"))
    delete(file("${rootProject.projectDir}/client/.nuxt"))
    delete(file("${rootProject.projectDir}/client/dist"))
    delete(file("${rootProject.projectDir}/server/src/main/resources/static"))
  }

  register<PnpmTask>("pnpmLint") {
    dependsOn(pnpmInstall)
    pnpmCommand.set(listOf("lint"))
    ignoreExitValue.set(false)
  }

  val pnpmBuild = register<PnpmTask>("pnpmBuild") {
    dependsOn(pnpmInstall)
    pnpmCommand.set(listOf("generate"))
    ignoreExitValue.set(false)
  }

  register<Copy>("bundleClient") {
    dependsOn(pnpmBuild)
    from("${rootProject.projectDir}/client/.output/public")
    into("${rootProject.projectDir}/server/src/main/resources/static")
  }
}
