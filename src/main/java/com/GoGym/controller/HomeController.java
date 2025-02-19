package com.GoGym.controller;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@ComponentScan({ "com.GoGym.*" })
public class HomeController {

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    @Lazy
    private com.GoGym.service.UserService UserService;
    @Autowired
    private com.GoGym.repository.UserRepository UserRepository;
    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // Login zalogowanego użytkownika
        Long idUser = UserRepository.findByEmail(username).get().getIdUser();

        if (auth != null && auth.isAuthenticated()) {
            // Zakładamy, że role to "ROLE_USER" i "ROLE_TRAINER"
            boolean isUser = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("CLIENT"));
            boolean isTrainer = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("TRAINER"));
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));

            model.addAttribute("isUser", isUser);
            model.addAttribute("isTrainer", isTrainer);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("idUser", idUser);
        }
        model.addAttribute("username", username);
        return "home"; // Zwraca widok home.html
    }
}

