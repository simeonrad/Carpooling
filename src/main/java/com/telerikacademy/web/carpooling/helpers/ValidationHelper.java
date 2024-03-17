package com.telerikacademy.web.carpooling.helpers;

import com.telerikacademy.web.carpooling.exceptions.InvalidEmailException;
import com.telerikacademy.web.carpooling.exceptions.InvalidPasswordException;
import com.telerikacademy.web.carpooling.exceptions.InvalidPhoneNumberException;
import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ValidationHelper {
    public static final String PASSWORD_VALIDATION_ERROR_MESSAGE = "Password must be at least 8 symbols long and" +
            " should contain capital letter, digit and special symbol (+, -, *, &, ^, …)";
    public String emailValidator(String email) {
        String MAIL_REGEX = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z]+\\.[a-z]+$";
        Pattern MAIL_PATTERN = Pattern.compile(MAIL_REGEX);

        Matcher matcher = MAIL_PATTERN.matcher(email);
        if (matcher.matches()) {
            return email;
        } else {
            throw new InvalidEmailException(email);
        }
    }

    public String phoneNumberValidator(String phoneNumber) {
        String PHONE_REGEX = "^0[0-9]{9}$";
        Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

        Matcher matcher = PHONE_PATTERN.matcher(phoneNumber);
        if (matcher.matches()) {
            return phoneNumber;
        } else {
            throw new InvalidPhoneNumberException(phoneNumber);
        }
    }


    public String passwordValidator(String password) {
        String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[+\\-*&^.!_|\\\\]).{8,}$";
        Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        if (matcher.matches() && password.length() >= 8) {
            return password;
        } else {
            throw new InvalidPasswordException(PASSWORD_VALIDATION_ERROR_MESSAGE);
        }
    }
}
