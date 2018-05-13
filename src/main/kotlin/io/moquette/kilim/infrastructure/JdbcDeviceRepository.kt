package io.moquette.kilim.infrastructure

import io.moquette.kilim.model.Device
import io.moquette.kilim.model.IDeviceRepository
import io.moquette.kilim.model.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.util.*

@Repository
internal class JdbcDeviceRepository @Autowired constructor(val jdbc: NamedParameterJdbcTemplate) : IDeviceRepository {

    private val LOG = LoggerFactory.getLogger(JdbcUserRepository::class.java)

    override fun listUserDevices(user: User): List<Device> {
        LOG.info("listUserDevices invoked, to be implemented")
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return Arrays.asList(Device("externalThermometerZone1"),
                             Device("externalThermometerZone2"),
                             Device("heatingValve"))
    }
}