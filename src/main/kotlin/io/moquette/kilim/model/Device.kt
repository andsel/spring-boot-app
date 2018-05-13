package io.moquette.kilim.model

import javax.validation.constraints.NotNull

class Device(@get:NotNull val clientId: String)