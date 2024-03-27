package com.kamelia.hedera.util

import com.kamelia.hedera.core.Hasher
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Hasher tests")
class HasherTest {

    @DisplayName("Verifying correct password is ok")
    @Test
    fun correct() {
        val pwd = "password"
        val hashedPwd = Hasher.hash(pwd)
        assertTrue(Hasher.verify(pwd, hashedPwd).verified)
    }

    @DisplayName("Verifying incorrect password is not ok")
    @Test
    fun wrong() {
        val pwd = "password"
        val hashedPwd = Hasher.hash(pwd)
        assertFalse(Hasher.verify("wrong_password", hashedPwd).verified)
    }

    @DisplayName("Verifying very long correct password is ok")
    @Test
    fun veryLong() {
        val pwd = "a".repeat(1024 * 1024)
        val hashedPwd = Hasher.hash(pwd)
        assertTrue(Hasher.verify(pwd, hashedPwd).verified)
    }

    @DisplayName("Verifying very long almost identical password is not ok")
    @Test
    fun veryLongAlmostIdentical() {
        val password = "a".repeat(1024 * 1024) + "a"
        val almostPassword = "a".repeat(1024 * 1024) + "b"
        val hashedPwd = Hasher.hash(password)
        assertFalse(Hasher.verify(almostPassword, hashedPwd).verified)
    }
}
