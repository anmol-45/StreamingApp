package com.stream.app.user_service.services.authService;


import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${twilio.phone.number}")
    private String fromNumber;

    public void sendSms(String to, String otp) {
        String formatted = to.startsWith("+") ? to : "+91" + to;

        Message.creator(
                new PhoneNumber(formatted),
                new PhoneNumber(fromNumber),
                "Your OTP is: " + otp + ". Valid for 5 minutes."
        ).create();
    }
}
