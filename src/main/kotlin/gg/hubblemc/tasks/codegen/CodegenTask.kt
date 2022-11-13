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
