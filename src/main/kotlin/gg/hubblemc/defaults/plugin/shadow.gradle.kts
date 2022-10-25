package gg.hubblemc.defaults.plugin

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val shadow: Configuration by configurations.getting {}
val shadowJar by tasks.named<ShadowJar>("shadowJar")

// Ensure that the shadow configuration is used for the API
configurations {
    "api" {
        isCanBeResolved = true
        extendsFrom(shadow)
    }
}

// Configure shadow
shadowJar.apply {
    configurations = listOf(shadow)

    // Exclude some Kotlin metadata as it causes a rare
    // IDE bug where some classes cannot be resolved
    exclude("**/*.kotlin_metadata", "**/*.kotlin_module")
}

// Run on build task
tasks.named("build") { dependsOn("shadowJar") }
