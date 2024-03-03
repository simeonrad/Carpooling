package com.telerikacademy.web.carpooling.controllers.rest;

import com.telerikacademy.web.carpooling.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.exceptions.ForbiddenOperationException;
import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.helpers.AuthenticationHelper;
import com.telerikacademy.web.carpooling.helpers.FeedbackMapper;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.repositories.FeedbackRepository;
import com.telerikacademy.web.carpooling.repositories.UserRepository;
import com.telerikacademy.web.carpooling.services.FeedbackService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    public List<FeedbackDto> getFeedbacksForUser(@RequestParam(required = false) String author,
                                              @RequestParam(required = false) String recipient,
                                              @RequestParam(required = false) String sortBy,
                                              @RequestParam(required = false) String sortOrder,
                                              @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            FilterFeedbackOptions filterFeedbackOptions = new FilterFeedbackOptions(author, recipient, sortBy, sortOrder);
            List<Feedback> feedbacks = feedbackService.getForUser(filterFeedbackOptions, currentUser);
            return feedbacks.stream().map(this::convertToDto).collect(Collectors.toList());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
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

    public FeedbackDto convertToDto(Feedback feedback) {
        String recipientName = feedback.getRecipient().getFirstName() + " " + feedback.getRecipient().getLastName();
        String comment = feedback.getComment().getComment();
        return new FeedbackDto(feedback.getTravel().getId(), recipientName, feedback.getRating(), comment);
    }
}