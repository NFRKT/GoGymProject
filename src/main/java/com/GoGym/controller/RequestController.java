package com.GoGym.controller;

import com.GoGym.module.Request;
import com.GoGym.module.TrainerClient;
import com.GoGym.module.User;
import com.GoGym.repository.RequestRepository;
import com.GoGym.repository.TrainerClientRepository;
import com.GoGym.repository.UserRepository;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.service.NotificationService;
import com.GoGym.service.RequestService;
import com.GoGym.service.TrainerClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    private final RequestService requestService;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final TrainerClientService trainerClientService;

    private final NotificationService notificationService;

    @Autowired
    public RequestController(RequestService requestService, RequestRepository requestRepository, NotificationService notificationService, UserRepository userRepository, TrainerClientService trainerClientService) {
        this.requestService = requestService;
        this.requestRepository = requestRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.trainerClientService = trainerClientService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendRequest(@RequestParam Long trainerId, Authentication authentication) {
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

        // Tworzymy JSON z danymi zapytania
        Map<String, String> response = new HashMap<>();
        response.put("requestId", String.valueOf(newRequest.getId())); // Dodajemy ID zapytania
        response.put("trainerId", String.valueOf(trainer.getIdUser()));
        response.put("trainerName", trainer.getFirstName());
        response.put("requestDate", newRequest.getRequestDate().toString());

        return ResponseEntity.ok(response);
    }


    @PostMapping("/{requestId}/cancel")
    public ResponseEntity<Map<String, String>> cancelRequest(@PathVariable Long requestId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Prośba nie istnieje"));

        if (!request.getClient().getIdUser().equals(loggedInUser.getIdUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Nie możesz anulować tej prośby"));
        }

        requestRepository.delete(request);

        // ✅ Zwracamy JSON z ID anulowanej prośby
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("requestId", String.valueOf(requestId));
        return ResponseEntity.ok(response);
    }


    @GetMapping("/trainer/{trainerId}")
    public List<Request> getTrainerRequests(@PathVariable Long trainerId) {
        return requestService.getRequestsForTrainer(trainerId);
    }

    @PostMapping("/{requestId}/update")
    public ResponseEntity<Map<String, String>> updateRequestStatus(@PathVariable Long requestId, @RequestParam String status) {
        Request.RequestStatus requestStatus = Request.RequestStatus.valueOf(status);
        Request updatedRequest = requestService.updateRequestStatus(requestId, requestStatus);

        Map<String, String> response = new HashMap<>();

        if (requestStatus == Request.RequestStatus.accepted) {
            trainerClientService.createTrainerClient(updatedRequest.getTrainer().getIdUser(), updatedRequest.getClient().getIdUser());

            response.put("status", "accepted");
            response.put("firstName", updatedRequest.getClient().getFirstName());
            response.put("secondName", updatedRequest.getClient().getSecondName());
            response.put("id", String.valueOf(updatedRequest.getClient().getIdUser()));
        } else if (requestStatus == Request.RequestStatus.rejected) {
            response.put("status", "rejected");
        }

        // 🔹 Usunięcie zapytania po zmianie statusu
        requestRepository.delete(updatedRequest);

        return ResponseEntity.ok(response);
    }



    @GetMapping("/accepted")
    public ResponseEntity<List<Map<String, String>>> getAcceptedRequests(Authentication authentication) {
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
    }
    @GetMapping("/new-requests")
    public ResponseEntity<List<Map<String, String>>> getNewRequests(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInTrainer = userDetails.getUser();

        // Pobierz tylko PENDING zapytania dla zalogowanego trenera
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
    }

    @GetMapping("/client/pending")
    public ResponseEntity<List<Map<String, String>>> getClientPendingRequests(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();

        List<Request> pendingRequests = requestRepository.findByClientAndRequestStatus(
                loggedInUser, Request.RequestStatus.pending);

        List<Map<String, String>> response = pendingRequests.stream().map(request -> {
            Map<String, String> requestData = new HashMap<>();
            requestData.put("id", String.valueOf(request.getId())); // 👈 Upewniamy się, że ID to string!
            requestData.put("trainerId", String.valueOf(request.getTrainer().getIdUser()));
            requestData.put("status", request.getRequestStatus().name());
            return requestData;
        }).toList();

        return ResponseEntity.ok(response);
    }

}
