package com.telerikacademy.web.carpooling.controllers.rest;

import com.telerikacademy.web.carpooling.exceptions.*;
import com.telerikacademy.web.carpooling.helpers.AuthenticationHelper;
import com.telerikacademy.web.carpooling.helpers.UserMapper;
import com.telerikacademy.web.carpooling.helpers.UserShow;
import com.telerikacademy.web.carpooling.helpers.UserShowAdmin;
import com.telerikacademy.web.carpooling.models.FilterUserOptions;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.models.dtos.UserDto;
import com.telerikacademy.web.carpooling.repositories.contracts.UserRepository;
import com.telerikacademy.web.carpooling.services.contracts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @Operation(
            summary = "Get a user",
            description = "This method is used for getting a user",
            parameters = {
                    @Parameter(name = "username", description = "This is the username of the user you are trying to get."),
                    @Parameter(name = "email", description = "This is the email of the user you are trying to get."),
                    @Parameter(name = "phoneNumber", description = "This is the phone number of the user you are trying to get."),
                    @Parameter(name = "sortBy", description = "This is used to sort the users you are trying to get."),
                    @Parameter(name = "sortOrder", description = "This is used to sort the results ASC or DESC.")},
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = UserDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE),
                    description = "Successfully got user"),
                    @ApiResponse(responseCode = "404",
                            description = "There is no such user.")}
    )
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

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by id",
            description = "This method is used for getting a user by id",
            parameters = {@Parameter(name = "id", description = "This is the Id of the user you are trying to get.")},
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = UserDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE),
                    description = "Successfully got a user"),
                    @ApiResponse(responseCode = "404", description = "There is no such user")}
    )
    public UserShow getById(@PathVariable int id) {
        try {
            return userMapper.toDto(userService.get(id));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    @PostMapping()
    @Operation(
            summary = "Creating a user",
            description = "This method is used for creating a user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = UserDto.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ),
                    description = "Include content in the request body."
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of a user", content = @Content(schema = @Schema(implementation = UserDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "400", description = "Invalid password/phone number."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user is blocked.") ,
                    @ApiResponse(responseCode = "404", description = "No such user found."),
                    @ApiResponse(responseCode = "409", description = "User with same username/email/phone number exists.")
            }
    )
    public UserShow createUser(@Valid @RequestBody UserDto userDto) {
        try {
            User user = userMapper.fromDto(userDto);
            userService.create(user);
            return userMapper.toDto(user);
        } catch (DuplicateExistsException | DuplicateEmailExists de) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, de.getMessage());
        } catch (InvalidEmailException ie) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ie.getMessage());
        } catch (InvalidPasswordException | UnsupportedOperationException | InvalidPhoneNumberException epe) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, epe.getMessage());
        }
    }

    @GetMapping("/verify-email")
    @Operation(
            summary = "Verify email",
            description = "This method is used for verifying a user's email address",
            parameters = {@Parameter(name = "username", description = "This is the username of the user which email is going to be verified.")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully got the email verified")}
    )
    public String verifyEmail(@RequestParam("username") String username) {
        userService.verifyUser(username);
        return "Email successfully verified!";
    }

    @PutMapping
    @Operation(
            summary = "Updating a user",
            description = "This method is used for updating a user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = User.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ),
                    description = "Include content in the request body."
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful updating of a user", content = @Content(schema = @Schema(implementation = User.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "No such user found."),
                    @ApiResponse(responseCode = "409", description = "User with same username/email/phone number exists.")
            }
    )
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
    @Operation(
            summary = "Deleting a user",
            description = "This method is used for deleting a user",
            parameters = {@Parameter(name = "username", description = "This is the username of the user who will be deleted.")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of a user", content = @Content(schema = @Schema(implementation = User.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "No such user found."),
                    @ApiResponse(responseCode = "409", description = "User with same username/email/phone number exists.")
            }
    )
    public String deleteUser(@RequestHeader HttpHeaders headers, @RequestParam String username) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            User user = userRepository.getByUsername(username);
            userService.delete(user, currentUser);
            return String.format("User with username %s successfully deleted.", user.getUsername());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (UserIsAlreadyDeletedException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/block")
    @Operation(
            summary = "Blocking a user",
            description = "This method is used for blocking a user",
            parameters = {@Parameter(name = "username", description = "This is the username of the user who will be blocked.")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful blocking of a user", content = @Content(schema = @Schema(implementation = UserShowAdmin.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "No such user found."),
                    @ApiResponse(responseCode = "409", description = "User with same username/email/phone number exists.")
            }
    )
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
        } catch (UserIsAlreadyBlockedException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/unblock")
    @Operation(
            summary = "Unblocking a user",
            description = "This method is used for unblocking a user",
            parameters = {@Parameter(name = "username", description = "This is the username of the user who will be unblocked.")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful blocking of a user", content = @Content(schema = @Schema(implementation = UserShowAdmin.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "No such user found.")
            }
    )
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
    @Operation(
            summary = "Making user an admin",
            description = "This method is used for making user an admin",
            parameters = {@Parameter(name = "username", description = "This is the username of the user who will be made an admin.")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful making an admin of a user", content = @Content(schema = @Schema(implementation = UserShowAdmin.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "No such user found.")
            }
    )
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
    @Operation(
            summary = "Unmaking user an admin",
            description = "This method is used for unmaking user an admin",
            parameters = {@Parameter(name = "username", description = "This is the username of the user who will be unmade an admin.")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful unmaking an admin of a user", content = @Content(schema = @Schema(implementation = UserShowAdmin.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "No such user found.")
            }
    )
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