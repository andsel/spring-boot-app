package io.moquette.kilim.model

import java.util.*
import javax.validation.constraints.NotNull

class Device(@get:NotNull val clientId: String,
             @get:NotNull val username: String,
             @get:NotNull val password: String) {

    fun isAuthenticated(username: String, password: ByteArray): Boolean {
        return this.username == username && this.password == String(password)
    }

    fun receivedMessage(content: String) {
        messages += Message(content)
    }

    var messages: List<Message> = Collections.emptyList()
}

class Message(val body: String)