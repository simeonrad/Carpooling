package com.telerikacademy.web.carpooling;

import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.models.Car;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.models.UserBlock;
import com.telerikacademy.web.carpooling.repositories.contracts.CarRepository;
import com.telerikacademy.web.carpooling.services.CarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CarServiceTests {
    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    private User user;
    private Car car;
    private User admin;
    private UserBlock userBlock;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        car = new Car();
        admin = new User();
        userBlock = new UserBlock();
        car = mock(Car.class);
        admin = mock(User.class);
        when(admin.isAdmin()).thenReturn(true);
        when(car.getOwner()).thenReturn(user);
    }

    @Test
    public void whenUserIsNotBlocked_thenCreateCar() {
        carService.create(car, user);
        verify(carRepository, times(1)).create(car);
    }

    @Test
    public void whenUserIsBlocked_thenThrowExceptionOnCreate() {
        user.setUserBlocks(userBlock);
        userBlock.setBlockExpireTimestamp(LocalDateTime.now().plusDays(1));
        assertThrows(UnauthorizedOperationException.class, () -> carService.create(car, user), "Blocked users cannot create cars!");
    }

//    @Test
//    public void testUpdateCarByOwner() {
//        carService.update(car, user);
//        verify(carRepository, times(1)).update(car);
//    }

//    @Test
//    public void testUpdateCarBlockedUser() {
//        user.setUserBlocks(userBlock);
//        userBlock.setBlockExpireTimestamp(LocalDateTime.now().plusDays(1));
//        assertThrows(UnauthorizedOperationException.class, () -> carService.update(car, user));
//    }

    @Test
    public void testDeleteCarByOwner() {
        carService.delete(car, user);
        verify(carRepository, times(1)).delete(car);
    }

    @Test
    public void testDeleteCarBlockedUser() {
        user.setUserBlocks(userBlock);
        userBlock.setBlockExpireTimestamp(LocalDateTime.now().plusDays(1));
        assertThrows(UnauthorizedOperationException.class, () -> carService.delete(car, user));
    }

    @Test
    public void testGetById() {
        int carId = 1;
        Car expectedCar = new Car();
        expectedCar.setId(carId);
        when(carRepository.getById(carId)).thenReturn(expectedCar);

        Car resultCar = carService.getById(carId);

        assertEquals(expectedCar, resultCar, "The car returned by the service should match the expected car");
        verify(carRepository, times(1)).getById(carId);
    }

    @Test
    public void testGetByUserId() {
        int userId = 1;
        List<Car> expectedCars = Arrays.asList(new Car(), new Car());
        when(carRepository.getByUserId(userId)).thenReturn(expectedCars);

        List<Car> resultCars = carService.getByUserId(userId);

        assertEquals(expectedCars, resultCars, "The list of cars returned by the service should match the expected list");
        verify(carRepository, times(1)).getByUserId(userId);
    }


}
