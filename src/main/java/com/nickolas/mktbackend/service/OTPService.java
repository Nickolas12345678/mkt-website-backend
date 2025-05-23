package com.nickolas.mktbackend.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class OTPService {
//    private final JavaMailSender javaMailSender;
//    private final Map<String, String> otpStore = new HashMap<>();
//    private final Map<String, Long> otpExpiryStore = new HashMap<>();
//
//
//
//    @Autowired
//    public OTPService(JavaMailSender javaMailSender) {
//        this.javaMailSender = javaMailSender;
//    }
//
//
//    public String generateOtp(String email) {
//        String otp = RandomStringUtils.randomNumeric(6);
//        otpStore.put(email, otp);
//        otpExpiryStore.put(email, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
//
//        sendOtpEmail(email, otp);
//
//        return otp;
//    }
//
//
//    private void sendOtpEmail(String email, String otp) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Ваш OTP код");
//        message.setText("Ваш OTP код: " + otp);
//        javaMailSender.send(message);
//    }
//
//
//    public boolean verifyOtp(String email, String otp) {
//        String storedOtp = otpStore.get(email);
//        Long expiryTime = otpExpiryStore.get(email);
//
//        if (storedOtp == null || expiryTime == null || System.currentTimeMillis() > expiryTime) {
//            return false;
//        }
//
//        return storedOtp.equals(otp);
//    }

    private final JavaMailSender javaMailSender;
    private final Map<String, String> otpStore = new HashMap<>();
    private final Map<String, Long> otpExpiryStore = new HashMap<>();
    private final Set<String> verifiedEmails = new HashSet<>();

    @Autowired
    public OTPService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public String generateOtp(String email) {
        String otp = RandomStringUtils.randomNumeric(6);
        otpStore.put(email, otp);
        otpExpiryStore.put(email, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
        sendOtpEmail(email, otp);
        return otp;
    }

    private void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Ваш OTP код");
        message.setText("Ваш OTP код: " + otp);
        javaMailSender.send(message);
    }

    public boolean verifyOtp(String email, String otp) {
        String storedOtp = otpStore.get(email);
        Long expiryTime = otpExpiryStore.get(email);

        if (storedOtp == null || expiryTime == null || System.currentTimeMillis() > expiryTime) {
            return false;
        }

        if (storedOtp.equals(otp)) {
            verifiedEmails.add(email);
            otpStore.remove(email);
            otpExpiryStore.remove(email);
            return true;
        }

        return false;
    }

    public boolean isEmailVerified(String email) {
        return verifiedEmails.contains(email);
    }
}
