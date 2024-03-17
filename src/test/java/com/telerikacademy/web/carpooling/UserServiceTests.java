package com.telerikacademy.web.carpooling;

import com.telerikacademy.web.carpooling.exceptions.*;
import com.telerikacademy.web.carpooling.helpers.EmailSenderHelper;
import com.telerikacademy.web.carpooling.helpers.UIMapper;
import com.telerikacademy.web.carpooling.helpers.UserMapper;
import com.telerikacademy.web.carpooling.helpers.ValidationHelper;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.repositories.contracts.RoleRepository;
import com.telerikacademy.web.carpooling.repositories.contracts.UserRepository;
import com.telerikacademy.web.carpooling.services.contracts.UserBlockService;
import com.telerikacademy.web.carpooling.services.UserServiceImpl;
import com.telerikacademy.web.carpooling.services.contracts.UserService;
import org.mockito.ArgumentCaptor;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.List;

import static com.telerikacademy.web.carpooling.services.UserServiceImpl.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private MimeMessage mimeMessage;
    @Mock
    private UserBlockService userBlockService;
    @Mock
    private Pageable pageable;
    @Mock
    private UIMapper uiMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @InjectMocks
    private ValidationHelper validationHelper;

    @InjectMocks
    private EmailSenderHelper emailSenderHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, roleRepository, mailSender,
                userBlockService, uiMapper, validationHelper, emailSenderHelper);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testCreateUserSuccess() {
        // Arrange
        // Arrange
        UserRepository userRepository = mock(UserRepository.class);
        UserMapper userMapper = mock(UserMapper.class);
        // Mock other dependencies as necessary

        User user = new User();
        user.setId(1); // Ensure user has an ID
        user.setUsername("testUser");
        user.setEmail("user@test.com");
        user.setPhoneNumber("0884361520");
        user.setPassword("Password1.");
        user.setPhotoUrl("url");

        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPhoneNumber(user.getPhoneNumber());
        newUser.setPassword(user.getPassword());
        newUser.setPhotoUrl(user.getPhotoUrl());

        // Assuming getDeletedById is supposed to return null for non-deleted users
        when(userRepository.getByUsername(anyString())).thenReturn(newUser);
        when(userRepository.getDeletedById(newUser.getId())).thenReturn(null);
        when(userMapper.fromDtoUpdate(user)).thenReturn(user);
        UserService userService = new UserServiceImpl(userRepository, userMapper, roleRepository, mailSender, userBlockService, uiMapper, validationHelper, emailSenderHelper);
        // Initialize the user with required fields

        // Act
        userService.create(user);

        // Assert
        verify(userRepository).create(any(User.class));
    }


    @Test
    void whenPhoneNumberDoesNotExist_thenNoExceptionThrown() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setPhoneNumber("0123456789");

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.telephoneExists("0123456789", 1)).thenReturn(false);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, roleRepository, mailSender,
                userBlockService, uiMapper, validationHelper, emailSenderHelper);

        assertDoesNotThrow(() -> userService.checkIfPhoneNumberExists(user));
    }

    @Test
    void whenPhoneNumberExists_thenThrowDuplicatePhoneNumberExistsException() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setPhoneNumber("0123456789");

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.telephoneExists("0123456789", 1)).thenReturn(true);

        ValidationHelper validationHelper = mock(ValidationHelper.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, roleRepository, mailSender,
                userBlockService, uiMapper, validationHelper, emailSenderHelper);

        // Act & Assert
        assertThrows(DuplicatePhoneNumberExists.class, () -> userService.checkIfPhoneNumberExists(user));
    }


    @Test
    void whenEmailDoesNotExist_thenReturnOriginalUser() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getByEmail("test@example.com")).thenThrow(new EntityNotFoundException("User", "email", "test@example.com"));

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, roleRepository, mailSender,
                userBlockService, uiMapper, validationHelper, emailSenderHelper);
        // Act
        User result = userService.checkIfEmailExists(user);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void whenEmailExists_thenThrowDuplicateEmailExistsException() {
        // Arrange
        User user = new User();
        user.setEmail("existing@example.com");

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getByEmail("existing@example.com")).thenReturn(new User());

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, roleRepository, mailSender,
                userBlockService, uiMapper, validationHelper, emailSenderHelper);
        // Act & Assert
        assertThrows(DuplicateEmailExists.class, () -> userService.checkIfEmailExists(user));
    }

    @Test
    void givenVerifiedUser_whenCheckIfVerified_thenNoExceptionThrown() {
        // Arrange
        User user = new User();
        user.setId(1);

        when(userRepository.getNonVerifiedById(user.getId())).thenReturn(null);

        // Act & Assert
        assertDoesNotThrow(() -> userService.checkIfVerified(user));
    }

    @Test
    void givenNonVerifiedUser_whenCheckIfVerified_thenThrowUnauthorizedOperationException() {
        // Arrange
        User user = new User();
        user.setId(1);
        NonVerifiedUser nonVerifiedUser = new NonVerifiedUser();

        when(userRepository.getNonVerifiedById(user.getId())).thenReturn(nonVerifiedUser);

        // Act & Assert
        assertThrows(UnauthorizedOperationException.class, () -> userService.checkIfVerified(user));
    }

    @Test
    void givenValidUser_whenSendVerificationEmail_thenNonVerifiedUserCreated() {
        // Arrange
        User user = new User();
        user.setUsername("john.doe");
        user.setId(1);
        UserRepository userRepositoryMock = mock(UserRepository.class);
        when(userRepositoryMock.getByUsername("john.doe")).thenReturn(user);

        JavaMailSender mailSenderMock = mock(JavaMailSender.class);
        when(mailSenderMock.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, roleRepository, mailSenderMock,
                userBlockService, uiMapper, validationHelper, emailSenderHelper);
        // Act
        userService.sendVerificationEmail(user);

        // Assert
        verify(userRepositoryMock).getByUsername("john.doe");
        verify(userRepositoryMock).create(any(NonVerifiedUser.class));
    }

    @Test
    void givenAdminUser_whenDeleteNotAlreadyDeletedUser_thenSuccess() {
        // Arrange
        User userToBeDeleted = new User();
        userToBeDeleted.setUsername("userToDelete");
        userToBeDeleted.setId(1);

        User adminUser = new User();
        Role adminRole = new Role();
        adminRole.setName("Admin");
        adminUser.setRole(adminRole);
        adminUser.setUsername("adminUser");

        UserRepository userRepositoryMock = mock(UserRepository.class);
        when(userRepositoryMock.getByUsername("userToDelete")).thenReturn(userToBeDeleted);
        doThrow(new EntityNotFoundException("User", userToBeDeleted.getId())).when(userRepositoryMock).getDeletedById(userToBeDeleted.getId());

        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock, userMapper, roleRepository, mailSender,
                userBlockService, uiMapper, validationHelper, emailSenderHelper);
        // Act
        userService.delete(userToBeDeleted, adminUser);

        // Assert
        verify(userRepositoryMock).getDeletedById(userToBeDeleted.getId());
        verify(userRepositoryMock).delete(any(IsDeleted.class));
    }

    @Test
    void givenRegularUser_whenDeleteAnotherUser_thenUnauthorized() {
        // Arrange
        User targetUser = new User();
        targetUser.setUsername("targetUser");
        targetUser.setId(2);

        User regularUser = new User();
        Role userRole = new Role();
        userRole.setName("User");
        regularUser.setRole(userRole);
        regularUser.setUsername("regularUser");
        regularUser.setId(3);

        UserRepository userRepositoryMock = mock(UserRepository.class);
        when(userRepositoryMock.getByUsername("targetUser")).thenReturn(targetUser);

        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock, userMapper, roleRepository, mailSender,
                userBlockService, uiMapper, validationHelper, emailSenderHelper);
        // Act & Assert
        assertThrows(UnauthorizedOperationException.class, () -> userService.delete(targetUser, regularUser));
    }

    @Test
    void givenValidUser_whenDelete_thenUserIsMarkedAsDeleted() {
        // Arrange
        int userId = 1;
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        UserRepository userRepositoryMock = mock(UserRepository.class);
        when(userRepositoryMock.getById(userId)).thenReturn(mockUser);

        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock, userMapper, roleRepository, mailSender,
                userBlockService, uiMapper, validationHelper, emailSenderHelper);
        // Act
        userService.delete(mockUser);

        // Assert
        verify(userRepositoryMock).getById(userId);
        verify(userRepositoryMock).delete(any(IsDeleted.class));
    }

    @Test
    void givenUserWithUI_whenDeleteUI_thenUIIsDeleted() {
        // Arrange
        ForgottenPasswordUI mockForgottenPasswordUI = mock(ForgottenPasswordUI.class);
        User mockUser = mock(User.class);
        when(mockUser.getForgottenPasswordUI()).thenReturn(mockForgottenPasswordUI);

        UserRepository userRepositoryMock = mock(UserRepository.class);
        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock, userMapper, roleRepository, mailSender,
                userBlockService, uiMapper, validationHelper, emailSenderHelper);
        // Act
        userService.deleteUI(mockUser);

        // Assert
        verify(mockUser).getForgottenPasswordUI();
        verify(userRepositoryMock).deleteUI(mockForgottenPasswordUI);
    }

    @Test
    void givenValidUserAndUpdatedBySameUser_whenUpdate_thenUserIsUpdated() {
        // Arrange
        UserRepository userRepositoryMock = mock(UserRepository.class);
        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock, userMapper, roleRepository, mailSender,
                userBlockService, uiMapper, validationHelper, emailSenderHelper);
        User user = new User();
        user.setId(1);
        user.setUsername("user");
        user.setEmail("user@example.com");
        user.setPassword("ValidPassword1.");

        when(userRepository.updateEmail(user.getEmail(), user.getId())).thenReturn(false);

        // Act
        userService.update(user, user);

        // Assert
        verify(userRepositoryMock).updateEmail(user.getEmail(), user.getId());
        verify(userRepositoryMock).update(user);
    }

    @Test
    void givenValidUser_whenUpdate_thenSuccess() {
        // Arrange
        UserRepository userRepositoryMock = mock(UserRepository.class);
        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock, userMapper, roleRepository, mailSender,
                userBlockService, uiMapper, validationHelper, emailSenderHelper);
        User user = new User();
        user.setId(1);
        user.setUsername("user");
        user.setEmail("newemail@example.com");
        user.setPhoneNumber("0123456789");
        user.setPassword("ValidPassword1.");

        when(userRepository.updateEmail(anyString(), anyInt())).thenReturn(false);
        when(userRepository.telephoneExists(anyString(), anyInt())).thenReturn(false);

        // Act
        userService.update(user);

        // Assert
        verify(userRepositoryMock).updateEmail(user.getEmail(), user.getId());
        verify(userRepositoryMock).telephoneExists(user.getPhoneNumber(), user.getId());
        verify(userRepositoryMock).update(user);
    }

    @Test
    void givenUserId_whenMarkUserAsDeleted_thenUserIsMarkedAsDeleted() {
        // Arrange
        int userId = 1;
        User user = new User();
        user.setId(userId);

        UserRepository userRepositoryMock = mock(UserRepository.class);
        when(userRepositoryMock.getById(userId)).thenReturn(user);

        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock, userMapper, roleRepository, mailSender,
                userBlockService, uiMapper, validationHelper, emailSenderHelper);
        // Act
        userService.markUserAsDeleted(userId);

        // Assert
        verify(userRepositoryMock).getById(userId);
        verify(userRepositoryMock).delete(any(IsDeleted.class));
    }

    @Test
    void givenAdminUser_whenGetUsers_thenReturnListOfUsers() {
        // Arrange
        User adminUser = new User();
        adminUser.setRole(new Role(ADMIN));
        FilterUserOptions filterUserOptions = new FilterUserOptions(null, null, null, null, null);

        List<User> expectedUsers = List.of(new User(), new User());
        when(userRepository.get(filterUserOptions)).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.get(filterUserOptions, adminUser);

        // Assert
        assertEquals(expectedUsers, result);
    }

    @Test
    void givenNonAdminUser_whenGetUsers_thenThrowUnauthorizedOperationException() {
        // Arrange
        User regularUser = new User();
        regularUser.setRole(new Role("REGULAR_USER"));
        FilterUserOptions filterUserOptions = new FilterUserOptions(null, null, null, null, null);

        // Act & Assert
        assertThrows(UnauthorizedOperationException.class, () -> userService.get(filterUserOptions, regularUser));
    }

    @Test
    void givenAdminUser_whenGetUsersWithPagination_thenReturnUserPage() {
        // Arrange
        User adminUser = new User();
        adminUser.setRole(new Role(ADMIN)); // Adjust based on your role management
        FilterUserOptions filterUserOptions = new FilterUserOptions(null, null, null, null, null);

        Page<User> expectedPage = new PageImpl<>(List.of(new User(), new User()));
        when(userRepository.get(filterUserOptions, pageable)).thenReturn(expectedPage);

        // Act
        Page<User> result = userService.get(filterUserOptions, adminUser, pageable);

        // Assert
        assertEquals(expectedPage, result);
    }

    @Test
    void givenNonAdminUser_whenGetUsersWithPagination_thenThrowUnauthorizedOperationException() {
        // Arrange
        User regularUser = new User();
        regularUser.setRole(new Role("REGULAR_USER"));
        FilterUserOptions filterUserOptions = new FilterUserOptions(null, null, null, null, null);

        // Act & Assert
        assertThrows(UnauthorizedOperationException.class, () -> userService.get(filterUserOptions, regularUser, pageable));
    }

    @Test
    void whenGetUsersWithFilterOptions_thenReturnFilteredUserList() {
        // Arrange
        FilterUserOptions filterUserOptions = new FilterUserOptions("testUsername", null, null, "username", "asc");
        List<User> expectedUsers = List.of(new User());
        when(userRepository.get(filterUserOptions)).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.get(filterUserOptions);

        // Assert
        assertEquals(expectedUsers, result, "The expected and actual user lists do not match");
    }

    @Test
    void whenBlockUserAsAdmin_thenBlockUserSuccessfully() {
        // Arrange
        User admin = new User();
        admin.setRole(new Role(ADMIN));

        User userToBlock = new User();
        userToBlock.setUsername("userToBlock");
        userToBlock.setId(1);

        when(userRepository.getByUsername("userToBlock")).thenReturn(userToBlock);
        when(userRepository.isDeleted(userToBlock.getId())).thenReturn(false);
        when(userRepository.isBlocked(userToBlock.getId())).thenReturn(false);

        // Act
        userService.blockUser("userToBlock", admin);

        // Assert
        verify(userBlockService, times(1)).create(userToBlock);
    }

    @Test
    void whenBlockUserAsNonAdmin_thenThrowUnauthorizedOperationException() {
        User nonAdmin = new User();
        nonAdmin.setRole(new Role(REGULAR_USER));

        // Act & Assert
        assertThrows(UnauthorizedOperationException.class, () -> userService.blockUser("userToBlock", nonAdmin));
    }

    @Test
    void whenBlockUserThatDoesNotExist_thenThrowEntityNotFoundException() {
        // Arrange
        User admin = new User();
        admin.setRole(new Role(ADMIN));

        when(userRepository.getByUsername("nonExistingUser")).thenThrow(new EntityNotFoundException("User", "username", "nonExistingUser"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.blockUser("nonExistingUser", admin));
    }

    @Test
    void whenBlockAlreadyBlockedUser_thenThrowUserIsAlreadyBlockedException() {
        // Arrange
        User admin = new User();
        admin.setRole(new Role(ADMIN));

        User alreadyBlockedUser = new User(); // Setup already blocked user
        alreadyBlockedUser.setUsername("alreadyBlocked");
        alreadyBlockedUser.setId(2);

        when(userRepository.getByUsername("alreadyBlocked")).thenReturn(alreadyBlockedUser);
        when(userRepository.isBlocked(alreadyBlockedUser.getId())).thenReturn(true);

        // Act & Assert
        assertThrows(UserIsAlreadyBlockedException.class, () -> userService.blockUser("alreadyBlocked", admin));
    }

    @Test
    void whenUnblockUserAsAdmin_thenUnblockUserSuccessfully() {
        // Arrange
        User admin = new User();
        admin.setRole(new Role(ADMIN));

        User userToUnblock = new User();
        userToUnblock.setUsername("userToUnblock");
        userToUnblock.setId(1);

        when(userRepository.getByUsername("userToUnblock")).thenReturn(userToUnblock);
        when(userRepository.isDeleted(userToUnblock.getId())).thenReturn(false);

        // Act
        userService.unblockUser("userToUnblock", admin);

        // Assert
        verify(userBlockService, times(1)).delete(userToUnblock);
    }

    @Test
    void whenUnblockUserAsNonAdmin_thenThrowUnauthorizedOperationException() {
        // Arrange
        User nonAdmin = new User();
        nonAdmin.setRole(new Role(REGULAR_USER));

        // Act & Assert
        assertThrows(UnauthorizedOperationException.class, () -> userService.unblockUser("userToUnblock", nonAdmin));
    }

    @Test
    void whenUnblockUserThatDoesNotExist_thenThrowEntityNotFoundException() {
        // Arrange
        User admin = new User();
        admin.setRole(new Role(ADMIN));

        when(userRepository.getByUsername("nonExistingUser")).thenThrow(new EntityNotFoundException("User", "username", "nonExistingUser"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.unblockUser("nonExistingUser", admin));
    }

    @Test
    void whenMakeAdminByAdmin_thenSuccess() {
        // Arrange
        User admin = new User();
        admin.setRole(new Role(ADMIN));
        User userToMakeAdmin = new User();
        userToMakeAdmin.setUsername("user");
        userToMakeAdmin.setRole(new Role(REGULAR_USER));

        when(userRepository.getByUsername("user")).thenReturn(userToMakeAdmin);
        when(roleRepository.findByName(ADMIN)).thenReturn(new Role(ADMIN));

        // Act
        userService.makeAdmin("user", admin);

        // Assert
        assertEquals(ADMIN, userToMakeAdmin.getRole().getName());
        verify(userRepository, times(1)).update(userToMakeAdmin);
    }

    @Test
    void whenUnmakeAdminByAdmin_thenSuccess() {
        // Arrange
        User admin = new User();
        admin.setRole(new Role(ADMIN));
        User userToUnmakeAdmin = new User();
        userToUnmakeAdmin.setUsername("adminUser");
        userToUnmakeAdmin.setRole(new Role(ADMIN));

        when(userRepository.getByUsername("adminUser")).thenReturn(userToUnmakeAdmin);
        when(roleRepository.findByName(REGULAR_USER)).thenReturn(new Role(REGULAR_USER));

        // Act
        userService.unmakeAdmin("adminUser", admin);

        // Assert
        assertEquals(REGULAR_USER, userToUnmakeAdmin.getRole().getName());
        verify(userRepository, times(1)).update(userToUnmakeAdmin);
    }

    @Test
    void whenMakeAdminByNonAdmin_thenThrowUnauthorizedOperationException() {
        // Arrange
        User nonAdmin = new User();
        nonAdmin.setRole(new Role(REGULAR_USER));

        // Act & Assert
        assertThrows(UnauthorizedOperationException.class, () -> userService.makeAdmin("user", nonAdmin));
    }

    @Test
    void whenUnmakeAdminByNonAdmin_thenThrowUnauthorizedOperationException() {
        // Arrange
        User nonAdmin = new User();
        nonAdmin.setRole(new Role(REGULAR_USER));

        // Act & Assert
        assertThrows(UnauthorizedOperationException.class, () -> userService.unmakeAdmin("adminUser", nonAdmin));
    }

    @Test
    void whenMakeAdminOnNonExistingUser_thenThrowEntityNotFoundException() {
        // Arrange
        User admin = new User();
        admin.setRole(new Role(ADMIN));
        when(userRepository.getByUsername("nonExistingUser")).thenThrow(new EntityNotFoundException("User", "username", "nonExistingUser"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.makeAdmin("nonExistingUser", admin));
    }

    @Test
    void whenUnmakeAdminOnNonExistingUser_thenThrowEntityNotFoundException() {
        // Arrange
        User admin = new User();
        admin.setRole(new Role(ADMIN));
        when(userRepository.getByUsername("nonExistingUser")).thenThrow(new EntityNotFoundException("User", "username", "nonExistingUser"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.unmakeAdmin("nonExistingUser", admin));
    }

    @Test
    void givenAdminUserAndRegularUser_whenMakeAdmin_thenSucceeds() {
        // Set up
        User admin = new User();
        admin.setRole(new Role(ADMIN));

        User regularUser = new User();
        regularUser.setUsername("regularUser");
        regularUser.setRole(new Role(REGULAR_USER));

        when(userRepository.getByUsername("regularUser")).thenReturn(regularUser);
        when(roleRepository.findByName(ADMIN)).thenReturn(new Role(ADMIN));
        doNothing().when(userRepository).update(any(User.class));

        ArgumentCaptor<User> regularUserArgumentCaptor = ArgumentCaptor.forClass(User.class);

        // Action
        userService.makeAdmin("regularUser", admin);

        // Assertion
        verify(userRepository).update(regularUserArgumentCaptor.capture());
        User capturedUser = regularUserArgumentCaptor.getValue();
        assertEquals(ADMIN, capturedUser.getRole().getName());
    }


    @Test
    void givenAdminUserAndDeletedUser_whenMakeAdmin_thenThrowsEntityNotFoundException() {
        User admin = new User();
        admin.setRole(new Role(ADMIN));

        User deletedUser = new User();
        deletedUser.setUsername("deletedUser");
        deletedUser.setRole(new Role(REGULAR_USER));

        when(userRepository.getByUsername("deletedUser")).thenThrow(new EntityNotFoundException("User", "username", "deletedUser"));

        assertThrows(EntityNotFoundException.class, () -> userService.makeAdmin("deletedUser", admin));
    }

    @Test
    void givenRegularUserTryingToMakeAdmin_whenMakeAdmin_thenThrowsUnauthorizedOperationException() {
        User regularUser = new User();
        regularUser.setRole(new Role(REGULAR_USER));

        assertThrows(UnauthorizedOperationException.class, () -> userService.makeAdmin("anyUser", regularUser));
    }

    @Test
    void givenUsername_whenVerifyUser_thenUserVerified() {
        // Arrange
        String username = "testUser";
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername(username);

        ArgumentCaptor<NonVerifiedUser> nonVerifiedUserArgumentCaptor = ArgumentCaptor.forClass(NonVerifiedUser.class);

        NonVerifiedUser mockNonVerifiedUser = new NonVerifiedUser();
        mockNonVerifiedUser.setUserId(mockUser.getId());
        mockNonVerifiedUser.setVerified(false);

        when(userRepository.getByUsername(username)).thenReturn(mockUser);
        when(userRepository.getNonVerifiedById(mockUser.getId())).thenReturn(mockNonVerifiedUser);

        // Act
        userService.verifyUser(username);

        // Assert
        verify(userRepository).verify(nonVerifiedUserArgumentCaptor.capture());
        NonVerifiedUser captured = nonVerifiedUserArgumentCaptor.getValue();
        assertTrue(captured.isVerified());
    }

    @Test
    void givenPhotoUrlAndUser_whenAddProfilePhoto_thenPhotoUrlIsUpdated() {
        // Arrange
        String photoUrl = "http://example.com/photo.jpg";
        User user = new User();
        user.setId(1);
        user.setUsername("testUser");

        UserRepository userRepository = mock(UserRepository.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, roleRepository, mailSender,
                userBlockService, uiMapper, validationHelper, emailSenderHelper);

        // Act
        userService.addProfilePhoto(photoUrl, user);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).update(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals(photoUrl, capturedUser.getPhotoUrl());
    }

    @Test
    void getAll_ReturnsAllUsers() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(
                new User("User1", "pasS.123", "user", "user", "email@email.com", "0885478963"),
                new User("User2", "pasS.123", "user", "user", "email@email.org", "0885478962")
        );

        UserRepository userRepositoryMock = mock(UserRepository.class);
        when(userRepositoryMock.getAll()).thenReturn(expectedUsers);

        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock, userMapper, roleRepository, mailSender, userBlockService, uiMapper, validationHelper, emailSenderHelper);

        // Act
        List<User> actualUsers = userService.getAll();

        // Assert
        assertEquals(expectedUsers.size(), actualUsers.size(), "The size of the returned user list should match the expected size.");
        assertTrue(actualUsers.containsAll(expectedUsers), "The returned list of users should match the expected list.");
    }

    @Test
    void whenGetById_thenReturnsExpectedUser() {
        // Arrange
        int userId = 1;
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setUsername("testUser");
        expectedUser.setEmail("test@example.com");

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getById(userId)).thenReturn(expectedUser);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, roleRepository, mailSender, userBlockService, uiMapper, validationHelper, emailSenderHelper);

        // Act
        User actualUser = userService.get(userId);

        // Assert
        assertEquals(expectedUser, actualUser, "The returned user should match the expected user.");
        verify(userRepository).getById(userId);
    }

    @Test
    void whenGetByUsername_thenReturnsExpectedUser() {
        // Arrange
        String username = "testUser";
        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setUsername(username);
        expectedUser.setEmail("test@example.com");

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getByUsername(username)).thenReturn(expectedUser);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, roleRepository, mailSender, userBlockService, uiMapper, validationHelper, emailSenderHelper);

        // Act
        User actualUser = userService.get(username);

        // Assert
        assertEquals(expectedUser, actualUser, "The returned user should match the expected user.");

        // Verify repository interaction
        verify(userRepository).getByUsername(username);
    }

    @Test
    void givenNewUser_whenCreate_thenUserIsCreatedAndVerificationEmailSent() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setEmail("newUser@example.com");
        newUser.setPhoneNumber("1234567890");

        when(userRepository.getByUsername(anyString())).thenThrow(new EntityNotFoundException("User", "username", newUser.getUsername()));
        when(userRepository.getByEmail(anyString())).thenThrow(new EntityNotFoundException("User", "email", newUser.getEmail()));
        doNothing().when(validationHelper).emailValidator(anyString());
        doNothing().when(validationHelper).passwordValidator(anyString());
        doNothing().when(emailSenderHelper).sendVerificationEmail(any(User.class), anyString());
        doNothing().when(emailSenderHelper).sendVerificationEmail(any(User.class), anyString());

        // Act
        userService.create(newUser);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Assert
        verify(userRepository, times(1)).create(userCaptor.capture());
        verify(emailSenderHelper, times(1)).sendVerificationEmail(userCaptor.capture(), anyString());

        User createdUser = userCaptor.getValue();
        assertEquals(newUser.getUsername(), createdUser.getUsername());
        assertEquals(DEFAULT_IMAGE_URL, createdUser.getPhotoUrl());
    }

    @Test
    void givenExistingUsername_whenCreate_thenThrowDuplicateExistsException() {
        // Arrange
        User existingUser = new User();
        existingUser.setUsername("existingUser");

        when(userRepository.getByUsername(existingUser.getUsername())).thenReturn(existingUser);

        // Act & Assert
        assertThrows(DuplicateExistsException.class, () -> userService.create(existingUser));
    }

    @Test
    public void testGetTop10Passengers() {
        List<User> expectedUsers = List.of(new User(), new User());
        when(userRepository.getTop10Passengers()).thenReturn(expectedUsers);

        List<User> result = userService.getTop10Passengers();

        assertEquals(expectedUsers, result);
        verify(userRepository, times(1)).getTop10Passengers();
    }

    @Test
    public void testGetTop10Organisers() {
        List<User> expectedUsers = List.of(new User(), new User());
        when(userRepository.getTop10Organisers()).thenReturn(expectedUsers);

        List<User> result = userService.getTop10Organisers();

        assertEquals(expectedUsers, result);
        verify(userRepository, times(1)).getTop10Organisers();
    }

    @Test
    public void testGetAllNotDeleted() {
        List<User> expectedUsers = List.of(new User(), new User());

        when(userRepository.getAllNotDeleted()).thenReturn(expectedUsers);

        List<User> result = userService.getAllNotDeleted();

        // Assertions
        assertEquals(expectedUsers, result, "The returned list of users should match the expected list");
        verify(userRepository, times(1)).getAllNotDeleted();
    }

    @Test
    public void testGetByUI() {
        String ui = "uniqueIdentifier";
        User expectedUser = new User();

        when(userRepository.getByUI(ui)).thenReturn(expectedUser);

        User resultUser = userService.getByUI(ui);

        assertEquals(expectedUser, resultUser, "The returned user should match the expected user from the repository");
        verify(userRepository, times(1)).getByUI(ui);
    }

    @Test
    public void testIsUIExisting() {
        String ui = "uniqueIdentifier";
        boolean expectedResult = true;

        when(userRepository.isUIExisting(ui)).thenReturn(expectedResult);

        boolean result = userService.isUIExisting(ui);

        assertEquals(expectedResult, result, "The isUIExisting method should return true when the UI exists in the repository");
        verify(userRepository, times(1)).isUIExisting(ui);
    }







}
