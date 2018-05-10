package io.moquette.kilim.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class User(val login: String, val pwd: String, val role: String, val accountEnabled: Boolean,
           val accountLocked: Boolean) : UserDetails {

    override fun getUsername(): String {
        return login
    }

    override fun getPassword(): String {
        return pwd
    }

    override fun isEnabled(): Boolean {
        return accountEnabled
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return !accountLocked
    }

    fun isBlocked(): Boolean {
        return !(isAccountNonLocked || isEnabled)
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return Arrays.asList(SimpleGrantedAuthority(role))
    }
}