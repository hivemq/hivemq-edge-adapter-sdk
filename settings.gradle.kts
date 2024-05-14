rootProject.name = "hivemq-edge-adapter-sdk"

pluginManagement {
    plugins {
        id("io.github.gradle-nexus.publish-plugin") version "${extra["plugin.nexus-publish.version"]}"
        id("com.github.sgtsilvio.gradle.metadata") version "${extra["plugin.metadata.version"]}"
        id("com.github.sgtsilvio.gradle.javadoc-links") version "${extra["plugin.javadoc-links.version"]}"
        if (file("../hivemq-edge/edge-plugins").exists()) {
            includeBuild("../hivemq-edge/edge-plugins")
        }
    }
}