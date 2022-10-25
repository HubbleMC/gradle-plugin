package gg.hubblemc.defaults

import java.util.Properties

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
version = project.property("version")?.toString().takeUnless { it == "unspecified" }
    ?: rootProject.version.toString().takeUnless { it == "unspecified" }
    ?: version
