package com.telerikacademy.web.carpooling.controllers.mvc;

import com.telerikacademy.web.carpooling.exceptions.*;
import com.telerikacademy.web.carpooling.helpers.AuthenticationHelper;
import com.telerikacademy.web.carpooling.helpers.UserMapper;
import com.telerikacademy.web.carpooling.models.*;
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
    public String showLoginPage(Model model) {
        model.addAttribute("login", new LoginDto());
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute("login") LoginDto dto, BindingResult bindingResult,
                              HttpSession session, RedirectAttributes redirectAttributes, Model model) {
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
            redirectAttributes.addFlashAttribute("loginError", "Login was not possible due to wrong username or password or non-existent user.");
            return "redirect:/auth/login";
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
    public String handleRegister(@Valid @ModelAttribute("register") RegisterDto register, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (!register.getPassword().equals(register.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "password_error", "Password confirmation should match password");
            return "register";
        }
        try {
            User user = userMapper.fromDto(register);
            userService.create(user);
            return "redirect:/auth/login";
        } catch (DuplicateExistsException e) {
            bindingResult.rejectValue("username", "username-error", e.getMessage());
            return "register";
        } catch (InvalidEmailException e) {
            bindingResult.rejectValue("email", "email-error", e.getMessage());
            return "register";
        } catch (DuplicateEmailExists e) {
            bindingResult.rejectValue("email", "email-error", e.getMessage());
            return "register";
        } catch (DuplicatePhoneNumberExists e) {
            bindingResult.rejectValue("phoneNumber", "phoneNumber-error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/forgotten_password")
    public String showForgottenPasswordPage(Model model) {
        model.addAttribute("username", new UsernameDto());
        return "forgotten-password";
    }

    @PostMapping("/forgotten_password")
    public String handleForgottenPassword(@Valid @ModelAttribute("username") UsernameDto passwordDto,
                                          BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "forgotten-password";
        }
        try {
            User user = userService.get(passwordDto.getUsername());
            userService.sendForgottenPasswordEmail(user);
            redirectAttributes.addFlashAttribute("successMessage", "Email sent. Please check your inbox.");
            return "redirect:/auth/forgotten_password";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("userNotFound", e.getMessage());
            return "redirect:/auth/forgotten_password";
        }
    }

    @GetMapping("/recover_password/{id}")
    public String showPasswordRecoveryPage(Model model, @PathVariable String id) {
        model.addAttribute("username", new UsernameDto());
        return "recover-password";
    }

    @PostMapping("/recover_password/{id}")
    public String handleRecoveryPassword(@Valid @ModelAttribute("username") ForgottenPasswordDto passwordDto,
                                         BindingResult bindingResult, RedirectAttributes redirectAttributes, @PathVariable String id) {
        if (bindingResult.hasErrors()) {
            return "recover-password";
        }
        try {
            User user = userService.getByUI(id);
            if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
                bindingResult.rejectValue("confirmNewPassword", "error.passwordDto", "New password and confirm password do not match.");
            }
            user.setPassword(passwordDto.getNewPassword());
            userService.update(user);
            redirectAttributes.addFlashAttribute("successMessage", "Password successfully changed");
            return "redirect:/auth/recover_password/{id}";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("UINotFound", e.getMessage());
            return "redirect:/auth/forgotten_password/{id}";
        }
    }
}
