package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.UserRoles;

public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {
}
