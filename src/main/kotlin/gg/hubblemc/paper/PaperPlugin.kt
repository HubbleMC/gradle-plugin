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

package gg.hubblemc.paper

import gg.hubblemc.tasks.FileDownloadTask
import gg.hubblemc.util.addProjectDependency
import io.papermc.paperweight.userdev.PaperweightUser
import net.minecrell.pluginyml.bukkit.BukkitPlugin
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources
import xyz.jpenilla.runpaper.RunPaper
import xyz.jpenilla.runpaper.task.RunServerTask

@Suppress("unused", "UnstableApiUsage")
abstract class PaperPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.run {
            // Register the extension
            val extension = extensions.create("paper", PaperExtension::class.java, project)

            // Apply third-party plugins
            apply<BukkitPlugin>()
            apply<PaperweightUser>()
            apply<RunPaper>()

            // Create a new configuration to download plugin jars and
            // add it as a dependency to the "runServer" task
            val pluginJarsConfiguration = configurations.create("pluginJars") {
                isTransitive = false
            }

            // Add task dependencies
            tasks.named("assemble") {
                dependsOn("reobfJar")
            }

            // Configure the runServer tasks
            tasks.withType<RunServerTask> {
                // Spigot
                systemProperty("IReallyKnowWhatIAmDoingISwear", "true")
                systemProperty("LetMeReload", "true")
                systemProperty("com.mojang.eula.agree", "true")
            }

            tasks.withType<ProcessResources> {
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
                filesMatching("plugin.yml") { expand(project.properties) }
            }

            // Add dependencies
            dependencies {
                "paperweightDevelopmentBundle"("gg.hubblemc:dev-bundle")
            }

            configurations.named("paperweightDevelopmentBundle").configure {
                resolutionStrategy.eachDependency {
                    if (requested.group == "gg.hubblemc" && requested.name == "dev-bundle") {
                        useTarget("${extension.forkPackage.get()}:dev-bundle:${extension.mcVersion.get()}-R0.1-SNAPSHOT")
                        because("Use the fork's dev-bundle")
                    }
                }
            }

            gradle.projectsEvaluated {
                // Configure the Bukkit YAML plugin
                configure<BukkitPluginDescription> {
                    apiVersion = extension.mcVersion.get().split(".").take(2).joinToString(".")
                    authors = listOf("Hubble Team")
                }

                // If there are any plugins which need to be added to the server,
                // download them and add them to the server arguments
                val pluginUrls = extension.pluginUrls.get()
                if (pluginUrls.isNotEmpty()) {
                    val downloadPlugins = tasks.register<FileDownloadTask>("downloadPlugins") {
                        urls(*pluginUrls.toTypedArray())
                    }

                    tasks.withType<RunServerTask> {
                        doFirst {
                            args(args.plus(downloadPlugins.get().outputFiles.map { "--add-plugin=${it.absolutePath}" }))
                        }

                        dependsOn(downloadPlugins)
                    }
                }

                // If there are any dependency plugins, add them to the runServer tasks
                val dependencyPlugins = extension.dependencyProjects.get()
                if (dependencyPlugins.isNotEmpty()) {
                    tasks.withType<RunServerTask> {
                        dependencyPlugins.forEach {
                            addProjectDependency(project(it))
                        }
                    }
                }

                // Add JARs from the pluginJars configuration to the runServer tasks
                tasks.withType<RunServerTask> {
                    doFirst {
                        args(
                            args.plus(
                                pluginJarsConfiguration.files.map { it.absolutePath }.distinct()
                                    .map { "--add-plugin=$it" }
                            )
                        )

                        logger.lifecycle("Running server with args: $args")
                    }
                }
            }
        }
    }
}
