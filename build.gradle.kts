plugins {
    `kotlin-dsl`
    `maven-publish`
}

repositories {
    mavenCentral()
    gradlePluginPortal()

    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Core API
    implementation(gradleApi())
    implementation(localGroovy())

    // Codegen
    implementation("com.squareup:javapoet:1.13.0")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("com.google.code.gson:gson:2.9.1")

    // Feature - Linting
    implementation("com.diffplug.spotless:com.diffplug.spotless.gradle.plugin:6.10.0")
    implementation("org.jlleitschuh.gradle.ktlint:org.jlleitschuh.gradle.ktlint.gradle.plugin:11.0.0")
    implementation("org.jlleitschuh.gradle.ktlint-idea:org.jlleitschuh.gradle.ktlint-idea.gradle.plugin:11.0.0")

    // Feature - Defaults
    implementation("com.github.johnrengelman.shadow:com.github.johnrengelman.shadow.gradle.plugin:7.1.2")
    implementation("com.palantir.git-version:com.palantir.git-version.gradle.plugin:0.15.0")

    // Feature - Paper
    implementation("net.minecrell:plugin-yml:0.5.2")
    implementation("com.github.InnitGG:run-paper:7388ecca0f")
    implementation("io.papermc.paperweight.userdev:io.papermc.paperweight.userdev.gradle.plugin:1.3.7")

    // Other dependencies
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
}

java.withSourcesJar()

gradlePlugin {
    plugins {
        create("gg.hubblemc.paper") {
            id = "gg.hubblemc.paper"
            implementationClass = "gg.hubblemc.paper.PaperPlugin"
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
            credentials {
                username = project.findProperty("hubble.username") as String? ?: System.getenv("HUBBLE_USERNAME")
                password = project.findProperty("hubble.password") as String? ?: System.getenv("HUBBLE_PASSWORD")
            }
        }
    }
}
