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
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.report.ReportMergeTask

// Apply plugins
apply<SpotlessPlugin>()
apply<DetektPlugin>()

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
        gson().indentWithSpaces(4).sortByKeys()
    }

    pluginManager.withPlugin("java") {
        java {
            licenseFile?.let { licenseHeaderFile(it) }
            removeUnusedImports()
            trimTrailingWhitespace()
            indentWithSpaces(4)
        }
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        kotlin {
            licenseFile?.let { licenseHeaderFile(it) }
        }
    }
}

// Create tasks
tasks {
    register("lint") {
        group = "verification"
        description = "Runs all linting tasks"

        dependsOn("spotlessCheck", "detekt")
    }

    register("lintFix") {
        group = "verification"
        description = "Runs all linting tasks and fixes any issues"

        dependsOn("spotlessApply", "detektFix")
    }
}

// Detekt
configure<DetektExtension> {
    buildUponDefaultConfig = true

    // Configure the detekt config
    val configFile = rootProject.file("gradle/detekt.yml")
    config.from(
        if (!configFile.exists()) {
            // Use a temp file
            val tempFile = rootProject.file("build/tmp/detekt.yml")
            tempFile.delete()

            // Write the default config
            tempFile.parentFile.mkdirs()
            LintingPlugin::class.java.getResourceAsStream("/detekt.yml")
                .use { input -> tempFile.outputStream().use { output -> input?.copyTo(output) } }

            // Set the config file
            files(tempFile)
        } else files(configFile)
    )
}

dependencies {
    "detektPlugins"("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")
    "detektPlugins"("io.gitlab.arturbosch.detekt:detekt-rules-libraries:1.22.0")
    "detektPlugins"("io.gitlab.arturbosch.detekt:detekt-rules-ruleauthors:1.22.0")
}

if (project == rootProject) {
    tasks.register<ReportMergeTask>("detektReportMergeSarif") {
        input.from(tasks.withType<Detekt>().map { it.sarifReportFile })
        output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif"))
    }
}

tasks {
    withType<Detekt> {
        reports {
            sarif.required.set(true)
            md.required.set(true)
        }

        jvmTarget = "1.8"
        basePath = rootProject.projectDir.absolutePath
        finalizedBy(":detektReportMergeSarif")
    }

    register<Detekt>("detektFix") {
        group = "verification"
        description = "Runs Detekt and fixes any issues"

        // We need to manually clone the detekt config for the default task
        val detekt = tasks.named<Detekt>("detekt").get()
        this.jdkHome.convention(detekt.jdkHome)
        this.jvmTarget = detekt.jvmTarget

        this.config.setFrom(detekt.config)
        this.debug = detekt.debug
        this.parallel = detekt.parallel
        this.disableDefaultRuleSets = detekt.disableDefaultRuleSets
        this.buildUponDefaultConfig = detekt.buildUponDefaultConfig
        this.ignoreFailures = detekt.ignoreFailures
        this.basePath = detekt.basePath
        this.allRules = detekt.allRules

        // And now for the one we actually care about :)
        this.autoCorrect = true
    }
}