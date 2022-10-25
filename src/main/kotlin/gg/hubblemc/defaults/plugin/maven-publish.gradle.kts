package gg.hubblemc.defaults.plugin

import gg.hubblemc.util.authenticatedMaven

plugins {
    `maven-publish`
}

configure<PublishingExtension> {
    repositories {
        // Hubble - Authenticated
        // To use this, you must set either
        // Properties: hubble.username, hubble.password
        // Environment: HUBBLE_USERNAME, HUBBLE_PASSWORD
        val snapshot = project.extra["hubble.release"] != true
        val repoType = if (snapshot) "snapshots" else "releases"
        authenticatedMaven("https://repo.koding.dev/hubble-$repoType/", "hubble") {
            name = "Hubble${repoType[0].toUpperCase()}${repoType.substring(1)}"
        }
    }
}
