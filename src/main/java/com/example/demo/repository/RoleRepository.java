package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    java.util.Optional<Role> findByName(String name);
}
