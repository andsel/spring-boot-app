package io.moquette.kilim.config

import io.moquette.kilim.web.WebSocketMessagesNotification
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(webSocketHandler(), "/websocket/messages")
    }

    @Bean
    fun webSocketHandler(): WebSocketMessagesNotification {
        return WebSocketMessagesNotification()
    }
}