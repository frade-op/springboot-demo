package com.example.demo.service;

import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRolesRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.model.User;
import com.example.demo.model.UserRoles;
import com.example.demo.model.Role;

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
    private final RoleRepository roleRepository;

    public DatabaseUserDetailsService(UserRepository userRepository, UserRolesRepository userRolesRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userRolesRepository = userRolesRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User account = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not found with email: " + username));
        
        List<SimpleGrantedAuthority> authorities =
            userRolesRepository.findAllByIdUser(account.getIdUser())
                .stream()
                .map(userRole -> userRole.getIdRole())
                .map(roleId -> roleRepository.findById(roleId)
                    .orElseThrow(() -> new IllegalStateException(
                        "Role not found with id: " + roleId)))
                .map(role -> role.getName())
                .map(name -> new SimpleGrantedAuthority("ROLE_" + name))
                .toList();

        return org.springframework.security.core.userdetails.User
            .withUsername(account.getEmail())
            .password(account.getPasswordHash())
            .authorities(authorities)
            .build();
    }
}
