package io.moquette.kilim.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("xodus")
class ApplicationConfig {
    lateinit var path: String
}