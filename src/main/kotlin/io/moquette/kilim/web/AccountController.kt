package io.moquette.kilim.web

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

/**
 * Manages the views related to reset password, create an account etc
 * */
@Controller
class AccountController {

    @GetMapping(value = "/subscribe")
    fun subscribeForm(model: Model): String {
        model.addAttribute("subscription", NewSubscriptionDTO())
        return "subscribeView"
    }

    @PostMapping(value = "/subscribe")
    fun subscribeSubmit(model: Model,
                        redirectAttributes: RedirectAttributes,
                        @ModelAttribute data: NewSubscriptionDTO): String {
        redirectAttributes.addFlashAttribute("email", data.email)
        return "redirect:thanks"
    }

    @GetMapping("/thanks")
    fun showThanks(model: Model): String {
        return "thanksView"
    }
}


class NewSubscriptionDTO {
    var email: String = ""
    var password: String = ""
}