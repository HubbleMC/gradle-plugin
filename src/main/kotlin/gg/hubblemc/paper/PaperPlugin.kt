package gg.hubblemc.paper

import gg.hubblemc.tasks.FileDownloadTask
import gg.hubblemc.util.addPluginDependency
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

            afterEvaluate {
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
                            addPluginDependency(project(it))
                        }
                    }
                }
            }
        }
    }
}
