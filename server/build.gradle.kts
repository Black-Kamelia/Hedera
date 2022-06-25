val kotlinVersion: String = project.properties["kotlin.version"] as String
val ktorVersion: String = project.properties["ktor.version"] as String
val exposedVersion: String = project.properties["exposed.version"] as String
val postgresqlVersion: String = project.properties["postgresql.version"] as String
val liquibaseVersion: String = project.properties["liquibase.version"] as String
val hikaricpVersion: String = project.properties["hikaricp.version"] as String
val bcryptVersion: String = project.properties["bcrypt.version"] as String
val h2Version: String = project.properties["h2.version"] as String
val logbackVersion: String = project.properties["logback.version"] as String

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.kamelia"
version = project.properties["project.version"] as String

application {
    mainClass.set("com.kamelia.jellyfish.ApplicationKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor", "ktor-server-core-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-auth-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-auth-jwt-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-sessions-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-host-common-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-content-negotiation-jvm", ktorVersion)
    implementation("io.ktor", "ktor-serialization-kotlinx-json-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-netty-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-cors", ktorVersion)
    implementation("ch.qos.logback", "logback-classic", logbackVersion)

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.6.1")
    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-java-time", exposedVersion)
    implementation("org.postgresql", "postgresql", postgresqlVersion)
    implementation("org.liquibase", "liquibase-core", liquibaseVersion)
    implementation("com.zaxxer", "HikariCP", hikaricpVersion)
    implementation("at.favre.lib", "bcrypt", bcryptVersion)

    testImplementation("com.h2database", "h2", h2Version)
    testImplementation("io.ktor", "ktor-client-content-negotiation", ktorVersion)
    testImplementation("io.ktor", "ktor-server-test-host", ktorVersion)
    testImplementation("io.ktor", "ktor-server-tests-jvm", ktorVersion)
    testImplementation("org.jetbrains.kotlin", "kotlin-test", kotlinVersion)
}

tasks.shadowJar {
    archiveBaseName.set("Jellyfish")
    archiveClassifier.set("")
    archiveVersion.set(project.version.toString())
    destinationDirectory.set(file("$rootDir/executables"))
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.kamelia.jellyfish.ApplicationKt"
    }
}

tasks.register<JavaExec>("runDev") {
    group = "application"
    environment = mapOf("JELLYFISH_ENV" to "dev")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.kamelia.jellyfish.ApplicationKt")
}

tasks.test {
    useJUnitPlatform()
    environment = mapOf("JELLYFISH_ENV" to "dev")
}
