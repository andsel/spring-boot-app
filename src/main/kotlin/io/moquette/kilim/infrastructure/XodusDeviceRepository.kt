package io.moquette.kilim.infrastructure

import io.moquette.kilim.config.ApplicationConfig
import io.moquette.kilim.model.Device
import io.moquette.kilim.model.IDeviceRepository
import io.moquette.kilim.model.Message
import io.moquette.kilim.model.User
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.PersistentEntityStore
import jetbrains.exodus.entitystore.PersistentEntityStores
import jetbrains.exodus.env.Environment
import jetbrains.exodus.env.Environments
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.util.Assert
import java.io.File
import java.nio.file.Files


@Repository
internal class XodusDeviceRepository @Autowired constructor(val config: ApplicationConfig,
                                                            val springEnv: org.springframework.core.env.Environment):
        IDeviceRepository, InitializingBean, DisposableBean {

    private val LOG = LoggerFactory.getLogger(XodusDeviceRepository::class.java)

    lateinit var env: Environment
    lateinit var entityStore: PersistentEntityStore

    override fun afterPropertiesSet() {
        LOG.debug("path from config: {}", config.xodusPath)

        if (springEnv.activeProfiles.contains("dev")) {
            val devTempDir = Files.createTempDirectory("dev_kilim_xodus")!!
            env = Environments.newInstance(devTempDir.toFile())
        } else {
            env = Environments.newInstance(config.xodusPath)
        }
        entityStore = PersistentEntityStores.newInstance(env, "devices")
    }

    override fun destroy() {
        entityStore.close()
        val storeDir = env.location
        env.close()

        // in dev, delete the temp directory
        if (springEnv.activeProfiles.contains("dev")) {
            val dir = File(storeDir)
            dir.delete()
        }
    }

    override fun findByClientId(clientId: String): Device {
        return entityStore.computeInReadonlyTransaction { txn ->
            val iterable = txn.find("Device", "clientId", clientId)
            Assert.isTrue(!iterable.isEmpty, "One device must be present with clientId: $clientId")
            val deviceXd: Entity = iterable.first!!
            val login: String = deviceXd.getProperty("login") as String
            val username: String = deviceXd.getProperty("username") as String
            val password: String = deviceXd.getProperty("password") as String

            val deviceReceiveMessages = ArrayList<Message>()

            for (messageXd in deviceXd.getLinks("messages")) {
                val message = Message(messageXd.getProperty("body") as String)
                deviceReceiveMessages.add(message)
            }
            val device = Device(login, clientId, username, password)
            device.messages = deviceReceiveMessages
            device
        }!!
    }

    override fun update(device: Device) {
        entityStore.executeInTransaction { txn ->
            val iterable = txn.find("Device", "clientId", device.clientId)
            if (iterable.isEmpty) {
                Assert.isTrue(device.userLogin.isNotEmpty(), "During insertion Device MUST have a login")
                // insert
                val deviceXd = txn.newEntity("Device")
                deviceXd.setProperty("login", device.userLogin)
                deviceXd.setProperty("clientId", device.clientId)
                deviceXd.setProperty("username", device.username)
                deviceXd.setProperty("password", device.password)
            } else {
                // update
                val deviceXd = iterable.first!!
                deviceXd.setProperty("username", device.username)
                deviceXd.setProperty("password", device.password)
            }
        }
    }

    override fun addMessage(clientId: String, message: Message) {
        entityStore.executeInTransaction { txn ->
            val iterable = txn.find("Device", "clientId", clientId)
            if (iterable.isEmpty) {
                return@executeInTransaction
            }
            val deviceXd = iterable.first!!

            val messageXd = txn.newEntity("Message")
            messageXd.setProperty("body", message.body)
            deviceXd.addLink("messages", messageXd)
            messageXd.setLink("device", deviceXd)
        }
    }

    override fun listUserDevices(user: User): List<Device> {
        // TODO we miss the filter by user!!
        return entityStore.computeInReadonlyTransaction { txn ->
            val iterable = txn.getAll("Device")
            val devices = ArrayList<Device>()
            for (deviceEntity: Entity in iterable) {
                val login: String = deviceEntity.getProperty("login") as String
                val clientId: String = deviceEntity.getProperty("clientId") as String
                val username: String = deviceEntity.getProperty("username") as String
                val password: String = deviceEntity.getProperty("password") as String
                devices.add(Device(login, clientId, username, password))
            }
            devices
        }
    }

}