import org.cadixdev.gradle.licenser.LicenseProperties

plugins {
    id("org.cadixdev.licenser") version "0.6.1"
}

defaultTasks("updateLicenses", "shadowJar")

version = "0.25.0"

subprojects {
    version = rootProject.version
    group = "xyz.kyngs.librelogin"

    apply {
        plugin("org.cadixdev.licenser")
    }

    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }

    license {
        header(rootProject.file("HEADER.txt"))
        include("**/*.java")
        newLine(true)

        matching("", closureOf<LicenseProperties> {
            header.set(rootProject.resources.text.fromFile("licenses/FASTLOGIN_LICENSE"))
        });
    }
}