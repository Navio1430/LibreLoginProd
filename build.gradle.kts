import org.cadixdev.gradle.licenser.LicenseProperties

plugins {
    id("org.cadixdev.licenser") version "0.6.1"
    id("com.diffplug.spotless") version "7.2.1"
}

defaultTasks("updateLicenses", "shadowJar")

version = "0.25.1"

subprojects {
    version = rootProject.version
    group = "xyz.kyngs.librelogin"

    apply {
        plugin("org.cadixdev.licenser")
        plugin("com.diffplug.spotless")
    }

    tasks.configureEach {
        if (name.contains("jar", true)) {
            dependsOn("updateLicenses")
//            dependsOn("spotlessJavaApply")
        }
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

    spotless {
        java {
            googleJavaFormat()
                .aosp()
                .reflowLongStrings()
                .reorderImports(false)
        }
    }
}