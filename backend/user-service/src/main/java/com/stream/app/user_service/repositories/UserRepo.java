package com.stream.app.user_service.repositories;


import com.stream.app.user_service.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface

UserRepo extends JpaRepository<User, String> {
    User findByEmail(String email);
}
