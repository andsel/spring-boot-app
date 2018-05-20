package io.moquette.kilim.model

import java.util.*
import javax.validation.constraints.NotNull

class Device(@get:NotNull val clientId: String, @get:NotNull val password: String) {
    var messages: List<Message> = Collections.emptyList()
}

class Message(val body: String)