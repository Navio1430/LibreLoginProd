import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.security.MessageDigest
import java.util.Base64
import org.gradle.api.artifacts.repositories.MavenArtifactRepository

plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.8"
    id("net.kyori.blossom").version("1.3.1")
    id("java-library")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Inlined replacement for the discontinued xyz.kyngs.libby.plugin (1.2.1).
// Upstream removed the plugin artifacts when rebranding Libby to Librarian, so the
// plugin can no longer be resolved. This reproduces its behaviour: a `libby`
// configuration that is also available at compile time, and a `libby` task that
// generates build/libby/libby.json describing the runtime dependencies.

open class LibbyExtension {
    val excludedDependencies = mutableListOf<String>()
    val noChecksumDependencies = mutableListOf<String>()

    fun excludeDependency(dependency: String) {
        excludedDependencies.add(dependency)
    }

    fun noChecksumDependency(dependency: String) {
        noChecksumDependencies.add(dependency)
    }
}

fun jsonEscape(value: String): String {
    val out = StringBuilder(value.length)
    for (c in value) {
        when (c) {
            '"' -> out.append("\\\"")
            '\\' -> out.append("\\\\")
            '\n' -> out.append("\\n")
            '\r' -> out.append("\\r")
            '\t' -> out.append("\\t")
            else ->
                if (c < ' ') out.append("\\u%04x".format(c.code)) else out.append(c)
        }
    }
    return out.toString()
}

// The dependency accessor that plugins{} used to generate is unavailable for a
// configuration declared in the build script itself, so provide it explicitly.
fun DependencyHandler.libby(notation: Any) = add("libby", notation)

val libbyConfiguration = configurations.create("libby")
configurations.named("compileOnly") {
    extendsFrom(libbyConfiguration)
}

val libbyExtension = extensions.create<LibbyExtension>("libby")

val libbyOutputDir = layout.buildDirectory.dir("libby")

val libbyTask =
    tasks.register("libby") {
        val shadowJarTask = tasks.named<ShadowJar>("shadowJar")
        val repositoryHandler = project.repositories
        val extension = libbyExtension
        val configuration = libbyConfiguration

        outputs.dir(libbyOutputDir)
        inputs.files(configuration)

        doLast {
            val output = libbyOutputDir.get().asFile.resolve("libby.json")
            output.parentFile.mkdirs()

            // "{}" stands in for "." so shade cannot rewrite these coordinates
            fun dotted(value: String) = value.replace(".", "{}")

            val json = StringBuilder()
            json.append("{\"version\":0,\"libraries\":[")

            val digest = MessageDigest.getInstance("SHA-256")
            var firstLibrary = true

            for (artifact in configuration.resolvedConfiguration.resolvedArtifacts) {
                val id = artifact.moduleVersion.id
                val idString = "${id.group}:${id.name}:${id.version}"

                if (extension.excludedDependencies.any { Regex(it).matches(idString) }) continue

                if (!firstLibrary) json.append(',')
                firstLibrary = false

                json.append("{\"group\":\"").append(jsonEscape(dotted(id.group)))
                json.append("\",\"name\":\"").append(jsonEscape(id.name))
                json.append("\",\"version\":\"").append(jsonEscape(id.version)).append('"')

                artifact.classifier?.let {
                    json.append(",\"classifier\":\"").append(jsonEscape(it)).append('"')
                }

                val skipChecksum =
                    artifact.type != "jar" ||
                        extension.noChecksumDependencies.any { Regex(it).matches(idString) }

                if (!skipChecksum) {
                    val hash = digest.digest(artifact.file.readBytes())
                    json.append(",\"checksum\":\"")
                        .append(Base64.getEncoder().encodeToString(hash))
                        .append('"')
                }

                json.append('}')
            }

            json.append("],\"repositories\":[")
            var firstRepository = true
            for (repository in repositoryHandler) {
                if (repository !is MavenArtifactRepository) continue
                val url = repository.url.toString()
                if (!url.startsWith("http")) continue
                if (!firstRepository) json.append(',')
                firstRepository = false
                json.append('"').append(jsonEscape(url)).append('"')
            }
            json.append(']')

            // Relocations are only emitted when shade is applied, matching the
            // original plugin, which always wrote the object in that case.
            if (tasks.findByName("shadowJar") != null) {
                json.append(",\"relocations\":{")
                var firstRelocation = true
                for (relocator in shadowJarTask.get().relocators) {
                    if (relocator !is SimpleRelocator) continue
                    if (!firstRelocation) json.append(',')
                    firstRelocation = false
                    json.append('"')
                        .append(jsonEscape(dotted(relocator.pattern)))
                        .append("\":\"")
                        .append(jsonEscape(dotted(relocator.shadedPattern)))
                        .append('"')
                }
                json.append('}')
            }

            json.append('}')

            output.writeText(json.toString())
        }
    }

// Pulling the generated file in through a task output provider registers the
// implicit task dependency, so libby.json is produced before resources are copied.
tasks.named<ProcessResources>("processResources") {
    from(libbyTask)
}

repositories {
    // mavenLocal()
    maven { url = uri("https://repo.opencollab.dev/maven-snapshots/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://hub.spigotmc.org/nexus/") }
    maven { url = uri("https://repo.kyngs.xyz/public/") }
    maven { url = uri("https://mvn.exceptionflug.de/repository/exceptionflug-public/") }
    maven { url = uri("https://repo.dmulloy2.net/repository/public/") }
    maven { url = uri("https://repo.alessiodp.com/releases/") }
    maven { url = uri("https://jitpack.io/") }
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
}

