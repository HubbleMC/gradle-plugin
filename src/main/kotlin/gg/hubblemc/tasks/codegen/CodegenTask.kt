package gg.hubblemc.tasks.codegen

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

@Suppress("unused")
abstract class CodegenTask : DefaultTask() {
    init {
        group = "hubble"
    }

    @get:Input
    abstract var generators: List<Codegen>

    @get:InputDirectory
    abstract var folder: File

    @TaskAction
    fun run() {
        generators.forEach {
            project.logger.lifecycle("generating: ${it.javaClass.simpleName}")
            it.generate(project, folder)
        }
    }
}
