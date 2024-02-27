package com.telerikacademy.web.carpooling.controllers.mvc;

import com.telerikacademy.web.carpooling.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.carpooling.exceptions.DuplicateEmailExists;
import com.telerikacademy.web.carpooling.exceptions.DuplicateExistsException;
import com.telerikacademy.web.carpooling.exceptions.InvalidEmailException;
import com.telerikacademy.web.carpooling.helpers.AuthenticationHelper;
import com.telerikacademy.web.carpooling.helpers.UserMapper;
import com.telerikacademy.web.carpooling.models.LoginDto;
import com.telerikacademy.web.carpooling.models.RegisterDto;
import com.telerikacademy.web.carpooling.models.User;
import com.telerikacademy.web.carpooling.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;
    private final UserService userService;


    public AuthenticationController(AuthenticationHelper authenticationHelper, UserMapper userMapper, UserService userService) {
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model, @RequestParam(required = false) String error) {
        model.addAttribute("login", new LoginDto());
        model.addAttribute("loginError", error);
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute("login") LoginDto dto, BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        try {
            User user = authenticationHelper.verifyAuthentication(dto.getUsername(), dto.getPassword());
            session.setAttribute("currentUser", user);
            if (user.getRole().getName().equals("Admin")) {
                return "redirect:/admin";
            }
            return "redirect:/";
        } catch (AuthenticationFailureException e) {
//            redirectAttributes.addFlashAttribute("loginError", "Login was not possible due to wrong username or password or non-existent user.");
//            bindingResult.rejectValue("username", "login_error", "Invalid user");
//            System.out.println(bindingResult.getAllErrors());

            model.addAttribute("loginError", "Invalid username or password.");

            return "login";
        }
    }


    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.removeAttribute("currentUser");
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("register", new RegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("register") RegisterDto register, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (!register.getPassword().equals(register.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "password_error", "Password confirmation should match password");
            System.out.println(bindingResult.getAllErrors());
            return "register";
        }
        try {
            User user = userMapper.fromDto(register);
            userService.create(user);
            return "redirect:/auth/login";
        } catch (DuplicateExistsException e) {
            bindingResult.rejectValue("username", "username-error", e.getMessage());
            System.out.println(bindingResult.getAllErrors());
            return "register";
        } catch (InvalidEmailException e) {
            bindingResult.rejectValue("email", "email-error", e.getMessage());
            System.out.println(bindingResult.getAllErrors());
            return "register";
        } catch (DuplicateEmailExists e) {
            bindingResult.rejectValue("email", "email-error", e.getMessage());
            System.out.println(bindingResult.getAllErrors());
            return "register";
        }
    }


}
