package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import ru.kata.spring.boot_security.demo.service.RoleInUserDetails;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final SuccessUserHandler successUserHandler;
    private final RoleInUserDetails roleInUserDetails;

    @Autowired
    public WebSecurityConfig(SuccessUserHandler successUserHandler, RoleInUserDetails roleInUserDetails) {
        this.successUserHandler = successUserHandler;
        this.roleInUserDetails = roleInUserDetails;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(roleInUserDetails).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/new/**").permitAll()
                .antMatchers("/a/user", "/new/**").hasAnyRole("USER")
                .antMatchers("/**","/edit/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler(successUserHandler)
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .permitAll();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(roleInUserDetails);
        return authProvider;
    }
}