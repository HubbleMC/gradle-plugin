package gg.hubblemc.defaults.plugin

import gg.hubblemc.util.authenticatedMaven
import gg.hubblemc.util.releaseType

plugins {
    `maven-publish`
}

configure<PublishingExtension> {
    repositories {
        // Hubble - Authenticated
        // To use this, you must set either
        // Properties: hubble.username, hubble.password
        // Environment: HUBBLE_USERNAME, HUBBLE_PASSWORD
        val releaseType = project.releaseType
        authenticatedMaven("https://repo.koding.dev/hubble-${releaseType.id}/", "hubble", project) {
            name = "Hubble${releaseType.display}"
        }
    }
}
