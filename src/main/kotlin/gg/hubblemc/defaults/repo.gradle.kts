package gg.hubblemc.defaults

import gg.hubblemc.util.authenticatedMaven

repositories {
    mavenCentral()

    // Koding.dev - Public
    maven("https://repo.koding.dev/releases/")
    maven("https://repo.koding.dev/snapshots/")

    // Hubble - Authenticated
    // To use this, you must set either
    // Properties: hubble.username, hubble.password
    // Environment: HUBBLE_USERNAME, HUBBLE_PASSWORD
    authenticatedMaven("https://repo.koding.dev/hubble-releases/", "hubble") {
        name = "HubbleReleases"
    }

    authenticatedMaven("https://repo.koding.dev/hubble-snapshots/", "hubble") {
        name = "HubbleSnapshots"
    }
}
