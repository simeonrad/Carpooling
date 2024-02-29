package com.telerikacademy.web.carpooling.controllers.mvc;

import com.telerikacademy.web.carpooling.exceptions.DuplicateEmailExists;
import com.telerikacademy.web.carpooling.exceptions.DuplicatePhoneNumberExists;
import com.telerikacademy.web.carpooling.exceptions.InvalidPhoneNumberException;
import com.telerikacademy.web.carpooling.models.FilterUserDto;
import com.telerikacademy.web.carpooling.models.FilterUserOptions;
import com.telerikacademy.web.carpooling.models.Role;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.repositories.RoleRepository;
import com.telerikacademy.web.carpooling.services.UserBlockService;
import com.telerikacademy.web.carpooling.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("users")
public class UserMvcController {

    private final UserService userService;
    private final RoleRepository roleRepository;

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
    public UserMvcController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping()
    public String filterUsers(@ModelAttribute("filterOptions") FilterUserDto filterUserDto, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            FilterUserOptions filterUserOptions = new FilterUserOptions(filterUserDto.getUsername(), filterUserDto.getEmail(), filterUserDto.getPhoneNumber(), filterUserDto.getSortBy(), filterUserDto.getSortOrder());
            List<User> users = userService.get(filterUserOptions, currentUser);
            model.addAttribute("filterOptions", filterUserDto);
            model.addAttribute("allRoles", roleRepository.getAll());
            model.addAttribute("users", users);
            return "searchUserView";
        } else {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/toggle-block/{id}")
    public String toggleBlockUser(@PathVariable("id") int userId, HttpSession session, Model model, HttpServletRequest request) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null && currentUser.isAdmin()) {
            User userToToggle = userService.get(userId);
            try {
                if (userToToggle.isBlocked()) {
                    userService.unblockUser(userToToggle.getUsername(), currentUser);
                } else {
                    userService.blockUser(userToToggle.getUsername(), currentUser);
                }
            } catch (Exception e) {
                model.addAttribute("error", "Error toggling user block status.");
            }
            String refererUrl = request.getHeader("Referer");
            return "redirect:" + refererUrl;
        } else {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/update-role/{id}")
    public String updateUserRole(@PathVariable int id, @RequestParam("role") int roleId, RedirectAttributes redirectAttributes,
                                 Model model, @ModelAttribute("filterOptions") FilterUserDto filterUserDto) {
        FilterUserOptions filterUserOptions = new FilterUserOptions(filterUserDto.getUsername(), filterUserDto.getEmail(), filterUserDto.getPhoneNumber(), filterUserDto.getSortBy(), filterUserDto.getSortOrder());
        List<User> users = userService.get(filterUserOptions);
        model.addAttribute("users", users);
        model.addAttribute("filterOptions", filterUserOptions);
        try {
            User user = userService.get(id);
            Role newRole = roleRepository.findById(roleId);
            user.setRole(newRole);
            userService.update(user);
        } catch (DuplicateEmailExists | DuplicatePhoneNumberExists | InvalidPhoneNumberException ignored) {
        }
        return "redirect:/users";
    }

}
