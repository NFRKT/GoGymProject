package com.GoGym.controller;

import com.GoGym.module.User;
import com.GoGym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AllUserController {

    private final UserRepository userRepository;

    @Autowired
    public AllUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/all-user-list")
    public String allUserList(Model model, Authentication authentication) {
        // Pobieramy wszystkich użytkowników oprócz adminów
        List<User> users = userRepository.findAll()
                .stream()
                .filter(u -> u.getUserType() != null && !u.getUserType().name().equals("ADMIN"))
                .collect(Collectors.toList());
        model.addAttribute("users", users);

        // Przekazujemy informację o roli zalogowanego użytkownika, jeżeli potrzebna
        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("CLIENT"));
        model.addAttribute("isUser", isUser);
        return "all-user-list";
    }
}
