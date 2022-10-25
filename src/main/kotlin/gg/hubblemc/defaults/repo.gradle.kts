package gg.hubblemc.defaults

import gg.hubblemc.util.authenticatedMaven

repositories {
    mavenCentral()

    // Koding.dev - Public
    maven("https://repo.koding.dev/repository/releases/")
    maven("https://repo.koding.dev/repository/snapshots/")

    // Hubble - Authenticated
    // To use this, you must set either
    // Properties: hubble.username, hubble.password
    // Environment: HUBBLE_USERNAME, HUBBLE_PASSWORD
    authenticatedMaven("https://repo.koding.dev/repository/hubble-releases/", "hubble")
    authenticatedMaven("https://repo.koding.dev/repository/hubble-snapshots/", "hubble")
}
