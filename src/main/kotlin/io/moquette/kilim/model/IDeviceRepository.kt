package io.moquette.kilim.model

import io.moquette.kilim.model.User

interface IDeviceRepository {
    fun listUserDevices(user: User): List<Device>
}