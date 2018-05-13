package io.moquette.kilim.web

import io.moquette.kilim.model.IDeviceRepository
import io.moquette.kilim.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/devices")
class DeviceController @Autowired constructor(val devices: IDeviceRepository) {

    @GetMapping("/list")
    fun listDevices(authentication: Authentication,
                    model: Model): String {
        val user: User = authentication.principal as User
        val devices = devices.listUserDevices(user)
        model.addAttribute("devices", devices)
        return "devices/list"
    }
}