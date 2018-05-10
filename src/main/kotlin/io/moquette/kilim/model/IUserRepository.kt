package io.moquette.kilim.model

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetails

interface IUserRepository {

    fun findByUserName(username: String): UserDetails?

    fun listAll(pagination: Pageable): Page<User>
}
