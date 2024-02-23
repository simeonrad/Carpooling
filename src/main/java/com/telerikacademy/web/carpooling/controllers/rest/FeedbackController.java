package com.telerikacademy.web.carpooling.controllers.rest;

import com.telerikacademy.web.carpooling.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.carpooling.exceptions.ForbiddenOperationException;
import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.helpers.AuthenticationHelper;
import com.telerikacademy.web.carpooling.helpers.FeedbackMapper;
import com.telerikacademy.web.carpooling.models.Feedback;
import com.telerikacademy.web.carpooling.models.FeedbackDto;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.repositories.FeedbackRepository;
import com.telerikacademy.web.carpooling.services.FeedbackService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final AuthenticationHelper authenticationHelper;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackService feedbackService;
    private final FeedbackRepository feedbackRepository;

    public FeedbackController(AuthenticationHelper authenticationHelper, FeedbackMapper feedbackMapper, FeedbackService feedbackService, FeedbackRepository feedbackRepository) {
        this.authenticationHelper = authenticationHelper;
        this.feedbackMapper = feedbackMapper;
        this.feedbackService = feedbackService;
        this.feedbackRepository = feedbackRepository;
    }


    @PostMapping
    public FeedbackDto create(@RequestBody FeedbackDto feedbackDto, @RequestHeader HttpHeaders headers) {
        try {
            User author = authenticationHelper.tryGetUser(headers);
            Feedback feedback = feedbackMapper.fromDto(feedbackDto, author);
            feedbackService.create(feedback, author);
            return feedbackMapper.toDto(feedback);
        } catch (AuthenticationFailureException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (ForbiddenOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

//    @PutMapping
//    public FeedbackDto update (@RequestBody FeedbackDto feedbackDto, @RequestHeader HttpHeaders headers) {
//        User author = authenticationHelper.tryGetUser(headers);
//        //Feedback updatedFeedback = feedbackRepository.
//    }


}