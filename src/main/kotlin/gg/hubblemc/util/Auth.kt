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

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven

fun RepositoryHandler.authenticatedMaven(url: String, name: String, project: Project, block: MavenArtifactRepository.() -> Unit = {}) {
    maven(url) {
        this.block()
        propertyCredentials(project, name)
    }
}

fun MavenArtifactRepository.propertyCredentials(project: Project, prefix: String) {
    val username = project.propertyOrEnv("$prefix.username")
    val password = project.propertyOrEnv("$prefix.password")

    if (username != null && password != null) {
        credentials {
            this.username = username
            this.password = password
        }
    } else {
        project.logger.warn("No credentials found for repository $name, set $prefix.username and $prefix.password to use authentication.")
    }
}

/**
 * Get a property from the environment, or from the project properties.
 *
 * @param name The name of the property, formatted as `project.property.name`.
 */
internal fun Project.propertyOrEnv(name: String): String? =
    if (hasProperty(name)) property(name) as String
    else System.getenv(name.uppercase().replace('.', '_'))

/**
 * Get a required property from the environment, or from the project properties.
 *
 * @param name The name of the property, formatted as `project.property.name`.
 * @throws IllegalStateException If the property is not found.
 */
internal fun Project.requiredPropertyOrEnv(name: String): String =
    propertyOrEnv(name) ?: throw IllegalStateException("Required property $name not found. Please set it in the environment or in the gradle.properties file.")