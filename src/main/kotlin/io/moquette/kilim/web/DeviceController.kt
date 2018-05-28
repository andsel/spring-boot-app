package io.moquette.kilim.web

import io.moquette.kilim.model.Device
import io.moquette.kilim.model.IDeviceRepository
import io.moquette.kilim.model.Message
import io.moquette.kilim.model.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Controller
@RequestMapping("/devices")
class DeviceController @Autowired constructor(val devices: IDeviceRepository) {

    private val LOG = LoggerFactory.getLogger(DeviceController::class.java)

    @GetMapping("/list")
    fun listDevices(authentication: Authentication,
                    model: Model): String {
        val user: User = authentication.principal as User
        val userDevices = devices.listUserDevices(user)
        model.addAttribute("devices", userDevices)
        return "devices/list"
    }

    @GetMapping("/device")
    fun showDevice(@RequestParam clientId: String,
                   model: Model): String {
        val device: Device = devices.findByClientId(clientId)
        model.addAttribute("device", device)
        return "devices/show"
    }

    @GetMapping("/create")
    fun createDevice(model: Model): String {
        model.addAttribute("device", DeviceDTO("", "", "", "", ""))
        return "devices/create"
    }

    @PostMapping("/device")
    fun submitCreateDevice(@Valid updatedDevice: DeviceDTO,
                           result: BindingResult,
                           model: Model): String {
        LOG.info("submitCreateDevice new data: {}",  updatedDevice)
        model.addAttribute("device", updatedDevice)

        if (!updatedDevice.assertNewPasswordTypeCorrectly()) {
            result.rejectValue("newPassword", "Retyped passwords doesn't match")
            result.rejectValue("repeatedNewPassword", "Retyped passwords doesn't match")
            return "devices/edit"
        }

        // TODO check the oldPassword match
        // TODO encode the password stored on DB
        devices.update(Device(updatedDevice.clientId, updatedDevice.username, updatedDevice.repeatedNewPassword))
        devices.addMessage(updatedDevice.clientId, Message("first message received"))
        devices.addMessage(updatedDevice.clientId, Message("second message received"))
        devices.addMessage(updatedDevice.clientId, Message("third message received"))
        devices.addMessage(updatedDevice.clientId, Message("forth message received"))
        return "redirect:list"
    }

    @GetMapping("/edit/{clientId}")
    fun editDevice(@PathVariable clientId: String,
                   model: Model): String {
        val device: Device = devices.findByClientId(clientId)
        model.addAttribute("device", asDto(device))
        return "devices/edit"
    }

    @PostMapping("/device/{clientId}")
    fun submitUpdateDevice(@PathVariable clientId: String,
                           @Valid updatedDevice: DeviceDTO,
                           result: BindingResult,
                           model: Model): String {
        LOG.info("submitUpdateDevice update to clientId: {}, updated data: {}", clientId, updatedDevice)
        model.addAttribute("device", updatedDevice)
        if (result.hasErrors()) {
            return "devices/edit"
        }
        if (!updatedDevice.assertNewPasswordTypeCorrectly()) {
            result.rejectValue("newPassword", "Retyped passwords doesn't match")
            result.rejectValue("repeatedNewPassword", "Retyped passwords doesn't match")
            return "devices/edit"
        }

        // TODO check the oldPassword match
        // TODO encode the password stored on DB
        devices.update(Device(clientId, updatedDevice.username, updatedDevice.repeatedNewPassword))
        return "redirect:/devices/list"
    }

    private fun asDto(device: Device): DeviceDTO {
        return DeviceDTO(device.clientId, device.username, device.password, "", "")
    }
}

data class DeviceDTO(val clientId: String, val username: String, val oldPassword: String,
                     @get:NotNull @get:Size(min=2, max=8) val newPassword: String,
                     @get:NotNull @get:Size(min=2, max=8) val repeatedNewPassword: String) {

    fun assertNewPasswordTypeCorrectly(): Boolean {
        return newPassword == repeatedNewPassword
    }
}