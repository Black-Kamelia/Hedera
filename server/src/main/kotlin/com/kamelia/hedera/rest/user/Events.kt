package com.kamelia.hedera.rest.user

import com.kamelia.hedera.core.event

object UserEvents {

    val userUpdatedEvent = event<UserRepresentationDTO>()
}