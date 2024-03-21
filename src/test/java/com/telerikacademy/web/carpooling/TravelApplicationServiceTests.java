package com.telerikacademy.web.carpooling;

import com.telerikacademy.web.carpooling.exceptions.*;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.models.enums.ApplicationStatus;
import com.telerikacademy.web.carpooling.repositories.contracts.*;
import com.telerikacademy.web.carpooling.services.TravelApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TravelApplicationServiceTests {

    @Mock
    private TravelApplicationRepository applicationRepository;

    @Mock
    private TravelRepository travelRepository;

    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private TravelApplicationServiceImpl service;

    private User user, driver, newUser;
    private Travel travel;
    private Status pendingStatus;
    private Status declinedStatus;
    private Status cancelledStatus;
    private Status approvedStatus;
    private TravelApplication application;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("user");

        // Setup Driver
        driver = new User();
        driver.setId(2);
        driver.setUsername("driver");

        newUser = new User();
        newUser.setId(3);
        driver.setUsername("newUser");

        // Setup Travel
        travel = new Travel();
        travel.setId(1);
        travel.setDriver(driver);
        travel.setDepartureTime(LocalDateTime.now().plusDays(1));

        // Setup Status
        pendingStatus = new Status();
        pendingStatus.setId(1); // Assuming IDs are not unique across types
        pendingStatus.setStatus(ApplicationStatus.PENDING);
        declinedStatus = new Status();
        declinedStatus.setId(2);
        declinedStatus.setStatus(ApplicationStatus.DECLINED);
        cancelledStatus = new Status();
        cancelledStatus.setStatus(ApplicationStatus.CANCELLED);
        cancelledStatus.setId(3);
        approvedStatus = new Status();
        approvedStatus.setStatus(ApplicationStatus.APPROVED);
        approvedStatus.setId(4);

        // Mocking repository responses
        lenient().when(statusRepository.getByValue(ApplicationStatus.PENDING)).thenReturn(pendingStatus);
        lenient().when(travelRepository.getById(1)).thenReturn(travel);
    }

    @Test
    void createTravelApplication_ValidConditions_Success() {
        // Preparing the application to be tested
        TravelApplication application = new TravelApplication();
        application.setPassenger(user); // Assigning the user as the passenger
        application.setTravel(travel); // Assigning the travel

        // Asserting the method execution does not throw any exceptions
        assertDoesNotThrow(() -> service.create(application));

        // Verifying the repository's create method is called with our application
        verify(applicationRepository).create(application);
    }

    @Test
    void testApplyingForOwnTravelThrowsException() {
        User user = mock(User.class);
        Travel travel = mock(Travel.class);
        when(travel.getDriver()).thenReturn(user);
        lenient().when(travel.getId()).thenReturn(1);
        lenient().when(travelRepository.getById(1)).thenReturn(travel);

        TravelApplication application = new TravelApplication();
        application.setPassenger(user);
        application.setTravel(travel);

        assertThrows(ForbiddenOperationException.class, () -> service.create(application));
    }

    @Test
    void create_WhenApplyingForOwnTravel_ShouldThrowForbiddenOperationException() {
        application = new TravelApplication();
        application.setPassenger(user);
        application.setTravel(travel);
        travel.setDriver(user); // The user is the driver of the travel

        ForbiddenOperationException thrown = assertThrows(
                ForbiddenOperationException.class,
                () -> service.create(application),
                "Expected create() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Travel organizer cannot apply for his own travel!"));
    }

    @Test
    void create_WhenAfterDepartureTime_ShouldThrowForbiddenOperationException() {
        application = new TravelApplication();
        application.setPassenger(user);
        application.setTravel(travel);
        LocalDateTime pastDepartureTime = LocalDateTime.now().minusDays(1);
        travel.setDepartureTime(pastDepartureTime);

        ForbiddenOperationException thrown = assertThrows(
                ForbiddenOperationException.class,
                () -> service.create(application),
                "Expected create() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("A travel with departure date already passed cannot be created"));
    }

    @Test
    void checkIfCreated_WhenUserAlreadyApplied_ShouldThrowDuplicateExistsException() {
        application = new TravelApplication();
        application.setPassenger(user);
        application.setTravel(travel);
        when(applicationRepository.isUserAppliedForTravel(travel.getId(), user.getId())).thenReturn(true);

        DuplicateExistsException thrown = assertThrows(
                DuplicateExistsException.class,
                () -> service.checkIfCreated(travel, user),
                "Expected checkIfCreated() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("User already applied for travel"));
    }

//    @Test
//    void update_WhenUserIsNotOwner_ShouldThrowUnauthorizedOperationException() {
//        application = new TravelApplication();
//        application.setPassenger(user);
//        application.setTravel(travel);
//        User newUser = new User();
//        newUser.setId(3);
//        newUser.setUsername("newUser");
////        TravelApplication existingApplication = new TravelApplication();
////        existingApplication.setPassenger(driver);
//
//
//        UnauthorizedOperationException thrown = assertThrows(
//                UnauthorizedOperationException.class,
//                () -> service.update(newUser, application),
//                "Expected update() to throw, but it didn't"
//        );
//
//        assertTrue(thrown.getMessage().contains("Only the creator of an application can update it"));
//    }

    @Test
    void delete_WhenUserIsNotOwner_ShouldThrowUnauthorizedOperationException() {
        application = new TravelApplication();

        application.setPassenger(new User());

        application.setPassenger(user);
        application.setTravel(travel);

        UnauthorizedOperationException thrown = assertThrows(
                UnauthorizedOperationException.class,
                () -> service.delete(user, application),
                "Expected delete() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Only the creator of an application can delete it"));
    }

    @Test
    void approve_WhenNotDriver_ShouldThrowUnauthorizedOperationException() {
        application = new TravelApplication();
        application.setPassenger(user);
        application.setTravel(travel);
        application.setStatus(pendingStatus);
        User notDriver = new User();
        notDriver.setId(99); // Assuming this user is not the driver

        UnauthorizedOperationException thrown = assertThrows(
                UnauthorizedOperationException.class,
                () -> service.approve(notDriver, application),
                "Expected approve() to throw due to unauthorized user"
        );

        assertTrue(thrown.getMessage().contains("Only the driver of an application can approve or decline it!"));
    }

    @Test
    void approve_WhenApplicationIsCancelled_ShouldThrowForbiddenOperationException() {
        application = new TravelApplication();
        travel.setDriver(driver);
        application.setPassenger(user);
        application.setTravel(travel);
        application.setStatus(cancelledStatus); // Assuming the status is set correctly

        ForbiddenOperationException thrown = assertThrows(
                ForbiddenOperationException.class,
                () -> service.approve(travel.getDriver(), application),
                "Expected approve() to throw due to cancelled application"
        );

        assertTrue(thrown.getMessage().contains("Cancelled applications cannot be approved or declined"));
    }

    @Test
    void approve_WhenAfterDepartureTime_ShouldThrowForbiddenOperationException() {
        application = new TravelApplication();
        application.setPassenger(user);
        application.setTravel(travel);
        application.setStatus(pendingStatus);
        travel.setDepartureTime(LocalDateTime.now().minusDays(1)); // Assuming the travel is in the past

        ForbiddenOperationException thrown = assertThrows(
                ForbiddenOperationException.class,
                () -> service.approve(travel.getDriver(), application),
                "Expected approve() to throw due to past departure time"
        );

        assertTrue(thrown.getMessage().contains("You cannot approve an application after the departure time"));
    }

    @Test
    void approve_Successful_ShouldUpdateApplicationStatus() {
        application = new TravelApplication();
        application.setPassenger(user);
        application.setTravel(travel);
        application.setStatus(pendingStatus);
        travel.setDriver(driver);

        // Assuming all conditions for a successful approval are met
        lenient().when(applicationRepository.getById(anyInt())).thenReturn(application);
        lenient().when(travelRepository.getById(anyInt())).thenReturn(travel);

        service.approve(travel.getDriver(), application);

        // Verify that the application status is updated to APPROVED
        assertEquals(statusRepository.getByValue(ApplicationStatus.APPROVED), application.getStatus());
        // Verify that the repository's update method is called
        verify(applicationRepository).update(application);
    }


    @Test
    void decline_WhenNotDriver_ShouldThrowUnauthorizedOperationException() {
        application = new TravelApplication();
        application.setPassenger(user);
        application.setTravel(travel);
        User notDriver = new User();
        notDriver.setId(99); // Assuming this user is not the driver

        UnauthorizedOperationException thrown = assertThrows(
                UnauthorizedOperationException.class,
                () -> service.decline(notDriver, application),
                "Expected decline() to throw due to unauthorized user"
        );

        assertTrue(thrown.getMessage().contains("Only the driver of an application can approve or decline it!"));
    }

    @Test
    void decline_WhenApplicationIsCancelled_ShouldThrowForbiddenOperationException() {
        application = new TravelApplication();
        application.setPassenger(user);
        application.setTravel(travel);
        application.setStatus(cancelledStatus);

        ForbiddenOperationException thrown = assertThrows(
                ForbiddenOperationException.class,
                () -> service.decline(travel.getDriver(), application),
                "Expected decline() to throw due to cancelled application"
        );

        assertTrue(thrown.getMessage().contains("Cancelled applications cannot be approved or declined"));
    }

    @Test
    void decline_WhenAfterDepartureTime_ShouldThrowForbiddenOperationException() {
        application = new TravelApplication();
        application.setPassenger(user);
        application.setTravel(travel);
        travel.setDepartureTime(LocalDateTime.now().minusDays(1));

        ForbiddenOperationException thrown = assertThrows(
                ForbiddenOperationException.class,
                () -> service.decline(travel.getDriver(), application),
                "Expected decline() to throw due to past departure time"
        );

        assertTrue(thrown.getMessage().contains("You cannot decline an application after the departure time"));
    }

    @Test
    void decline_Successful_ShouldUpdateApplicationStatus() {
        application = new TravelApplication();
        application.setPassenger(user);
        application.setTravel(travel);
        application.setStatus(pendingStatus);

        lenient().when(applicationRepository.getById(application.getId())).thenReturn(application);
        lenient().when(travelRepository.getById(travel.getId())).thenReturn(travel);
        lenient().when(statusRepository.getByValue(ApplicationStatus.DECLINED)).thenReturn(declinedStatus);

        service.decline(travel.getDriver(), application);

        assertEquals(ApplicationStatus.DECLINED, application.getStatus().getStatus());

        verify(applicationRepository).update(application);
    }



//    _________________


    @Test
    void cancel_Successful_ShouldUpdateApplicationStatusAndFreeSpots() {
        application = new TravelApplication();
        application.setPassenger(user);
        application.setTravel(travel);
        application.setStatus(approvedStatus); // Ensure application is initially approved
        travel.setFreeSpots(5);

        service.cancel(user, application);

        assertEquals(statusRepository.getByValue(ApplicationStatus.CANCELLED), application.getStatus());
        assertEquals(6, travel.getFreeSpots()); // Verify free spots incremented
        verify(applicationRepository).update(application); // Verify application is updated
        verify(travelRepository).update(travel); // Verify travel is updated
    }

    @Test
    void cancel_UnauthorizedUser_ShouldThrowException() {
        application = new TravelApplication();
        application.setPassenger(user);
        application.setTravel(travel);
        UnauthorizedOperationException thrown = assertThrows(
                UnauthorizedOperationException.class,
                () -> service.cancel(newUser, application),
                "Expected cancel() to throw due to unauthorized user"
        );

        assertTrue(thrown.getMessage().contains("Only the creator of an application can cancel it!"));
    }

    @Test
    void cancel_AfterDepartureTime_ShouldThrowException() {
        application = new TravelApplication();
        application.setPassenger(user);
        application.setTravel(travel);
        travel.setDepartureTime(LocalDateTime.now().minusDays(1)); // Past departure time

        ForbiddenOperationException thrown = assertThrows(
                ForbiddenOperationException.class,
                () -> service.cancel(user, application),
                "Expected cancel() to throw due to past departure time"
        );

        assertTrue(thrown.getMessage().contains("You cannot cancel an application after the departure time"));
    }

}
