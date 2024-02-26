package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.exceptions.ForbiddenOperationException;
import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.repositories.FeedbackRepository;
import com.telerikacademy.web.carpooling.repositories.TravelRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    public static final String UPDATE_UNAUTHORIZED_MESSAGE = "Only admins or the same user can update feedbacks!";
    public static final String DELETE_UNAUTHORIZED_MESSAGE = "Only admins or the same user can delete feedbacks!";
    private final FeedbackRepository feedbackRepository;
    private final TravelRepository travelRepository;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, TravelRepository travelRepository) {
        this.feedbackRepository = feedbackRepository;
        this.travelRepository = travelRepository;
    }

    @Override
    public void create(@Valid Feedback feedback, User author) {
        if (isAuthorAParticipantInTravel(feedback.getAuthor().getId(), feedback.getTravel().getId()) &&
                !feedback.getAuthor().equals(feedback.getTravel().getDriver())) {
            throw new UnauthorizedOperationException("You cannot give feedback to this user!");
        }
        if (isRecipientAParticipantInTravel(feedback.getRecipient().getId(), feedback.getTravel().getId()) &&
                !feedback.getRecipient().equals(feedback.getTravel().getDriver())) {
            throw new UnauthorizedOperationException("This recipient was not part of the travel!");
        }
        if (feedback.getRecipient().equals(author)) {
            throw new ForbiddenOperationException("You cannot give feedback to yourself!");
        }
        if (!userHasAlreadyGivenFeedbackForTheRecipient(feedback.getAuthor().getId(), feedback.getRecipient().getId(), feedback.getTravel().getId())) {
            throw new UnauthorizedOperationException("The participant cannot give feedback " +
                    "again to the same recipient!");
        }
        if (driverHasAlreadyGivenFeedbackForTheRecipient(feedback.getAuthor().getId(), feedback.getRecipient().getId(), feedback.getTravel().getId())) {
            throw new UnauthorizedOperationException("The driver cannot give feedback " +
                    "again to the same recipient!");
        }
        feedbackRepository.create(feedback);
    }

    @Override
    public void update(Feedback feedback, User user) {
        if (isRecipientAParticipantInTravel(feedback.getRecipient().getId(), feedback.getTravel().getId()) &&
                !feedback.getRecipient().equals(feedback.getTravel().getDriver())) {
            throw new UnauthorizedOperationException("This recipient was not part of the travel!");
        }
        feedbackRepository.update(feedback);
    }

    @Override
    public void delete(Feedback feedback, User user) {
        if (!user.equals(feedback.getAuthor()) && !user.getRole().getName().equals("Admin")) {
            throw new UnauthorizedOperationException(DELETE_UNAUTHORIZED_MESSAGE);
        }
        feedbackRepository.delete(feedback);
    }

    @Override
    public Feedback getByTravelId(int travelId, int authorId, int recipientId) {
        return feedbackRepository.getByTravelId(travelId, authorId, recipientId);
    }

    @Override
    public List<Feedback> getForUser(FilterFeedbackOptions filterFeedbackOptions, User user) {
        return feedbackRepository.getFeedbacksForUser(filterFeedbackOptions);
    }

    @Override
    public boolean isAuthorAParticipantInTravel(int authorId, int travelId) {
        return travelRepository.isUserAParticipantInTravel(authorId, travelId);
    }

    @Override
    public boolean isRecipientAParticipantInTravel(int recipientId, int travelId) {
        return travelRepository.isRecipientAParticipantInTravel(recipientId, travelId);
    }

    @Override
    public boolean userHasAlreadyGivenFeedbackForTheRecipient(int authorId, int recipientId, int travelId) {
        return travelRepository.hasUserAlreadyGiveFeedbackForTheRecipient(authorId, recipientId, travelId);
    }

    @Override
    public boolean driverHasAlreadyGivenFeedbackForTheRecipient(int driverId, int recipientId, int travelId) {
        return travelRepository.driverHasAlreadyGivenFeedbackForTheRecipient(driverId, recipientId, travelId);
    }

}
