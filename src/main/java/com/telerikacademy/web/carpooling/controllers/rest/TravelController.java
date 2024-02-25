package com.telerikacademy.web.carpooling.controllers.rest;

import com.telerikacademy.web.carpooling.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.exceptions.ForbiddenOperationException;
import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.helpers.AuthenticationHelper;
import com.telerikacademy.web.carpooling.helpers.TravelMapper;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.services.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/travels")
public class TravelController {
    private final AuthenticationHelper authenticationHelper;
    private final TravelService travelService;
    private final TravelMapper travelMapper;
    @Autowired
    public TravelController(AuthenticationHelper authenticationHelper, TravelService travelService, TravelMapper travelMapper) {
        this.authenticationHelper = authenticationHelper;
        this.travelService = travelService;
        this.travelMapper = travelMapper;
    }

    @GetMapping()
    public List<TravelDto> getAll(
            @RequestParam(required = false) String startPoint,
            @RequestParam(required = false) String endPoint,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) int freeSpots,
            @RequestParam(required = false) String driverUsername,
            @RequestParam(required = false) LocalDateTime departureTime,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder
    ){
        try{
            FilterTravelOptions filterTravelOptions = new FilterTravelOptions(driverUsername,startPoint,endPoint,departureTime,freeSpots,status,sortBy,sortOrder);
            return travelService.get(filterTravelOptions).stream()
                    .map(travelMapper::toDto)
                    .collect(Collectors.toList());
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public TravelDto getById(@PathVariable int id){
        try{
            return travelMapper.toDto(travelService.getById(id));
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping()
    public TravelDto create(@RequestBody TravelDto travelDto,
                                       @RequestHeader HttpHeaders headers){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Travel travel = travelMapper.fromDto(travelDto,user);
            travelService.create(travel, user);
            return travelMapper.toDto(travel);
        } catch (AuthenticationFailureException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/cancel/{id}")
    public TravelDto cancel(@PathVariable int id,
                                       @RequestHeader HttpHeaders headers){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Travel travel = travelService.getById(id);
            travelService.cancel(user, travel);
            return travelMapper.toDto(travel);
        } catch (AuthenticationFailureException | UnauthorizedOperationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
     catch (ForbiddenOperationException e){
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }

    }
    @PutMapping("/complete/{id}")
    public TravelDto complete(@PathVariable int id,
                                        @RequestHeader HttpHeaders headers){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Travel travel = travelService.getById(id);
            travelService.complete(user, travel);
            return travelMapper.toDto(travel);
        } catch (AuthenticationFailureException | UnauthorizedOperationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (ForbiddenOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id,
                                        @RequestHeader HttpHeaders headers){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Travel travel = travelService.getById(id);
            travelService.delete(travel, user);
        } catch (AuthenticationFailureException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
