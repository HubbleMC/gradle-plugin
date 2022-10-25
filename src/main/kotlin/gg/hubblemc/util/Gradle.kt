package gg.hubblemc.util

import org.gradle.api.Project

enum class ReleaseType(val display: String) {
    SNAPSHOT("Snapshot"),
    RELEASE("Release"),
}

val Project.releaseType: ReleaseType
    get() {
        // Check release type property
        val property = findProperty("hubble.release")?.toString()
        if (property != null) {
            return if (property == "true") ReleaseType.RELEASE else ReleaseType.SNAPSHOT
        }

        // Infer release type from version
        val version = version.toString()
        return if (version.endsWith("-SNAPSHOT")) ReleaseType.SNAPSHOT else ReleaseType.RELEASE
    }
