package com.telerikacademy.web.carpooling.helpers;

import com.telerikacademy.web.carpooling.exceptions.ForgottenPasswordEmailSentException;
import com.telerikacademy.web.carpooling.models.ForgottenPasswordUI;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.repositories.contracts.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Component
public class EmailSenderHelper {
    public static final String VERIFICATION_SUBJECT = "Please verify your registration";
    public static final String SENDER_NAME = "Carpooling A56";
    public static final String SENDER_EMAIL = "car.pooling.a56@gmail.com";
    public static final String VERIFICATION_CONTENT = "<p>Please click the link below to verify your registration:</p>";
    public static final String VERIFICATION_ENDPOINT = "/users/verify-email?username=";
    public static final String EMAIL_ENDING = "<p>Thank you<br>The Carpooling A56 Team</p>";
    public static final String VERIFICATION_FAILURE_MESSAGE = "Sending verification email was not possible! Please try again!";
    public static final String UNIQUE_SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!-_+";
    public static final String FORGOTTEN_PASS_SUBJECT = "Forgotten Password";
    public static final String FORGOTTEN_PASS_CONTENT = "<p>Please click the link below to change your password:</p>";
    public static final String FORGOTTEN_PASS_ENDPOINT = "/auth/recover_password/";
    public static final String PASSWORD_FAILIRE_MESSAGE = "Sending email was not possible! Please try again!";
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final UIMapper UIMapper;

    public EmailSenderHelper(JavaMailSender mailSender, UserRepository userRepository, com.telerikacademy.web.carpooling.helpers.UIMapper uiMapper) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        UIMapper = uiMapper;
    }

    public void sendVerificationEmail(User user, String siteURL) {
        String mailContent = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>";
        mailContent += EmailSenderHelper.VERIFICATION_CONTENT;

        String verifyURL = siteURL + VERIFICATION_ENDPOINT + user.getUsername();

        mailContent += "<h3><a href=\"" + verifyURL + "\">VERIFY</a></h3>";
        mailContent += EMAIL_ENDING;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(SENDER_EMAIL, SENDER_NAME);
            helper.setTo(user.getEmail());
            helper.setSubject(VERIFICATION_SUBJECT);
            helper.setText(mailContent, true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(VERIFICATION_FAILURE_MESSAGE);
        }
        mailSender.send(message);
    }

    public String generateUI() {
        String characters = UNIQUE_SYMBOLS;
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }
        return result.toString();
    }

    public void sendForgottenPasswordEmail(User user) {
        String url = generateUI();
        ForgottenPasswordUI FPUI = UIMapper.toFPUI(user, url);
        if (userRepository.passwordEmailAlreadySent(user)) {
            throw new ForgottenPasswordEmailSentException();
        }
        userRepository.setNewUI(FPUI);
        sendForgottenPasswordEmail(user, "http://localhost:8080", url);
    }

    public void sendForgottenPasswordEmail(User user, String siteURL, String endpoint) {
        String mailContent = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>";
        mailContent += FORGOTTEN_PASS_CONTENT;

        String forgottenPassURL = siteURL + FORGOTTEN_PASS_ENDPOINT + endpoint;

        mailContent += "<h3><a href=\"" + forgottenPassURL + "\">FORGOTTEN PASSWORD CHANGE</a></h3>";
        mailContent += EMAIL_ENDING;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(SENDER_EMAIL, SENDER_NAME);
            helper.setTo(user.getEmail());
            helper.setSubject(FORGOTTEN_PASS_SUBJECT);
            helper.setText(mailContent, true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(PASSWORD_FAILIRE_MESSAGE);
        }
        mailSender.send(message);
    }
}
