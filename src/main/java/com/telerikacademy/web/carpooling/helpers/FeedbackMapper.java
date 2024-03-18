package com.telerikacademy.web.carpooling.helpers;

import com.telerikacademy.web.carpooling.models.Feedback;
import com.telerikacademy.web.carpooling.models.FeedbackComment;
import com.telerikacademy.web.carpooling.models.dtos.FeedbackDto;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.repositories.contracts.TravelRepository;
import com.telerikacademy.web.carpooling.repositories.contracts.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {
    private final TravelRepository travelRepository;
    private final UserRepository userRepository;

    public FeedbackMapper(TravelRepository travelRepository, UserRepository userRepository) {
        this.travelRepository = travelRepository;
        this.userRepository = userRepository;
    }

    public Feedback fromDto(FeedbackDto feedbackDto, User author) {
        Feedback feedback = new Feedback();
        feedback.setTravel(travelRepository.getById(feedbackDto.getTravelId()));
        feedback.setAuthor(author);
        feedback.setRecipient(userRepository.getByUsername(feedbackDto.getRecipient()));
        feedback.setRating(feedbackDto.getRating());
        if (feedbackDto.getComment() != null && !feedbackDto.getComment().isBlank()) {
            FeedbackComment feedbackComment = new FeedbackComment();
            feedbackComment.setComment(feedbackDto.getComment());
            feedbackComment.setFeedback(feedback);
            feedback.setComment(feedbackComment);
        }
        return feedback;
    }

    public FeedbackDto toDto(Feedback feedback, FeedbackDto feedbackDtoNew) {
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setTravelId(feedback.getTravel().getId());
        feedbackDto.setRecipient(feedback.getRecipient().getUsername());
        feedbackDto.setRating(feedback.getRating());
        feedbackDto.setComment(feedbackDtoNew.getComment());
        return feedbackDto;
    }

    public Feedback fromDtoUpdate(FeedbackDto feedbackDto, Feedback feedback) {
        feedback.setRating(feedbackDto.getRating());
        if (feedbackDto.getComment() != null && !feedbackDto.getComment().isEmpty()) {
            if (feedback.getComment() != null) {
                FeedbackComment existingComment = feedback.getComment();
                existingComment.setComment(feedbackDto.getComment());
            } else {
                FeedbackComment newComment = new FeedbackComment();
                newComment.setComment(feedbackDto.getComment());
                newComment.setFeedback(feedback);
                feedback.setComment(newComment);
            }
        }
        return feedback;
    }

    public FeedbackDto convertToDto(Feedback feedback) {
        String recipientName = feedback.getRecipient().getFirstName() + " " + feedback.getRecipient().getLastName();
        String comment = feedback.getComment() != null ? feedback.getComment().getComment() : "-";
        return new FeedbackDto(feedback.getTravel().getId(), recipientName, feedback.getRating(), comment);
    }
}
