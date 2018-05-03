package io.moquette.kilim.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    //    @Autowired
    //    UserDetailsService customUserService;

    /*@Bean
    fun successHandler(): AuthenticationSuccessHandler {
        return object : SimpleUrlAuthenticationSuccessHandler() {
            override fun determineTargetUrl(request: HttpServletRequest, response: HttpServletResponse?): String {
                return "/projects"

                //This auth success handler is a customization to use referrer only
                //for navbar login, to stay on the same page.
                //                boolean useReferToIncomingPage = !request.getRequestURI().endsWith("/login") ||
                //                        request.getParameterValues("_fromLoginPage") == null;
                //                setUseReferer(useReferToIncomingPage);
                //                return super.determineTargetUrl(request, response);
            }
        }
    }*/
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

                return if (isUser) {
                    "/projects"
                } else if (isAdmin) {
                    "/admin/console"
                } else {
                    throw IllegalStateException()
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
        auth!!.inMemoryAuthentication()
                .withUser("user").password("{noop}pwd").roles("USER").and()
                .withUser("admin").password("{noop}pwd").roles("ADMIN")
// use NoOpPasswordEncoder or:
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.withDefaultPasswordEncoder().username("user").password("user").roles("USER").build());
//        return manager;
//                .and()
//                .withUser("tasker").password("pwd").roles("TASKER")
//auth.userDetailsService(this.customUserService);
    }
}
