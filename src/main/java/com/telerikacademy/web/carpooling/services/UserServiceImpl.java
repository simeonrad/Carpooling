package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.exceptions.*;
import com.telerikacademy.web.carpooling.helpers.UserMapper;
import com.telerikacademy.web.carpooling.models.FilterUserOptions;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.repositories.RoleRepository;
import com.telerikacademy.web.carpooling.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

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

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
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
}
