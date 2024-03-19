package com.telerikacademy.web.carpooling.controllers.rest;

import com.telerikacademy.web.carpooling.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.exceptions.ForbiddenOperationException;
import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.helpers.AuthenticationHelper;
import com.telerikacademy.web.carpooling.helpers.FeedbackMapper;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.models.dtos.FeedbackDto;
import com.telerikacademy.web.carpooling.repositories.contracts.FeedbackRepository;
import com.telerikacademy.web.carpooling.repositories.contracts.UserRepository;
import com.telerikacademy.web.carpooling.services.contracts.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    public static final String FEEDBACK_DELETED_SUCCESS_MESSAGE = "Feedback with id %s successfully deleted!";
    private final AuthenticationHelper authenticationHelper;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackService feedbackService;
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public FeedbackController(AuthenticationHelper authenticationHelper, FeedbackMapper feedbackMapper, FeedbackService feedbackService, FeedbackRepository feedbackRepository, UserRepository userRepository) {
        this.authenticationHelper = authenticationHelper;
        this.feedbackMapper = feedbackMapper;
        this.feedbackService = feedbackService;
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(
            summary = "Get feedbacks for user",
            description = "This method is used for getting feedbacks associated with a user.",
            parameters = {@Parameter(name = "author", description = "This is the username of the author of the feedback"),
                    @Parameter(name = "recipient", description = "This is the username of the recipient of the feedback"),
                    @Parameter(name = "sortBy", description = "This is the the sorting for the feedbacks"),
                    @Parameter(name = "sortOrder", description = "This is the sort order for the feedbacks")},
                    responses = {@ApiResponse(responseCode = "200",
            content = @Content(schema = @Schema(implementation = FeedbackDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE),
            description = "Successful got feedbacks"),
            @ApiResponse(responseCode = "404",
                    description = "There is no such feedback or no feedbacks associated with the author/recipient")}
    )
    public List<FeedbackDto> getFeedbacksForUser(@RequestParam(required = false) String author,
                                                 @RequestParam(required = false) String recipient,
                                                 @RequestParam(required = false) String sortBy,
                                                 @RequestParam(required = false) String sortOrder,
                                                 @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            FilterFeedbackOptions filterFeedbackOptions = new FilterFeedbackOptions(author, recipient, sortBy, sortOrder);
            List<Feedback> feedbacks = feedbackService.getForUser(filterFeedbackOptions, currentUser);
            return feedbacks.stream().map(feedbackMapper::convertToDto).collect(Collectors.toList());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    @Operation(
            summary = "Creating a feedback",
            description = "This method is used for creating feedback",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = FeedbackDto.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of a feedback", content = @Content(schema = @Schema(implementation = FeedbackDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "Travel not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user cannot give feedback to him/herself.")
            }
    )
    public FeedbackDto create(@RequestBody FeedbackDto feedbackDto, @RequestHeader HttpHeaders headers) {
        try {
            User author = authenticationHelper.tryGetUser(headers);
            Feedback feedback = feedbackMapper.fromDto(feedbackDto, author);
            feedbackService.create(feedback, author);
            return feedbackMapper.toDto(feedback, feedbackDto);
        } catch (AuthenticationFailureException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (ForbiddenOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping
    @Operation(
            summary = "Updating a feedback",
            description = "This method is used for updating feedback",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = FeedbackDto.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful updating of a feedback", content = @Content(schema = @Schema(implementation = FeedbackDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
            }
    )
    public FeedbackDto update(@RequestBody FeedbackDto feedbackDto, @RequestHeader HttpHeaders headers) {
        User author = authenticationHelper.tryGetUser(headers);
        int recipientId = userRepository.getByUsername(feedbackDto.getRecipient()).getId();
        try {
            if(feedbackService.isRecipientAParticipantInTravel(recipientId, feedbackDto.getTravelId())) {
                throw new UnauthorizedOperationException("This recipient was not part of the travel!");
            }
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        Feedback feedback = feedbackService.getByTravelId(feedbackDto.getTravelId(), author.getId(), recipientId);
        Feedback updatedFeedback = feedbackMapper.fromDtoUpdate(feedbackDto, feedback);
        feedbackService.update(feedback, author);
        return feedbackMapper.toDto(updatedFeedback, feedbackDto);
    }

    @DeleteMapping
    @Operation(
            summary = "Deleting a feedback",
            description = "This method is used for deleting feedback",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = FeedbackDto.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of a feedback", content = @Content(schema = @Schema(implementation = FeedbackDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    }
    )
    public String delete(@RequestBody FeedbackDto feedbackDto, @RequestHeader HttpHeaders headers) {
        User author = authenticationHelper.tryGetUser(headers);
        int recipientId = userRepository.getByUsername(feedbackDto.getRecipient()).getId();
        try {
            if(feedbackService.isRecipientAParticipantInTravel(recipientId, feedbackDto.getTravelId())) {
                throw new UnauthorizedOperationException("This recipient was not part of the travel!");
            }
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        Feedback feedback = feedbackRepository.getByTravelId(feedbackDto.getTravelId(), author.getId(), recipientId);
        feedbackService.delete(feedback, author);
        return String.format(FEEDBACK_DELETED_SUCCESS_MESSAGE, feedback.getId());
    }
}