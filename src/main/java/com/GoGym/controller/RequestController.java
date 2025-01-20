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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final TrainerClientService trainerClientService;

    //private final TrainerClientRepository trainerClientRepository;

    @Autowired
    public RequestController(RequestService requestService, RequestRepository requestRepository, UserRepository userRepository, TrainerClientService trainerClientService) {
        this.requestService = requestService;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.trainerClientService = trainerClientService;
        //this.trainerClientRepository = trainerClientRepository;
    }

//    @PostMapping("/send")
//    public Request sendRequest(@RequestParam Long clientId, @RequestParam Long trainerId) {
//
//        List<TrainerClient> existingTrainer = trainerClientService.getClientTrainers(clientId);
//        User client = userRepository.findById(clientId)
//                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta o podanym ID"));
//        User trainer = userRepository.findById(trainerId)
//                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trenera o podanym ID"));
//
//        Request request = new Request();
//        request.setClient(client);
//        request.setTrainer(trainer);
//        request.setRequestStatus(Request.RequestStatus.pending);
//        request.setRequestDate(new Timestamp(System.currentTimeMillis()));
//
//        return requestRepository.save(request);
//    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendRequest(@RequestParam Long trainerId, Authentication authentication) {
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

        return ResponseEntity.status(HttpStatus.FOUND).header("Location", "/trainer-clients/client-panel").build();

    }


    @PostMapping("/{requestId}/cancel")
    public ResponseEntity<String> cancelRequest(@PathVariable Long requestId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Prośba nie istnieje"));

        if (!request.getClient().getIdUser().equals(loggedInUser.getIdUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nie możesz anulować tej prośby");
        }

        requestRepository.delete(request);
        return ResponseEntity.ok("Prośba została anulowana");
    }

    @GetMapping("/trainer/{trainerId}")
    public List<Request> getTrainerRequests(@PathVariable Long trainerId) {
        return requestService.getRequestsForTrainer(trainerId);
    }

    @PostMapping("/{requestId}/update")
    public Request updateRequestStatus(@PathVariable Long requestId, @RequestParam String status) {
        Request.RequestStatus requestStatus = Request.RequestStatus.valueOf(status);
        Request updatedRequest = requestService.updateRequestStatus(requestId, requestStatus);

        if (requestStatus == Request.RequestStatus.accepted) {
            trainerClientService.createTrainerClient(updatedRequest.getTrainer().getIdUser(), updatedRequest.getClient().getIdUser());
        }

        return updatedRequest;
    }
}
