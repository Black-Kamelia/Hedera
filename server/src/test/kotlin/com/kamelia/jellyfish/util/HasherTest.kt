package com.kamelia.jellyfish.util

import com.kamelia.jellyfish.core.Hasher
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class HasherTest {

    @Test
    fun `verifying correct password is ok`() {
        val pwd = "password"
        val hashedPwd = Hasher.hash(pwd)
        assertTrue(Hasher.verify(pwd, hashedPwd).verified)
    }

    @Test
    fun `verifying wrong password is not ok`() {
        val pwd = "password"
        val hashedPwd = Hasher.hash(pwd)
        assertFalse(Hasher.verify("wrong_password", hashedPwd).verified)
    }

    @Test
    fun `verifying very long correct password is ok`() {
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
