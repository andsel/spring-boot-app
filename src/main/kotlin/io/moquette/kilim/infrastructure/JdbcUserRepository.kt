package io.moquette.kilim.infrastructure

import io.moquette.kilim.model.IUserRepository
import io.moquette.kilim.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.util.*

@Repository
internal class JdbcUserRepository @Autowired constructor(val jdbc: NamedParameterJdbcTemplate) : IUserRepository {

    private val MAPR = UserMapper()

    override fun findByUserName(username: String): UserDetails? {
        val params: SqlParameterSource = MapSqlParameterSource("username", username)
        return this.jdbc.queryForObject("SELECT * FROM users WHERE username=:username", params, MAPR)
    }

    @Transactional
    override fun listAll(pagination: Pageable): Page<User> {
        val params = MapSqlParameterSource()
                .addValue("offset", pagination.offset)
                .addValue("pageSize", pagination.pageSize)
        val data = jdbc.query("SELECT * FROM users OFFSET :offset LIMIT :pageSize", params, MAPR)
        val count: Long? = jdbc.queryForObject("SELECT count(*) FROM users", Collections.emptyMap<String, String>(), Long::class.java)
        return PageImpl(data, pagination, count!!)
    }
}

private class UserMapper: RowMapper<User> {
    override fun mapRow(rs: ResultSet, runNum: Int): User? {
        return User(rs.getString("username"), rs.getString("password"),
                    rs.getString("role"))
    }
}

