package com.GoGym.repository;

import com.GoGym.module.Request;
import com.GoGym.module.TrainerClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByTrainer_IdUserAndRequestStatus(Long trainerId, Request.RequestStatus status);

    List<Request> findByClient_IdUser(Long clientId);
    List<Request> findByTrainer_IdUser(Long trainerId);
}


