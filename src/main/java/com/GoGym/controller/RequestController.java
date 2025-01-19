package com.GoGym.controller;

import com.GoGym.module.Request;
import com.GoGym.module.User;
import com.GoGym.repository.RequestRepository;
import com.GoGym.repository.UserRepository;
import com.GoGym.service.ExerciseService;
import com.GoGym.service.RequestService;
import com.GoGym.service.TrainerClientService;
import com.GoGym.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private RequestService requestService;
    private RequestRepository requestRepository;
    private UserRepository userRepository;
    private TrainerClientService trainerClientService;

    @Autowired
    public RequestController(RequestService requestService, RequestRepository requestRepository, UserRepository userRepository, TrainerClientService trainerClientService) {
        this.requestService = requestService;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.trainerClientService = trainerClientService;
    }

    @PostMapping("/send")
    public Request sendRequest(@RequestParam Long clientId, @RequestParam Long trainerId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta o podanym ID"));
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trenera o podanym ID"));

        Request request = new Request();
        request.setClient(client);
        request.setTrainer(trainer);
        request.setRequestStatus(Request.RequestStatus.pending);
        request.setRequestDate(new Timestamp(System.currentTimeMillis()));

        return requestRepository.save(request);
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
