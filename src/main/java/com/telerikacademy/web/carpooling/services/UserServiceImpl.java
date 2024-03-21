package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.exceptions.*;
import com.telerikacademy.web.carpooling.helpers.EmailSenderHelper;
import com.telerikacademy.web.carpooling.helpers.UIMapper;
import com.telerikacademy.web.carpooling.helpers.UserMapper;
import com.telerikacademy.web.carpooling.helpers.ValidationHelper;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.repositories.contracts.RoleRepository;
import com.telerikacademy.web.carpooling.repositories.contracts.UserRepository;
import com.telerikacademy.web.carpooling.services.contracts.UserBlockService;
import com.telerikacademy.web.carpooling.services.contracts.UserService;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.List;

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
    private final UserBlockService userBlockService;
    private final ValidationHelper validationHelper;
    private final EmailSenderHelper emailSenderHelper;
    private final UIMapper UIMapper;


    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, RoleRepository roleRepository, JavaMailSender mailSender, UserBlockService userBlockService, UIMapper UIMapper, ValidationHelper validationHelper, EmailSenderHelper emailSenderHelper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.mailSender = mailSender;
        this.userBlockService = userBlockService;
        this.UIMapper = UIMapper;
        this.validationHelper = validationHelper;
        this.emailSenderHelper = emailSenderHelper;
    }

    @Override
    public void create(@Valid User user) {
        try {
            User newUser = userRepository.getByUsername(user.getUsername());
            IsDeleted isDeleted = userRepository.getDeletedById(newUser.getId());
            checkIfPhoneNumberExists(newUser);
            user = userMapper.fromDtoUpdate(user);
            user.setPhotoUrl(DEFAULT_IMAGE_URL);
            update(user, user);
            userRepository.unmarkAsDeleted(isDeleted);
            sendVerificationEmail(user);
        } catch (EntityNotFoundException e) {
            boolean usernameExists = true;
            try {
                userRepository.getByUsername(user.getUsername());
            } catch (EntityNotFoundException en) {
                usernameExists = false;
            }
            if (usernameExists) {
                throw new DuplicateExistsException("User", "username", user.getUsername());
            }
            user = checkIfEmailExists(user);
            checkIfPhoneNumberExists(user);
            user.setPhotoUrl(DEFAULT_IMAGE_URL);
            validationHelper.passwordValidator(user.getPassword());
            userRepository.create(user);
            sendVerificationEmail(user);
        }
    }

    public void checkIfPhoneNumberExists(User user) {
        validationHelper.phoneNumberValidator(user.getPhoneNumber());
        boolean phoneNumberExists = userRepository.telephoneExists(user.getPhoneNumber(), user.getId());
        if (phoneNumberExists) {
            throw new DuplicatePhoneNumberExists("User", "phone number", user.getPhoneNumber());
        }
    }

    @NotNull
    public User checkIfEmailExists(User user) {
        validationHelper.emailValidator(user.getEmail());
        boolean emailExists = true;
        try {
            user = userRepository.getByEmail(user.getEmail());
        } catch (EntityNotFoundException e) {
            emailExists = false;
        }
        if (emailExists) {
            throw new DuplicateEmailExists("User", "email", user.getEmail());
        }
        return user;
    }

    @Override
    public void checkIfVerified(User user) {
        if (userRepository.getNonVerifiedById(user.getId()) != null) {
            throw new UnauthorizedOperationException("Unverified users cannot create travels or apply for travels.");
        }
    }

    public void sendVerificationEmail(User user) {
        User savedUser = userRepository.getByUsername(user.getUsername());
        NonVerifiedUser nonVerified = new NonVerifiedUser();
        nonVerified.setUserId(savedUser.getId());
        userRepository.create(nonVerified);
        emailSenderHelper.sendVerificationEmail(user, "http://localhost:8080");
    }

    @Override
    public void delete(User user, User deletedBy) {
        user = userRepository.getByUsername(user.getUsername());
        if (!deletedBy.getRole().getName().equals(ADMIN) && !user.getUsername().equals(deletedBy.getUsername())) {
            throw new UnauthorizedOperationException("Only admins or the same user can delete user profiles!");
        }
        try {
            userRepository.getDeletedById(user.getId());
            throw new UserIsAlreadyDeletedException("User", "username", user.getUsername());
        } catch (EntityNotFoundException e) {
            markUserAsDeleted(user.getId());
        }
    }

    @Override
    public void delete(User user) {
        markUserAsDeleted(user.getId());
    }

    @Override
    public void deleteUI(User user) {
        ForgottenPasswordUI forgottenPasswordUI = user.getForgottenPasswordUI();
        userRepository.deleteUI(forgottenPasswordUI);
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
        validationHelper.emailValidator(user.getEmail());
        validationHelper.passwordValidator(user.getPassword());
        userRepository.update(user);
    }

    @Override
    public void update(User user) {
        boolean emailExists = userRepository.updateEmail(user.getEmail(), user.getId());
        validationHelper.emailValidator(user.getEmail());
        if (emailExists) {
            throw new DuplicateExistsException("User", "email", user.getEmail());
        }
        checkIfPhoneNumberExists(user);
        validationHelper.passwordValidator(user.getPassword());
        userRepository.update(user);
    }

    public void markUserAsDeleted(int userId) {
        User user = userRepository.getById(userId);
        IsDeleted isDeleted = new IsDeleted();
        isDeleted.setUser(user);
        userRepository.delete(isDeleted);
    }

    public void unmarkUserAsDeleted(int userId) {
        IsDeleted isDeleted = userRepository.getDeletedById(userId);
        if (isDeleted != null) {
            userRepository.unmarkAsDeleted(isDeleted);
        }
    }

    @Override
    public List<User> get(FilterUserOptions filterUserOptions, User user) {
        if (!user.getRole().getName().equals(ADMIN)) {
            throw new UnauthorizedOperationException(REGULAR_USERS_UNAUTHORIZED_OPERATION);
        }
        return userRepository.get(filterUserOptions);
    }

    @Override
    public List<User> getTop10Passengers() {
        return userRepository.getTop10Passengers();
    }

    @Override
    public List<User> getTop10Organisers() {
        return userRepository.getTop10Organisers();
    }

    @Override
    public Page<User> get(FilterUserOptions filterUserOptions, User user, Pageable pageable) {
        if (!user.getRole().getName().equals(ADMIN)) {
            throw new UnauthorizedOperationException(REGULAR_USERS_UNAUTHORIZED_OPERATION);
        }
        return userRepository.get(filterUserOptions, pageable);
    }

    @Override
    public List<User> get(FilterUserOptions filterUserOptions) {
        return userRepository.get(filterUserOptions);
    }

    @Override
    public void blockUser(String username, User admin) {
        if (!admin.getRole().getName().equals(ADMIN)) {
            throw new UnauthorizedOperationException(REGULAR_USERS_UNAUTHORIZED_OPERATION);
        }
        User userToBlock = userRepository.getByUsername(username);
        if (userRepository.isDeleted(userToBlock.getId())) {
            throw new EntityNotFoundException("User", "username", userToBlock.getUsername());
        }
        if (userRepository.isBlocked(userToBlock.getId())) {
            throw new UserIsAlreadyBlockedException(userToBlock.getId());
        }
        userBlockService.create(userToBlock);
    }

    @Override
    public void unblockUser(String username, User admin) {
        if (!admin.getRole().getName().equals(ADMIN)) {
            throw new UnauthorizedOperationException(REGULAR_USERS_UNAUTHORIZED_OPERATION);
        }
        User userToUnblock = userRepository.getByUsername(username);
        if (userRepository.isDeleted(userToUnblock.getId())) {
            throw new EntityNotFoundException("User", "username", userToUnblock.getUsername());
        }
        userBlockService.delete(userToUnblock);
    }

    @Override
    public void makeAdmin(String username, User admin) {
        User user = checkUserRole(username, admin);
        user.setRole(roleRepository.findByName(ADMIN));
        userRepository.update(user);
    }

    @Override
    public void unmakeAdmin(String username, User admin) {
        User user = checkUserRole(username, admin);
        user.setRole(roleRepository.findByName(REGULAR_USER));
        userRepository.update(user);
    }

    @NotNull
    private User checkUserRole(String username, User admin) {
        User user = userRepository.getByUsername(username);
        if (!admin.getRole().getName().equals(ADMIN)) {
            throw new UnauthorizedOperationException(REGULAR_USERS_UNAUTHORIZED_OPERATION);
        }
        if (userRepository.isDeleted(user.getId())) {
            throw new EntityNotFoundException("User", "username", user.getUsername());
        }
        return user;
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
    public User get(int id) {
        return userRepository.getById(id);
    }

    @Override
    public User get(String username) {
        return userRepository.getByUsername(username);
    }

    @Override
    public User getByUI(String UI) {
        return userRepository.getByUI(UI);
    }

    @Override
    public boolean isUIExisting(String UI) {
        return userRepository.isUIExisting(UI);
    }

    @Override
    public List<User> getAllNotDeleted() {
        return userRepository.getAllNotDeleted();
    }
}