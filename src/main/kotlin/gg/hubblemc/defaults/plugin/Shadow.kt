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

package gg.hubblemc.defaults.plugin

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.kotlin.dsl.named

internal fun applyDefaultsPluginShadow(project: Project) {
    val shadow = project.configurations.named<Configuration>("shadow").get()
    val shadowJar = project.tasks.named<ShadowJar>("shadowJar").get()

    // Ensure that the shadow configuration is used for the API
    project.configurations.apply {
        findByName("api")?.apply {
            isCanBeResolved = true
            extendsFrom(shadow)
        }
    }

    // Configure shadow
    shadowJar.apply {
        configurations = listOf(shadow)

        // Exclude some Kotlin metadata as it causes a rare
        // IDE bug where some classes cannot be resolved
        exclude("**/*.kotlin_metadata", "**/*.kotlin_module")
    }

    // Run on build task
    project.tasks.named("build") { dependsOn("shadowJar") }
}
