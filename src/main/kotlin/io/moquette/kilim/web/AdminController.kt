package io.moquette.kilim.web

import io.moquette.kilim.model.IUserRepository
import io.moquette.kilim.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin")
class AdminController @Autowired constructor(val users: IUserRepository) {

    @GetMapping("/console")
    fun console(model: Model): String {
        return "admin/console"
    }

    @GetMapping("/list")
    fun listUsers(@RequestParam("page") page: Int?,
                  model: Model): String {
        val pagination: PageRequest
        val size = 10
        if (page != null) {
            pagination = PageRequest.of(page - 1, size)
        } else {
            pagination = PageRequest.of(0, size)
        }

        val users: Page<User> = users.listAll(pagination)
        model.addAttribute("users", users)

        val pager = Pager(users.getTotalPages(), users.getNumber(), 5)

        model.addAttribute("selectedPageSize", size)
        model.addAttribute("pageSizes", size)
        model.addAttribute("pager", pager)

        return "admin/list"
    }
}
