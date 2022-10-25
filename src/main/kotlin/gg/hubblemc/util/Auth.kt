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
    credentials {
        username = project.propertyOrEnv("$prefix.username")
        password = project.propertyOrEnv("$prefix.password")
    }
}

/**
 * Get a property from the environment, or from the project properties.
 *
 * @param name The name of the property, formatted as `project.property.name`.
 */
private fun Project.propertyOrEnv(name: String): String? =
    if (hasProperty(name)) property(name) as String
    else System.getenv(name.toUpperCase().replace('.', '_'))
