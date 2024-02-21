package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.repositories.FeedbackRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import javax.xml.stream.events.Comment;
import java.util.List;
@Service
public class FeedbackServiceImpl implements FeedbackService{
    private final FeedbackRepository feedbackRepository;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public void create(@Valid Feedback feedback) {
        feedbackRepository.create(feedback);
    }

    @Override
    public void update(Feedback feedback, User user) {
        if (!user.equals(feedback.getAuthor()) && !user.getRole().getName().equals("Admin")) {
            throw new UnauthorizedOperationException("Only admins or the same user can delete user profiles!");
        }
        feedbackRepository.update(feedback);
    }

    @Override
    public void delete(Feedback feedback, User user) {
        if (!user.equals(feedback.getAuthor()) && !user.getRole().getName().equals("Admin")) {
            throw new UnauthorizedOperationException("Only admins or the same user can delete user profiles!");
        }
        feedbackRepository.delete(feedback);
    }

    @Override
    public List<Feedback> getForUser(FilterFeedbackOptions filterFeedbackOptions, User user) {
        return feedbackRepository.getFeedbacksForUser(filterFeedbackOptions);
    }
}
