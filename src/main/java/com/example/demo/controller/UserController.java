package com.example.demo.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.NewUser;
import com.example.demo.model.User;
import com.example.demo.model.Role;
import com.example.demo.model.UserRoles;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRolesRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRolesRepository userRolesRepository;

    public UserController(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        RoleRepository roleRepository,
        UserRolesRepository userRolesRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRolesRepository = userRolesRepository;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @PostMapping("/signup")
    public User signup(@Valid @RequestBody NewUser newUser) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        User user = new User();
        user.setName(newUser.getName());
        user.setEmail(newUser.getEmail());
        user.setPasswordHash(passwordEncoder.encode(newUser.getPassword()));
        User savedUser = userRepository.save(user);
        
        java.util.Optional<Role> roleUser = roleRepository.findByName("USER");
        roleUser.ifPresentOrElse(role -> {
            UserRoles userRoles = new UserRoles();
            userRoles.setUser(savedUser);
            userRoles.setRole(role);
            userRolesRepository.save(userRoles);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default role not found");
        });
        
        return savedUser;
    }
}
