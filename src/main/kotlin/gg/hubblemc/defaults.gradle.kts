package gg.hubblemc

import gg.hubblemc.util.propertyOrEnv
import gg.hubblemc.util.releaseType
import gg.hubblemc.util.yesNo

// Apply other scripts
apply(plugin = "gg.hubblemc.defaults.convention")
apply(plugin = "gg.hubblemc.defaults.repo")

// Apply plugins
pluginManager.withPlugin("java") { apply(plugin = "gg.hubblemc.defaults.plugin.java") }
pluginManager.withPlugin("maven-publish") { apply(plugin = "gg.hubblemc.defaults.plugin.maven-publish") }
pluginManager.withPlugin("org.jetbrains.kotlin.jvm") { apply(plugin = "gg.hubblemc.defaults.plugin.kotlin") }
pluginManager.withPlugin("com.github.johnrengelman.shadow") { apply(plugin = "gg.hubblemc.defaults.plugin.shadow") }

// Print build info
gradle.projectsEvaluated {
    if (!project.hasProperty("silent")) {
        if (rootProject == project) {
            val username = rootProject.propertyOrEnv("hubble.username")
            val password = rootProject.propertyOrEnv("hubble.password")

            logger.lifecycle(
                """
                  _  _  _   _  ___  ___  _     ___ 
                 | || || | | || _ )| _ )| |   | __|
                 | __ || |_| || _ \| _ \| |__ | _| 
                 |_||_| \___/ |___/|___/|____||___|
                """.trimIndent()
            )

            logger.lifecycle("")
            logger.lifecycle("Build Info")
            logger.lifecycle("  Local properties? ${rootProject.file("local.properties").exists().yesNo()}")
            logger.lifecycle("  Authenticated? ${(username != null && password != null).yesNo()}${if (username != null && password != null) " ($username)" else ""}")
            logger.lifecycle("")
        }

        val groupPrefix = group.takeIf { it.toString().isNotBlank() }?.let { "$it." } ?: ""
        logger.lifecycle("Building $groupPrefix${project.name} (version $version - release type ${project.releaseType})")
    }
}
