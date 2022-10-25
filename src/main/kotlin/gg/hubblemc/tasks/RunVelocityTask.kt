package gg.hubblemc.tasks

import com.google.gson.Gson
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByName
import java.io.File
import java.net.URL

abstract class RunVelocity : JavaExec() {
    private val pluginJars: MutableList<File> = arrayListOf()

    @Internal
    val runDirectory: DirectoryProperty = project.objects.directoryProperty()
        .convention(project.layout.projectDirectory.dir("run/velocity"))

    private val downloadTask: FileDownloadTask = createDownloadTask()

    init {
        group = "hubble"
        configureDependencies()

        // TODO: Re-organize this
        @Suppress("LeakingThis")
        dependsOn(downloadTask)
    }

    override fun exec() {
        configure()
        beforeExec()
        super.exec()
    }

    private fun configure() {
        standardInput = System.`in`
        workingDir(runDirectory)
        classpath(downloadTask.outputFiles)
    }

    private fun beforeExec() {
        val workingDir = runDirectory.asFile.get()
        if (!workingDir.isDirectory) workingDir.mkdirs()

        val pluginsDir = workingDir.resolve("plugins")
        if (!pluginsDir.isDirectory) pluginsDir.mkdirs()

        pluginJars.forEach {
            it.copyTo(pluginsDir.resolve(it.name), overwrite = true)
        }
    }

    private fun configureDependencies() {
        val buildTask = project.tasks.getByName<Jar>("shadowJar")
        pluginJars.add(buildTask.archiveFile.get().asFile)
        dependsOn(buildTask)

        // Add any other projects that will be depended on here
    }

    @Suppress("unused")
    private fun addDependency(path: String) {
        val dependency = project.configurations.getByName("api").allDependencies
            .filterIsInstance<ProjectDependency>()
            .firstOrNull { it.dependencyProject.path == path }
            ?: return

        val buildTask = dependency.dependencyProject.tasks.getByName<Jar>("shadowJar")
        val pluginJar = buildTask.archiveFile.get().asFile

        dependsOn(buildTask)
        pluginJars.add(pluginJar)
    }

    private fun createDownloadTask(): FileDownloadTask {
        val project = VelocityDownloadsAPI.project()
        val versionName = project.versions.last()

        val version = VelocityDownloadsAPI.version(versionName)
        val buildNum = version.builds.last()

        val build = VelocityDownloadsAPI.build(versionName, buildNum)
        val download = VelocityDownloadsAPI.downloadURL(versionName, buildNum, build.downloads.application.name)

        return this.project.tasks.create("downloadVelocity", FileDownloadTask::class) {
            urls(download)
        }
    }
}

object VelocityDownloadsAPI {
    private const val base = "https://papermc.io/api/v2/"
    private val gson = Gson()

    private inline fun <reified T> fetch(url: String): T {
        val response = URL("$base$url").readText(Charsets.UTF_8)
        return gson.fromJson(response, T::class.java)
    }

    fun project(): ProjectResponse = fetch("projects/velocity")
    fun version(version: String): VersionResponse = fetch("projects/velocity/versions/$version")
    fun build(version: String, build: Int): BuildResponse = fetch("projects/velocity/versions/$version/builds/$build")
    fun downloadURL(version: String, build: Int, file: String): String =
        "${base}projects/velocity/versions/$version/builds/$build/downloads/$file"
}

data class ProjectResponse(
    val versions: List<String>
)

data class VersionResponse(
    val builds: List<Int>
)

data class BuildResponse(
    val downloads: Downloads
) {
    data class Downloads(
        val application: Download
    )

    data class Download(
        val name: String
    )
}
