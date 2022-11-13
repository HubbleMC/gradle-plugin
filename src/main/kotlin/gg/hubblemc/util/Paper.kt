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

import io.papermc.paperweight.tasks.RemapJar
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import xyz.jpenilla.runpaper.task.RunServerTask

fun RunServerTask.addProjectDependency(project: Project) {
    val mojangMapped = name.endsWith("runMojangMappedServer") // TODO: This is a hack, find a better way to do this

    // Get the build task
    val buildTaskProvider =
        if (mojangMapped) project.tasks.named<Jar>("shadowJar")
        else project.tasks.named<RemapJar>("reobfJar")

    // Get the output jar
    val pluginJar = when (val buildTask = buildTaskProvider.get()) {
        is Jar -> buildTask.archiveFile.get().asFile
        is RemapJar -> buildTask.outputJar.get().asFile
        else -> throw IllegalStateException("Unknown build task type: ${buildTask::class.java}")
    }

    // Depend on the build task
    dependsOn(buildTaskProvider)

    // Add the plugin
    // Ignore the thrown exception, happens randomly with the
    // cleanCaches task and nowhere else
    runCatching {
        pluginJars(*pluginJars.plus(pluginJar).toTypedArray())
    }
}
