package com.kamelia.hedera.rest.setting

import io.ktor.http.*

open class UserSettingsTestsExpectedResults(
    val getSettings: HttpStatusCode,
    val updateSetting: HttpStatusCode,
)

val ownerExpectedResults = UserSettingsTestsExpectedResults(
    getSettings = HttpStatusCode.OK,
    updateSetting = HttpStatusCode.OK,
)
val adminExpectedResults = UserSettingsTestsExpectedResults(
    getSettings = HttpStatusCode.OK,
    updateSetting = HttpStatusCode.OK,
)
val regularUserExpectedResults = UserSettingsTestsExpectedResults(
    getSettings = HttpStatusCode.OK,
    updateSetting = HttpStatusCode.OK,
)
val guestExpectedResults = UserSettingsTestsExpectedResults(
    getSettings = HttpStatusCode.Unauthorized,
    updateSetting = HttpStatusCode.Unauthorized,
)
