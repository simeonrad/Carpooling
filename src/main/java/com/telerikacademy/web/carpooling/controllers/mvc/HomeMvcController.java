package com.telerikacademy.web.carpooling.controllers.mvc;

import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.services.contracts.TravelApplicationService;
import com.telerikacademy.web.carpooling.services.contracts.TravelService;
import com.telerikacademy.web.carpooling.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private UserService userService;
    private TravelService travelService;
    private TravelApplicationService travelApplicationService;

    @Autowired
    public HomeMvcController(UserService userService, TravelService travelService, TravelApplicationService travelApplicationService) {
        this.userService = userService;
        this.travelService = travelService;
        this.travelApplicationService= travelApplicationService;
    }
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


    @GetMapping
    public String showHomePage(Model model) {
        try {
            int totalUsers = userService.getAllNotDeleted().size();
            int totalTravels = travelService.getAll().size();
            int totalApplications = travelApplicationService.getAll().size();
            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("totalTravels", totalTravels);
            model.addAttribute("totalApplications", totalApplications);

        }catch (EntityNotFoundException ignored){}
        return "index";
    }

    @GetMapping("/about-us")
    public String showAboutUsPage() {
        return "about-us-page";
    }

    @GetMapping("/top-10-passengers")
    public String showTop10PassengersPage(Model model) {
        List<User> top10Passengers = userService.getTop10Passengers();
        model.addAttribute("top10Passengers", top10Passengers);
        return "top-10-passengers-page";
    }

    @GetMapping("/top-10-organisers")
    public String showTop10OrganisersPage(Model model) {
        List<User> top10Organisers = userService.getTop10Organisers();
        model.addAttribute("top10Organisers", top10Organisers);
        return "top-10-organisers-page";
    }
}
