package com.telerikacademy.web.carpooling.controllers.mvc;

import com.telerikacademy.web.carpooling.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.exceptions.ForbiddenOperationException;
import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.helpers.AuthenticationHelper;
import com.telerikacademy.web.carpooling.models.Feedback;
import com.telerikacademy.web.carpooling.models.FeedbackComment;
import com.telerikacademy.web.carpooling.models.dtos.FeedbackDto;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.services.contracts.FeedbackService;
import com.telerikacademy.web.carpooling.services.contracts.TravelService;
import com.telerikacademy.web.carpooling.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/feedbacks")
public class FeedbackMvcController {

    private FeedbackService feedbackService;
    private UserService userService;
    private AuthenticationHelper authenticationHelper;
    private final TravelService travelService;

    @Autowired
    public FeedbackMvcController(FeedbackService feedbackService, UserService userService, AuthenticationHelper authenticationHelper, TravelService travelService) {
        this.feedbackService = feedbackService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.travelService = travelService;
    }

    @GetMapping("/{recipientId}/travel/{travelId}")
    private String createFeedbackView(Model model, @PathVariable("recipientId") int recipientId, @PathVariable("travelId") int travelId){
        try {
            FeedbackDto feedback = new FeedbackDto();
            model.addAttribute("feedbackObject", feedback);
            model.addAttribute("recipientId", recipientId);
            model.addAttribute("travelId", travelId);
            return "createFeedback";
        }
        catch (EntityNotFoundException e){
            model.addAttribute("message", e.getMessage());
            model.addAttribute("status", e.getLocalizedMessage());
            return "404-page";
        }
        }

    @PostMapping("/{id}/travel/{travelId}")
    private String createFeedback(Model model, HttpSession session,
                                  @ModelAttribute("feedbackObject") FeedbackDto feedbackDto,
                                  @PathVariable("id") int id
            , @PathVariable("travelId") int travelId){
        try {
            User author = authenticationHelper.tryGetUser(session);
            User recipient = userService.get(id);
            Feedback feedback = new Feedback();
            feedback.setAuthor(author);
            feedback.setRecipient(recipient);
            feedback.setTravel(travelService.getById(travelId));
            FeedbackComment feedbackComment = new FeedbackComment();
            feedbackComment.setComment(feedbackDto.getComment());
            feedbackComment.setFeedback(feedback);
            feedback.setComment(feedbackComment);
            feedback.setRating(feedbackDto.getRating());
            feedbackService.create(feedback, author);
            return "redirect:/";
        } catch (AuthenticationFailureException e){
            return "redirect:/auth/login";
        }
        catch (ForbiddenOperationException | UnauthorizedOperationException | EntityNotFoundException e){
            model.addAttribute("message", e.getMessage());
            model.addAttribute("status", e.getLocalizedMessage());
            return "404-page";
        }
    }
}
