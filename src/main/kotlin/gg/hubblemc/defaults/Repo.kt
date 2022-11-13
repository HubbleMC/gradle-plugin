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

import gg.hubblemc.util.authenticatedMaven
import org.gradle.api.Project
import org.gradle.kotlin.dsl.maven

internal fun applyDefaultsRepo(project: Project) {
    project.afterEvaluate {
        repositories.apply {
            mavenCentral()

            // Koding.dev - Public
            maven("https://repo.koding.dev/releases/")
            maven("https://repo.koding.dev/snapshots/")

            // Hubble - Authenticated
            // To use this, you must set either
            // Properties: hubble.username, hubble.password
            // Environment: HUBBLE_USERNAME, HUBBLE_PASSWORD
            authenticatedMaven("https://repo.koding.dev/hubble-releases/", "hubble", project) {
                name = "HubbleReleases"
            }

            authenticatedMaven("https://repo.koding.dev/hubble-snapshots/", "hubble", project) {
                name = "HubbleSnapshots"
            }
        }
    }
}
