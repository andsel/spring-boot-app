package io.moquette.kilim.infrastructure

import io.moquette.kilim.model.IUserRepository
import io.moquette.kilim.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository

@Repository
internal class JdbcUserRepository @Autowired constructor(val jdbc: JdbcTemplate,
                                                         val passwordEncoder: PasswordEncoder) : IUserRepository {

    override fun findByUserName(username: String): UserDetails? {
        val encodedPwd = passwordEncoder.encode("pwd")
        if (username.equals("user")) {
            return User("user", /*"{noop}pwd"*/ encodedPwd, "ROLE_USER")
        } else if (username.equals("admin")) {
            return User("admin", /*"{noop}pwd"*/ encodedPwd, "ROLE_ADMIN")
        }
        // TODO throw exception if not found
        return null
    }
}
