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
import org.gradle.kotlin.dsl.extra

enum class ReleaseType(val id: String, val display: String) {
    SNAPSHOT("snapshots", "Snapshot"),
    RELEASE("releases", "Release"),
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

val Project.hubbleOwned: Boolean
    get() = group.toString().startsWith("gg.hubblemc") ||
        (project.hasProperty("hubble.owned") && project.property("hubble.owned").toString() == "true") ||
        (project.extra.has("hubble.owned") && project.extra["hubble.owned"]?.toString() == "true")
