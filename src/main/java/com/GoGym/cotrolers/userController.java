package com.GoGym.cotrolers;
import com.GoGym.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.GoGym.Module.user;
import com.GoGym.Service.userService;
import com.GoGym.Module.UserRegistrationDTO;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@ComponentScan({ "com.GoGym.*" })
public class userController {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private userService UserService;
    private final userService userService;
    @Autowired
    private userRepository UserRepository;

    public userController(userService userService) {
        this.userService = userService;
    }

    @GetMapping("/Login")
    public String Login(Model model) {
        model.addAttribute("message", "Logowanie");
        return "Login";
    }
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        model.addAttribute("message", "Register User");
        return "registerUser";
    }
    @PostMapping("/register")
    public String processRegister(@ModelAttribute UserRegistrationDTO userDTO) {
        userService.registerUser(userDTO);
        return "redirect:/Login";
    }
}


