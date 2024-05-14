rootProject.name = "hivemq-edge-adapter-sdk"

pluginManagement {
    plugins {
        if (file("../hivemq-edge/edge-plugins").exists()) {
            includeBuild("../hivemq-edge/edge-plugins")
        }
    }
}