blossom {
    replaceToken("@version@", version)
}

tasks.withType<ShadowJar> {
    destinationDirectory.set(file("../target"))

    archiveBaseName.set("LibreLoginProd")
    archiveClassifier = null

    dependencies {
        exclude(dependency("org.slf4j:.*:.*"))
        exclude(dependency("org.checkerframework:.*:.*"))
        exclude(dependency("com.google.errorprone:.*:.*"))
        exclude(dependency("com.google.protobuf:.*:.*"))
    }

    relocate("co.aikar.acf", "xyz.kyngs.librelogin.lib.acf")
    relocate("com.github.benmanes.caffeine", "xyz.kyngs.librelogin.lib.caffeine")
    relocate("com.typesafe.config", "xyz.kyngs.librelogin.lib.hocon")
    relocate("com.zaxxer.hikari", "xyz.kyngs.librelogin.lib.hikari")
    relocate("org.mariadb", "xyz.kyngs.librelogin.lib.mariadb")
    relocate("org.bstats", "xyz.kyngs.librelogin.lib.metrics")
    relocate("org.intellij", "xyz.kyngs.librelogin.lib.intellij")
    relocate("org.jetbrains", "xyz.kyngs.librelogin.lib.jetbrains")
    relocate("io.leangen.geantyref", "xyz.kyngs.librelogin.lib.reflect")
    relocate("org.spongepowered.configurate", "xyz.kyngs.librelogin.lib.configurate")
    relocate("net.byteflux.libby", "xyz.kyngs.librelogin.lib.libby")
    relocate("org.postgresql", "xyz.kyngs.librelogin.lib.postgresql")
    relocate("com.github.retrooper.packetevents", "xyz.kyngs.librelogin.lib.packetevents.api")
    relocate("io.github.retrooper.packetevents", "xyz.kyngs.librelogin.lib.packetevents.platform")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<Jar> {
    from("../LICENSE.txt")
}

configure<LibbyExtension> {
    excludeDependency("org.slf4j:.*:.*")
    excludeDependency("org.checkerframework:.*:.*")
    excludeDependency("com.google.errorprone:.*:.*")
    excludeDependency("com.google.protobuf:.*:.*")

    // Often redeploys the same version, so calculating checksum causes false flags
    noChecksumDependency("com.github.retrooper.packetevents:.*:.*")
}

//configurations.all {
//    // I hate this, but it needs to be done as bungeecord does not support newer versions of adventure, and packetevents includes it
//    resolutionStrategy {
//        force("net.kyori:adventure-text-minimessage:4.14.0")
//        force("net.kyori:adventure-text-serializer-gson:4.14.0")
//        force("net.kyori:adventure-text-serializer-legacy:4.14.0")
//        force("net.kyori:adventure-text-serializer-json:4.14.0")
//        force("net.kyori:adventure-api:4.14.0")
//        force("net.kyori:adventure-nbt:4.14.0")
//        force("net.kyori:adventure-key:4.14.0")
//    }
//}

dependencies {
    //API
    implementation(project(":API"))

    //Velocity
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-proxy:3.2.0-SNAPSHOT-277")
    compileOnly("com.github.ProxioDev.ValioBungee:RedisBungee-Bungee:0.13.0")

    //MySQL
    libby("org.mariadb.jdbc:mariadb-java-client:3.5.4")
    libby("com.zaxxer:HikariCP:6.3.2")

    //SQLite
    libby("org.xerial:sqlite-jdbc:3.50.3.0")

    //PostgreSQL
    libby("org.postgresql:postgresql:42.7.8")

    //ACF
    libby("com.github.kyngs.commands:acf-velocity:7d5bf7cac0")
    libby("com.github.kyngs.commands:acf-paper:7d5bf7cac0")

    //Utils
    libby("com.github.ben-manes.caffeine:caffeine:3.2.2")
    libby("org.spongepowered:configurate-hocon:4.2.0")
    libby("at.favre.lib:bcrypt:0.10.2")
    libby("dev.samstevens.totp:totp:1.7.1")
    compileOnly("dev.simplix:protocolize-api:2.4.2")
    libby("org.bouncycastle:bcprov-jdk18on:1.81")
    libby("org.apache.commons:commons-email:1.6.0")
    libby("net.kyori:adventure-text-minimessage:4.25.0")
    libby("com.github.kyngs:LegacyMessage:0.2.0")

    //Geyser
    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT")
    //LuckPerms
    compileOnly("net.luckperms:api:5.5")

    //BStats
    libby("org.bstats:bstats-velocity:3.1.0")
    libby("org.bstats:bstats-bukkit:3.1.0")

    //Paper
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation("com.github.retrooper:packetevents-spigot:2.14.0")
    compileOnly("org.apache.logging.log4j:log4j-core:2.25.1")

    //Libby
    implementation("xyz.kyngs.libby:libby-bukkit:1.6.0")
    implementation("xyz.kyngs.libby:libby-velocity:1.6.0")
    implementation("xyz.kyngs.libby:libby-paper:1.6.0")
}

tasks.withType<ProcessResources> {
    outputs.upToDateWhen { false }
    filesMatching("plugin.yml") {
        expand(mapOf("version" to version))
    }
    filesMatching("paper-plugin.yml") {
        expand(mapOf("version" to version))
    }
}