package com.GoGym.controller;

import com.GoGym.module.Request;
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

        // Pobierz listę obecnych trenerów klienta
        List<TrainerClient> clientTrainers = trainerClientService.getClientTrainers(clientId);
        List<Long> currentTrainerIds = clientTrainers.stream()
                .map(tc -> tc.getTrainer().getIdUser())
                .toList();

        // Lista trenerów z oczekującymi zapytaniami
        List<Long> pendingRequestTrainerIds = requestService.getPendingRequestsByClient(clientId).stream()
                .map(request -> request.getTrainer().getIdUser())
                .toList();

        // Pobierz listę wszystkich dostępnych trenerów i odfiltruj obecnych
        List<User> availableTrainers = trainerClientService.getAllTrainers().stream()
                .filter(trainer -> !currentTrainerIds.contains(trainer.getIdUser()))
                .toList();

        model.addAttribute("clientTrainers", clientTrainers);
        model.addAttribute("trainers", availableTrainers);
        model.addAttribute("clientId", clientId);
        model.addAttribute("pendingTrainerIds", pendingRequestTrainerIds); // ID trenerów z oczekującymi zapytaniami
        model.addAttribute("currentTrainerIds", currentTrainerIds); // ID obecnych trenerów

        // Dodanie listy wysłanych zapytań
        List<Request> requests = requestService.getRequestsByClientId(clientId);
        model.addAttribute("requests", requests);

        List<TrainerClient> trainerClients = trainerClientService.getClientTrainers(clientId);
        model.addAttribute("clientTrainers", clientTrainers);

        return "client-panel"; // Załaduj widok Thymeleaf
    }





    @GetMapping("/trainer-panel")
    public String trainerPanel(Authentication authentication, Model model) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInTrainer = userDetails.getUser(); // Pobranie użytkownika z CustomUserDetails
        Long trainerId = loggedInTrainer.getIdUser(); // Pobranie ID trenera

        List<TrainerClient> trainerClients = trainerClientService.getTrainerClients(trainerId);
        model.addAttribute("trainerClients", trainerClients);
        model.addAttribute("requests", requestService.getRequestsForTrainer(trainerId));
        return "trainer-panel";
    }

    @PostMapping("/rejectTrainer")
    public String rejectTrainer(@RequestParam Long trainerId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();

        // Znajdź relację klient-trener
        TrainerClient trainerClient = trainerClientRepository.findByTrainer_IdUserAndClient_IdUser(trainerId, loggedInUser.getIdUser())
                .orElseThrow(() -> new IllegalArgumentException("Nie masz przypisanego tego trenera"));

        // Usuń relację
        trainerClientRepository.delete(trainerClient);

        return "redirect:/trainer-clients/client-panel";
    }

    @PostMapping("/rejectClient")
    public String rejectClient(@RequestParam Long clientId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();

        // Znajdź relację klient-trener
        TrainerClient trainerClient = trainerClientRepository.findByTrainer_IdUserAndClient_IdUser(loggedInUser.getIdUser(), clientId)
                .orElseThrow(() -> new IllegalArgumentException("Nie masz przypisanego tego klienta"));

        // Usuń relację
        trainerClientRepository.delete(trainerClient);

        return "redirect:/trainer-clients/trainer-panel";
    }







}
