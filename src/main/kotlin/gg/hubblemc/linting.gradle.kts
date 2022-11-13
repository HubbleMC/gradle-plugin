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

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.KtlintIdeaPlugin
import org.jlleitschuh.gradle.ktlint.KtlintPlugin

// Apply plugins
apply<SpotlessPlugin>()
apply<KtlintPlugin>()
apply<KtlintIdeaPlugin>()

// Configure spotless
configure<SpotlessExtension> {
    val licenseFile = rootProject.file("LICENSE.header").takeIf { it.exists() }

    encoding = Charsets.UTF_8

    format("misc") {
        target("*.gradle", "*.md", ".gitignore", ".gitattributes", ".editorconfig")

        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    json {
        target("src/**/*.json")
        gson().indentWithSpaces(4).sortByKeys().escapeHtml()
    }

    pluginManager.withPlugin("java") {
        java {
            licenseFile?.let { licenseHeaderFile(it) }
        }
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        kotlin {
            licenseFile?.let { licenseHeaderFile(it) }
        }
    }
}

// Configure ktlint
configure<KtlintExtension> {
    disabledRules.set(setOf("filename"))
}

// Create tasks
tasks {
    register("lint") {
        group = "verification"
        description = "Runs all linting tasks"

        dependsOn("spotlessCheck", "ktlintCheck")
    }

    register("lintFix") {
        group = "verification"
        description = "Runs all linting tasks and fixes any issues"

        dependsOn("spotlessApply", "ktlintFormat")
    }
}
