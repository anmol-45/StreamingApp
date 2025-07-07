package com.stream.app.user_service.repositories;

import com.stream.app.user_service.entities.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepo extends JpaRepository<Login, String> {
//    Optional<Login> findById(String Id);

    Login findByUserId(String id);

    Optional<Login> findByRefreshToken(String refreshToken);
}
