package gg.hubblemc.util

import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven

fun Project.authenticatedMaven(url: String, name: String, block: MavenArtifactRepository.() -> Unit = {}) {
    repositories.maven(url) {
        this.block()
        propertyCredentials(this@authenticatedMaven, name)
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
