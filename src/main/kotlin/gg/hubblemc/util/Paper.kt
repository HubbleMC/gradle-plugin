package gg.hubblemc.util

import io.papermc.paperweight.tasks.RemapJar
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import xyz.jpenilla.runpaper.task.RunServerTask

fun RunServerTask.addPluginDependency(project: Project) {
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
    pluginJars(*pluginJars.plus(pluginJar).toTypedArray())
}
