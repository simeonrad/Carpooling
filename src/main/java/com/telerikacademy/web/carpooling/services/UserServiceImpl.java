package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.exceptions.*;
import com.telerikacademy.web.carpooling.helpers.UserMapper;
import com.telerikacademy.web.carpooling.models.FilterUserOptions;
import com.telerikacademy.web.carpooling.models.NonVerifiedUser;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.repositories.RoleRepository;
import com.telerikacademy.web.carpooling.repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    public static final String PASSWORD_VALIDATION_ERROR_MESSAGE = "Password does not meet the requirements! " +
            "It should contain capital letter, digit and special symbol (+, -, *, &, ^, …)";
    public static final String REGULAR_USERS_UNAUTHORIZED_OPERATION = "Only admins have access to the requested " +
            "functionality!";
    public static final String ADMIN = "Admin";
    public static final String REGULAR_USER = "Regular user";
    public static final String DEFAULT_IMAGE_URL = "https://i.ibb.co/3dVFMxL/default-profile-account-unknown-icon" +
            "-black-silhouette-free-vector.jpg";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final JavaMailSender mailSender;


    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, RoleRepository roleRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.mailSender = mailSender;
    }

    @Override
    public void create(@Valid User user) {
        boolean usernameExists = true;
        try {
            User userCreated = userRepository.getByUsername(user.getUsername());
            if (userCreated.isDeleted()) {
                user = userMapper.fromDtoUpdate(user);
                user.setDeleted(false);
                emailValidator(user.getEmail());
                boolean emailExists = true;
                try {
                    user = userRepository.getByEmail(user.getEmail());
                } catch (EntityNotFoundException e) {
                    emailExists = false;
                }
                if (emailExists) {
                    throw new DuplicateEmailExists("User", "email", user.getEmail());
                }
                passwordValidator(user.getPassword());
                userRepository.update(user);
                return;
            }
        } catch (EntityNotFoundException e) {
            usernameExists = false;
        }
        if (usernameExists && !user.isDeleted()) {
            throw new DuplicateExistsException("User", "username", user.getUsername());
        }
        emailValidator(user.getEmail());
        boolean emailExists = true;
        try {
            user = userRepository.getByEmail(user.getEmail());
        } catch (EntityNotFoundException e) {
            emailExists = false;
        }
        if (emailExists) {
            throw new DuplicateEmailExists("User", "email", user.getEmail());
        }
        boolean phoneNumberExists = userRepository.telephoneExists(user.getPhoneNumber());
        if (phoneNumberExists) {
            throw new DuplicateExistsException("User", "phone number", user.getPhoneNumber());
        }
        passwordValidator(user.getPassword());
        user.setPhotoUrl(DEFAULT_IMAGE_URL);
        userRepository.create(user);

        User savedUser = userRepository.getByUsername(user.getUsername());
        NonVerifiedUser nonVerified = new NonVerifiedUser();
        nonVerified.setUserId(savedUser.getId());
        userRepository.create(nonVerified);
        sendVerificationEmail(user, "http://localhost:8080");
    }

    @Override
    public void delete(User user, User deletedBy) {
        user = userRepository.getByUsername(user.getUsername());
        if (user.isDeleted()) {
            throw new EntityNotFoundException("User", "username", user.getUsername());
        }
        if (!deletedBy.getRole().getName().equals(ADMIN) && !user.getUsername().equals(deletedBy.getUsername())) {
            throw new UnauthorizedOperationException("Only admins or the same user can delete user profiles!");
        }
        user.setDeleted(true);
        userRepository.delete(user);
    }

    @Override
    public void update(User user, User updatedBy) {
        if (!user.equals(updatedBy)) {
            throw new UnauthorizedOperationException("Only the user can modify it's data!");
        }
        if (!user.getUsername().equals(updatedBy.getUsername())) {
            throw new UnauthorizedOperationException("Username cannot be changed");
        }
        boolean emailExists = userRepository.updateEmail(user.getEmail(), user.getId());
        if (emailExists) {
            throw new DuplicateExistsException("User", "email", user.getEmail());
        }
        passwordValidator(user.getPassword());
        userRepository.update(user);
    }

    @Override
    public List<User> get(FilterUserOptions filterUserOptions, User user) {
        if (!user.getRole().getName().equals(ADMIN)) {
            throw new UnauthorizedOperationException(REGULAR_USERS_UNAUTHORIZED_OPERATION);
        }
        return userRepository.get(filterUserOptions);
    }

    @Override
    public void blockUser(String username, User admin) {
        if (!admin.getRole().getName().equals(ADMIN)) {
            throw new UnauthorizedOperationException(REGULAR_USERS_UNAUTHORIZED_OPERATION);
        }
        User userToBlock = userRepository.getByUsername(username);
        if (userToBlock.isDeleted()) {
            throw new EntityNotFoundException("User", "username", userToBlock.getUsername());
        }
        userToBlock.setBlocked(true);
        userRepository.update(userToBlock);
    }

    @Override
    public void unblockUser(String username, User admin) {
        if (!admin.getRole().getName().equals(ADMIN)) {
            throw new UnauthorizedOperationException(REGULAR_USERS_UNAUTHORIZED_OPERATION);
        }
        User userToUnblock = userRepository.getByUsername(username);
        if (userToUnblock.isDeleted()) {
            throw new EntityNotFoundException("User", "username", userToUnblock.getUsername());
        }
        userToUnblock.setBlocked(false);
        userRepository.update(userToUnblock);
    }

    @Override
    public void makeAdmin(String username, User admin) {
        User user = userRepository.getByUsername(username);
        if (!admin.getRole().getName().equals(ADMIN)) {
            throw new UnauthorizedOperationException(REGULAR_USERS_UNAUTHORIZED_OPERATION);
        }
        if (user.isDeleted()) {
            throw new EntityNotFoundException("User", "username", user.getUsername());
        }
        user.setRole(roleRepository.findByName(ADMIN));
        userRepository.update(user);
    }

    @Override
    public void unmakeAdmin(String username, User admin) {
        User user = userRepository.getByUsername(username);
        if (!admin.getRole().getName().equals(ADMIN)) {
            throw new UnauthorizedOperationException(REGULAR_USERS_UNAUTHORIZED_OPERATION);
        }
        if (user.isDeleted()) {
            throw new EntityNotFoundException("User", "username", user.getUsername());
        }
        user.setRole(roleRepository.findByName(REGULAR_USER));
        userRepository.update(user);
    }

    @Override
    public void verifyUser(String username) {
        User user = userRepository.getByUsername(username);
        NonVerifiedUser nonVerifiedUser = userRepository.getNonVerifiedById(user.getId());
        nonVerifiedUser.setVerified(true);
        userRepository.verify(nonVerifiedUser);
    }

    @Override
    public void addProfilePhoto(String photoUrl, User user) {
        user.setPhotoUrl(photoUrl);
        userRepository.update(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public List<User> getAllNotDeleted() {
        return userRepository.getAllNotDeleted();
    }

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

    public String passwordValidator(String password) {
        String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[+\\-*&^._|\\\\]).{8,}$";
        Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        if (matcher.matches() && password.length() >= 8) {
            return password;
        } else {
            throw new InvalidPasswordException(PASSWORD_VALIDATION_ERROR_MESSAGE);
        }
    }

    public void sendVerificationEmail(User user, String siteURL) {
        String subject = "Please verify your registration";
        String senderName = "Carpooling A56";

        String senderEmail = "car.pooling.a56@gmail.com\n";

        String mailContent = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>";
        mailContent += "<p>Please click the link below to verify your registration:</p>";

        String verifyURL = siteURL + "/verify?code=" + user.getUsername();

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
            e.printStackTrace();
        }

        mailSender.send(message);
    }
}
