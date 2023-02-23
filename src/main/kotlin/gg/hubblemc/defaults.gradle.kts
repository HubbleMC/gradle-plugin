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

package gg.hubblemc

import gg.hubblemc.defaults.applyDefaultsConvention
import gg.hubblemc.defaults.applyDefaultsRepo
import gg.hubblemc.defaults.plugin.applyDefaultsPluginJava
import gg.hubblemc.defaults.plugin.applyDefaultsPluginKotlin
import gg.hubblemc.defaults.plugin.applyDefaultsPluginMavenPublish
import gg.hubblemc.defaults.plugin.applyDefaultsPluginShadow
import gg.hubblemc.util.hubbleOwned
import gg.hubblemc.util.propertyOrEnv
import gg.hubblemc.util.releaseType
import gg.hubblemc.util.yesNo

// Apply other scripts
applyDefaultsConvention(project)
applyDefaultsRepo(project)

// Apply plugins
pluginManager.withPlugin("java") { applyDefaultsPluginJava(project) }
pluginManager.withPlugin("org.jetbrains.kotlin.jvm") { applyDefaultsPluginKotlin(project) }
pluginManager.withPlugin("com.github.johnrengelman.shadow") { applyDefaultsPluginShadow(project) }

if (hubbleOwned) {
    pluginManager.withPlugin("maven-publish") { applyDefaultsPluginMavenPublish(project) }
}

// Print build info
gradle.projectsEvaluated {
    if (!project.hasProperty("hubble.verbose")) return@projectsEvaluated
    if (rootProject == project && hubbleOwned) {
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
    logger.lifecycle("")
}
