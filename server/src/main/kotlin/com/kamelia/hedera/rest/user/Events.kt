@file:UseSerializers(UUIDSerializer::class)

package com.kamelia.hedera.rest.user

import com.kamelia.hedera.core.event
import com.kamelia.hedera.rest.configuration.GlobalConfigurationRepresentationDTO
import com.kamelia.hedera.util.UUIDSerializer
import kotlinx.serialization.UseSerializers

object UserEvents {

    val userUpdatedEvent = event<UserRepresentationDTO>()

    val userForcefullyLoggedOutEvent = event<UserForcefullyLoggedOutDTO>()
}
