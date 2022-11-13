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

package gg.hubblemc.tasks

import gg.hubblemc.util.sha256
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URL

@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class FileDownloadTask : DefaultTask() {

    @Input
    var urls: MutableList<String> = arrayListOf()

    @OutputFiles
    var outputFiles: MutableList<File> = arrayListOf()

    @Internal
    val destDir: DirectoryProperty =
        project.objects
            .directoryProperty()
            .convention(project.layout.buildDirectory.dir("downloadedFiles"))

    init {
        // Forcibly disable up-to-date checks
        outputs.upToDateWhen { false }
    }

    fun urls(vararg urls: String) {
        this.urls.addAll(urls)
    }

    @TaskAction
    fun run() {
        urls.forEach { url ->
            // Store the filename
            val fileName = "${url.sha256()}.${url.split(".").last()}"
            val dest = destDir.file(fileName).get().asFile

            // Add the file to the output files
            outputFiles.add(dest)

            // If the file already exists, skip it
            if (dest.exists()) {
                logger.info("Existing file $url, skipping")
                return@forEach
            }

            // Download the file
            logger.info("Downloading $url to $dest")
            dest.parentFile.mkdirs()
            dest.createNewFile()
            dest.writeBytes(URL(url).readBytes())
            logger.info("Downloaded $url to $dest")
        }
    }
}
