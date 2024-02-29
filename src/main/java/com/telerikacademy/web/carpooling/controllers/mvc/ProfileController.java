package com.telerikacademy.web.carpooling.controllers.mvc;

import com.telerikacademy.web.carpooling.exceptions.*;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.repositories.UserRepository;
import com.telerikacademy.web.carpooling.services.ImageStorageService;
import com.telerikacademy.web.carpooling.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;

    @Autowired
    public ProfileController(UserService userService, ImageStorageService imageStorageService, UserRepository userRepository) {
        this.userService = userService;
        this.imageStorageService = imageStorageService;
        this.userRepository = userRepository;
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

    @GetMapping()
    public String showProfile(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        boolean isAdmin = currentUser.getRole().getName().equals("Admin");
        model.addAttribute("isAdmin", isAdmin);

        UserProfileDto namesDto = new UserProfileDto();
        namesDto.setFirstName(currentUser.getFirstName());
        namesDto.setLastName(currentUser.getLastName());

        UserEmailUpdateDto emailDto = new UserEmailUpdateDto();
        emailDto.setEmail(currentUser.getEmail());

        UserPasswordUpdateDto passwordDto = new UserPasswordUpdateDto();

        UserPhoneNumberUpdateDto phoneDto = new UserPhoneNumberUpdateDto();
        phoneDto.setPhoneNumber(currentUser.getPhoneNumber());

        model.addAttribute("namesDto", namesDto);
        model.addAttribute("emailDto", emailDto);
        model.addAttribute("passwordDto", passwordDto);
        model.addAttribute("phoneDto", phoneDto);

        return "profile";
    }

    @PostMapping("/update-password")
    public String updatePassword(@Valid @ModelAttribute("passwordDto") UserPasswordUpdateDto passwordDto,
                                 BindingResult bindingResult, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser.getUsername() == null) {
            return "redirect:/login";
        }

        if (!currentUser.getPassword().equals(passwordDto.getCurrentPassword())) {
            bindingResult.rejectValue("currentPassword", "error.passwordDto", "Invalid current password.");
        }

        if (passwordDto.getNewPassword().isEmpty()) {
            bindingResult.rejectValue("newPassword", "error.passwordDto", "New password cannot be empty.");
        } else {
            if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
                bindingResult.rejectValue("confirmNewPassword", "error.passwordDto", "New password and confirm password do not match.");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("namesDto", new UserProfileDto());
            model.addAttribute("emailDto", new UserEmailUpdateDto());
            model.addAttribute("passwordDto", passwordDto);
            return "profile";
        }

        currentUser.setPassword(passwordDto.getNewPassword());
        try {
            userService.update(currentUser);
            redirectAttributes.addFlashAttribute("passwordUpdateSuccess", "Password updated successfully.");
            model.addAttribute("successMessage", "Password updated successfully.");
        } catch (InvalidPasswordException e) {
            redirectAttributes.addFlashAttribute("invalidPassword", "Password does not meet the requirements! " +
                    "It should contain capital letter, digit and special symbol (+, -, *, &, ^, …)");
        }
        return "redirect:/profile";
    }


    @PostMapping("/update-names")
    public String updateNames(@Valid @ModelAttribute("namesDto") UserProfileDto namesDto,
                              BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("emailDto", new UserEmailUpdateDto());
            model.addAttribute("passwordDto", new UserPasswordUpdateDto());
            model.addAttribute("namesDto", namesDto);
            return "profile";
        }

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser.getUsername() == null) {
            return "redirect:/login";
        }

        if (currentUser == null) {
            model.addAttribute("errorMessage", "User not found.");
            return "profile";
        }

        currentUser.setFirstName(namesDto.getFirstName());
        currentUser.setLastName(namesDto.getLastName());
        userService.update(currentUser);

        model.addAttribute("successMessage", "Names updated successfully.");
        redirectAttributes.addFlashAttribute("namesUpdateSuccess", "Names updated successfully.");
        return "redirect:/profile";
    }


    @PostMapping("/update-email")
    public String updateEmail(@Valid @ModelAttribute("emailDto") UserEmailUpdateDto emailDto,
                              BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("namesDto", new UserProfileDto());
            model.addAttribute("passwordDto", new UserPasswordUpdateDto());
            model.addAttribute("emailDto", emailDto);
            return "profile";
        }

        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            model.addAttribute("errorMessage", "User not found.");
            return "profile";
        }

        currentUser.setEmail(emailDto.getEmail());
        try {
            userRepository.getByEmail(currentUser.getEmail());
        } catch (EntityNotFoundException e) {
            try {
                userService.update(currentUser);
            } catch (InvalidEmailException iee) {
                redirectAttributes.addFlashAttribute("emailUpdateDenied", "The provided email is not valid.");
                return "redirect:/profile";
            }
            model.addAttribute("successMessage", "Email updated successfully.");
            redirectAttributes.addFlashAttribute("emailUpdateSuccess", "Email updated successfully.");
            return "redirect:/profile";
        }
        redirectAttributes.addFlashAttribute("emailUpdateDenied", "This Email is already in use.");
        return "redirect:/profile";
    }

    @GetMapping("/delete-confirm")
    public String showDeleteConfirmation(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser.getUsername() == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("firstName", currentUser.getFirstName());
        model.addAttribute("lastName", currentUser.getLastName());
        return "delete-confirm";
    }

    @PostMapping("/delete")
    public String deleteProfile(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser.getUsername() != null) {
            userService.delete(currentUser);
            session.invalidate();
            return "redirect:/auth/login";
        }

        return "redirect:/auth/login";
    }

    @PostMapping("/upload-image")
    public String uploadProfileImage(@ModelAttribute ProfileImageForm form, HttpSession session, Model model,
                                     RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/auth/login";
        }
        try {
            User currentUser = (User) session.getAttribute("currentUser");
            String imageUrl = imageStorageService.saveImage(form.getImage());
            currentUser.setPhotoUrl(imageUrl);
            userService.addProfilePhoto(imageUrl, currentUser);
            model.addAttribute("message", "Profile image updated successfully.");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to upload image.");
        }
        redirectAttributes.addFlashAttribute("photoSuccess", "Profile photo successfully updated!");
        return "redirect:/profile";
    }

    @PostMapping("/update-telephone")
    public String updateTelephoneNumber(@Valid @ModelAttribute("phoneDto") UserPhoneNumberUpdateDto phoneDto, HttpSession session,
                                        Model model, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("namesDto", new UserProfileDto());
            model.addAttribute("passwordDto", new UserPasswordUpdateDto());
            model.addAttribute("emailDto", new UserEmailUpdateDto());
            model.addAttribute("phoneDto", phoneDto);
            return "profile";
        }

        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            model.addAttribute("errorMessage", "User not found.");
            return "profile";
        }

        currentUser.setPhoneNumber(phoneDto.getPhoneNumber());
        try {
            userService.update(currentUser);
        } catch (InvalidPhoneNumberException iee) {
            redirectAttributes.addFlashAttribute("phoneNumberUpdateDenied", "The provided phone number is not valid.");
            return "redirect:/profile";
        } catch (DuplicateExistsException e) {
            redirectAttributes.addFlashAttribute("phoneNumberUpdateDenied", "The provided phone number is already used.");
            return "redirect:/profile";
        }
        model.addAttribute("successMessage", "Phone number updated successfully.");
        redirectAttributes.addFlashAttribute("phoneNumberUpdateSuccess", "Phone number updated successfully.");
        return "redirect:/profile";
    }
}

