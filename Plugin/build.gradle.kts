import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.8"
    id("net.kyori.blossom").version("1.3.1")
    id("java-library")
    id("xyz.kyngs.libby.plugin").version("1.2.1")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
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

libby {
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
    libby("net.kyori:adventure-text-minimessage:4.24.0")
    libby("com.github.kyngs:LegacyMessage:0.2.0")

    //Geyser
    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT")
    //LuckPerms
    compileOnly("net.luckperms:api:5.5")

    //BStats
    libby("org.bstats:bstats-velocity:3.1.0")
    libby("org.bstats:bstats-bukkit:3.1.0")

    //Paper
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    implementation("com.github.retrooper:packetevents-spigot:2.9.3")
    compileOnly("org.apache.logging.log4j:log4j-core:2.25.2")

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