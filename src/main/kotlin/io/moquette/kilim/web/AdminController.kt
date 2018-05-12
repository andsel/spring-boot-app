package io.moquette.kilim.web

import io.moquette.kilim.model.IUserRepository
import io.moquette.kilim.model.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
@Controller
@RequestMapping("/admin")
class AdminController @Autowired constructor(val users: IUserRepository) {

    private val LOG = LoggerFactory.getLogger(AdminController::class.java)

    @GetMapping("/console")
    fun console(model: Model): String {
        return "admin/console"
    }

    @GetMapping("/list")
    fun listUsers(@RequestParam("page") page: Int?,
                  @RequestParam("pageSize") size: Int?,
                  model: Model): String {
        val pageSize = if (size != null) { Math.max(10, size)} else {10}
        val pageNum = if (page != null) { Math.max(0, page - 1)} else {0}
        val pagination = PageRequest.of(pageNum, pageSize)

        val users: Page<User> = users.listAll(pagination)
        val pager = Pager(users.totalPages, users.number, 5)

        model.addAttribute("users", users)
        model.addAttribute("selectedPageSize", size)
        model.addAttribute("pageSizes", size)
        model.addAttribute("pager", pager)
        return "admin/list"
    }

    @GetMapping("/users")
    fun showNewUser(model: Model): String {
        model.addAttribute("newUser", User(null, "", "", "", false, false))
        return "admin/users/create"
    }

    @PostMapping("/users")
    fun submitNewUser(@Valid newUser: User,
                      result: BindingResult,
                      model: Model): String {
        LOG.info("submitNewUser invoked with $newUser")
        if (result.hasErrors()) {
            model.addAttribute("newUser", newUser)
            return "admin/users/create"
        }
//        try {
            users.save(newUser)
//        } catch (daex: DataAccessException) {
//            result.rejectValue("global", "global.error", "Something bad happened storing the data, retry later")
//            LOG.error("Error saving user", daex)
//            model.addAttribute("newUser", newUser)
//            return "admin/users/create"
//        }
        return "redirect:list"
    }

    @GetMapping("/user")
    fun showUser(@RequestParam id: Int, model: Model): String {
        val user = users.findByKey(id)
        if (user == null) {
            //TODO report a problem
            return "redirect:list"
        }
        model.addAttribute("editUser", user)
        return "admin/users/edit"
    }

    @PostMapping("/user")
    fun submitUserEdit(@RequestParam id: Int,
//                       result: BindingResult,
                       editedUser: User,
                       model: Model): String {
        LOG.info("submitUserEdit invoked with $editedUser")
//        if (result.hasErrors()) {
//            model.addAttribute("editUser", editedUser)
//            return "admin/users/edit"
//        }
        val user = users.findByKey(id)
        val updatedUser = User(id, user!!.username, user.password, editedUser.role, editedUser.accountEnabled,
                               editedUser.accountLocked)
        users.save(updatedUser)
        return "redirect:list"
    }

}

