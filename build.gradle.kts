import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/*
 * HubbleMC - Gradle Plugin
 * Copyright (C) 2022  Zerite Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

plugins {
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.1.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    // Core API
    implementation(gradleApi())
    implementation(localGroovy())

    // Codegen
    implementation("com.squareup:javapoet:1.13.0")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("com.google.code.gson:gson:2.10")

    // Feature - Linting
    implementation("com.diffplug.spotless:com.diffplug.spotless.gradle.plugin:6.10.0")
    implementation("org.jlleitschuh.gradle.ktlint:org.jlleitschuh.gradle.ktlint.gradle.plugin:11.2.0")
    implementation("org.jlleitschuh.gradle.ktlint-idea:org.jlleitschuh.gradle.ktlint-idea.gradle.plugin:11.2.0")

    // Feature - Defaults
    implementation("com.github.johnrengelman.shadow:com.github.johnrengelman.shadow.gradle.plugin:7.1.2")
    implementation("com.palantir.git-version:com.palantir.git-version.gradle.plugin:0.15.0")

    // Feature - Paper
    implementation("com.github.jpenilla.plugin-yml:net.minecrell.plugin-yml.bukkit.gradle.plugin:4553fb2dd8")
    implementation("xyz.jpenilla.run-paper:xyz.jpenilla.run-paper.gradle.plugin:2.0.1")
    implementation("io.papermc.paperweight.userdev:io.papermc.paperweight.userdev.gradle.plugin:1.3.7")

    // Feature - Velocity
    implementation("xyz.jpenilla.run-velocity:xyz.jpenilla.run-velocity.gradle.plugin:2.0.1")

    // Other dependencies
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

@Suppress("UnstableApiUsage")
gradlePlugin {
    website.set("https://zerite.dev")
    vcsUrl.set("https://github.com/HubbleMC/gradle-plugin")

    (plugins) {
        "gg.hubblemc.paper" {
            displayName = "HubbleMC Paper Plugin"
            description = "Utilities for developing Paper plugins."
            tags.set(listOf("spigot", "paper", "minecraft"))
        }

        "gg.hubblemc.linting" {
            displayName = "HubbleMC Linting Plugin"
            description = "Pre-configured linting for HubbleMC projects."
            tags.set(listOf("linting", "formatting", "code-style"))
        }

        "gg.hubblemc.velocity" {
            displayName = "HubbleMC Velocity Plugin"
            description = "Utilities for developing Velocity plugins."
            tags.set(listOf("velocity", "minecraft"))
        }

        "gg.hubblemc.defaults" {
            displayName = "HubbleMC Defaults Plugin"
            description = "Pre-configured defaults for HubbleMC projects."
            tags.set(listOf("defaults", "conventions"))
        }
    }
}

configure<PublishingExtension> {
    repositories {
        // Hubble - Authenticated
        // To use this, you must set either
        // Properties: hubble.username, hubble.password
        // Environment: HUBBLE_USERNAME, HUBBLE_PASSWORD
        maven("https://repo.koding.dev/hubble-releases/") {
            name = "HubbleReleases"
            credentials {
                username = project.findProperty("hubble.username") as String? ?: System.getenv("HUBBLE_USERNAME")
                password = project.findProperty("hubble.password") as String? ?: System.getenv("HUBBLE_PASSWORD")
            }
        }
    }
}
