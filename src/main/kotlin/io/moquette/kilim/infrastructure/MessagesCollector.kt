package io.moquette.kilim.infrastructure

import io.moquette.interception.messages.InterceptPublishMessage
import io.moquette.interception.AbstractInterceptHandler
import io.moquette.kilim.model.IDeviceRepository
import io.moquette.kilim.model.Message
import io.moquette.kilim.web.WebSocketMessagesNotification
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MessagesCollector @Autowired constructor(val devices: IDeviceRepository,
                                               val webSocketMessagesNotification: WebSocketMessagesNotification) :
        AbstractInterceptHandler() {

    private val log = LoggerFactory.getLogger(MessagesCollector::class.java)

    override fun getID(): String {
        return "Kilim collector"
    }

    override fun getInterceptedMessageTypes(): Array<Class<*>> {
        return arrayOf(InterceptPublishMessage::class.java)
    }

    override fun onPublish(msg: InterceptPublishMessage?) {
        val message = msg!!
        log.info("publish received msg from {}", message.clientID)
        val content = readContentAsString(message)
        log.info("red content {}", content)

        // store the message for the device
        val device = devices.findByClientId(message.clientID)
        device.receivedMessage(content)
        devices.addMessage(message.clientID, Message(content))

        // notify to websocket connected clients
        webSocketMessagesNotification.sendMessage(device.userLogin, content)
    }

    private fun readContentAsString(message: InterceptPublishMessage): String {
        val rawContent = ByteArray(message.payload.readableBytes())
        val startIdx = message.payload.readerIndex()
        message.payload.getBytes(startIdx, rawContent)
        return String(rawContent)
    }
}