package com.telerikacademy.web.carpooling.services;

import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.models.Car;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public void create(Car car, User user){
        if (!user.isBlocked()) {
            car.setOwner(user);
            carRepository.create(car);
        }
        else {
            throw new UnauthorizedOperationException("Blocked users cannot create cars!");
        }
    }

    @Override
    public void update(Car car, User user){
        if (!car.getOwner().equals(user) && !user.isAdmin()){
            throw new UnauthorizedOperationException("Only owners and admins can edit a car!");
        }
        if (!user.isBlocked()) {
            car.setOwner(user);
            carRepository.update(car);
        }
        else {
            throw new UnauthorizedOperationException("Blocked users cannot edit cars!");
        }
    }
    @Override
    public void delete(Car car, User user){
        if (!car.getOwner().equals(user) && !user.isAdmin()){
            throw new UnauthorizedOperationException("Only owners and admins can delete a car!");
        }
        if (!user.isBlocked()) {
            car.setOwner(user);
            carRepository.delete(car);
        }
        else {
            throw new UnauthorizedOperationException("Blocked users cannot delete cars!");
        }
    }

    @Override
    public Car getById(int id){
      return carRepository.getById(id);
    }
    @Override
    public List<Car> getByUserId(int id){
        return carRepository.getByUserId(id);
    }
}
