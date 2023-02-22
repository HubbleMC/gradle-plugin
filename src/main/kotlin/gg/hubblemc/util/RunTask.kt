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

package gg.hubblemc.util

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import gg.hubblemc.tasks.FileDownloadTask
import io.papermc.paperweight.tasks.RemapJar
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.RegularFile
import org.gradle.api.internal.provider.Providers
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskCollection
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.register
import xyz.jpenilla.runpaper.task.RunServer
import xyz.jpenilla.runtask.task.RunWithPlugins
import xyz.jpenilla.runvelocity.task.RunVelocity
import java.io.File

internal val RunServer.isMojangMapped: Boolean
    get() = name == "runMojangMappedServer"

/**
 * Get the build task for the project dependency and return it.
 *
 * @param project The project to get the build task for
 * @return The build task
 */
internal fun RunWithPlugins.getBuildTasks(project: Project): TaskCollection<out DefaultTask> =
    when (this) {
        is RunServer -> {
            if (isMojangMapped) project.tasks.lazyNamed<ShadowJar>("shadowJar")
            else project.tasks.lazyNamed<RemapJar>("reobfJar")
        }

        is RunVelocity -> project.tasks.lazyNamed<Jar>("jar")
        else -> throw IllegalStateException("Unknown run task type: ${this::class.java}")
    }

/**
 * Get the primary output of a task.
 *
 * @return The primary output
 */
internal fun Task.getPrimaryJar(): Provider<RegularFile> =
    when (this) {
        is Jar -> archiveFile
        is RemapJar -> outputJar
        else -> throw IllegalStateException("Unknown build task type: ${this::class.java}")
    }

/**
 * Set up the run task.
 *
 * @param project The project to set up the run task for
 * @param type The type of run task
 */
fun RunWithPlugins.setup(project: Project, type: String) {
    // Properties
    val downloadPlugins = (project.propertyOrEnv("hubble.$type.download-plugins") ?: "")
        .split(",").filter { it.isNotBlank() }

    val projectPlugins = (project.propertyOrEnv("hubble.$type.project-plugins") ?: "")
        .split(",").filter { it.isNotBlank() }

    // Create a new configuration to download plugin jars and
    // add it as a dependency to the "runServer" task
    val pluginJarsConfiguration: Configuration = project.configurations.getOrCreate("pluginJars") {
        isTransitive = false
    }

    // Add any plugin jars from the pluginJars configuration to the server arguments
    pluginJars.from(pluginJarsConfiguration)

    // If there are any plugins which need to be added to the server,
    // download them and add them to the server arguments
    if (downloadPlugins.isNotEmpty()) {
        // Register a task to download the files
        val downloadPluginsTask = project.tasks.register<FileDownloadTask>("downloadPlugins_$name") {
            urls(*downloadPlugins.toTypedArray())
        }

        // Add the plugin jars to the server arguments
        dependsOn(downloadPluginsTask)
        pluginJars.from(downloadPluginsTask.map { it.outputFiles })
    }

    // If there are any dependency plugins, add them to the runServer tasks
    projectPlugins.forEach {
        val depProj = project.project(it)
        val buildTasks = getBuildTasks(depProj)

        // Depend on the build task & add the plugin jars to the server arguments
        dependsOn(buildTasks)
        buildTasks.forEach { t -> pluginJars(t.getPrimaryJar()) }
        buildTasks.whenTaskAdded { pluginJars(getPrimaryJar()) }

        // Inherit dependency tasks from the project
        // (specifically to download external plugin jars)
        dependsOn(depProj.tasks.lazyNamed<RunWithPlugins>(name).map { i -> i.dependsOn })
    }

    // Clone the pluginJars from dependency projects
    val dependencyTasks = projectPlugins.map {
        project.project(it).tasks.lazyNamed<RunWithPlugins>(name).map { i -> i.pluginJars }
    }

    pluginJars.from(*dependencyTasks.toTypedArray())

    // Add a cleanup task
    val cleanTask = project.tasks.register<Delete>("clean${name.capitalized()}") {
        group = "hubble"
        delete(this@setup.runDirectory)
    }

    // Add a cleanup task for all run tasks
    val cleanAllTask = project.tasks.findByName("cleanRuns") ?: project.tasks.register("cleanRuns") {
        group = "hubble"
        description = "Cleans all run tasks"
    }.get()

    cleanAllTask.dependsOn(cleanTask)

    doFirst {
        // We allow servers to have a "gradle/server" directory
        // which we can copy to the run directory if it doesn't exist
        // to create some initial config
        val runDir = runDirectory.get().asFile
        if (!runDir.exists()) {
            // Copy from the root project, then dependencies, then the current project
            project.rootProject.configureRunDirectory(runDir)
            projectPlugins.forEach { project.project(it).configureRunDirectory(runDir) }
            project.configureRunDirectory(runDir)
        }

        // AWFUL HACK HERE!
        // As far as I'm concerned, there's no other way to get these
        // args before the pre-exec hook is called, so we have to
        // hijack the "displayName" property and use it as a hook

        val name = displayName.get()
        displayName.set(Providers.changing {
            // More importantly, use this as the hook after preExec
            logger.lifecycle("Running server with args: $args")

            // Return the original display
            name
        })
    }
}

/**
 * We allow servers to have a "gradle/server" directory
 * which we can copy to the run directory if it doesn't exist
 * to create some initial config
 *
 * @param dir The directory to copy to
 */
private fun Project.configureRunDirectory(dir: File) {
    val serverDir = projectDir.resolve("gradle/server")
    if (!serverDir.exists() || serverDir.listFiles()?.isNotEmpty() != true) return

    logger.lifecycle("[Run - $name] Copying server directory from ${serverDir.absolutePath} to ${dir.absolutePath}")
    serverDir.copyRecursively(dir, overwrite = true)

    // We also support a ".unzip" directory which will have all
    // the zip files in it unzipped into the run directory.
    // This is mainly designed for worlds, so we don't need to
    // commit a bunch of world files to the repo
    val unzipDir = dir.resolve(".unzip")
    if (unzipDir.exists()) {
        unzipDir.listFiles()
            ?.filter { it.extension == "zip" }
            ?.forEach {
                logger.lifecycle("[Run - $name] Unzipping ${it.name} to ${dir.absolutePath}")
                it.unzipTo(dir)
            }

        // Delete the unzip directory
        unzipDir.deleteRecursively()
    }
}