pluginManagement {
    repositories {
        maven {
            url = uri("https://repo.kyngs.xyz/gradle-plugins")
        }
        gradlePluginPortal()
    }
}

rootProject.name = "LibreLoginProd"
include("Plugin", "API")