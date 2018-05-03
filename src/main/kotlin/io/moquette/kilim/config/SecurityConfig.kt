package io.moquette.kilim.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    //    @Autowired
    //    UserDetailsService customUserService;

    @Bean
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
                .antMatchers(HttpMethod.POST, "/projects").hasRole("REQUESTER")//.authenticated()
                //                .antMatchers("/tasker/requests").hasRole("TASKER")
                .anyRequest().permitAll()

        //WARN only in Dev mode
        //TODO use environment to selectively execute this
        http.csrf().disable()
        http.headers().frameOptions().disable()
    }

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder?) {
        //just for dev test
        auth!!.inMemoryAuthentication()
                .withUser("user").password("{noop}pwd").roles("REQUESTER")// use NoOpPasswordEncoder or:
        //        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        //        manager.createUser(User.withDefaultPasswordEncoder().username("user").password("user").roles("USER").build());
        //        return manager;
        //                .and()
        //                .withUser("tasker").password("pwd").roles("TASKER")
        //auth.userDetailsService(this.customUserService);
    }
}
