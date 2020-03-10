package ro.secur.auth.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import ro.secur.auth.security.filter.AuthenticationFilter;
import ro.secur.auth.security.filter.JwtAuthenticationEntryPoint;
import ro.secur.auth.security.filter.CrossOriginFilter;
import ro.secur.auth.security.filter.TokenVerifierFilter;
import ro.secur.auth.service.UserService;

import static ro.secur.auth.util.Api.FORGOT_PASSWORD_URL;
import static ro.secur.auth.util.Api.RESET_PASSWORD_URL;
import static ro.secur.auth.util.Api.USERS_URL;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    private final JwtConfiguration jwtConfiguration;

    private final PasswordConfiguration passwordConfiguration;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfiguration(UserService userService, JwtConfiguration jwtConfiguration, PasswordConfiguration passwordConfiguration, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.userService = userService;
        this.jwtConfiguration = jwtConfiguration;
        this.passwordConfiguration = passwordConfiguration;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(RESET_PASSWORD_URL, FORGOT_PASSWORD_URL, USERS_URL).permitAll()
                .and()
                .addFilterBefore(new CrossOriginFilter(), AuthenticationFilter.class)
                .addFilter(new AuthenticationFilter(authenticationManager(), jwtConfiguration))
                .addFilterAfter(new TokenVerifierFilter(jwtConfiguration), AuthenticationFilter.class)
                .authorizeRequests()
                .anyRequest()
                .authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordConfiguration.getEncoder());
        authProvider.setUserDetailsService(userService);
        return authProvider;
    }


    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
