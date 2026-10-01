package com.example.demo.service;

import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRolesRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.model.User;
import com.example.demo.model.UserRoles;
import com.example.demo.model.Role;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService {
    private final UserRepository userRepository = null;
    private final UserRolesRepository userRolesRepository = null;
    private final RoleRepository roleRepository = null;

    private Optional<User> user = null;
    private List<UserRoles> userRoles = null;
    private Optional<Role> role = null;

    public Object loadUserByEmail(String email) {
        this.user = userRepository.findByEmail(email);
        if (!this.user.isPresent()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        this.userRoles = userRolesRepository.findAllByIdUser(this.user.get().getIdUser());
        for (UserRoles userRole : this.userRoles) {
            this.role = roleRepository.findById(userRole.getIdRole());
        }
        return this.user;
    }
}
