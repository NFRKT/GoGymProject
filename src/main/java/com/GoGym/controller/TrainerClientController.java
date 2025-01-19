package com.GoGym.controller;

import com.GoGym.module.TrainerClient;
import com.GoGym.module.User;
import com.GoGym.repository.RequestRepository;
import com.GoGym.repository.TrainerClientRepository;
import com.GoGym.repository.UserRepository;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.service.RequestService;
import com.GoGym.service.TrainerClientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/trainer-clients")
public class TrainerClientController {
    private TrainerClientService trainerClientService;
    private RequestService requestService;
    private TrainerClientRepository trainerClientRepository;
    private UserRepository userRepository;

    @Autowired
    public TrainerClientController(TrainerClientService trainerClientService, RequestService requestService, UserRepository userRepository, TrainerClientRepository trainerClientRepository) {
        this.trainerClientService = trainerClientService;
        this.requestService = requestService;
        this.userRepository = userRepository;
        this.trainerClientRepository = trainerClientRepository;
    }

    @GetMapping("/trainer/{trainerId}")
    public List<TrainerClient> getTrainerClients(@PathVariable Long trainerId) {
        return trainerClientService.getTrainerClients(trainerId);
    }

    @GetMapping("/client/{clientId}")
    public List<TrainerClient> getClientTrainers(@PathVariable Long clientId) {
        return trainerClientService.getClientTrainers(clientId);
    }


    @GetMapping("/client-panel")
    public String clientPanel(Model model, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();
        Long clientId = loggedInUser.getIdUser();

        List<TrainerClient> clientTrainers = trainerClientService.getClientTrainers(clientId);
        String trainerName = clientTrainers.isEmpty()
                ? "Brak przypisanego trenera"
                : clientTrainers.get(0).getTrainer().getFirstName();

        // Lista dostępnych trenerów
        List<User> availableTrainers = trainerClientService.getAllTrainers();

        // Ustaw dane w modelu
        model.addAttribute("trainerName", trainerName);
        model.addAttribute("trainers", availableTrainers);
        model.addAttribute("clientId", clientId);

        return "client-panel"; // Załaduj widok Thymeleaf
    }




    @GetMapping("/trainer-panel")
    public String trainerPanel(Authentication authentication, Model model) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInTrainer = userDetails.getUser(); // Pobranie użytkownika z CustomUserDetails
        Long trainerId = loggedInTrainer.getIdUser(); // Pobranie ID trenera

        model.addAttribute("requests", requestService.getRequestsForTrainer(trainerId));
        return "trainer-panel";
    }



}
