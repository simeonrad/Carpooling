package com.telerikacademy.web.carpooling.helpers;

import com.telerikacademy.web.carpooling.exceptions.ForgottenPasswordEmailSentException;
import com.telerikacademy.web.carpooling.models.ForgottenPasswordUI;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Component
public class EmailSenderHelper {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final UIMapper UIMapper;

    public EmailSenderHelper(JavaMailSender mailSender, UserRepository userRepository, com.telerikacademy.web.carpooling.helpers.UIMapper uiMapper) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        UIMapper = uiMapper;
    }

    public void sendVerificationEmail(User user, String siteURL) {
        String subject = "Please verify your registration";
        String senderName = "Carpooling A56";

        String senderEmail = "car.pooling.a56@gmail.com";

        String mailContent = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>";
        mailContent += "<p>Please click the link below to verify your registration:</p>";

        String verifyURL = siteURL + "/users/verify-email?username=" + user.getUsername();

        mailContent += "<h3><a href=\"" + verifyURL + "\">VERIFY</a></h3>";
        mailContent += "<p>Thank you<br>The Carpooling A56 Team</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(senderEmail, senderName);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(mailContent, true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new UnsupportedOperationException("Sending verification email was not possible! Please try again!");
        }
        mailSender.send(message);
    }

    public String generateUI() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!-_+";
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
        String subject = "Forgotten Password";
        String senderName = "Carpooling A56";

        String senderEmail = "car.pooling.a56@gmail.com";

        String mailContent = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>";
        mailContent += "<p>Please click the link below to change your password:</p>";

        String forgottenPassURL = siteURL + "/auth/recover_password/" + endpoint;

        mailContent += "<h3><a href=\"" + forgottenPassURL + "\">FORGOTTEN PASSWORD CHANGE</a></h3>";
        mailContent += "<p>Thank you<br>The Carpooling A56 Team</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(senderEmail, senderName);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(mailContent, true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new UnsupportedOperationException("Sending email was not possible! Please try again!");
        }
        mailSender.send(message);
    }
}
