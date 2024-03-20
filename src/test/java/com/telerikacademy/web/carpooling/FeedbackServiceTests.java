package com.telerikacademy.web.carpooling;

import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.models.Feedback;
import com.telerikacademy.web.carpooling.models.Role;
import com.telerikacademy.web.carpooling.models.Travel;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.repositories.contracts.FeedbackRepository;
import com.telerikacademy.web.carpooling.repositories.contracts.TravelRepository;
import com.telerikacademy.web.carpooling.services.FeedbackServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class FeedbackServiceTests {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private TravelRepository travelRepository;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createFeedback_Success() {
        Feedback feedback = new Feedback();
        User author = new User();
        author.setId(1);
        User recipient = new User();
        recipient.setId(2);
        Travel travel = new Travel();
        travel.setId(3);
        travel.setDriver(new User());
        author.setRole(new Role("User"));
        recipient.setRole(new Role("User"));

        feedback.setAuthor(author);
        feedback.setRecipient(recipient);
        feedback.setTravel(travel);

        when(feedbackService.userHasAlreadyGivenFeedbackForTheRecipient(eq(author.getId()), eq(recipient.getId()), eq(travel.getId()))).thenReturn(true);
        when(feedbackService.isRecipientAParticipantInTravel(eq(recipient.getId()), eq(travel.getId()))).thenReturn(false);
        feedbackService.create(feedback, author);

        verify(feedbackRepository).create(feedback);
    }

    @Test
    public void createFeedback_ThrowsUnauthorizedForNonDriverAuthor() {
        Feedback feedback = setupFeedbackScenario(false, true, false, false, false);

        assertThrows(UnauthorizedOperationException.class, () -> feedbackService.create(feedback, feedback.getAuthor()));
    }

    @Test
    public void createFeedback_ThrowsUnauthorizedForNonParticipantRecipient() {
        Feedback feedback = setupFeedbackScenario(true, false, false, false, false);

        assertThrows(UnauthorizedOperationException.class, () -> feedbackService.create(feedback, feedback.getAuthor()));
    }

    @Test
    public void createFeedback_ThrowsUnauthorizedForDuplicateFeedback() {
        Feedback feedback = setupFeedbackScenario(true, true, false, true, false);

        assertThrows(UnauthorizedOperationException.class, () -> feedbackService.create(feedback, feedback.getAuthor()));
    }

    @Test
    public void updateFeedback_ThrowsUnauthorizedWhenUserNotAuthorOrAdmin() {
        Feedback feedback = setupFeedbackForUpdateScenario();
        User nonAuthorNonAdminUser = new User();
        nonAuthorNonAdminUser.setId(3);
        nonAuthorNonAdminUser.setRole(new Role("User"));

        assertThrows(UnauthorizedOperationException.class, () -> feedbackService.update(feedback, nonAuthorNonAdminUser));
    }

    @Test
    public void updateFeedback_ThrowsUnauthorizedWhenRecipientNotParticipant() {
        Feedback feedback = setupFeedbackForUpdateScenario();
        User authorOrAdminUser = feedback.getAuthor();

        when(travelRepository.isRecipientAParticipantInTravel(eq(feedback.getRecipient().getId()), eq(feedback.getTravel().getId()))).thenReturn(true);

        assertThrows(UnauthorizedOperationException.class, () -> feedbackService.update(feedback, authorOrAdminUser));
    }



    private Feedback setupFeedbackScenario(boolean authorIsParticipant, boolean recipientIsParticipant,
                                           boolean isSelfFeedback, boolean hasGivenFeedback, boolean driverHasGivenFeedback) {
        Feedback feedback = new Feedback();
        User author = new User();
        author.setId(1);
        User recipient = isSelfFeedback ? author : new User();
        recipient.setId(isSelfFeedback ? 1 : 2);
        Travel travel = new Travel();
        travel.setId(3);
        User driver = new User();
        driver.setId(4);
        travel.setDriver(driver);

        feedback.setAuthor(author);
        feedback.setRecipient(recipient);
        feedback.setTravel(travel);

        when(travelRepository.isUserAParticipantInTravel(eq(author.getId()), eq(travel.getId()))).thenReturn(authorIsParticipant);
        when(travelRepository.isRecipientAParticipantInTravel(eq(recipient.getId()), eq(travel.getId()))).thenReturn(recipientIsParticipant);
        when(travelRepository.hasUserAlreadyGiveFeedbackForTheRecipient(eq(author.getId()), eq(recipient.getId()), eq(travel.getId()))).thenReturn(hasGivenFeedback);
        when(travelRepository.driverHasAlreadyGivenFeedbackForTheRecipient(eq(driver.getId()), eq(recipient.getId()), eq(travel.getId()))).thenReturn(driverHasGivenFeedback);

        return feedback;
    }

    private Feedback setupFeedbackForUpdateScenario() {
        User author = new User();
        author.setId(1);
        author.setRole(new Role("User"));

        User recipient = new User();
        recipient.setId(2);

        Travel travel = new Travel();
        travel.setId(3);

        Feedback feedback = new Feedback();
        feedback.setAuthor(author);
        feedback.setRecipient(recipient);
        feedback.setTravel(travel);

        when(travelRepository.isRecipientAParticipantInTravel(eq(recipient.getId()), eq(travel.getId()))).thenReturn(true);

        return feedback;
    }

    private Feedback setupFeedbackForDeleteScenario() {
        User author = new User();
        author.setId(1);
        author.setUsername("authorUser");
        author.setRole(new Role("User"));

        User recipient = new User();
        recipient.setId(2);
        recipient.setUsername("recipientUser");
        recipient.setRole(new Role("User"));

        Travel travel = new Travel();
        travel.setId(3);

        Feedback feedback = new Feedback();
        feedback.setId(1);
        feedback.setAuthor(author);
        feedback.setRecipient(recipient);
        feedback.setTravel(travel);

        return feedback;
    }

    @Test
    public void deleteFeedback_AuthorizedUser_Success() {
        Feedback feedback = setupFeedbackForDeleteScenario();
        User authorOrAdmin = feedback.getAuthor();

        feedbackService.delete(feedback, authorOrAdmin);

        verify(feedbackRepository).delete(feedback);
    }

    @Test
    public void createFeedback_SelfFeedback_ThrowsForbiddenOperationException() {
        Feedback feedback = setupFeedbackScenario(true, true, true, false, false); // isSelfFeedback set to true

        assertThrows(UnauthorizedOperationException.class, () -> feedbackService.create(feedback, feedback.getAuthor()));
    }

    @Test
    public void updateFeedback_AuthorizedUser_Success() {
        User author = new User();
        author.setId(1);
        author.setRole(new Role("User"));

        User recipient = new User();
        recipient.setId(2);

        Travel travel = new Travel();
        travel.setId(3);
        travel.setDriver(recipient);

        Feedback feedback = new Feedback();
        feedback.setAuthor(author);
        feedback.setRecipient(recipient);
        feedback.setTravel(travel);

        when(travelRepository.isRecipientAParticipantInTravel(recipient.getId(), travel.getId())).thenReturn(true);

        feedbackService.update(feedback, author);

        verify(feedbackRepository).update(feedback);
    }


    @Test
    public void deleteFeedback_AdminUser_Success() {
        Feedback feedback = setupFeedbackForDeleteScenario();
        User adminUser = new User();
        adminUser.setRole(new Role("Admin"));

        feedbackService.delete(feedback, adminUser);

        verify(feedbackRepository).delete(feedback);
    }

    @Test
    public void getFeedbackByTravelId_ValidIds_ReturnsFeedback() {
        Feedback expectedFeedback = new Feedback();
        int travelId = 1, authorId = 1, recipientId = 2;

        when(feedbackRepository.getByTravelId(travelId, authorId, recipientId)).thenReturn(expectedFeedback);

        Feedback actualFeedback = feedbackService.getByTravelId(travelId, authorId, recipientId);

        assertEquals(expectedFeedback, actualFeedback);
    }


}