package com.stream.app.user_service.services.authService;

import com.stream.app.user_service.dto.*;
import com.stream.app.user_service.dto.AuthResponse;
import com.stream.app.user_service.entities.Admin;
import com.stream.app.user_service.entities.Login;
import com.stream.app.user_service.entities.Role;
import com.stream.app.user_service.entities.User;
import com.stream.app.user_service.payload.CustomResponseMessage;
import com.stream.app.user_service.repositories.AdminRepo;
import com.stream.app.user_service.repositories.LoginRepo;
import com.stream.app.user_service.repositories.UserRepo;
import com.stream.app.user_service.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepo userRepo;
    private final LoginRepo loginRepo;
    private final AdminRepo adminRepo;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mailSender;
    private final SmsService smsService;
    private final Random random = new Random();

    @Transactional
    public ResponseEntity<CustomResponseMessage<?>> generateAndSendOtp(OtpRequestDto otpRequest) {
        String id = otpRequest.getId();
        String ip = otpRequest.getIp();
        String device = otpRequest.getDevice();

        boolean isEmail = id.contains("@");
        String otp = String.valueOf(random.nextInt(900000) + 100000);
        Optional<User> optionalUser = isEmail ? userRepo.findByEmail(id) : userRepo.findByPhone(id);

        String loginId;
        if (optionalUser.isPresent()) {
            loginId = updateLoginForExistingUser(optionalUser.get().getId(), otp, device, ip);
        } else {
            loginId = createFirstTimeUserAndLogin(id, isEmail, otp, device, ip);
        }

        logger.info("Sending otp via email");
        boolean sent = isEmail ? sendOtpByEmail(id, otp) : sendOtpBySms(id, otp);
        if (sent) {
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("OTP sent successfully on input: " + id)
                            .data(
                                    OtpResponseDto.builder().loginId(loginId).build())
                            .build(),
                    HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("Error sending OPT on input: " + loginId)
                            .data(
                                    OtpResponseDto.builder().loginId(null).build())
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

    }

    private String updateLoginForExistingUser(String userId, String otp, String device, String ip) {
        logger.info("inside the updateLoginForExistingUser: ");
        logger.info("devices: {}" , device);
        logger.info("ip: {}" , ip);
        Login login = loginRepo.findByUserId(userId);
        login.setOtp(otp);
        login.setOtpGeneratedAt(LocalDateTime.now());
        if (!login.isLoggedIn()) {
            login.setDevice(device);
            login.setIp(ip);
        }

        logger.info("inside the updateLoginForExistingUser after setting login details: ");

        logger.info("devices: {}" , login.getDevice());
        logger.info("ip: {}" , login.getIp());
        loginRepo.save(login);
        return login.getId();
    }

    private String createFirstTimeUserAndLogin(String id, boolean isEmail, String otp, String device, String ip) {
        logger.info("inside the createFirstTimeUser: ");
        logger.info("device: {}" , device);
        logger.info("ip: {}" , ip);
        Optional<Admin> admin = adminRepo.findByEmail(id);
        User.UserBuilder userBuilder = User.builder()
                .id(UUID.randomUUID().toString())
                .name("VISITOR")
                .createdAt(LocalDateTime.now())
                .role(admin.map(value -> Role.valueOf(value.getRole())).orElse(Role.STUDENT))
                .isPremium(false);

        if(admin.isPresent()){
            userBuilder.email(admin.get().getEmail());
            userBuilder.phone(admin.get().getPhone());
        }
        else{
            if (isEmail) userBuilder.email(id);
            else userBuilder.phone(id);
        }

        User user = userBuilder.build();
        userRepo.save(user);

        Login login = Login.builder()
                .id(UUID.randomUUID().toString())
                .userId(user.getId())
                .otp(otp)
                .otpGeneratedAt(LocalDateTime.now())
                .device(device)
                .ip(ip)
                .isLoggedIn(false)
                .build();

        logger.info("inside the createFirstTimeUser after setting login details: ");

        logger.info("device: {}" , login.getDevice());
        logger.info("ip: {}" , login.getDevice());
        loginRepo.save(login);
        return login.getId();
    }

    private boolean sendOtpByEmail(String to, String otp) {
        try{

            logger.info("📧 OTP email sent to {}", to);
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject("Your OTP Code");
            msg.setText("Your OTP is: " + otp + "\nValid for 5 minutes.");
            mailSender.send(msg);
            logger.info("📧 OTP email successfully sent to {}", to);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    private boolean sendOtpBySms(String phone, String otp) {
        try{
            smsService.sendSms(phone, otp);
            logger.info("📱 Sent OTP via SMS to: {}", phone);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //completed status code for otp send above method


    public ResponseEntity<CustomResponseMessage<?>> verifyOtp(OtpVerificationRequest otpRequest) {

        String loginId = otpRequest.getLoginId();
        Login login = loginRepo.findById(loginId)
                .orElseThrow(() -> new IllegalArgumentException("OTP not requested for: " + loginId));

        System.out.println("login details: \n"+ login.toString());
        if (login.getOtpGeneratedAt() == null ||
                Duration.between(login.getOtpGeneratedAt(), LocalDateTime.now()).toMinutes() > 5) {
            throw new IllegalArgumentException("OTP expired for: " + loginId);
        }

        if (!otpRequest.getOtp().equals(login.getOtp())) {
            throw new IllegalArgumentException("Invalid OTP for: " + loginId);
        }

        //after all successful checks otp is verified so removed from db
        login.setOtp(null);
        login.setOtpGeneratedAt(null);

        if (login.isLoggedIn()) {
            logger.warn("User {} already logged in from device: {}", login.getUserId(), login.getDevice());

            // Don't generate token — ask frontend to confirm force login
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("User Already Logged in")
                            .data(
                                    LoggedInUserResponse.builder()
                                            .loginId(login.getUserId())
                                            .existingDevice(login.getDevice())
                                            .existingLoginTime(login.getLoggedInAt())
                                            .loggedInIp(login.getIp())
                                            .build()
                            ).build()
                    ,HttpStatus.BAD_REQUEST);
        }

        login.setLoggedIn(true);
        login.setLoggedInAt(LocalDateTime.now());
        login.setRefreshToken(UUID.randomUUID().toString());
        login.setRefreshTokenExpiry(LocalDateTime.now().plusDays(30));
        loginRepo.save(login);

        Optional<User> userOptional = userRepo.findById(login.getUserId());

        User user = userOptional.orElseThrow(() -> new RuntimeException("User not found for: " + login.getUserId()));

        String token = jwtUtil.generateToken(user.getEmail() != null? user.getEmail() : user.getPhone(), user.getRole().name());

        return new ResponseEntity<>(
                CustomResponseMessage.builder()
                        .message("User Logged in successfully")
                        .data(
                                AuthResponse.builder()
                                        .accessToken(token)
                                        .refreshToken(login.getRefreshToken())
                                        .userId(login.getUserId())
                                        .email(user.getEmail())
                                        .phone(user.getPhone())
                                        .name(user.getName())
                                        .role(user.getRole().name())
                                        .build()
                        )
                        .build()
                , HttpStatus.OK);
    }

    //done with the response code for otp verification above

    public ResponseEntity<CustomResponseMessage<?>> forceLogin(ForceLoginRequest forceLoginRequest) {
        String loginId = forceLoginRequest.getLoginId();
        String device = forceLoginRequest.getDevice();
        String ip = forceLoginRequest.getIp();
        Optional<Login> loginOptional = loginRepo.findById(loginId);

        if (loginOptional.isEmpty()) {
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("No login session found for ID: " + loginId)
                            .data(null)
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }
        Login login = loginOptional.get();

        if (!login.isLoggedIn()) {
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("User is not logged in.")
                            .data(null)
                            .build(),
                    HttpStatus.FORBIDDEN
            );
        }
        login.setLoggedIn(true);
        login.setLoggedInAt(LocalDateTime.now());
        login.setDevice(device);
        login.setIp(ip);
        login.setOtp(null);
        login.setOtpGeneratedAt(null);
        login.setRefreshToken(UUID.randomUUID().toString());
        login.setRefreshTokenExpiry(LocalDateTime.now().plusDays(30));
        loginRepo.save(login);

        Optional<User> userOptional = userRepo.findById(login.getUserId());
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("User not found for login ID: " + loginId)
                            .data(null)
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }
        User user = userOptional.get();

        String token = null;
        if(user.getEmail() != null)
            token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        else if(user.getPhone() != null){
            token = jwtUtil.generateToken(user.getPhone() , user.getRole().name());
        }

        if (token == null) {
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("User email or phone is missing, token generation failed.")
                            .data(null)
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        return new ResponseEntity<>(
                CustomResponseMessage.builder()
                        .message("User Logged In successfully")
                        .data(AuthResponse.builder()
                                .accessToken(token)
                                .refreshToken(login.getRefreshToken())
                                .userId(login.getUserId())
                                .name(user.getName())
                                .role(user.getRole().name())
                                .email(user.getEmail())
                                .phone(user.getPhone())
                                .build()
                        )
                        .build(),
                HttpStatus.OK);
    }

//    completed the force login with proper response above


    public ResponseEntity<CustomResponseMessage<?>> logout(HttpServletRequest request) {



        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            logger.warn("❌ Missing or invalid Authorization header");
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("Missing or invalid Authorization header")
                            .data(null)
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
        String token = header.substring(7);

        // 2. Extract email/phone from token
        String userName = jwtUtil.extractUserName(token);
        if (userName == null) {
            logger.warn("❌ Unable to extract user from token");
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("Unable to extract user from token")
                            .data(null)
                            .build(),
                    HttpStatus.UNAUTHORIZED
            );
        }

        logger.info("🔑 Logout request received for: {}", userName);

        // 3. Fetch User
        Optional<User> optionalUser = userName.contains("@") ?
                userRepo.findByEmail(userName) : userRepo.findByPhone(userName);

        if (optionalUser.isEmpty()) {
            logger.warn("❌ No user found with username: {}", userName);
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("No user found with username: " + userName)
                            .data(null)
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        User user = optionalUser.get();

        // 4. Fetch Login record for this user
        Login login = loginRepo.findByUserId(user.getId());
        if (login == null || !login.isLoggedIn()) {
            logger.warn("⚠️ User {} is already logged out", userName);
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("User is already logged out")
                            .data(null)
                            .build(),
                    HttpStatus.CONFLICT
            );
        }

        // 5. Clear login fields
        login.setLoggedIn(false);
        login.setDevice(null);
        login.setIp(null);
        login.setOtp(null);
        login.setOtpGeneratedAt(null);
        login.setLoggedInAt(null);
        login.setLoggedOutAt(LocalDateTime.now());
        login.setRefreshTokenExpiry(null);
        login.setRefreshToken(null);
        loginRepo.save(login);

        logger.info("✅ User {} logged out successfully", userName);
        return new ResponseEntity<>(
                CustomResponseMessage.builder()
                        .message("User logged out successfully")
                        .data(null)
                        .build(),
                HttpStatus.OK
        );
    }

    //user logout added with proper response code above

    public ResponseEntity<CustomResponseMessage<?>> updateToken(String refreshToken) {
        logger.info("🔄 Received refresh token request");

        // Log only partial refresh token for security reasons
        String maskedToken = refreshToken.length() > 10 ? refreshToken.substring(0, 5) + "..." : refreshToken;
        logger.debug("🔑 Refresh token: {}", maskedToken);

        // Step 1: Find Login by refresh token
        Optional<Login> optionalLogin = loginRepo.findByRefreshToken(refreshToken);
        if (optionalLogin.isEmpty()) {
            logger.warn("❌ Invalid refresh token: {}", maskedToken);
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("Invalid refresh token")
                            .data(null)
                            .build(),
                    HttpStatus.UNAUTHORIZED
            );
        }

        Login login = optionalLogin.get();

        // Step 2: Check if refresh token expired
        if (login.getRefreshTokenExpiry() == null || login.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            logger.warn("⚠️ Refresh token expired for userId: {}", login.getUserId());
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("Refresh token expired")
                            .data(null)
                            .build(),
                    HttpStatus.UNAUTHORIZED
            );
        }

        // Step 3: Fetch user
        Optional<User> optionalUser = userRepo.findById(login.getUserId());
        if (optionalUser.isEmpty()) {
            logger.error("❌ User not found for userId: {}", login.getUserId());
            return new ResponseEntity<>(
                    CustomResponseMessage.builder()
                            .message("User not found")
                            .data(null)
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        User user = optionalUser.get();

        // Step 4: Generate new access token
        String id = user.getEmail() != null ? user.getEmail() : user.getPhone();
        String accessToken = jwtUtil.generateToken(id, user.getRole().name());

        // Step 5: Update refresh token expiry (if desired)
        login.setRefreshTokenExpiry(LocalDateTime.now().plusDays(30));
        loginRepo.save(login);
        logger.info("✅ Access token refreshed successfully for userId: {}", login.getUserId());

        // Step 6: Build response
//        AuthResponse authResponse =

        return new ResponseEntity<>(
                CustomResponseMessage.builder()
                        .message("Access token refreshed successfully")
                        .data(AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .name(user.getName())
                                .role(user.getRole().name())
                                .phone(user.getPhone())
                                .email(user.getEmail())
                                .build())
                        .build(),
                HttpStatus.OK
        );
    }

    //refresh Toke response status is updated above

    public ResponseEntity<?> checkGoogleLogin(String email, String name) {
        logger.info("Processing Google login for email: {}", email);

        Optional<User> user = userRepo.findByEmail(email);

        if (user.isEmpty()) {
            logger.info("No user found with email: {}, creating a new one", email);

            Admin userFromAdminDb = null;
            try {
                userFromAdminDb = adminRepo.getByEmail(email);
            } catch (Exception e) {
                logger.error("Failed to fetch admin from DB for email: {}", email, e);
            }

            User userForDb = new User();
            userForDb.setRole(userFromAdminDb == null ? Role.STUDENT : Role.valueOf(userFromAdminDb.getRole()));
            userForDb.setEmail(email);
            userForDb.setName(name);
            userForDb.setCreatedAt(LocalDateTime.now());

            try {
                userRepo.save(userForDb);
                logger.info("User registered successfully: {}", email);
                return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
            } catch (Exception e) {
                logger.error("Error while saving user to DB: {}", email, e);
                return new ResponseEntity<>("Internal Server Error while saving user", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            logger.info("User exists, generating token for: {}", email);
            String accessToken = jwtUtil.generateToken(email, user.get().getRole().toString().toUpperCase());

            AuthResponse authResponse = new AuthResponse();
            authResponse.setRole(user.get().getRole().toString());
            authResponse.setAccessToken(accessToken);
            authResponse.setName(user.get().getName());

            return new ResponseEntity<>(authResponse, HttpStatus.ACCEPTED);
        }
    }
}
