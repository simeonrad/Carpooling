package com.telerikacademy.web.carpooling;

import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.models.Role;
import com.telerikacademy.web.carpooling.models.Status;
import com.telerikacademy.web.carpooling.models.Travel;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.models.enums.ApplicationStatus;
import com.telerikacademy.web.carpooling.repositories.contracts.StatusRepository;
import com.telerikacademy.web.carpooling.repositories.contracts.TravelRepository;
import com.telerikacademy.web.carpooling.services.contracts.DistanceAndDuration;
import com.telerikacademy.web.carpooling.services.TravelServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TravelServiceTests {

    @Mock
    private TravelRepository travelRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private DistanceAndDuration distanceAndDuration;

    @InjectMocks
    private TravelServiceImpl travelService;

    private User user;
    private Travel travel;
    private Status plannedStatus;

    @BeforeEach
    void setUp() {
        user = new User();
        travel = new Travel();
        plannedStatus = new Status();
        plannedStatus.setStatus(ApplicationStatus.PLANNED);

        Mockito.lenient().when(statusRepository.getByValue(ApplicationStatus.PLANNED)).thenReturn(plannedStatus);
        Mockito.lenient().when(distanceAndDuration.getRouteDetails(anyString(), anyString())).thenReturn(new int[]{100, 60});
    }

//    @Test
//    void createTravel_WithValidUser_CreatesTravelSuccessfully() {
//        travel.setStartPoint("Start");
//        travel.setEndPoint("End");
//
//        travelService.create(travel, user);
//
//        verify(travelRepository).create(travel);
//        assertEquals(plannedStatus, travel.getStatus());
//        assertEquals(100, travel.getDistanceKm());
//        assertEquals(60, travel.getDurationMinutes());
//    }

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
