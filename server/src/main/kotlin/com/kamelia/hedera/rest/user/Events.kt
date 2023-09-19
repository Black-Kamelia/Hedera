@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.hedera.rest.user

import com.kamelia.hedera.core.event
import com.kamelia.hedera.util.UUIDSerializer
import com.kamelia.hedera.websocket.UserForcefullyLoggedOutPayload
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.*

object UserEvents {

    val userUpdatedEvent = event<UserRepresentationDTO>()

    val userForcefullyLoggedOutEvent = event<UserForcefullyLoggedOutPayload>()
}
