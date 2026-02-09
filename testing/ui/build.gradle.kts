import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

plugins {
    `java-library`
    `maven-publish`
    application
}

group = "com.hivemq"
version = "1.0.0-SNAPSHOT"
description = "Visual testing suite for HiveMQ Edge protocol adapter configuration forms"

repositories {
    mavenCentral()
}

dependencies {
    // HiveMQ Edge Adapter SDK for annotations
    implementation("com.hivemq:hivemq-edge-adapter-sdk:2026.1")

    // JSON Schema generation (same as hivemq-edge core)
    implementation("com.github.victools:jsonschema-generator:${libs.versions.victools.get()}")
    implementation("com.github.victools:jsonschema-module-jackson:${libs.versions.victools.get()}")

    // Jackson for JSON processing
    implementation(libs.jackson.databind)

    // Annotations
    implementation(libs.jetbrains.annotations)

    // Commons IO (needed by adapters that use IOUtils to load UI schemas)
    implementation("commons-io:commons-io:2.18.0")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    runtimeOnly("ch.qos.logback:logback-classic:1.4.14")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
}

application {
    mainClass.set("com.hivemq.edge.adapters.testing.AdapterTestServer")
}

// For testing with an adapter JAR
tasks.named<JavaExec>("run") {
    // Add adapter JAR: explicit parameter or auto-detect from build/libs/*-all.jar
    doFirst {
        val adapterJarParam = project.findProperty("adapterJar") as String?
        val adapterJar: File? = if (adapterJarParam != null) {
            file(adapterJarParam)
        } else {
            // Auto-detect: look for *-all.jar (shadowJar) in build/libs
            val libsDir = file("build/libs")
            val shadowJar = libsDir.listFiles()?.find { it.name.endsWith("-all.jar") }
            if (shadowJar != null) {
                logger.lifecycle("Auto-detected adapter JAR: ${shadowJar.absolutePath}")
            }
            shadowJar
        }

        if (adapterJar != null && adapterJar.exists()) {
            classpath += files(adapterJar)
        } else if (adapterJarParam != null) {
            throw GradleException("Adapter JAR not found: $adapterJarParam")
        } else {
            logger.warn("No adapter JAR found. Run with -PadapterJar=/path/to/adapter.jar or build with shadowJar first.")
        }
    }
}

// Configure source sets to use 'server/src' structure
sourceSets {
    main {
        java {
            srcDirs("server/src/main/java")
        }
        resources {
            srcDirs("server/src/main/resources", "frontend/dist")
        }
    }
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Main-Class" to application.mainClass.get()
        )
    }
}

// Task to copy frontend dist to resources for JAR packaging
tasks.register<Copy>("copyFrontendDist") {
    from("frontend/dist")
    into("build/resources/main/static")
    doFirst {
        if (!file("frontend/dist").exists()) {
            logger.warn("Frontend dist directory not found. Run 'npm run build' in frontend/ first.")
        }
    }
}

tasks.processResources {
    dependsOn("copyFrontendDist")
}

/* ******************** QA Check Tasks ******************** */

// Install npm dependencies
tasks.register<Exec>("npmInstall") {
    group = "verification"
    description = "Install npm dependencies for the frontend"
    workingDir = file("frontend")
    commandLine("npm", "install")
    inputs.file("frontend/package.json")
    inputs.file("frontend/package-lock.json")
    outputs.dir("frontend/node_modules")
}

// Start test server in background and return process handle
val serverPort = 8080
val serverStartupTimeout = 10_000L // 10 seconds

// Run Cypress tests and generate QA report
tasks.register<Exec>("cypressQaCheck") {
    group = "verification"
    description = "Run Cypress QA tests and generate report"
    workingDir = file("frontend")
    commandLine("npm", "run", "qa:check")
    dependsOn("npmInstall")
}

