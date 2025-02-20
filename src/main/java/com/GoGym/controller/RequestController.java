package com.GoGym.controller;

import com.GoGym.module.Request;
import com.GoGym.module.TrainerClient;
import com.GoGym.module.User;
import com.GoGym.repository.RequestRepository;
import com.GoGym.repository.UserRepository;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.service.NotificationService;
import com.GoGym.service.RequestService;
import com.GoGym.service.TrainerClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    private final RequestService requestService;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final TrainerClientService trainerClientService;
    private final NotificationService notificationService;

    public RequestController(RequestService requestService,
                             RequestRepository requestRepository,
                             NotificationService notificationService,
                             UserRepository userRepository,
                             TrainerClientService trainerClientService) {
        this.requestService = requestService;
        this.requestRepository = requestRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.trainerClientService = trainerClientService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendRequest(@RequestParam Long trainerId, Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User loggedInUser = userDetails.getUser();

            User trainer = userRepository.findById(trainerId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trenera"));

            Request newRequest = new Request();
            newRequest.setClient(loggedInUser);
            newRequest.setTrainer(trainer);
            newRequest.setRequestDate(new Timestamp(System.currentTimeMillis()));
            newRequest.setRequestStatus(Request.RequestStatus.pending);

            requestRepository.save(newRequest);

            // Powiadomienie dla trenera
            notificationService.createNotification(trainer, loggedInUser, "new_request");

            Map<String, String> response = new HashMap<>();
            response.put("requestId", String.valueOf(newRequest.getId()));
            response.put("trainerId", String.valueOf(trainer.getIdUser()));
            response.put("trainerName", trainer.getFirstName());
            response.put("requestDate", newRequest.getRequestDate().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Błąd podczas wysyłania prośby", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Wystąpił błąd podczas wysyłania prośby"));
        }
    }

    @PostMapping("/{requestId}/cancel")
    public ResponseEntity<Map<String, String>> cancelRequest(@PathVariable Long requestId, Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User loggedInUser = userDetails.getUser();

            Request request = requestRepository.findById(requestId)
                    .orElseThrow(() -> new IllegalArgumentException("Prośba nie istnieje"));

            if (!request.getClient().getIdUser().equals(loggedInUser.getIdUser())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nie możesz anulować tej prośby"));
            }

            requestRepository.delete(request);
            return ResponseEntity.ok(Map.of("status", "success", "requestId", String.valueOf(requestId)));
        } catch (Exception e) {
            logger.error("Błąd podczas anulowania prośby", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Błąd przy anulowaniu prośby"));
        }
    }

    @GetMapping("/trainer/{trainerId}")
    public List<Request> getTrainerRequests(@PathVariable Long trainerId) {
        return requestService.getRequestsForTrainer(trainerId);
    }

    @PostMapping("/{requestId}/update")
    public ResponseEntity<Map<String, String>> updateRequestStatus(@PathVariable Long requestId, @RequestParam String status) {
        try {
            Request.RequestStatus requestStatus = Request.RequestStatus.valueOf(status);
            Request updatedRequest = requestService.updateRequestStatus(requestId, requestStatus);
            User client = updatedRequest.getClient();
            User trainer = updatedRequest.getTrainer();

            Map<String, String> response = new HashMap<>();

            if (requestStatus == Request.RequestStatus.accepted) {
                trainerClientService.createTrainerClient(trainer.getIdUser(), client.getIdUser());
                response.put("status", "accepted");
                response.put("firstName", client.getFirstName());
                response.put("secondName", client.getSecondName());
                response.put("id", String.valueOf(client.getIdUser()));
                notificationService.createNotification(client, trainer, "accepted");
            } else if (requestStatus == Request.RequestStatus.rejected) {
                response.put("status", "rejected");
                notificationService.createNotification(client, trainer, "rejected");
            } else {
                response.put("status", "unknown");
            }

            // Po zmianie statusu – usuwamy prośbę
            requestRepository.delete(updatedRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Nieprawidłowy status prośby lub nie znaleziono prośby", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Błędny status lub prośba nie istnieje"));
        } catch (Exception e) {
            logger.error("Błąd przy aktualizacji statusu prośby", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Błąd przy aktualizacji statusu prośby"));
        }
    }

    @GetMapping("/accepted")
    public ResponseEntity<List<Map<String, String>>> getAcceptedRequests(Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User loggedInUser = userDetails.getUser();

            List<TrainerClient> acceptedTrainers = trainerClientService.getClientTrainers(loggedInUser.getIdUser());
            List<Map<String, String>> response = acceptedTrainers.stream().map(trainerClient -> {
                Map<String, String> trainerData = new HashMap<>();
                trainerData.put("id", String.valueOf(trainerClient.getTrainer().getIdUser()));
                trainerData.put("firstName", trainerClient.getTrainer().getFirstName());
                trainerData.put("secondName", trainerClient.getTrainer().getSecondName());
                return trainerData;
            }).toList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Błąd przy pobieraniu zaakceptowanych próśb", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/new-requests")
    public ResponseEntity<List<Map<String, String>>> getNewRequests(Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User loggedInTrainer = userDetails.getUser();

            // Pobieramy tylko zapytania o status PENDING dla trenera
            List<Request> pendingRequests = requestRepository.findByTrainerAndRequestStatus(
                    loggedInTrainer, Request.RequestStatus.pending);

            List<Map<String, String>> response = pendingRequests.stream().map(request -> {
                Map<String, String> requestData = new HashMap<>();
                requestData.put("id", String.valueOf(request.getId()));
                requestData.put("clientId", String.valueOf(request.getClient().getIdUser()));
                requestData.put("firstName", request.getClient().getFirstName());
                return requestData;
            }).toList();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Błąd przy pobieraniu nowych próśb", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/client/pending")
    public ResponseEntity<List<Map<String, String>>> getClientPendingRequests(Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User loggedInUser = userDetails.getUser();

            List<Request> pendingRequests = requestRepository.findByClientAndRequestStatus(
                    loggedInUser, Request.RequestStatus.pending);

            List<Map<String, String>> response = pendingRequests.stream().map(request -> {
                Map<String, String> requestData = new HashMap<>();
                requestData.put("id", String.valueOf(request.getId()));
                requestData.put("trainerId", String.valueOf(request.getTrainer().getIdUser()));
                requestData.put("status", request.getRequestStatus().name());
                return requestData;
            }).toList();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Błąd przy pobieraniu oczekujących próśb klienta", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
