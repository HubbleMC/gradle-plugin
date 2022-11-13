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

import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("unused")
abstract class PaperExtension @Inject constructor(project: Project) {
    val mcVersion: Property<String> = project.objects.property(String::class.java)
    val forkPackage: Property<String> = project.objects.property(String::class.java).convention("io.papermc.paper")
    val pluginUrls: ListProperty<String> = project.objects.listProperty(String::class.java).convention(emptyList())
    val dependencyProjects: ListProperty<String> =
        project.objects.listProperty(String::class.java).convention(emptyList())

    fun dependencyProjects(vararg projects: String) {
        dependencyProjects.set(projects.toList())
    }

    fun downloadPlugins(vararg urls: String) {
        pluginUrls.set(urls.toList())
    }
}
