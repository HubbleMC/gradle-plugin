# gradle-plugin

Simplified utilities for developing with various server-side Minecraft toolchains.

![Paper](https://img.shields.io/gradle-plugin-portal/v/gg.hubblemc.paper?label=gg.hubblemc.paper&logo=gradle&style=flat-square)
![Linting](https://img.shields.io/gradle-plugin-portal/v/gg.hubblemc.paper?label=gg.hubblemc.paper&logo=gradle&style=flat-square)
![Velocity](https://img.shields.io/gradle-plugin-portal/v/gg.hubblemc.paper?label=gg.hubblemc.paper&logo=gradle&style=flat-square)
![Defaults](https://img.shields.io/gradle-plugin-portal/v/gg.hubblemc.paper?label=gg.hubblemc.paper&logo=gradle&style=flat-square)

## Usage

### Groovy

```groovy
plugins {
    id "gg.hubblemc.paper" version "VERSION"
    id "gg.hubblemc.linting" version "VERSION"
    id "gg.hubblemc.velocity" version "VERSION"
    id "gg.hubblemc.defaults" version "VERSION"
}
```

### Kotlin

```kotlin
plugins {
    id("gg.hubblemc.paper") version "VERSION"
    id("gg.hubblemc.linting") version "VERSION"
    id("gg.hubblemc.velocity") version "VERSION"
    id("gg.hubblemc.defaults") version "VERSION"
}
```

## TODO

- [ ] Automatically add dependency projects based on configurations
- [ ] Generate parts of the Bukkit plugin.yml automatically (dependencies, etc)
- [ ] Allow dependencies to be downloaded from Jenkins with a utility function
- [ ] Allow dependencies to be downloaded from GitHub (and processed) with a utility function

## Misc

This Gradle plugin is forced to use a *few* hacks to work.

<details>
<summary>Certificate</summary>

![Certificate](.github/assets/gradle-hack-cert.png)
</details>