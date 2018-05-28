package io.moquette.kilim.infrastructure

import io.moquette.BrokerConstants.ALLOW_ANONYMOUS_PROPERTY_NAME
import io.moquette.BrokerConstants.PORT_PROPERTY_NAME
import io.moquette.server.Server
import io.moquette.server.config.MemoryConfig
import io.moquette.spi.security.IAuthenticator
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class BrokerService @Autowired constructor(val xodusAuthenticator: IAuthenticator, val collector: MessagesCollector)
    : InitializingBean, DisposableBean {

    lateinit var mqttBroker: Server

    override fun afterPropertiesSet() {
//        val classpathLoader = ClasspathResourceLoader()
//        val classPathConfig = ResourceLoaderConfig(classpathLoader)

        val props = Properties()
//        props[PERSISTENT_STORE_PROPERTY_NAME] = IntegrationUtils.localMapDBPath()
        props[PORT_PROPERTY_NAME] = "1883"
        props[ALLOW_ANONYMOUS_PROPERTY_NAME] = "false"

        val brokerConfig = MemoryConfig(props)

        val mqttBroker = Server()
        val userHandlers = Collections.singletonList(collector)
        mqttBroker.startServer(brokerConfig, userHandlers, null, xodusAuthenticator, null)
    }

    override fun destroy() {
        mqttBroker.stopServer()
    }
}