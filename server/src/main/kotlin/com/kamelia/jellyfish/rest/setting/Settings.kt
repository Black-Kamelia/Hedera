package com.kamelia.jellyfish.rest.setting

@Suppress("EnumEntryName")
enum class Locale {
    en_US,
    fr_FR,
}

@JvmInline
value class Quota(val value: Long) {
    companion object {
        val UNLIMITED = Quota(-2)
        val DEFAULT = Quota(-1)
    }
}
