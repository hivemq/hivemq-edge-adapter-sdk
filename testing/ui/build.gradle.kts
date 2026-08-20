import java.net.HttpURLConnection
import java.net.URI
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
    implementation("com.hivemq:hivemq-edge-adapter-sdk:2026.2")

    // JSON Schema generation (same as hivemq-edge core)
    implementation("com.github.victools:jsonschema-generator:${libs.versions.victools.get()}")
    implementation("com.github.victools:jsonschema-module-jackson:${libs.versions.victools.get()}")

    // Jackson for JSON processing
    implementation(libs.jackson.databind)

    // Annotations
    implementation(libs.jetbrains.annotations)

    // Commons IO (needed by adapters that use IOUtils to load UI schemas)
    implementation("commons-io:commons-io:2.22.0")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.18")
    runtimeOnly("ch.qos.logback:logback-classic:1.6.3")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
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

/* ******************** Frontend Build Tasks ******************** */

// `frontend/dist` is git-ignored, so a fresh checkout has no built frontend and the server would
// serve nothing. Gradle therefore has to own the whole chain -- npm install, vite build, copy --
// rather than assuming someone ran the npm commands by hand beforehand.

// Install npm dependencies
tasks.register<Exec>("npmInstall") {
    group = "build"
    description = "Install npm dependencies for the frontend"
    workingDir = file("frontend")
    commandLine("npm", "install")
    inputs.file("frontend/package.json")
    inputs.file("frontend/package-lock.json")
    outputs.dir("frontend/node_modules")
}

// Build the React frontend into frontend/dist
tasks.register<Exec>("buildFrontend") {
    group = "build"
    description = "Build the React frontend (tsc + vite build) into frontend/dist"
    dependsOn("npmInstall")
    workingDir = file("frontend")
    commandLine("npm", "run", "build")
    inputs.dir("frontend/src")
    inputs.files("frontend/index.html", "frontend/vite.config.ts", "frontend/tsconfig.json")
    inputs.file("frontend/package.json")
    outputs.dir("frontend/dist")
}

// Task to copy frontend dist to resources for JAR packaging
tasks.register<Copy>("copyFrontendDist") {
    dependsOn("buildFrontend")
    from("frontend/dist")
    into("build/resources/main/static")
}

tasks.processResources {
    dependsOn("copyFrontendDist")
}

// `frontend/dist` is a declared resources srcDir (see the sourceSets block above), so every task that
// reads the main resources consumes buildFrontend's output and has to say so. processResources gets
// there via copyFrontendDist; sourcesJar needs it spelled out.
tasks.named("sourcesJar") {
    dependsOn("buildFrontend")
}

/* ******************** QA Check Tasks ******************** */

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

    // The task spawns `java -cp <runtimeClasspath>` and points Cypress at the served frontend, so
    // both have to exist before doLast runs. Reading sourceSets["main"].runtimeClasspath below only
    // names the output directories; it does not schedule the tasks that fill them.
    dependsOn("classes", "processResources", "npmInstall")

    // Everything the action needs is resolved here, at configuration time. Reaching for `project`
    // (or `file()`/`sourceSets`, which go through it) from inside doLast is deprecated and becomes an
    // error in Gradle 10, because it cannot work with the configuration cache.
    val adapterJarParam = project.findProperty("adapterJar") as String?
    val runtimeClasspath = sourceSets["main"].runtimeClasspath
    val libsDir = file("build/libs")
    val parentLibsDir = file("../build/libs")
    val frontendDir = file("frontend")
    val serverLog = file("build/qa-server.log")
    val workingDir = projectDir

    doLast {
        // Determine adapter JAR: explicit parameter or auto-detect
        val adapterJar: String = if (adapterJarParam != null) {
            adapterJarParam
        } else {
            // Auto-detect: look for *-all.jar (shadowJar) in build/libs
            val shadowJar = libsDir.listFiles()?.find { it.name.endsWith("-all.jar") }

            if (shadowJar != null) {
                logger.lifecycle("Auto-detected adapter JAR: ${shadowJar.absolutePath}")
                shadowJar.absolutePath
            } else {
                // Check parent project's build/libs (when run from adapter project with testing-ui as submodule)
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

        val adapterJarFile = File(adapterJar)
        if (!adapterJarFile.exists()) {
            throw GradleException("Adapter JAR not found: $adapterJar")
        }

        val classpathString = runtimeClasspath.asPath + File.pathSeparator + adapterJarFile.absolutePath

        // Start server process. The output MUST go to a file rather than an undrained pipe: the server
        // logs on every request, and nothing here reads getInputStream(), so with the default pipe the
        // server blocks forever on write once the OS buffer fills. That shows up much later as Cypress
        // specs timing out on requests that "never respond", with no hint as to why.
        serverLog.parentFile.mkdirs()
        logger.lifecycle("Starting test server on port $serverPort (log: ${serverLog.relativeTo(workingDir)})...")
        val serverProcess = ProcessBuilder(
            "java",
            "-cp", classpathString,
            "-Dserver.port=$serverPort",
            "com.hivemq.edge.adapters.testing.AdapterTestServer"
        )
            .directory(workingDir)
            .redirectErrorStream(true)
            .redirectOutput(serverLog)
            .start()

        // Wait for server to be ready
        val startTime = System.currentTimeMillis()
        var serverReady = false
        while (System.currentTimeMillis() - startTime < serverStartupTimeout) {
            try {
                val conn = URI("http://localhost:$serverPort/api/v1/management/protocol-adapters/types")
                    .toURL()
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

        var exitCode = -1
        try {
            // Run Cypress QA check
            logger.lifecycle("Running QA checks...")
            val npmProcess = ProcessBuilder("npm", "run", "qa:check")
                .directory(frontendDir)
                .inheritIO()
                .start()

            exitCode = npmProcess.waitFor()

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

        // Fail after the server is stopped and the report is on disk, so a failing suite still
        // leaves its artifacts behind. Logging the exit code without rethrowing made every Cypress
        // failure -- and every failed merge or report generation -- come back as BUILD SUCCESSFUL,
        // which is worse than having no gate at all.
        if (exitCode != 0) {
            throw GradleException("QA check failed with exit code $exitCode. See frontend/qa-report.json")
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
