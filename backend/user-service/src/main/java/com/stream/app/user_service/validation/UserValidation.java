package com.stream.app.user_service.validation;

import java.util.regex.Pattern;

public class UserValidation {

    // Best-practice email regex (RFC 5322 compliant but simplified)
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    // Password must have:
    // - At least 1 uppercase
    // - At least 1 lowercase
    // - At least 1 digit
    // - At least 1 special character
    // - At least 8 characters in total
    private static final String PASSWORD_REGEX =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // ✅ Validate email format
    public static boolean isEmailValid(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
