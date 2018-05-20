package io.moquette.kilim.infrastructure

import io.moquette.kilim.model.Device
import io.moquette.kilim.model.IDeviceRepository
import io.moquette.kilim.model.Message
import io.moquette.kilim.model.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
internal class JdbcDeviceRepository @Autowired constructor(val jdbc: NamedParameterJdbcTemplate) : IDeviceRepository {

    private val LOG = LoggerFactory.getLogger(JdbcUserRepository::class.java)

    override fun listUserDevices(user: User): List<Device> {
        LOG.info("listUserDevices invoked, to be implemented")
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return listOf(Device("externalThermometerZone1", "pwd1"),
                      Device("externalThermometerZone2", "pwd1"),
                      Device("heatingValve", "pwd1"))
    }

    override fun findByClientId(clientId: String): Device {
        LOG.info("findByClientId {}, to be implemented", clientId)
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val device = Device(clientId, "pwd1")
        device.messages = listOf(Message("{temperature: 32, humidity: 60%}"))
        return device
    }

    override fun update(device: Device) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}