package com.GoGym.service;

import com.GoGym.module.Request;
import com.GoGym.module.User;
import com.GoGym.repository.RequestRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public List<Request> getRequestsForTrainer(Long trainerId) {
        return requestRepository.findByTrainer_IdUserAndRequestStatus(trainerId, Request.RequestStatus.pending);
    }

    public List<Request> getRequestsByClientId(Long clientId) {
        return requestRepository.findByClient_IdUser(clientId);
    }

    public Request updateRequestStatus(Long requestId, Request.RequestStatus status) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        request.setRequestStatus(status);
        return requestRepository.save(request);
    }

    public List<Request> getPendingRequestsByClient(Long clientId) {
        return requestRepository.findByClient_IdUserAndRequestStatus(clientId, Request.RequestStatus.pending);
    }
}
