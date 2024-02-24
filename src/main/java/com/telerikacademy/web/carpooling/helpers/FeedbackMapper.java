package com.telerikacademy.web.carpooling.helpers;

import com.telerikacademy.web.carpooling.models.Feedback;
import com.telerikacademy.web.carpooling.models.FeedbackComment;
import com.telerikacademy.web.carpooling.models.FeedbackDto;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.repositories.TravelRepository;
import com.telerikacademy.web.carpooling.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class FeedbackMapper {
    private final TravelRepository travelRepository;
    private final UserRepository userRepository;
    public FeedbackMapper(TravelRepository travelRepository, UserRepository userRepository){
        this.travelRepository = travelRepository;
        this.userRepository = userRepository;
    }

    public Feedback fromDto(FeedbackDto feedbackDto, User author) {
        Feedback feedback = new Feedback();
        feedback.setTravel(travelRepository.getById(feedbackDto.getTravelId()));
        feedback.setAuthor(author);
        feedback.setRecipient(userRepository.getByUsername(feedbackDto.getRecipient()));
        feedback.setRating(feedbackDto.getRating());
        if (feedbackDto.getComment() != null && !feedbackDto.getComment().isEmpty()) {
            FeedbackComment comment = new FeedbackComment();
            comment.setComment(feedbackDto.getComment());
            comment.setFeedback(feedback);

            if (feedback.getComments() == null) {
                feedback.setComments(new HashSet<>());
            }

            feedback.getComments().add(comment);
        }
        return feedback;
    }

    public FeedbackDto toDto (Feedback feedback, FeedbackDto feedbackDtoNew) {
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
            if (!feedback.getComments().isEmpty()) {
                FeedbackComment existingComment = feedback.getComments().iterator().next();
                existingComment.setComment(feedbackDto.getComment());
            } else {
                FeedbackComment newComment = new FeedbackComment();
                newComment.setComment(feedbackDto.getComment());
                newComment.setFeedback(feedback);
                feedback.getComments().add(newComment);
            }
        }
        return feedback;
    }
}