// Full QA check task - starts server, runs tests, stops server
tasks.register("qaCheck") {
    group = "verification"
    description = "Run full QA check: start server, run Cypress tests, generate report"

    doLast {
        // Determine adapter JAR: explicit parameter or auto-detect
        val adapterJarParam = project.findProperty("adapterJar") as String?
        val adapterJar: String = if (adapterJarParam != null) {
            adapterJarParam
        } else {
            // Auto-detect: look for *-all.jar (shadowJar) in build/libs
            val libsDir = file("build/libs")
            val shadowJar = libsDir.listFiles()?.find { it.name.endsWith("-all.jar") }

            if (shadowJar != null) {
                logger.lifecycle("Auto-detected adapter JAR: ${shadowJar.absolutePath}")
                shadowJar.absolutePath
            } else {
                // Check parent project's build/libs (when run from adapter project with testing-ui as submodule)
                val parentLibsDir = file("../build/libs")
                val parentShadowJar = parentLibsDir.listFiles()?.find { it.name.endsWith("-all.jar") }

                if (parentShadowJar != null) {
                    logger.lifecycle("Auto-detected adapter JAR: ${parentShadowJar.absolutePath}")
                    parentShadowJar.absolutePath
                } else {
                    throw GradleException(
                        """
                        No adapter JAR found. Either:
                        1. Run './gradlew shadowJar' first to build the adapter
                        2. Or specify: ./gradlew qaCheck -PadapterJar=/path/to/adapter.jar
                        """.trimIndent()
                    )
                }
            }
        }

        if (!file(adapterJar).exists()) {
            throw GradleException("Adapter JAR not found: $adapterJar")
        }

        val classpath = sourceSets["main"].runtimeClasspath + files(adapterJar)
        val classpathString = classpath.asPath

        // Start server process
        logger.lifecycle("Starting test server on port $serverPort...")
        val serverProcess = ProcessBuilder(
            "java",
            "-cp", classpathString,
            "-Dserver.port=$serverPort",
            "com.hivemq.edge.adapters.testing.AdapterTestServer"
        )
            .directory(projectDir)
            .redirectErrorStream(true)
            .start()

        // Wait for server to be ready
        val startTime = System.currentTimeMillis()
        var serverReady = false
        while (System.currentTimeMillis() - startTime < serverStartupTimeout) {
            try {
                val conn = URL("http://localhost:$serverPort/api/v1/management/protocol-adapters/types")
                    .openConnection() as HttpURLConnection
                conn.connectTimeout = 1000
                conn.readTimeout = 1000
                conn.inputStream.close()
                serverReady = true
                break
            } catch (e: Exception) {
                Thread.sleep(500)
            }
        }

        if (!serverReady) {
            serverProcess.destroyForcibly()
            throw GradleException("Server failed to start within ${serverStartupTimeout}ms")
        }
        logger.lifecycle("Server ready at http://localhost:$serverPort")

        try {
            // Run Cypress QA check
            logger.lifecycle("Running QA checks...")
            val npmProcess = ProcessBuilder("npm", "run", "qa:check")
                .directory(file("frontend"))
                .inheritIO()
                .start()

            val exitCode = npmProcess.waitFor()

            if (exitCode != 0) {
                logger.warn("QA check completed with failures (exit code: $exitCode)")
            } else {
                logger.lifecycle("QA check completed successfully!")
            }

            // Print report location
            logger.lifecycle("")
            logger.lifecycle("=" .repeat(60))
            logger.lifecycle("QA Report: frontend/qa-report.json")
            logger.lifecycle("=" .repeat(60))

        } finally {
            // Stop server
            logger.lifecycle("Stopping test server...")
            serverProcess.destroyForcibly()
            serverProcess.waitFor(5, TimeUnit.SECONDS)
        }
    }
}

// Convenience task to just view the form (interactive mode)
tasks.register("testUI") {
    group = "verification"
    description = "Start the test server for interactive form testing"
    dependsOn("run")
}

// View the last QA report
tasks.register<Exec>("qaReport") {
    group = "verification"
    description = "View the last QA report (run qaCheck first to generate)"
    workingDir = file("frontend")
    commandLine("node", "scripts/generate-report.mjs", "cypress/results/combined.json")
    isIgnoreExitValue = true
}

/* ******************** publishing ******************** */

publishing {
    publications {
        register<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("HiveMQ Edge Adapter SDK Testing UI")
                description.set(project.description)
                url.set("https://github.com/hivemq/hivemq-edge-adapter-sdk")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("hivemq")
                        name.set("HiveMQ GmbH")
                        email.set("info@hivemq.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/hivemq/hivemq-edge-adapter-sdk.git")
                    developerConnection.set("scm:git:ssh://github.com/hivemq/hivemq-edge-adapter-sdk.git")
                    url.set("https://github.com/hivemq/hivemq-edge-adapter-sdk")
                }
            }
        }
    }
}
