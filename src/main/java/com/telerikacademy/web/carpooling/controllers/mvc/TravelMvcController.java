package com.telerikacademy.web.carpooling.controllers.mvc;

import com.telerikacademy.web.carpooling.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.carpooling.exceptions.ForbiddenOperationException;
import com.telerikacademy.web.carpooling.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.carpooling.helpers.AuthenticationHelper;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.services.TravelApplicationService;
import com.telerikacademy.web.carpooling.services.TravelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("travels")
public class TravelMvcController {

    private final TravelService travelService;
    private final TravelApplicationService travelApplicationService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public TravelMvcController(TravelService travelService, TravelApplicationService travelApplicationService, AuthenticationHelper authenticationHelper) {
        this.travelService = travelService;
        this.travelApplicationService = travelApplicationService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/search-travels")
    public String filterTravels(@ModelAttribute("filterOptions") FilterTravelDto filterTravelDto, Model model) {
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

    @GetMapping("/applications/{id}")
    public String travelApplications(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            if (user.equals(travelService.getById(id).getDriver())) {
                List<TravelApplication> applications = travelApplicationService.getByTravelId(id);
                model.addAttribute("applications", applications);
                return "travel-applications-view";
            }
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        return "redirect:/auth/login";
    }

    @PostMapping("/applications/approve/{id}")
    public String approveApplications(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            if (user.equals(travelService.getById(id).getDriver())) {
                TravelApplication travelApplication = travelApplicationService.getById(id);
                travelApplicationService.approve(user, travelApplication);
                return "travel-applications-view";
            }
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (ForbiddenOperationException | UnauthorizedOperationException e) {
            model.addAttribute("error-message", e.getMessage());
            return "redirect:/404-page";
        }
        return "redirect:/auth/login";
    }

    @PostMapping("/applications/decline/{id}")
    public String declineApplications(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            if (user.equals(travelService.getById(id).getDriver())) {
                TravelApplication travelApplication = travelApplicationService.getById(id);
                travelApplicationService.decline(user, travelApplication);
                return "travel-applications-view";
            }
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (ForbiddenOperationException | UnauthorizedOperationException e) {
            model.addAttribute("error-message", e.getMessage());
            return "redirect:/404-page";
        }
        return "redirect:/auth/login";
    }

}
