buildscript {
    if (gradle.includedBuilds.any { it.name == "edge-plugins" }) {
        plugins {
            id("com.hivemq.edge-version-updater")
        }
    }
}

plugins {
    `java-library`
    `maven-publish`
    signing
    alias(libs.plugins.mavencentralpublishing)
    alias(libs.plugins.defaults)
    alias(libs.plugins.metadata)
    alias(libs.plugins.javadoclinks)
    alias(libs.plugins.hivemq.license)
}

plugins.withId("com.hivemq.version-updater") {
    project.ext.set("versionUpdaterFiles", arrayOf("README.adoc"))
}

group = "com.hivemq"
description = "SDK for the development of HiveMQ Edge protocol adapters"

metadata {
    readableName.set("HiveMQ Edge Adapter SDK")
    organization {
        name.set("HiveMQ GmbH")
        url.set("https://www.hivemq.com/")
    }
    license {
        apache2()
    }
    developers {
        register("cschaebe") {
            fullName.set("Christoph Schaebel")
            email.set("christoph.schaebel@hivemq.com")
        }
        register("simon622") {
            fullName.set("Simon Johnson")
            email.set("simon.johnson@hivemq.com")
        }
        register("danielkruger") {
            fullName.set("Daniel Krüger")
            email.set("daniel.krueger@hivemq.com")
        }
    }
    github {
        org.set("hivemq")
        repo.set("hivemq-edge-adapter-sdk")
        issues()
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api(libs.jetbrains.annotations)
    compileOnly(libs.jackson.annotations)
    compileOnly(libs.jackson.databind)
    compileOnly(libs.swagger.annotations)
}

/* ******************** java ******************** */

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<Jar>().configureEach {
    manifest.attributes(
        "Implementation-Title" to project.name,
        "Implementation-Vendor" to metadata.organization.get().name.get(),
        "Implementation-Version" to project.version
    )
}

tasks.javadoc {
    title = "${metadata.readableName.get()} ${project.version} API"

    val javadocCleanerResult = providers.javaexec {
        classpath(layout.projectDirectory.file("gradle/tools/javadoc-cleaner-1.0.jar"))
    }.result
    doLast {
        javadocCleanerResult.get()
    }
}

/* ******************** publishing ******************** */

publishing {
    publications {
        register<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["maven"])
}

/* ******************** compliance ******************** */

hivemqLicense {
    projectName.set("HiveMQ Edge Adapter SDK")
    thirdPartyLicenseDirectory.set(layout.projectDirectory.dir("src/distribution/third-party-licenses"))
}
