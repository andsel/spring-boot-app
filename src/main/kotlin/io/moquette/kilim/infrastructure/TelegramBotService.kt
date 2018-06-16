package io.moquette.kilim.infrastructure

import io.moquette.kilim.config.ApplicationConfig
import io.moquette.kilim.model.IAdminNotificator
import io.moquette.kilim.model.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient

@Component
class TelegramBotService @Autowired constructor(val config: ApplicationConfig): IAdminNotificator {

    private val LOG = LoggerFactory.getLogger(TelegramBotService::class.java)

    override fun newUserRegistered(user: User) {
        val urlVariables = hashMapOf("botToken" to config.telegramToken,
                "groupId" to config.telegramGroupId,
                "message" to "New registration request from ${user.login}")

        WebClient.create("https://api.telegram.org")
                .get()
                .uri("/bot{botToken}/sendMessage?chat_id={groupId}&text={message}", urlVariables)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TelegramResponse::class.java)
                .doOnError { _ -> LOG.warn("Request to telegram gone bad")}
                .subscribe { response ->
                    if (response.ok) {
                        LOG.debug("Successfully notified")
                    } else {
                        LOG.warn("Some bad messages happened")
                    }
                }
    }
}

data class TelegramResponse(val ok: Boolean, val result: Map<String, /*Map<String, String>*/Any>)