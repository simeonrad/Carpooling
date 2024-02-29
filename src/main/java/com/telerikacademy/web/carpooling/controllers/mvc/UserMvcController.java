package com.telerikacademy.web.carpooling.controllers.mvc;

import com.telerikacademy.web.carpooling.models.FilterUserDto;
import com.telerikacademy.web.carpooling.models.FilterUserOptions;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.services.UserBlockService;
import com.telerikacademy.web.carpooling.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("users")
public class UserMvcController {

    private final UserService userService;
    private final UserBlockService userBlockService;

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
    public UserMvcController(UserService userService, UserBlockService userBlockService) {
        this.userService = userService;
        this.userBlockService = userBlockService;
    }

    @GetMapping()
    public String filterUsers(@ModelAttribute("filterOptions") FilterUserDto filterUserDto, HttpSession session, Model model){
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            FilterUserOptions filterUserOptions = new FilterUserOptions(filterUserDto.getUsername(), filterUserDto.getEmail(), filterUserDto.getPhoneNumber(), filterUserDto.getSortBy(), filterUserDto.getSortOrder());
            List<User> users = userService.get(filterUserOptions, currentUser);
            model.addAttribute("filterOptions", filterUserOptions);
            model.addAttribute("users", users);
            return "searchUserView";
        } else {
            return "searchUserView";
        }
    }
}
