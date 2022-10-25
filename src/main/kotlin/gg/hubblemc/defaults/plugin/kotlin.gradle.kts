package gg.hubblemc.defaults.plugin

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

tasks {
    named<KotlinCompile>("compileKotlin") {
        kotlinOptions.jvmTarget = "17"
    }

    named<KotlinCompile>("compileTestKotlin") {
        kotlinOptions.jvmTarget = "17"
    }
}
