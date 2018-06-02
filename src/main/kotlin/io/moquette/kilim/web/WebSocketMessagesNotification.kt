package io.moquette.kilim.web

import org.slf4j.LoggerFactory
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

class WebSocketMessagesNotification : TextWebSocketHandler() {

    private val LOG = LoggerFactory.getLogger(WebSocketMessagesNotification::class.java)

    private val sessions = ArrayList<WebSocketSession>()

    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession) {
        LOG.info("Connection established")
        sessions += session
    }

    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        LOG.info("Connection closed. Status: $status")
        sessions -= session
    }

    fun sendMessage(login: String, content: String) {
        for (session in sessions) {
            if (login.equals(session.principal!!.name!!)) {
                session.sendMessage(TextMessage(content))
            }
        }
    }
}