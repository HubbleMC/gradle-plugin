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
