package io.moquette.kilim.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("kilim")
class ApplicationConfig {
    lateinit var xodusPath: String
    lateinit var telegramToken: String
    lateinit var telegramGroupId: String
}