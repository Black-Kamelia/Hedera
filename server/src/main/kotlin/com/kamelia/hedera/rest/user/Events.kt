@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.hedera.rest.user

import com.kamelia.hedera.core.event
import com.kamelia.hedera.util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.*

object UserEvents {

    val userUpdatedEvent = event<UserRepresentationDTO>()

    val userForcefullyLoggedOutEvent = event<UserForcefullyLoggedOutDTO>()
}

@Serializable
data class UserForcefullyLoggedOutDTO(
    val userId: UUID,
    val reason: String,
)
