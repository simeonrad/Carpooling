package com.telerikacademy.web.carpooling.controllers.rest;

import com.telerikacademy.web.carpooling.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.exceptions.ForbiddenOperationException;
import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.helpers.AuthenticationHelper;
import com.telerikacademy.web.carpooling.helpers.TravelApplicationMapper;
import com.telerikacademy.web.carpooling.models.TravelApplication;
import com.telerikacademy.web.carpooling.models.dtos.TravelApplicationDto;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.services.contracts.TravelApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
public class TravelApplicationController {

    private final TravelApplicationService applicationService;
    private final TravelApplicationMapper travelApplicationMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public TravelApplicationController(TravelApplicationService applicationService,
                                       TravelApplicationMapper travelApplicationMapper,
                                       AuthenticationHelper authenticationHelper) {
        this.applicationService = applicationService;
        this.travelApplicationMapper = travelApplicationMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping()
    @Operation(
            summary = "Get all travel applications",
            description = "This method is used for getting all travel applications",
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = TravelApplicationDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE),
                    description = "Successful got all travel applications"),
                    @ApiResponse(responseCode = "404",
                            description = "There are no travel applications.")}
    )
    public List<TravelApplicationDto> getAll() {
        try {
            return applicationService.getAll().stream()
                    .map(travelApplicationMapper::toDto)
                    .collect(Collectors.toList());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get travel application by id",
            description = "This method is used for getting a travel application by id",
            parameters = {@Parameter(name = "id", description = "This is the Id of the travel you are trying to get.")},
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = TravelApplicationDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE),
                    description = "Successfully got a travel application"),
                    @ApiResponse(responseCode = "404",
                            description = "There is no such travel application.")}
    )
    public TravelApplicationDto getById(@PathVariable int id) {
        try {
            return travelApplicationMapper.toDto(applicationService.getById(id));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping()
    @Operation(
            summary = "Creating a Travel Application",
            description = "This method is used for creating a travel application",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = TravelApplicationDto.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ),
                    description = "Include content in the request body."
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful travel creation", content = @Content(schema = @Schema(implementation = TravelApplicationDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user is blocked.")
            }
    )
    public TravelApplicationDto create(@RequestBody TravelApplicationDto travelApplicationDto,
                                       @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            TravelApplication travelApplication = travelApplicationMapper.fromDto(travelApplicationDto, user);
            applicationService.create(travelApplication);
            return travelApplicationMapper.toDto(travelApplication);
        } catch (AuthenticationFailureException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (ForbiddenOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/cancel/{id}")
    @Operation(
            summary = "Canceling a Travel Application",
            description = "This method is used for canceling a travel application",
            parameters = {@Parameter(name = "id", description = "This is the Id of the travel you are trying update.")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = TravelApplicationDto.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ),
                    description = "Include content in the request body."
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful cancellation of a travel", content = @Content(schema = @Schema(implementation = TravelApplicationDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user is blocked."),
                    @ApiResponse(responseCode = "404", description = "Not found - there is no such travel application as the one you want to update.")
            }
    )
    public TravelApplicationDto cancel(@PathVariable int id,
                                       @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            TravelApplication application = applicationService.getById(id);
            applicationService.cancel(user, application);
            return travelApplicationMapper.toDto(application);
        } catch (AuthenticationFailureException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (ForbiddenOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/approve/{id}")
    @Operation(
            summary = "Approving a Travel Application",
            description = "This method is used for approving a travel application",
            parameters = {@Parameter(name = "id", description = "This is the Id of the travel you are trying approve.")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = TravelApplicationDto.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ),
                    description = "Include content in the request body."
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful approval of a travel application", content = @Content(schema = @Schema(implementation = TravelApplicationDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user is blocked."),
                    @ApiResponse(responseCode = "404", description = "Not found - there is no such travel application as the one you want to update.")
            }
    )
    public TravelApplicationDto approve(@PathVariable int id,
                                        @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            TravelApplication application = applicationService.getById(id);
            applicationService.approve(user, application);
            return travelApplicationMapper.toDto(application);
        } catch (AuthenticationFailureException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (ForbiddenOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/decline/{id}")
    @Operation(
            summary = "Declining a Travel Application",
            description = "This method is used for declining a travel application",
            parameters = {@Parameter(name = "id", description = "This is the Id of the travel you are trying decline.")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = TravelApplicationDto.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ),
                    description = "Include content in the request body."
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful declining of a travel application", content = @Content(schema = @Schema(implementation = TravelApplicationDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user is blocked."),
                    @ApiResponse(responseCode = "404", description = "Not found - there is no such travel application as the one you want to update.")
            }
    )
    public TravelApplicationDto decline(@PathVariable int id,
                                        @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            TravelApplication application = applicationService.getById(id);
            applicationService.decline(user, application);
            return travelApplicationMapper.toDto(application);
        } catch (AuthenticationFailureException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (ForbiddenOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}