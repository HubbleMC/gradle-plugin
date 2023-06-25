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

package gg.hubblemc.defaults

import com.palantir.gradle.gitversion.GitVersionPlugin
import gg.hubblemc.util.ReleaseType
import gg.hubblemc.util.releaseType
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.provideDelegate
import java.util.Properties

internal fun applyDefaultsConvention(project: Project) {
    project.apply<GitVersionPlugin>()

    // Inject properties from the root project's "local.properties" file
    // into the project's properties.
    val localProperties = project.rootProject.file("local.properties")
        .takeIf { it.exists() }
        ?.let { file -> Properties().also { it.load(file.inputStream()) } }
        ?: Properties()

    localProperties.forEach { key, value ->
        val keyString = key.toString()

        // because, gradle
        runCatching { project.setProperty(keyString, value) }
        project.extra.set(keyString, value)
    }

    // Allow the project version to be defined by a property
    val gitVersion: groovy.lang.Closure<String> by project.extra
    project.version = project.project.property("version")?.unlessDefault()
        ?: project.rootProject.version.unlessDefault()
                ?: project.version.unlessDefault()
                ?: runCatching { "Git-${gitVersion().removeSuffix(".dirty")}" }.getOrNull()
                ?: "unspecified"

    // Inherit project group from root project
    project.group = project.rootProject.group.toString().unlessDefault() ?: project.group

    // Suffix the version with "-SNAPSHOT" if the project is not a release
    if (project.project.releaseType == ReleaseType.SNAPSHOT && !project.version.toString().contains("-SNAPSHOT")) {
        project.version = "${project.version}-SNAPSHOT"
    }
}

fun Any.unlessDefault(): String? = toString().takeUnless { it == "unspecified" || it.isBlank() }
