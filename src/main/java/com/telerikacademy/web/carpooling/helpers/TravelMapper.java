package com.telerikacademy.web.carpooling.helpers;

import com.telerikacademy.web.carpooling.exceptions.ForbiddenOperationException;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.models.dtos.TravelDto;
import com.telerikacademy.web.carpooling.services.contracts.CarService;
import com.telerikacademy.web.carpooling.services.contracts.LocationService;
import com.telerikacademy.web.carpooling.services.contracts.TravelCommentService;
import org.springframework.stereotype.Component;

@Component
public class TravelMapper {
    private TravelCommentService travelCommentService;
    private CarService carService;
    private LocationService locationService;

    public TravelMapper(TravelCommentService travelCommentService,
                        CarService carService, LocationService locationService) {
        this.travelCommentService = travelCommentService;
        this.carService = carService;
        this.locationService = locationService;
    }

    public Travel fromDto(TravelDto travelDto, User author) {
        Travel travel = new Travel();
        travel.setDriver(author);
        travel.setStartPoint(locationService.create(travelDto.getStartPoint()));
        travel.setEndPoint(locationService.create(travelDto.getEndPoint()));
        travel.setDepartureTime(travelDto.getDepartureTime());
        travel.setFreeSpots(travelDto.getFreeSpots());
        Car car = carService.getById(travelDto.getCarId());
        if (!author.getCars().contains(car)){
            throw new ForbiddenOperationException("The car you are trying to add is not created my you!");
        }
        travel.setCar(car);
        return travel;
    }

    public Travel fromDto(TravelDto travelDto) {
        Travel travel = new Travel();
        travel.setStartPoint(locationService.create(travelDto.getStartPoint()));
        travel.setEndPoint(locationService.create(travelDto.getEndPoint()));
        travel.setDepartureTime(travelDto.getDepartureTime());
        travel.setFreeSpots(travelDto.getFreeSpots());
        travel.setCar(carService.getById(travelDto.getCarId()));
        return travel;
    }

    public TravelDto toDto(Travel travel) {
        TravelDto travelDto = new TravelDto();
        travelDto.setDepartureTime(travel.getDepartureTime());
        travelDto.setDriverUsername(travel.getDriver().getUsername());
        travelDto.setStartPoint(travel.getStartPoint().getValue());
        travelDto.setEndPoint(travel.getEndPoint().getValue());
        travelDto.setFreeSpots(travel.getFreeSpots());
        travelDto.setStatus(travel.getStatus().getStatus().toString());
        travelDto.setDistanceKm(travel.getDistanceKm());
        travelDto.setDurationMinutes(travel.getDurationMinutes());
        travelDto.setCarId(travel.getCar().getId());
        TravelComment comment = travelCommentService.findByTravelId(travel.getId());
        if (comment != null) {
            travelDto.setComment(comment.getComment());
        }
        return travelDto;
    }
}
