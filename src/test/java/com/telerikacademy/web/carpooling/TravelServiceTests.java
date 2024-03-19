package com.telerikacademy.web.carpooling;

import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.models.enums.ApplicationStatus;
import com.telerikacademy.web.carpooling.repositories.contracts.StatusRepository;
import com.telerikacademy.web.carpooling.repositories.contracts.TravelRepository;
import com.telerikacademy.web.carpooling.services.contracts.DistanceAndDuration;
import com.telerikacademy.web.carpooling.services.TravelServiceImpl;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.telerikacademy.web.carpooling.services.contracts.TravelApplicationService;
import com.telerikacademy.web.carpooling.services.contracts.UserBlockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TravelServiceTests {

    @Mock
    private TravelRepository travelRepository;
    @Mock
    private TravelApplicationService travelApplicationService;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private DistanceAndDuration distanceAndDuration;
    @Mock
    private UserBlockService userBlockService;


    @Mock
    private Pageable pageable;

    @Mock
    private FilterTravelOptions filterTravelOptions;

    @InjectMocks
    private TravelServiceImpl travelService;

    private User user;
    private Travel travel;
    private Status plannedStatus;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        travel = new Travel();
        plannedStatus = new Status();
        ApplicationStatus plannedStatusEnum = ApplicationStatus.PLANNED;
        plannedStatus.setStatus(plannedStatusEnum);

        Mockito.lenient().when(statusRepository.getByValue(ApplicationStatus.PLANNED)).thenReturn(plannedStatus);
        Mockito.lenient().when(distanceAndDuration.getRouteDetails(anyString(), anyString())).thenReturn(new int[]{100, 60});
    }

    @Test
    public void createTravel_whenUserNotBlocked_createsTravel() {
        // Setup
        Car car = new Car();
        car.setOwner(user);
        travel.setDriver(user);
        travel.setCar(car);
        Location location = new Location();
        location.setValue("start");
        travel.setStartPoint(location);
        travel.setEndPoint(location);

        // Execute
        travelService.create(travel, user);

        // Verify
        verify(travelRepository, times(1)).create(travel);
    }

    @Test
    public void createTravel_whenUserIsBlocked_throwsException() {
        // Setup
        when(userBlockService.isUserBlocked(user)).thenReturn(true);

        // Execute & Verify
        assertThrows(UnauthorizedOperationException.class, () -> travelService.create(travel, user), "No create permission, user is blocked");
    }

    @Test
    void getMyTravels_callsRepositoryWithCorrectParameters() {
        // Act
        travelService.getMyTravels(filterTravelOptions, pageable);

        // Assert
        verify(travelRepository).getMyTravels(filterTravelOptions, pageable);
    }

    @Test
    void getAllMyTravels_callsRepositoryWithCorrectParameters() {
        // Act
        travelService.getAllMyTravels(filterTravelOptions, pageable);

        // Assert
        verify(travelRepository).getAllMyTravels(filterTravelOptions, pageable);
    }

    @Test
    void getTravelsIParticipatedIn_callsRepositoryWithCorrectParameters() {
        int currentUserId = 1;

        // Act
        travelService.getTravelsIParticipatedIn(filterTravelOptions, pageable, currentUserId);

        // Assert
        verify(travelRepository).getTravelsIParticipatedIn(filterTravelOptions, pageable, currentUserId);
    }
    @Test
    void whenCreatingTravelWithAnotherUsersCar_thenThrowsUnauthorizedOperationException() {
        Car car = new Car();
        User anotherUser = new User();
        car.setOwner(anotherUser);
        travel.setCar(car);
        travel.setDriver(user);
        Location location = new Location();
        location.setValue("start");
        travel.setStartPoint(location);
        travel.setEndPoint(location);
        assertThrows(UnauthorizedOperationException.class, () -> travelService.create(travel, user),
                "Should throw UnauthorizedOperationException when trying to add someone else's car to travel");
    }
    @Test
    public void completeTravel_updatesTravelStatusToCompletedForDriver() {
        // Setup
        User user = new User();
        Travel travel = new Travel();
        travel.setDriver(user);
        Status plannedStatus = new Status();
        ApplicationStatus plannedStatusEnum = ApplicationStatus.PLANNED;
        plannedStatus.setStatus(plannedStatusEnum);
        travel.setStatus(plannedStatus);
        travel.setDepartureTime(LocalDateTime.MIN);

        Status completedStatus = new Status();
        ApplicationStatus completedStatusEnum = ApplicationStatus.COMPLETED;
        completedStatus.setStatus(completedStatusEnum);
        when(statusRepository.getByValue(ApplicationStatus.COMPLETED)).thenReturn(completedStatus);

        // Execute
        travelService.complete(user, travel);

        // Verify the outcome
        assertEquals(completedStatus, travel.getStatus(), "Travel status should be updated to COMPLETED.");
        verify(travelRepository).update(travel); // This verifies that travelRepository.update(travel) was called.
    }

    @Test
    public void completeTravel_throwsExceptionForNonDriver() {
        // Setup
        User notDriver = new User();
        notDriver.setId(2);
        travel.setDriver(user);
        travel.setStatus(plannedStatus);

        // Execute & Verify
        assertThrows(UnauthorizedOperationException.class, () -> travelService.complete(notDriver, travel), "Only the driver can complete the travel.");
    }



    @Test
    public void cancelTravel_updatesTravelStatusToCanceledForDriver() {
        // Setup
        User user = new User();
        Travel travel = new Travel();
        travel.setDriver(user);
        Status plannedStatus = new Status();
        plannedStatus.setStatus(ApplicationStatus.PLANNED);
        travel.setStatus(plannedStatus);
        travel.setDepartureTime(LocalDateTime.MAX);

        Status canceledStatus = new Status();
        canceledStatus.setStatus(ApplicationStatus.CANCELLED);
        when(statusRepository.getByValue(ApplicationStatus.CANCELLED)).thenReturn(canceledStatus);

        int travelId = 1;
        travel.setId(travelId);

        // Execute
        travelService.cancel(user,travel);

        // Verify outcome
        assertEquals(ApplicationStatus.CANCELLED, travel.getStatus().getStatus(), "Travel status should be CANCELED.");
        verify(travelRepository).update(travel);
    }


    @Test
    public void cancelTravel_throwsExceptionForNonDriver() {
        // Setup
        User notDriver = new User();
        notDriver.setId(2);
        travel.setDriver(user);
        travel.setStatus(plannedStatus);
        travel.setDepartureTime(LocalDateTime.MAX);

        // Execute & Verify
        assertThrows(UnauthorizedOperationException.class, () -> travelService.cancel(notDriver, travel), "Only the driver can cancel the travel.");
    }

    @Test
    void whenGetWithValidFilter_thenReturnsNonEmptyList() {
        FilterTravelOptions filter = new FilterTravelOptions();
        List<Travel> expectedTravels = List.of(new Travel());
        when(travelRepository.get(filter)).thenReturn(expectedTravels);

        List<Travel> result = travelService.get(filter);

        assertFalse(result.isEmpty(), "Result should not be empty");
        assertEquals(expectedTravels.size(), result.size(), "Result size should match expected travels");
    }

    @Test
    void whenGetWithFilterReturnsEmpty_thenReturnsEmptyList() {
        FilterTravelOptions filter = new FilterTravelOptions();
        when(travelRepository.get(filter)).thenReturn(Collections.emptyList());

        List<Travel> result = travelService.get(filter);

        assertTrue(result.isEmpty(), "Result should be empty");
    }

    @Test
    void whenGetWithPageable_thenReturnsPage() {
        FilterTravelOptions filter = new FilterTravelOptions();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Travel> expectedPage = new PageImpl<>(List.of(new Travel()));

        when(travelRepository.get(filter, pageable)).thenReturn(expectedPage);

        Page<Travel> result = travelService.get(filter, pageable);

        assertEquals(expectedPage.getTotalElements(), result.getTotalElements(), "Total elements should match");
        assertFalse(result.getContent().isEmpty(), "Content should not be empty");
    }

    @Test
    void updateTravel_ByDriver_UpdatesTravelSuccessfully() {
        travel.setDriver(user);

        travelService.update(travel, user);

        verify(travelRepository).update(travel);
    }

    @Test
    void updateTravel_ByNonDriver_ThrowsUnauthorizedOperationException() {
        User nonDriver = new User();

        assertThrows(UnauthorizedOperationException.class, () -> travelService.update(travel, nonDriver));
    }

    @Test
    void deleteTravel_ByAdmin_DeletesTravelSuccessfully() {
        User adminUser = new User();
        Role adminRole = new Role();
        adminRole.setName("Admin");
        adminUser.setRole(adminRole);

        travelService.delete(travel, adminUser);

        verify(travelRepository).delete(travel);
    }

    @Test
    void deleteTravel_ByNonAdmin_ThrowsUnauthorizedOperationException() {
        User nonAdminUser = new User();
        Role userRole = new Role();
        userRole.setName("User");
        nonAdminUser.setRole(userRole);

        assertThrows(UnauthorizedOperationException.class, () -> travelService.delete(travel, nonAdminUser));
    }

    @Test
    void cancelTravel_ByDriver_CancelsTravelSuccessfully() {
        travel.setDriver(user);
        travel.setDepartureTime(LocalDateTime.now().plusMinutes(20));
        Status cancelledStatus = new Status();
        cancelledStatus.setStatus(ApplicationStatus.CANCELLED);
        when(statusRepository.getByValue(ApplicationStatus.CANCELLED)).thenReturn(cancelledStatus);

        travelService.cancel(user, travel);

        verify(travelRepository).update(travel);
        assertEquals(cancelledStatus, travel.getStatus());
    }

    @Test
    void completeTravel_ByDriver_CompletesTravelSuccessfully() {
        travel.setDriver(user);
        travel.setDepartureTime(LocalDateTime.now().minusDays(2));

        Status initialStatus = new Status();
        initialStatus.setStatus(ApplicationStatus.PLANNED);
        travel.setStatus(initialStatus);

        Status completedStatus = new Status();
        completedStatus.setStatus(ApplicationStatus.COMPLETED);
        when(statusRepository.getByValue(ApplicationStatus.COMPLETED)).thenReturn(completedStatus);

        travelService.complete(user, travel);

        verify(travelRepository).update(travel);
        assertEquals(ApplicationStatus.COMPLETED, travel.getStatus().getStatus());
    }

    @Test
    void getAll_ReturnsAllTravels() {
        List<Travel> expectedTravels = List.of(new Travel(), new Travel());
        when(travelRepository.getAll()).thenReturn(expectedTravels);

        List<Travel> actualTravels = travelService.getAll();

        assertEquals(expectedTravels, actualTravels, "Should return all travels");
        verify(travelRepository).getAll();
    }


    @Test
    void getById_ExistingId_ReturnsTravel() {
        int travelId = 1;
        Travel expectedTravel = new Travel();
        when(travelRepository.getById(travelId)).thenReturn(expectedTravel);

        Travel actualTravel = travelService.getById(travelId);

        assertEquals(expectedTravel, actualTravel, "Should return the correct travel for given ID");
        verify(travelRepository).getById(travelId);
    }
}
