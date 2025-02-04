package com.GoGym.repository;

import com.GoGym.module.Request;
import com.GoGym.module.TrainerClient;
import com.GoGym.module.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByTrainer_IdUserAndRequestStatus(Long trainerId, Request.RequestStatus status);

    List<Request> findByClient_IdUserAndRequestStatus(Long clientId, Request.RequestStatus status);
    List<Request> findByClient_IdUser(Long clientId);
    List<Request> findByTrainer_IdUser(Long trainerId);
    // ✅ Pobiera wszystkie oczekujące zapytania dla danego klienta
    List<Request> findByClientAndRequestStatus(User client, Request.RequestStatus requestStatus);

    // ✅ Pobiera wszystkie oczekujące zapytania dla danego trenera
    List<Request> findByTrainerAndRequestStatus(User trainer, Request.RequestStatus requestStatus);

}


