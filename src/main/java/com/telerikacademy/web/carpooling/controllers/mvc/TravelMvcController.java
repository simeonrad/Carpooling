package com.telerikacademy.web.carpooling.controllers.mvc;

import com.telerikacademy.web.carpooling.helpers.AuthenticationHelper;
import com.telerikacademy.web.carpooling.helpers.TravelMapper;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.models.enums.ApplicationStatus;
import com.telerikacademy.web.carpooling.repositories.StatusRepository;
import com.telerikacademy.web.carpooling.services.TravelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping("travels")
public class TravelMvcController {

    private final TravelService travelService;
    private final AuthenticationHelper authenticationHelper;
    private final TravelMapper travelMapper;
    private final StatusRepository statusRepository;
    @Autowired
    public TravelMvcController(TravelService travelService, AuthenticationHelper authenticationHelper, TravelMapper travelMapper, StatusRepository statusRepository) {
        this.travelService = travelService;
        this.authenticationHelper = authenticationHelper;
        this.travelMapper = travelMapper;
        this.statusRepository = statusRepository;
    }

    @GetMapping("/search-travels")
    public String filterUsers(@ModelAttribute("filterOptions") FilterTravelDto filterTravelDto, Model model){
            FilterTravelOptions filterTravelOptions = new FilterTravelOptions(filterTravelDto.getAuthor(),
                    filterTravelDto.getStartPoint(), filterTravelDto.getEndPoint(),
                    filterTravelDto.getDepartureTime(), filterTravelDto.getFreeSpots(),
                    filterTravelDto.getTravelStatus(), filterTravelDto.getSortBy(),
                    filterTravelDto.getSortOrder());
            List<Travel> travels = travelService.get(filterTravelOptions);
            model.addAttribute("filterOptions", filterTravelDto);
            model.addAttribute("travels", travels);
            return "searchTravelView";
    }

    @GetMapping("/create")
    public String showCreateTravelForm(Model model) {
        model.addAttribute("createTravel", new TravelDto());
        return "createTravel";
    }

    @PostMapping("/create")
    public String handleCreateTravel(@ModelAttribute("createTravel") TravelDto travelDto, BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "createTravel";
        }
        try {
            User user = authenticationHelper.tryGetUser(session);

            Travel travel = travelMapper.fromDto(travelDto);
            travel.setDriver(user);
            travelService.create(travel, user);

            return "redirect:/travels/search-travels";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating travel: " + e.getMessage());
            return "createTravel";
        }
    }

    @GetMapping("/update/{id}")
    public String showUpdateTravelForm(@PathVariable("id") int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Travel travel = travelService.getById(id);

            if (!travel.getDriver().equals(user)) {
                model.addAttribute("errorMessage", "Unauthorized access to update travel");
                return "errorPage";
            }

            TravelDto travelDto = travelMapper.toDto(travel);
            travelDto.setId(travel.getId());
            model.addAttribute("updateTravel", travelDto);
            return "updateTravel";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error finding travel: " + e.getMessage());
            return "errorPage";
        }
    }


    @PostMapping("/update/{id}")
    public String handleUpdateTravel(@PathVariable("id") int id, @ModelAttribute("updateTravel") TravelDto travelDto, BindingResult bindingResult, HttpSession session, Model model) {
        System.out.println("Received ID for update: " + id);
        travelDto.setId(id);
        if (bindingResult.hasErrors()) {
            return "updateTravel";
        }
        try {
            User user = authenticationHelper.tryGetUser(session);
            Travel existingTravel = travelService.getById(id);

            if (!existingTravel.getDriver().equals(user)) {
                model.addAttribute("errorMessage", "Unauthorized attempt to update travel");
                return "errorPage";
            }

            existingTravel.setFreeSpots(travelDto.getFreeSpots());
            travelService.update(existingTravel, user);

            return "redirect:/travels/search-travels";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating travel: " + e.getMessage());
            return "updateTravel";
        }
    }




}
