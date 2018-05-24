package io.moquette.kilim.model

interface IDeviceRepository {

    fun listUserDevices(user: User): List<Device>

    fun findByClientId(clientId: String): Device

    fun update(device: Device)

    fun addMessage(device: String, message: Message)
}