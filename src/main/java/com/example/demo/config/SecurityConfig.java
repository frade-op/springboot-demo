package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/hello").permitAll()
                .requestMatchers("/csrf").permitAll()
                .requestMatchers("/signup").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/users", "/users/**").hasRole("ADMIN")
                .requestMatchers("/product", "/product/**", "/products").authenticated()
                .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults())
            .formLogin(formLogin -> formLogin.disable());

        return http.build();
    }
}
