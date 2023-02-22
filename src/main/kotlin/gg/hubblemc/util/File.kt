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

package gg.hubblemc.util

import java.io.File
import java.util.zip.ZipFile

/**
 * Unzips the file to the specified destination.
 *
 * @param destination The destination to unzip the file to
 */
fun File.unzipTo(destination: File) {
    // Create the destination directory if it doesn't exist
    destination.mkdirs()

    // Open the zip file
    val zip = ZipFile(this)

    // Iterate over the entries in the zip file
    zip.entries().asSequence().forEach { entry ->
        // Ignore __MACOSX & .DS_Store directories
        if (entry.name.startsWith("__MACOSX")) return@forEach
        if (entry.name.split("/").last() == ".DS_Store") return@forEach

        // Get the file for the entry
        val file = destination.resolve(entry.name)
        file.parentFile.mkdirs()

        // If the entry is a directory, create it
        if (entry.isDirectory) file.mkdirs()
        // Otherwise, write the file
        else file.writeBytes(zip.getInputStream(entry).readBytes())
    }
}