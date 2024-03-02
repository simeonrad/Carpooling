package com.telerikacademy.web.carpooling.controllers.mvc;

import com.telerikacademy.web.carpooling.helpers.AuthenticationHelper;
import com.telerikacademy.web.carpooling.helpers.TravelMapper;
import com.telerikacademy.web.carpooling.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.carpooling.exceptions.ForbiddenOperationException;
import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.services.TravelApplicationService;
import com.telerikacademy.web.carpooling.services.TravelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.util.List;
@Controller
@RequestMapping("travels")
public class TravelMvcController {

    private final TravelService travelService;
    private final AuthenticationHelper authenticationHelper;
    private final TravelMapper travelMapper;
    private final TravelApplicationService travelApplicationService;

    @ModelAttribute("isAdmin")
    public boolean populateIsAdmin(HttpSession session) {
        boolean isAdmin = false;
        if (populateIsAuthenticated(session)) {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser.getRole().getName().equals("Admin")) {
                isAdmin = true;
            }
        }
        return isAdmin;
    }
    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }


    @Autowired
    public TravelMvcController(TravelService travelService, TravelApplicationService travelApplicationService, AuthenticationHelper authenticationHelper, TravelMapper travelMapper) {
        this.travelService = travelService;
        this.authenticationHelper = authenticationHelper;
        this.travelApplicationService = travelApplicationService;
        this.travelMapper = travelMapper;
    }


    @GetMapping("/search-travels")
    public String filterUsers(@ModelAttribute("filterOptions") FilterTravelDto filterTravelDto, Model model,
                              @RequestParam(defaultValue = "0", name = "travelPage") int travelPage,
                              @RequestParam(defaultValue = "5", name = "travelSize") int travelSize){
            FilterTravelOptions filterTravelOptions = new FilterTravelOptions(filterTravelDto.getAuthor(),
                    filterTravelDto.getStartPoint(), filterTravelDto.getEndPoint(),
                    filterTravelDto.getDepartureTime(), filterTravelDto.getFreeSpots(),
                    filterTravelDto.getTravelStatus(), filterTravelDto.getSortBy(),
                    filterTravelDto.getSortOrder());
        Pageable travelsPageable = PageRequest.of(travelPage, travelSize);
        Page<Travel> travels = travelService.getMyTravels(filterTravelOptions, travelsPageable);
            model.addAttribute("filterOptions", filterTravelDto);
            model.addAttribute("travels", travels);
            return "searchTravelView";
    }

    @GetMapping("/create")
    public String showCreateTravelForm(Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetUser(session);
            model.addAttribute("createTravel", new TravelDto());
            return "createTravel";
        } catch (AuthenticationFailureException e){
            return "redirect:/auth/login";
        }

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


    @GetMapping("/applications/{id}")
    public String travelApplications(@PathVariable int id, Model model, HttpSession session){
    try {
        User user = authenticationHelper.tryGetUser(session);
        if (user.equals(travelService.getById(id).getDriver())) {
            List<TravelApplication> applications = travelApplicationService.getByTravelId(id);
            model.addAttribute("applications", applications);
            return "travel-applications-view";
        }
    } catch (AuthenticationFailureException e){
        return "redirect:/auth/login";
    }
        return "redirect:/auth/login";
    }
    @PostMapping("/applications/approve/{id}")
    public String approveApplications(@PathVariable int id,Model model, HttpSession session){
    try {
        User user = authenticationHelper.tryGetUser(session);
        if (user.equals(travelService.getById(id).getDriver())) {
        TravelApplication travelApplication = travelApplicationService.getById(id);
        travelApplicationService.approve(user, travelApplication);
            return "travel-applications-view";
        }
    } catch (AuthenticationFailureException e){
        return "redirect:/auth/login";
    }
    catch (ForbiddenOperationException | UnauthorizedOperationException e){
        model.addAttribute("error-message",e.getMessage());
        return "redirect:/404-page";
    }
        return "redirect:/auth/login";
    }
    @PostMapping("/delete/{id}")
    public String deleteTravel(@PathVariable int id,Model model, HttpSession session){
        try {
            User user = authenticationHelper.tryGetUser(session);
                Travel travel = travelService.getById(id);
                travelService.delete(travel, user);
                return "redirect:/travels/search-travels";
        } catch (AuthenticationFailureException e){
            return "redirect:/auth/login";
        }
        catch (ForbiddenOperationException | UnauthorizedOperationException e){
            model.addAttribute("error-message",e.getMessage());
            return "redirect:/404-page";
        }
    }
    @PostMapping("/applications/decline/{id}")
    public String declineApplications(@PathVariable int id,Model model, HttpSession session){
        try {
            User user = authenticationHelper.tryGetUser(session);
            if (user.equals(travelService.getById(id).getDriver())) {
                TravelApplication travelApplication = travelApplicationService.getById(id);
                travelApplicationService.decline(user, travelApplication);
                return "travel-applications-view";
            }
        } catch (AuthenticationFailureException e){
            return "redirect:/auth/login";
        }
        catch (ForbiddenOperationException | UnauthorizedOperationException e){
            model.addAttribute("error-message",e.getMessage());
            return "redirect:/404-page";
        }
        return "redirect:/auth/login";
    }

}
