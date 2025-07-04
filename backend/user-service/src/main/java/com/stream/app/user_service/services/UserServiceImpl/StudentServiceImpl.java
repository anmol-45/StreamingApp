package com.stream.app.user_service.services.UserServiceImpl;

import com.stream.app.user_service.entities.User;
import com.stream.app.user_service.repositories.UserRepo;
import com.stream.app.user_service.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("studentService")
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements UserService {

    private final UserRepo userRepo;

    @Override
    public User getDetails(String email) {
        log.debug("Getting student details from repository for email: {}", email);

        Optional<User> user = userRepo.findById(email);
        if (user.isEmpty()) {
            log.warn("No user found with email: {}", email);
            throw new RuntimeException("User not found");
        }

        if (!"student".equalsIgnoreCase(user.get().getRole().toString())) {
            log.warn("User is not a student: {}", email);
            throw new RuntimeException("Access denied: Not a student");
        }

        return user.get();
    }
}
