package com.GoGym.controller;
import com.GoGym.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.GoGym.dto.UserRegistrationDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class UserController {
    @Autowired
    @Lazy
    private final UserService userService;

    @GetMapping("/login")
    public String Login(Model model) {
        model.addAttribute("message", "Logowanie");
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("User", new UserRegistrationDTO());
        model.addAttribute("message", "Register User");
        return "registerUser";
    }
    @PostMapping("/register")
    public String processRegister(@ModelAttribute UserRegistrationDTO userDTO) {
        userService.registerUser(userDTO);
        return "redirect:/login";
    }
}


