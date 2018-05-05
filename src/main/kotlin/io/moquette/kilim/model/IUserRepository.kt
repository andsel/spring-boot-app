package io.moquette.kilim.model

import org.springframework.security.core.userdetails.UserDetails

interface IUserRepository {

    fun findByUserName(username: String): UserDetails?
}
