package com.GoGym.controller;

import com.GoGym.module.*;
import com.GoGym.repository.*;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.service.RequestService;
import com.GoGym.service.TrainerClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TrainerClientController {
    private TrainerClientService trainerClientService;
    private RequestService requestService;
    private TrainerClientRepository trainerClientRepository;

    @Autowired
    public TrainerClientController(TrainerClientService trainerClientService, RequestService requestService, TrainerClientRepository trainerClientRepository) {
        this.trainerClientService = trainerClientService;
        this.requestService = requestService;
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
    @PostMapping("/rejectClient")
    public ResponseEntity<Map<String, String>> rejectClient(@RequestParam Long clientId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();

        TrainerClient trainerClient = trainerClientRepository.findByTrainer_IdUserAndClient_IdUser(loggedInUser.getIdUser(), clientId)
                .orElseThrow(() -> new IllegalArgumentException("Nie masz przypisanego tego klienta"));

        trainerClientRepository.delete(trainerClient);

        // Zwracamy JSON zamiast pustej odpowiedzi
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Klient został usunięty");

        return ResponseEntity.ok(response);
    }


    @PostMapping("/rejectTrainer")
    public ResponseEntity<Map<String, String>> rejectTrainer(@RequestParam Long trainerId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();

        TrainerClient trainerClient = trainerClientRepository.findByTrainer_IdUserAndClient_IdUser(trainerId, loggedInUser.getIdUser())
                .orElseThrow(() -> new IllegalArgumentException("Nie masz przypisanego tego trenera"));

        trainerClientRepository.delete(trainerClient);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("trainerId", String.valueOf(trainerId));

        return ResponseEntity.ok(response);
    }

}
