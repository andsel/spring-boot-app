package io.moquette.kilim.infrastructure

import io.moquette.kilim.model.Device
import io.moquette.kilim.model.IDeviceRepository
import io.moquette.kilim.model.User
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.PersistentEntityStore
import jetbrains.exodus.entitystore.PersistentEntityStores
import jetbrains.exodus.env.Environment
import jetbrains.exodus.env.Environments
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Repository
import org.springframework.util.Assert


@Repository
internal class XodusDeviceRepository : IDeviceRepository, InitializingBean, DisposableBean {

    lateinit var env: Environment
    lateinit var entityStore: PersistentEntityStore

    override fun afterPropertiesSet() {
        env = Environments.newInstance("/tmp/kilimData")
        entityStore = PersistentEntityStores.newInstance(env, "devices")
    }

    override fun destroy() {
        entityStore.close()
        env.close()
    }

    override fun findByClientId(clientId: String): Device {
        return entityStore.computeInReadonlyTransaction { txn ->
            val iterable = txn.find("Device", "clientId", clientId)
            Assert.isTrue(!iterable.isEmpty, "One device must be present with clientId: $clientId")
            val deviceXd: Entity = iterable.first!!
            val password: String = deviceXd.getProperty("password") as String
            Device(clientId, password)
        }!!
    }

    override fun update(device: Device) {
        entityStore.executeInTransaction { txn ->
            val iterable = txn.find("Device", "clientId", device.clientId)
            if (iterable.isEmpty) {
                // insert
                val deviceXd = txn.newEntity("Device")
                deviceXd.setProperty("clientId", device.clientId)
                deviceXd.setProperty("password", device.password)
            } else {
                // update
                val deviceXd = iterable.first
                deviceXd!!.setProperty("password", device.password)
            }
        }
    }

    override fun listUserDevices(user: User): List<Device> {
        // TODO we miss the filter by user!!
        return entityStore.computeInReadonlyTransaction { txn ->
            val iterable = txn.getAll("Device")
            val devices = ArrayList<Device>()
            for (deviceEntity: Entity in iterable) {
                val clientId: String = deviceEntity.getProperty("password") as String
                val password: String = deviceEntity.getProperty("password") as String
                devices.add(Device(clientId, password))
            }

            devices
        }
    }

}