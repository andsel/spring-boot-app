package io.moquette.kilim.infrastructure

import io.moquette.kilim.model.IAdminNotificator
import io.moquette.kilim.model.User
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class TelegramBotService : IAdminNotificator {

    private val LOG = LoggerFactory.getLogger(TelegramBotService::class.java)

    override fun newUserRegistered(user: User) {
        val urlVariables = hashMapOf("botToken" to "391251051:AAE6NhJTpEwOVWxx1wI1baUbZbIVPLoGsCY",
                "groupId" to "-253056505",
                "message" to "New registration request from ${user.login}")
        val restTemplate = RestTemplate()
        val response = restTemplate.getForObject(
                "https://api.telegram.org/bot{botToken}/sendMessage?chat_id={groupId}&text={message}",
                TelegramResponse::class.java,
                urlVariables)!!
        if (response.ok) {
            LOG.debug("Successfully notified")
        } else {
            LOG.warn("Some bad messages happened")
        }
    }
}

data class TelegramResponse(val ok: Boolean, val result: Map<String, /*Map<String, String>*/Any>)