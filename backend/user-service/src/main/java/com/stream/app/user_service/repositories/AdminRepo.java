package com.stream.app.user_service.repositories;

import com.stream.app.user_service.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepo extends JpaRepository<Admin, String> {
    Admin getByEmail(String email);  // make sure this method exists in your Admin entity

    Optional<Admin> findByEmail(String input);

    Optional<Admin> findByPhone(String input);}
