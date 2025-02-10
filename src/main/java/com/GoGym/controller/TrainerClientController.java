package com.GoGym.controller;

import com.GoGym.module.*;
import com.GoGym.repository.*;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.service.RequestService;
import com.GoGym.service.TrainerClientService;
import com.GoGym.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class TrainerClientController {
    private TrainerClientService trainerClientService;
    private RequestService requestService;
    private TrainerClientRepository trainerClientRepository;

    private UserService userService;

    @Autowired
    public TrainerClientController(TrainerClientService trainerClientService, RequestService requestService, TrainerClientRepository trainerClientRepository) {
        this.trainerClientService = trainerClientService;
        this.requestService = requestService;
        this.trainerClientRepository = trainerClientRepository;
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
        List<User> availableTrainers = trainerClientService.findAllTrainers().stream()
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
        User loggedInTrainer = userDetails.getUser();

        trainerClientService.removeTrainerClient(loggedInTrainer.getIdUser(), clientId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("clientId", String.valueOf(clientId));

        return ResponseEntity.ok(response);
    }


    @PostMapping("/rejectTrainer")
    public ResponseEntity<Map<String, String>> rejectTrainer(@RequestParam Long trainerId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInClient = userDetails.getUser();

        trainerClientService.removeTrainerClient(trainerId, loggedInClient.getIdUser());

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("trainerId", String.valueOf(trainerId));

        return ResponseEntity.ok(response);
    }

    // ✅ Pobranie listy klientów trenera
    @GetMapping("/trainer/clients")
    public ResponseEntity<List<Map<String, String>>> getTrainerClients(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInTrainer = userDetails.getUser();

        List<TrainerClient> clients = trainerClientService.getTrainerClients(loggedInTrainer.getIdUser());

        List<Map<String, String>> response = clients.stream().map(client -> Map.of(
                "id", String.valueOf(client.getClient().getIdUser()),
                "firstName", client.getClient().getFirstName(),
                "secondName", client.getClient().getSecondName()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ✅ Pobranie listy trenerów klienta
    @GetMapping("/client/trainers")
    public ResponseEntity<List<Map<String, String>>> getClientTrainers(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInClient = userDetails.getUser();

        List<TrainerClient> trainers = trainerClientService.getClientTrainers(loggedInClient.getIdUser());

        List<Map<String, String>> response = trainers.stream().map(trainer -> Map.of(
                "id", String.valueOf(trainer.getTrainer().getIdUser()),
                "firstName", trainer.getTrainer().getFirstName(),
                "secondName", trainer.getTrainer().getSecondName()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/listTrainers")
    public String listTrainers(Model model) {
        List<User> trainers = trainerClientService.findAllTrainers();
        model.addAttribute("trainers", trainers);
        return "trainer-list";
    }



}
