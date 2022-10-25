package gg.hubblemc

// Apply other scripts
apply(plugin = "gg.hubblemc.defaults.convention")
apply(plugin = "gg.hubblemc.defaults.repo")

// Apply plugins
pluginManager.withPlugin("java") { apply(plugin = "gg.hubblemc.defaults.plugin.java") }
pluginManager.withPlugin("maven-publish") { apply(plugin = "gg.hubblemc.defaults.plugin.maven-publish") }
pluginManager.withPlugin("org.jetbrains.kotlin.jvm") { apply(plugin = "gg.hubblemc.defaults.plugin.kotlin") }
pluginManager.withPlugin("com.github.johnrengelman.shadow") { apply(plugin = "gg.hubblemc.defaults.plugin.shadow") }
