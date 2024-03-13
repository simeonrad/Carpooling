package com.telerikacademy.web.carpooling.controllers.mvc;

import com.telerikacademy.web.carpooling.exceptions.DuplicateEmailExists;
import com.telerikacademy.web.carpooling.exceptions.DuplicatePhoneNumberExists;
import com.telerikacademy.web.carpooling.exceptions.EntityNotFoundException;
import com.telerikacademy.web.carpooling.exceptions.InvalidPhoneNumberException;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.models.dtos.FilterFeedbackOptionsDto;
import com.telerikacademy.web.carpooling.models.dtos.FilterUserDto;
import com.telerikacademy.web.carpooling.repositories.contracts.RoleRepository;
import com.telerikacademy.web.carpooling.services.contracts.FeedbackService;
import com.telerikacademy.web.carpooling.services.contracts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final FeedbackService feedbackService;

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
    public UserMvcController(UserService userService, RoleRepository roleRepository, FeedbackService feedbackService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.feedbackService = feedbackService;
    }

    @GetMapping()
    public String filterUsers(@ModelAttribute("filterOptions") FilterUserDto filterUserDto, HttpSession session, Model model,
                              @RequestParam(defaultValue = "0", name = "userPage") int userPage,
                              @RequestParam(defaultValue = "5", name = "userSize") int userSize) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            FilterUserOptions filterUserOptions = new FilterUserOptions(filterUserDto.getUsername(), filterUserDto.getEmail(), filterUserDto.getPhoneNumber(), filterUserDto.getSortBy(), filterUserDto.getSortOrder());
            Pageable travelsPageable = PageRequest.of(userPage, userSize);
            Page<User> users = userService.get(filterUserOptions, currentUser, travelsPageable);
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
                model.addAttribute("status", "Error toggling user block status.");
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

    @GetMapping("/{id}")
    public String showUserProfile(@PathVariable int id, Model model, HttpSession session,
        @RequestParam(defaultValue = "0", name = "feedbackPage") int feedbackPage,
        @RequestParam(defaultValue = "5", name = "feedbackSize") int feedbackSize,
        @ModelAttribute ("feedbackFilterOptions") FilterFeedbackOptionsDto filterFeedbackOptionsDto) {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                return "redirect:/auth/login";
            }
            filterFeedbackOptionsDto.setRecipient(currentUser.getUsername());
            Pageable feedbacksPageable = PageRequest.of(feedbackPage, feedbackSize);
            User user = userService.get(id);
            String username = user.getFirstName() + " " + user.getLastName();
            filterFeedbackOptionsDto.setRecipient(user.getUsername());
            Page<Feedback> userFeedbacksReceived = feedbackService.getMyReceivedFeedbacks(
                    new FilterFeedbackOptions(filterFeedbackOptionsDto.getAuthor(),
                            filterFeedbackOptionsDto.getRecipient(),
                            filterFeedbackOptionsDto.getComment(),
                            filterFeedbackOptionsDto.getRating(),
                            filterFeedbackOptionsDto.getSortBy(),
                            filterFeedbackOptionsDto.getSortOrder()),
                    feedbacksPageable);
            model.addAttribute("feedbacks", userFeedbacksReceived);
            model.addAttribute("profileUser", user);
            model.addAttribute("username", username);
            return "user-dashboard";
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("username") String username, Model model) {
        try {
            userService.verifyUser(username);
            model.addAttribute("status", "Email successfully verified");
            return "verification-page";
        } catch (EntityNotFoundException e) {
            model.addAttribute("status", e.getMessage());
            return "verification-page";
        }
    }

}
