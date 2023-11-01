package com.kamelia.hedera.rest.setting

@Suppress("EnumEntryName")
enum class Locale {
    en,
    fr,
}

enum class DateStyle {
    SHORT,
    MEDIUM,
    LONG,
    FULL,
}

enum class TimeStyle {
    SHORT,
    MEDIUM,
    LONG,
    FULL,
}

enum class FilesSizeScale {
    BINARY,
    DECIMAL,
}

enum class UploadBehavior {
    INSTANT,
    MANUAL,
}

enum class FileDoubleClickAction {
    OPEN_NEW_TAB,
    OPEN_PREVIEW,
    COPY_LINK,
    COPY_CUSTOM_LINK,
    RENAME_FILE,
    DELETE_FILE,
    DOWNLOAD_FILE,
}
