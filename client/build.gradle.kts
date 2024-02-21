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
  pnpmInstall {
    doNotTrackState("node_modules")
    ignoreExitValue.set(false)

    outputs.dir(file("${rootProject.projectDir}/client/node_modules"))
    outputs.dir(file("${rootProject.projectDir}/client/.nuxt"))
  }

  val lint = register<PnpmTask>("lint") {
    dependsOn(pnpmInstall)
    ignoreExitValue.set(true)

    outputs.file(file("${rootProject.projectDir}/client/eslint-report.html"))

    pnpmCommand.set(listOf("lint:report"))
  }

  val icons = register<PnpmTask>("icons") {
    dependsOn(lint)
    ignoreExitValue.set(false)

    inputs.dir(file("${rootProject.projectDir}/client/public/assets/icons/files"))
    outputs.dir(file("${rootProject.projectDir}/client/public/assets/icons"))

    pnpmCommand.set(listOf("icons"))
  }

  val build= register<PnpmTask>("build") {
    dependsOn(icons)
    ignoreExitValue.set(false)

    inputs.dir(file("${rootProject.projectDir}/client/components"))
    inputs.dir(file("${rootProject.projectDir}/client/composables"))
    inputs.dir(file("${rootProject.projectDir}/client/layouts"))
    inputs.dir(file("${rootProject.projectDir}/client/locales"))
    inputs.dir(file("${rootProject.projectDir}/client/pages"))
    inputs.dir(file("${rootProject.projectDir}/client/plugins"))
    inputs.dir(file("${rootProject.projectDir}/client/public"))
    inputs.dir(file("${rootProject.projectDir}/client/stores"))
    inputs.dir(file("${rootProject.projectDir}/client/types"))
    inputs.dir(file("${rootProject.projectDir}/client/utils"))
    outputs.dir(file("${rootProject.projectDir}/client/.output"))

    pnpmCommand.set(listOf("generate"))
  }

  register<Copy>("bundle") {
    dependsOn(build)

    inputs.dir(file("${rootProject.projectDir}/client/.output/public"))
    outputs.dir(file("${rootProject.projectDir}/server/src/main/resources/static"))

    from("${rootProject.projectDir}/client/.output/public")
    into("${rootProject.projectDir}/server/src/main/resources/static")
  }

  register<Delete>("clean") {
    delete(file("${rootProject.projectDir}/client/node_modules"))
    delete(file("${rootProject.projectDir}/client/dist"))
    delete(file("${rootProject.projectDir}/client/.output"))
    delete(file("${rootProject.projectDir}/client/.nuxt"))
    delete(file("${rootProject.projectDir}/client/build"))
    delete(file("${rootProject.projectDir}/client/eslint-report.html"))
    delete(file("${rootProject.projectDir}/server/src/main/resources/static"))
  }
}
