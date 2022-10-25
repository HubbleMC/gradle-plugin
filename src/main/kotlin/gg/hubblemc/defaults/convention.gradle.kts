package gg.hubblemc.defaults

import com.palantir.gradle.gitversion.GitVersionPlugin
import java.util.Properties

apply<GitVersionPlugin>()

// Inject properties from the root project's "local.properties" file
// into the project's properties.
val localProperties = File("local.properties")
    .takeIf { it.exists() }
    ?.let { file -> Properties().also { it.load(file.inputStream()) } }
    ?: Properties()

localProperties.forEach { key, value ->
    val keyString = key.toString()
    if (hasProperty(keyString)) setProperty(keyString, value)
    else extra.set(keyString, value)
}

// Allow the project version to be defined by a property
val gitVersion: groovy.lang.Closure<String> by extra
version = project.property("version")?.unlessUnspecified()
    ?: rootProject.version.toString().unlessUnspecified()
    ?: version.unlessUnspecified()
    ?: "Git-${gitVersion()}"

fun Any.unlessUnspecified(): String? = toString().takeUnless { it == "unspecified" }
