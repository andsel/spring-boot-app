package io.moquette.kilim.infrastructure

import io.moquette.kilim.model.IUserRepository
import io.moquette.kilim.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Repository

@Repository
internal class JdbcUserRepository @Autowired constructor(val jdbc: NamedParameterJdbcTemplate) : IUserRepository {

    override fun findByUserName(username: String): UserDetails? {
        val params: SqlParameterSource = MapSqlParameterSource("username", username)
        return this.jdbc.queryForObject("SELECT * FROM users WHERE username=:username", params) { rs, rowNum ->
            User(rs.getString("username"),
                    rs.getString("password"), rs.getString("role"))
        }
    }
}
