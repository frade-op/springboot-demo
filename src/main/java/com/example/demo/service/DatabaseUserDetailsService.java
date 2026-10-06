package com.example.demo.service;

import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRolesRepository;
import com.example.demo.model.User;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRolesRepository userRolesRepository;

    public DatabaseUserDetailsService(UserRepository userRepository, UserRolesRepository userRolesRepository) {
        this.userRepository = userRepository;
        this.userRolesRepository = userRolesRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User account = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not found with email: " + username));
        
        List<SimpleGrantedAuthority> authorities =
            userRolesRepository.findAllByUser(account)
                .stream()
                .map(userRole -> new SimpleGrantedAuthority(
                    "ROLE_" + userRole.getRole().getName()))
                .toList();

        return org.springframework.security.core.userdetails.User
            .withUsername(account.getEmail())
            .password(account.getPasswordHash())
            .authorities(authorities)
            .build();
    }
}
