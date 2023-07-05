package com.pet.project.config;

import com.pet.project.filter.AuthTokenFilter;
import com.pet.project.utils.AuthEntryPointJwt;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final AuthTokenFilter authTokenFilter;
    private final AuthEntryPointJwt authEntryPointJwt;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authEntryPointJwt)
                .and()
                .authorizeRequests()
                .antMatchers("/", "/home").permitAll()
                .antMatchers("/api/auth/login").permitAll()
                .antMatchers("/api/auth/register").permitAll()
                .anyRequest().authenticated();

        httpSecurity.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
