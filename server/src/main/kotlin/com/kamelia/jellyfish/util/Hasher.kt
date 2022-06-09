package com.kamelia.jellyfish.util

import at.favre.lib.crypto.bcrypt.BCrypt
import at.favre.lib.crypto.bcrypt.LongPasswordStrategies
import java.security.SecureRandom

object Hasher {
    private val version = BCrypt.Version.VERSION_2B
    private val strategy = LongPasswordStrategies.truncate(version)
    private val hasher = BCrypt.with(version, SecureRandom(), strategy)
    private val verifier = BCrypt.verifyer(version, strategy)

    private const val strength = 6

    fun hash(password: String, strength: Int = this.strength): String =
        hasher.hashToString(strength, password.toCharArray())

    fun verify(password: String, hash: String): BCrypt.Result =
        verifier.verify(password.toCharArray(), hash.toCharArray())
}