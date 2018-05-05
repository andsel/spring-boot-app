package io.moquette.kilim.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    @Qualifier("customUserService")
    lateinit var userDetailsService: UserDetailsService

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    fun successHandler(): AuthenticationSuccessHandler {
        return object : SimpleUrlAuthenticationSuccessHandler() {
            protected fun determineTargetUrl(authentication: Authentication): String {
                var isUser = false
                var isAdmin = false
                val authorities = authentication.authorities
                for (grantedAuthority in authorities) {
                    if (grantedAuthority.authority == "ROLE_USER") {
                        isUser = true
                        break
                    } else if (grantedAuthority.authority == "ROLE_ADMIN") {
                        isAdmin = true
                        break
                    }
                }

                return when {
                    isUser -> "/projects"
                    isAdmin -> "/admin/console"
                    else -> throw IllegalStateException()
                }
            }

            override fun handle(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
                val targetUrl = determineTargetUrl(authentication!!)
                this.redirectStrategy.sendRedirect(request, response, targetUrl)
            }
        }
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.formLogin()
                .loginPage("/login")
                .successHandler(successHandler())
            .and()
                .logout()
                .logoutSuccessUrl("/index.html")
            .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/projects").hasRole("USER")//.authenticated()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()

        //WARN only in Dev mode
        //TODO use environment to selectively execute this
        http.csrf().disable()
        http.headers().frameOptions().disable()
    }

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder?) {
        //just for dev test, here the password are encoded with NoOpPasswordEncoder
//        auth!!.inMemoryAuthentication()
//                .withUser("user").password("{noop}pwd").roles("USER").and()
//                .withUser("admin").password("{noop}pwd").roles("ADMIN")

//        auth!!.userDetailsService<UserDetailsService>(UserDetailsService {
//            username -> customUserService.findByUserName(username)
//        })
        auth!!.userDetailsService<UserDetailsService>(userDetailsService)
    }
}
