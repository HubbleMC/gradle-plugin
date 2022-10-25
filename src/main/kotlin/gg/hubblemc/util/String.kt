package gg.hubblemc.util

import java.security.MessageDigest

fun String.sha256(): String = hashString(this, "SHA-256")

@Suppress("SameParameterValue")
private fun hashString(input: String, algorithm: String): String =
    MessageDigest.getInstance(algorithm)
        .digest(input.toByteArray())
        .joinToString("") { "%02x".format(it) }

fun Boolean.yesNo(): String = if (this) "Yes" else "No"
