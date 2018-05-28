package io.moquette.kilim.infrastructure

import io.moquette.kilim.model.IDeviceRepository
import io.moquette.spi.security.IAuthenticator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
internal class XodusAuthenticator @Autowired constructor(val devices: IDeviceRepository): IAuthenticator {

    private val LOG = LoggerFactory.getLogger(XodusAuthenticator::class.java)

    override fun checkValid(clientId: String?, username: String?, password: ByteArray?): Boolean {
        LOG.debug("Authenticating clientId: $clientId, username: $username")
        val device = devices.findByClientId(clientId!!)
        return device.isAuthenticated(username!!, password!!)
    }
}