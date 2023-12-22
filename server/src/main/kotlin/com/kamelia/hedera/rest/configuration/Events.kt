package com.kamelia.hedera.rest.configuration

import com.kamelia.hedera.core.event

object ConfigurationEvents {

    val configurationUpdatedEvent = event<GlobalConfigurationRepresentationDTO>()

}
