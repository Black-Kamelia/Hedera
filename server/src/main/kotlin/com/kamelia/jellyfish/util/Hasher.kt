package com.kamelia.jellyfish.util

import io.ktor.util.getDigestFunction

object Hasher {
    private val salt = System.getenv("JELLYFISH_SALT") ?: ""
    private val sha512 = getDigestFunction("SHA-512") { salt + it }

    fun hash(password: String): String = sha512(password)
        .fold("") { str, it -> str + "%02x".format(it) }
}