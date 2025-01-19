package com.GoGym.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.GoGym.dto.UserRegistrationDTO;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@ComponentScan({ "com.GoGym.*" })
public class UserController {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private com.GoGym.service.UserService UserService;
    private final com.GoGym.service.UserService userService;
    @Autowired
    private com.GoGym.repository.UserRepository UserRepository;

    public UserController(com.GoGym.service.UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/Login")
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


