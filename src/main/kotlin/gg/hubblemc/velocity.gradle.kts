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

package gg.hubblemc

import gg.hubblemc.util.getOrCreate
import gg.hubblemc.util.requiredPropertyOrEnv
import gg.hubblemc.util.setup
import xyz.jpenilla.runvelocity.RunVelocityPlugin
import xyz.jpenilla.runvelocity.task.RunVelocity

// Read the properties
val velocityVersion = requiredPropertyOrEnv("hubble.velocity.version")

// Create a new configuration to download plugin jars and
// add it as a dependency to the "runServer" task
val pluginJarsConfiguration: Configuration = configurations.getOrCreate("pluginJarsVelocity") {
    isTransitive = false
}

// Apply plugins
apply<RunVelocityPlugin>()

// Add the dependencies
dependencies {
    "implementation"("com.velocitypowered:velocity-api:$velocityVersion")
    "annotationProcessor"("com.velocitypowered:velocity-api:$velocityVersion")
}

// Configure the run task
tasks.withType<RunVelocity> {
    velocityVersion(velocityVersion)

    // Set up the run task
    setup(project, "velocity")
}
