package com.GoGym.controller;
import com.GoGym.repository.UserRepository;
import com.GoGym.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.GoGym.dto.UserRegistrationDTO;

import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class UserController {
    @Autowired
    @Lazy
    private final UserService userService;

    @Autowired
    private final UserRepository userRepository;

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
            // Dodajemy komunikat błędu do modelu
            model.addAttribute("emailErrorMessage", e.getMessage());
            // Ustawiamy ponownie formularz (jeśli chcesz, aby użytkownik nie musiał wpisywać wszystkiego od nowa)
            model.addAttribute("User", userDTO);
            return "register-user";
        }
    }

}


