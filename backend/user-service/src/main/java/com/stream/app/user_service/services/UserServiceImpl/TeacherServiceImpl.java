package com.stream.app.user_service.services.UserServiceImpl;

import com.stream.app.user_service.entities.User;
import com.stream.app.user_service.repositories.UserRepo;
import com.stream.app.user_service.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("teacherService")
@RequiredArgsConstructor
@Slf4j
public class TeacherServiceImpl implements UserService {

    private final UserRepo userRepo;

    @Override
    public User getDetails(String email) {
        log.debug("Getting teacher details for email: {}", email);

        Optional<User> user = userRepo.findByEmail(email);
        if (user.isEmpty()) {
            log.warn("No user found with email: {}", email);
            return null;
        }

        if (!"teacher".equalsIgnoreCase(user.get().getRole().toString())) {
            log.warn("Access denied: Not a teacher: {}", email);
            return null;
        }

        return user.get();
    }
}
