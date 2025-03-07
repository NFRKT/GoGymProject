package com.GoGym.controller;
import com.GoGym.module.User;
import com.GoGym.repository.UserRepository;
import com.GoGym.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.GoGym.dto.UserRegistrationDTO;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class UserController {
    @Autowired
    @Lazy
    private final UserService userService;

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @GetMapping("/login")
    public String Login(Model model) {
        model.addAttribute("message", "Logowanie");
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("User", new UserRegistrationDTO());
        model.addAttribute("message", "Register User");
        return "register-user";
    }
    @PostMapping("/register")
    public String processRegister(@ModelAttribute UserRegistrationDTO userDTO, Model model) {
        try {
            userService.registerUser(userDTO);
            return "redirect:/login";
        } catch (IllegalStateException e) {
            model.addAttribute("emailErrorMessage", e.getMessage());
            model.addAttribute("User", userDTO);
            return "register-user";
        }
    }
    @GetMapping("/edit-user")
    public String showProfileForm(Model model, Principal principal) {
        String userEmail = principal.getName();
        User user = userService.findByEmail(userEmail);
        model.addAttribute("user", user);
        return "edit-user";
    }

    @PostMapping("/edit-user")
    public String updateProfile(
            @ModelAttribute("user") User userForm,
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            Principal principal,
            Model model) {

        String userEmail = principal.getName();
        User user = userService.findByEmail(userEmail);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("error", "Aktualne hasło jest niepoprawne.");
            return "edit-profile";
        }

        user.setFirstName(userForm.getFirstName());
        user.setLastName(userForm.getLastName());
        user.setEmail(userForm.getEmail());
        user.setBirthDate(userForm.getBirthDate());
        user.setGender(userForm.getGender());

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (newPassword.trim().length() < 6) {
                model.addAttribute("error", "Hasło musi zawierać co najmniej 6 znaków.");
                return "edit-profile";
            }
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "Nowe hasło i potwierdzenie nie są zgodne.");
                return "edit-profile";
            }
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedNewPassword);
        }

        userService.saveUser(user);
        return "redirect:/home";
    }

}


