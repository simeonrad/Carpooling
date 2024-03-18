package com.telerikacademy.web.carpooling.controllers.rest;

import com.telerikacademy.web.carpooling.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.exceptions.ForbiddenOperationException;
import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.helpers.AuthenticationHelper;
import com.telerikacademy.web.carpooling.helpers.TravelApplicationMapper;
import com.telerikacademy.web.carpooling.helpers.TravelMapper;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.models.dtos.TravelApplicationDto;
import com.telerikacademy.web.carpooling.models.dtos.TravelDto;
import com.telerikacademy.web.carpooling.services.contracts.TravelApplicationService;
import com.telerikacademy.web.carpooling.services.contracts.TravelCommentService;
import com.telerikacademy.web.carpooling.services.contracts.TravelService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/travels")
public class TravelController {
    private final AuthenticationHelper authenticationHelper;
    private final TravelService travelService;
    private final TravelApplicationService travelApplicationService;
    private final TravelApplicationMapper travelApplicationMapper;
    private final TravelCommentService travelCommentService;
    private final TravelMapper travelMapper;

    @Autowired
    public TravelController(AuthenticationHelper authenticationHelper, TravelService travelService, TravelApplicationService travelApplicationService, TravelApplicationMapper travelApplicationMapper, TravelCommentService travelCommentService, TravelMapper travelMapper) {
        this.authenticationHelper = authenticationHelper;
        this.travelService = travelService;
        this.travelApplicationService = travelApplicationService;
        this.travelApplicationMapper = travelApplicationMapper;
        this.travelCommentService = travelCommentService;
        this.travelMapper = travelMapper;
    }

    @GetMapping()
    @Operation(
            summary = "Get all travels",
            description = "This method is used for getting all travels",
            parameters = {@Parameter(name = "startPoint",
                    description = "This is the start point of the travel you are trying to get."),
                    @Parameter(name = "endPoint", description = "This is the end point of the travel you are trying to get."),
                    @Parameter(name = "status", description = "This is the status of the travel you are trying to get."),
                    @Parameter(name = "freeSpots", description = "This are the free spots of the travel you are trying to get."),
                    @Parameter(name = "driverUsername", description = "This is the username of the driver of a travel you are trying to get."),
                    @Parameter(name = "departureTime", description = "This is the departure time of a travel you are trying to get."),
                    @Parameter(name = "sortBy", description = "This is used to sort the travels you are trying to get."),
                    @Parameter(name = "sortOrder", description = "This is used to sort the results ASC or DESC.")},
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = TravelDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE),
                    description = "Successful got travels"),
                    @ApiResponse(responseCode = "404",
                            description = "There are no travels.")}
    )
    public List<TravelDto> getAll(
            @RequestParam(required = false) String startPoint,
            @RequestParam(required = false) String endPoint,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer freeSpots,
            @RequestParam(required = false) String driverUsername,
            @RequestParam(required = false) LocalDateTime departureTime,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder
    ) {
        try {
            FilterTravelOptions filterTravelOptions = new FilterTravelOptions(driverUsername, startPoint,
                    endPoint, departureTime, freeSpots, status, sortBy, sortOrder);
            return travelService.get(filterTravelOptions).stream()
                    .map(travelMapper::toDto)
                    .collect(Collectors.toList());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get travel by id",
            description = "This method is used for getting a travel by id",
            parameters = {@Parameter(name = "id", description = "This is the Id of the travel you are trying to get.")},
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = TravelDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE),
                    description = "Successfully got a travel"),
                    @ApiResponse(responseCode = "404",
                            description = "There is no such travel")}
    )
    public TravelDto getById(@PathVariable int id) {
        try {
            return travelMapper.toDto(travelService.getById(id));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/applications")
    @Operation(
            summary = "Get applications for specific travel by id",
            description = "This method is used for getting applications for specific travel by id",
            parameters = {@Parameter(name = "id", description = "This is the Id of the travel you want to get the applications of.")},
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = TravelDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE),
                    description = "Successfully got applications for travel"),
                    @ApiResponse(responseCode = "404",
                            description = "There is no such travel")}
    )
    public List<TravelApplicationDto> getApplicationByTravelId(@PathVariable int id) {
        try {
            return travelApplicationService.getByTravelId(id).stream().map(travelApplicationMapper::toDto).toList();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping()
    @Operation(
            summary = "Creating a Travel",
            description = "This method is used for creating a travel",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = TravelDto.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ),
                    description = "Include content in the request body."
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of a travel", content = @Content(schema = @Schema(implementation = TravelDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user is blocked.") ,
                    @ApiResponse(responseCode = "404", description = "No such user found.")
            }
    )
    public TravelDto create(@RequestBody TravelDto travelDto,
                            @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Travel travel = travelMapper.fromDto(travelDto, user);
            travelService.create(travel, user);
            travelCommentService.addOrUpdateComment(travel.getId(), travelDto.getComment());
            return travelMapper.toDto(travel);
        } catch (AuthenticationFailureException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ForbiddenOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/cancel/{id}")
    @Operation(
            summary = "Canceling a Travel",
            description = "This method is used for canceling a travel",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = TravelDto.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ),
                    description = "Include content in the request body."
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful cancellation of a travel", content = @Content(schema = @Schema(implementation = TravelDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user is blocked."),
                    @ApiResponse(responseCode = "404", description = "There is no such travel.")
            }
    )
    public TravelDto cancel(@PathVariable int id,
                            @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Travel travel = travelService.getById(id);
            travelService.cancel(user, travel);
            return travelMapper.toDto(travel);
        } catch (AuthenticationFailureException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (ForbiddenOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    @PutMapping("/complete/{id}")
    @Operation(
            summary = "Completing a Travel",
            description = "This method is used for completing a travel",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = TravelDto.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ),
                    description = "Include content in the request body."
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful completing of a travel", content = @Content(schema = @Schema(implementation = TravelDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user is blocked."),
                    @ApiResponse(responseCode = "404", description = "There is no such travel.")
            }
    )
    public TravelDto complete(@PathVariable int id,
                              @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Travel travel = travelService.getById(id);
            travelService.complete(user, travel);
            return travelMapper.toDto(travel);
        } catch (AuthenticationFailureException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (ForbiddenOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deleting a Travel",
            description = "This method is used for deleting a travel",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = TravelDto.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ),
                    description = "Include content in the request body."
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deleting of a travel", content = @Content(schema = @Schema(implementation = TravelDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user is blocked."),
                    @ApiResponse(responseCode = "404", description = "There is no such travel.")
            }
    )
    public void delete(@PathVariable int id,
                       @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Travel travel = travelService.getById(id);
            travelService.delete(travel, user);
        } catch (AuthenticationFailureException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
