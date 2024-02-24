package com.telerikacademy.web.carpooling.controllers.rest;

import com.telerikacademy.web.carpooling.exceptions.*;
import com.telerikacademy.web.carpooling.helpers.AuthenticationHelper;
import com.telerikacademy.web.carpooling.helpers.UserMapper;
import com.telerikacademy.web.carpooling.helpers.UserShow;
import com.telerikacademy.web.carpooling.helpers.UserShowAdmin;
import com.telerikacademy.web.carpooling.models.FilterUserOptions;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.models.UserDto;
import com.telerikacademy.web.carpooling.repositories.UserRepository;
import com.telerikacademy.web.carpooling.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.InvalidParameterException;
import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserRepository userRepository, AuthenticationHelper authenticationHelper, UserMapper userMapper) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<User> get(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            FilterUserOptions filterOptions = new FilterUserOptions(username, email, phoneNumber, sortBy, sortOrder);
            return userService.get(filterOptions, currentUser);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InvalidParameterException ip) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ip.getMessage());
        }
    }

    @PostMapping()
    public UserShow createUser(@Valid @RequestBody UserDto userDto) {
        try {
            User user = userMapper.fromDto(userDto);
            userService.create(user);
            return userMapper.toDto(user);
        } catch (DuplicateExistsException de) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, de.getMessage());
        } catch (DuplicateEmailExists dee) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, dee.getMessage());
        } catch (InvalidEmailException ie) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ie.getMessage());
        } catch (InvalidPasswordException epe) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, epe.getMessage());
        }
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("username") String username) {
        userService.verifyUser(username);
        return "Email successfully verified!";
    }

    @PutMapping
    public UserShow updateUser(@RequestHeader HttpHeaders headers, @Valid @RequestBody User user) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            User updatedUser = userMapper.fromDtoUpdate(user);
            userService.update(updatedUser, currentUser);
            return userMapper.toDto(updatedUser);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateExistsException de) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, de.getMessage());
        }
    }

    @DeleteMapping
    public String deleteUser(@RequestHeader HttpHeaders headers, @RequestParam String username) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            User user = userRepository.getByUsername(username);
            userService.delete(user, currentUser);
            user = userRepository.getByUsername(user.getUsername());
            return String.format("User with username %s successfully deleted.", user.getUsername());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        }
    }

    @PutMapping("/block")
    public UserShowAdmin blockUser(@RequestParam String username, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            userService.blockUser(username, currentUser);
            User blockedUser = userRepository.getByUsername(username);
            return userMapper.toDtoAdmin(blockedUser);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/unblock")
    public UserShowAdmin unblockUser(@RequestParam String username, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            userService.unblockUser(username, currentUser);
            User unblockedUser = userRepository.getByUsername(username);
            return userMapper.toDtoAdmin(unblockedUser);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/makeAdmin")
    public UserShowAdmin makeUserAdmin(@RequestParam String username,
                                       @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            userService.makeAdmin(username, currentUser);
            User admin = userRepository.getByUsername(username);
            return userMapper.toDtoAdmin(admin);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/unmakeAdmin")
    public UserShowAdmin unmakeUserAdmin(@RequestParam String username,
                                         @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            userService.unmakeAdmin(username, currentUser);
            User admin = userRepository.getByUsername(username);
            return userMapper.toDtoAdmin(admin);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}