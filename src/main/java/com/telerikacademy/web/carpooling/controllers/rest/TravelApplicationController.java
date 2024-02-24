package com.telerikacademy.web.carpooling.controllers.rest;

import com.telerikacademy.web.carpooling.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.exceptions.ForbiddenOperationException;
import com.telerikacademy.web.carpooling.helpers.AuthenticationHelper;
import com.telerikacademy.web.carpooling.helpers.TravelApplicationMapper;
import com.telerikacademy.web.carpooling.models.TravelApplication;
import com.telerikacademy.web.carpooling.models.TravelApplicationDto;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.services.TravelApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
public class TravelApplicationController {

    private final TravelApplicationService applicationService;
    private final TravelApplicationMapper travelApplicationMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public TravelApplicationController(TravelApplicationService applicationService,
                                       TravelApplicationMapper travelApplicationMapper,
                                       AuthenticationHelper authenticationHelper) {
        this.applicationService = applicationService;
        this.travelApplicationMapper = travelApplicationMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping()
    public List<TravelApplicationDto> getAll() {
        try {
            return applicationService.getAll().stream()
                    .map(travelApplicationMapper::toDto)
                    .collect(Collectors.toList());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public TravelApplicationDto getById(@PathVariable int id) {
        try {
            return travelApplicationMapper.toDto(applicationService.getById(id));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping()
    public TravelApplicationDto create(@RequestBody TravelApplicationDto travelApplicationDto,
                                       @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            TravelApplication travelApplication = travelApplicationMapper.fromDto(travelApplicationDto, user);
            applicationService.create(travelApplication);
            return travelApplicationMapper.toDto(travelApplication);
        } catch (AuthenticationFailureException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (ForbiddenOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/cancel/{id}")
    public TravelApplicationDto cancel(@PathVariable int id,
                                       @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            TravelApplication application = applicationService.getById(id);
            applicationService.cancel(user, application);
            return travelApplicationMapper.toDto(application);
        } catch (AuthenticationFailureException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/approve/{id}")
    public TravelApplicationDto approve(@PathVariable int id,
                                        @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            TravelApplication application = applicationService.getById(id);
            applicationService.approve(user, application);
            return travelApplicationMapper.toDto(application);
        } catch (AuthenticationFailureException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/decline/{id}")
    public TravelApplicationDto decline(@PathVariable int id,
                                        @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            TravelApplication application = applicationService.getById(id);
            applicationService.decline(user, application);
            return travelApplicationMapper.toDto(application);
        } catch (AuthenticationFailureException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

}
