package com.GoGym.controller;
import com.GoGym.module.*;
import com.GoGym.repository.TrainerDetailsRepository;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.service.RequestService;
import com.GoGym.service.TrainerClientService;
import com.GoGym.service.TrainerReviewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class TrainerClientController {
    @Autowired
    private TrainerClientService trainerClientService;
    @Autowired
    private RequestService requestService;
    @Autowired
    private TrainerReviewService trainerReviewService;
    @Autowired
    private TrainerDetailsRepository trainerDetailsRepository;

    @PreAuthorize("hasAuthority('CLIENT')")
    @GetMapping("/client-panel")
    public String clientPanel(Model model, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();
        Long clientId = loggedInUser.getIdUser();

        List<TrainerClient> clientTrainers = trainerClientService.getClientTrainers(clientId);
        List<Long> currentTrainerIds = clientTrainers.stream()
                .map(tc -> tc.getTrainer().getIdUser())
                .toList();

        List<Long> pendingRequestTrainerIds = requestService.getPendingRequestsByClient(clientId).stream()
                .map(request -> request.getTrainer().getIdUser())
                .toList();

        List<User> availableTrainers = trainerClientService.findAllTrainers().stream()
                .filter(trainer -> !currentTrainerIds.contains(trainer.getIdUser()))
                .toList();

        model.addAttribute("clientTrainers", clientTrainers);
        model.addAttribute("trainers", availableTrainers);
        model.addAttribute("clientId", clientId);
        model.addAttribute("pendingTrainerIds", pendingRequestTrainerIds);
        model.addAttribute("currentTrainerIds", currentTrainerIds);
        List<Request> requests = requestService.getRequestsByClientId(clientId);
        model.addAttribute("requests", requests);
        List<TrainerClient> trainerClients = trainerClientService.getClientTrainers(clientId);
        model.addAttribute("clientTrainers", clientTrainers);

        return "client-panel";
    }

    @PreAuthorize("hasAuthority('TRAINER')")
    @GetMapping("/trainer-panel")
    public String trainerPanel(Authentication authentication, Model model) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInTrainer = userDetails.getUser();
        Long trainerId = loggedInTrainer.getIdUser();

        List<TrainerClient> trainerClients = trainerClientService.getTrainerClients(trainerId);
        model.addAttribute("trainerClients", trainerClients);
        model.addAttribute("requests", requestService.getRequestsForTrainer(trainerId));
        return "trainer-panel";
    }

    @PreAuthorize("hasAuthority('TRAINER')")
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

    @PreAuthorize("hasAuthority('CLIENT')")
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

    @GetMapping("/trainer/clients")
    public ResponseEntity<List<Map<String, String>>> getTrainerClients(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInTrainer = userDetails.getUser();

        List<TrainerClient> clients = trainerClientService.getTrainerClients(loggedInTrainer.getIdUser());

        List<Map<String, String>> response = clients.stream().map(client -> Map.of(
                "id", String.valueOf(client.getClient().getIdUser()),
                "firstName", client.getClient().getFirstName(),
                "lastName", client.getClient().getLastName()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/trainers")
    public ResponseEntity<List<Map<String, String>>> getClientTrainers(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInClient = userDetails.getUser();

        List<TrainerClient> trainers = trainerClientService.getClientTrainers(loggedInClient.getIdUser());

        List<Map<String, String>> response = trainers.stream().map(trainer -> Map.of(
                "id", String.valueOf(trainer.getTrainer().getIdUser()),
                "firstName", trainer.getTrainer().getFirstName(),
                "lastName", trainer.getTrainer().getLastName()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list-trainers")
    public String listTrainers(Model model) {
        List<User> trainers = trainerClientService.findAllTrainers();

        List<Map<String, Object>> trainerList = trainers.stream().map(trainer -> {
            TrainerDetails details = trainerDetailsRepository.findById(trainer.getIdUser()).orElse(null);
            String experienceDuration;
            if (details != null && details.getStartDate() != null) {
                LocalDate startDate = details.getStartDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate today = LocalDate.now();

                long years = ChronoUnit.YEARS.between(startDate, today);
                long months = ChronoUnit.MONTHS.between(startDate, today) % 12;
                long days = ChronoUnit.DAYS.between(startDate, today) % 30;

                if (years >= 1) {
                    experienceDuration = years + " lat";
                } else if (months >= 1) {
                    experienceDuration = months + " miesięcy";
                } else {
                    experienceDuration = days + " dni";
                }
            } else {
                experienceDuration = "Brak danych";
            }

            int clientCount = trainerClientService.countClientsByTrainer(trainer.getIdUser());
            double averageRating = trainerReviewService.getAverageRatingForTrainer(trainer.getIdUser());
            String formattedRating = (averageRating == 0.0) ? "Brak ocen" : String.format("%.1f", averageRating);

            Map<String, Object> trainerMap = new HashMap<>();
            trainerMap.put("idUser", trainer.getIdUser());
            trainerMap.put("firstName", trainer.getFirstName());
            trainerMap.put("lastName", trainer.getLastName());
            trainerMap.put("phone", details != null ? details.getPhoneNumber() : "Brak danych");
            trainerMap.put("experienceDuration", experienceDuration);
            trainerMap.put("clientCount", clientCount);
            trainerMap.put("averageRating", formattedRating);

            return trainerMap;
        }).collect(Collectors.toList());

        model.addAttribute("trainers", trainerList);
        return "trainer-list";
    }


}
