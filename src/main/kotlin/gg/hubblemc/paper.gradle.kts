/*
 * HubbleMC - Gradle Plugin
 * Copyright (C) 2023  Zerite Development
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

import gg.hubblemc.util.hubbleOwned
import gg.hubblemc.util.propertyOrEnv
import gg.hubblemc.util.requiredPropertyOrEnv
import gg.hubblemc.util.setup
import io.papermc.paperweight.userdev.PaperweightUser
import net.minecrell.pluginyml.bukkit.BukkitPlugin
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import xyz.jpenilla.runpaper.RunPaperPlugin
import xyz.jpenilla.runpaper.task.RunServer

// Read the properties
val mcVersion = requiredPropertyOrEnv("hubble.paper.mc-version")
val forkPackage = propertyOrEnv("hubble.paper.fork-package") ?: "io.papermc.paper"

// Prevent any dependencies from using "org.bukkit"
configurations.all {
    exclude(group = "org.bukkit", module = "bukkit")
}

// Apply third-party plugins
apply<BukkitPlugin>()
apply<PaperweightUser>()
apply<RunPaperPlugin>()

// Add task dependencies
tasks.named("assemble") {
    dependsOn("reobfJar")
}

@Suppress("UnstableApiUsage")
tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    filesMatching("plugin.yml") { expand(project.properties) }
}

// Add dependencies
dependencies {
    "paperweightDevelopmentBundle"("$forkPackage:dev-bundle:$mcVersion-R0.1-SNAPSHOT")
}

afterEvaluate {
    // Get the gradle plugin description & update archiveName
    val pluginDescription = project.extensions.getByType<BukkitPluginDescription>()
    extensions.getByName<BasePluginExtension>("base").archivesName.set(pluginDescription.name)
}

// Configure the Bukkit YAML plugin
configure<BukkitPluginDescription> {
    apiVersion = mcVersion.split(".").take(2).joinToString(".")
    if (hubbleOwned) authors = listOf("Hubble Team")
}

tasks.withType<RunServer> {
    // Spigot
    systemProperty("IReallyKnowWhatIAmDoingISwear", "true")
    systemProperty("LetMeReload", "true")
    systemProperty("com.mojang.eula.agree", "true")

    // Set up the run task
    setup(project, "paper")
}