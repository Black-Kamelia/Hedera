val jvmVersion: String = project.properties["jvm.version"] as String
val kotlinVersion: String = project.properties["kotlin.version"] as String
val ktorVersion: String = project.properties["ktor.version"] as String
val coroutinesVersion: String = project.properties["coroutines.version"] as String
val exposedVersion: String = project.properties["exposed.version"] as String
val postgresqlVersion: String = project.properties["postgresql.version"] as String
val liquibaseVersion: String = project.properties["liquibase.version"] as String
val liquibaseLoggingVersion: String = project.properties["liquibase.logging.version"] as String
val hikaricpVersion: String = project.properties["hikaricp.version"] as String
val bcryptVersion: String = project.properties["bcrypt.version"] as String
val h2Version: String = project.properties["h2.version"] as String
val logbackVersion: String = project.properties["logback.version"] as String
val junitVersion: String = project.properties["junit.version"] as String

plugins {
    application
    id("org.jetbrains.kotlinx.kover") version "0.7.2"
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.kamelia"
version = project.properties["project.version"] as String

application {
    mainClass.set("com.kamelia.hedera.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor", "ktor-server-core", ktorVersion)
    implementation("io.ktor", "ktor-server-auth", ktorVersion)
    implementation("io.ktor", "ktor-server-auth-jwt", ktorVersion)
    implementation("io.ktor", "ktor-server-sessions", ktorVersion)
    implementation("io.ktor", "ktor-server-host-common", ktorVersion)
    implementation("io.ktor", "ktor-server-content-negotiation", ktorVersion)
    implementation("io.ktor", "ktor-server-status-pages", ktorVersion)
    implementation("io.ktor", "ktor-serialization-kotlinx-json", ktorVersion)
    implementation("io.ktor", "ktor-server-netty", ktorVersion)
    implementation("io.ktor", "ktor-server-cors", ktorVersion)
    implementation("io.ktor", "ktor-server-auto-head-response", ktorVersion)
    implementation("io.ktor", "ktor-server-config-yaml", ktorVersion)
    implementation("io.ktor", "ktor-server-websockets", ktorVersion)
    implementation("io.ktor", "ktor-server-freemarker", ktorVersion)

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", coroutinesVersion)
    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-java-time", exposedVersion)

    implementation("ch.qos.logback", "logback-classic", logbackVersion)
    implementation("org.postgresql", "postgresql", postgresqlVersion)
    implementation("org.liquibase", "liquibase-core", liquibaseVersion)
    implementation("com.zaxxer", "HikariCP", hikaricpVersion)
    implementation("at.favre.lib", "bcrypt", bcryptVersion)
    implementation("io.trbl", "blurhash", "1.0.0")
    implementation("net.coobird", "thumbnailator", "0.4.20")
    implementation("com.sun.mail", "jakarta.mail", "2.0.1")
    implementation("com.black-kamelia.sprinkler", "i18n", "1.0.0")
    implementation("com.black-kamelia.sprinkler", "utils", "2.1.0")

    testImplementation("com.h2database", "h2", h2Version)
    testImplementation("io.ktor", "ktor-client-content-negotiation", ktorVersion)
    testImplementation("io.ktor", "ktor-server-test-host", ktorVersion)
    testImplementation("io.ktor", "ktor-server-tests", ktorVersion)
    testImplementation("org.jetbrains.kotlin", "kotlin-test", kotlinVersion)
    testImplementation("org.junit.jupiter", "junit-jupiter-params", junitVersion)

    runtimeOnly("com.mattbertolini", "liquibase-slf4j", liquibaseLoggingVersion)
}

tasks {
    compileJava {
        sourceCompatibility = jvmVersion
        targetCompatibility = jvmVersion
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = jvmVersion
        }
    }

    shadowJar {
        archiveBaseName.set("Hedera")
        archiveClassifier.set("")
        archiveVersion.set(project.version.toString())
        destinationDirectory.set(file("$rootDir/executables"))
    }

    processResources {
        mustRunAfter(":client:bundle")
    }

    jar {
        manifest {
            attributes["Main-Class"] = "com.kamelia.hedera.ApplicationKt"
        }
    }

    register<JavaExec>("runDev") {
        group = "application"
        environment = mapOf(
            "HEDERA_ENV" to "dev",
            "HEDERA_JWT_SECRET" to "secret",
            "HEDERA_JWT_REFRESH_SECRET" to "secretRefresh",
            "HEDERA_JWT_WS_SECRET" to "secretWSToken",
            "HEDERA_MAIL_AUTH" to "false",
            "HEDERA_MAIL_FROM" to "hedera@localhost",
            "HEDERA_MAIL_HOST" to "localhost",
            "HEDERA_MAIL_PORT" to "1025",
            "HEDERA_MAIL_TLS" to "false",
        )
        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("com.kamelia.hedera.ApplicationKt")
    }

    test {
        useJUnitPlatform()
        ignoreFailures = true
        environment = mapOf(
            "HEDERA_ENV" to "dev",
            "HEDERA_JWT_SECRET" to "secret",
            "HEDERA_JWT_REFRESH_SECRET" to "secretRefresh",
            "HEDERA_JWT_WS_SECRET" to "secretWSToken",
        )
        finalizedBy(koverVerify)
    }

    koverVerify {
        finalizedBy(koverXmlReport)
    }

    koverXmlReport {
        finalizedBy(koverHtmlReport)
    }
}

koverReport {
    filters {
        excludes {
            classes("**.*\$\$serializer")
        }
    }
}
