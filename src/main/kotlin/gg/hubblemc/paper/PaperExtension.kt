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
