package io.moquette.kilim.infrastructure

import io.moquette.kilim.model.IUserRepository
import io.moquette.kilim.model.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.util.*
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder



@Repository
internal class JdbcUserRepository @Autowired constructor(val jdbc: NamedParameterJdbcTemplate,
                                                         val pwdEncoder: PasswordEncoder) : IUserRepository {

    private val LOG = LoggerFactory.getLogger(JdbcUserRepository::class.java)

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

    override fun save(user: User) {
        val params = MapSqlParameterSource()
                .addValue("username", user.username)
                .addValue("encodedPwd", pwdEncoder.encode(user.pwd))
                .addValue("role", user.role)
                .addValue("enabled", user.accountEnabled)
                .addValue("locked", user.accountLocked)
        val keyHolder = GeneratedKeyHolder()
        jdbc.update("INSERT INTO users (id, username, password, role, enabled, locked) VALUES " +
                    "(null, :username, :encodedPwd, :role, :enabled, :locked)", params, keyHolder)

        LOG.debug("SAved user with key {}", keyHolder.key)
    }
}

private class UserMapper: RowMapper<User> {
    override fun mapRow(rs: ResultSet, runNum: Int): User? {
        val user = User(rs.getString("username"), rs.getString("password"),
                rs.getString("role"), rs.getBoolean("enabled"),
                rs.getBoolean("locked"))
        return user
    }
}

