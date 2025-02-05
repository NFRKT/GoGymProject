package com.GoGym.repository;

import com.GoGym.module.Request;
import com.GoGym.module.TrainerClient;
import com.GoGym.module.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface TrainerClientRepository extends JpaRepository<TrainerClient, Long> {
    List<TrainerClient> findByTrainer_IdUser(Long trainerId);

    List<TrainerClient> findByClient_IdUser(Long clientId);

    @Query("SELECT DISTINCT tc.trainer FROM TrainerClient tc")
    List<User> findAllTrainers();

    Optional<TrainerClient> findByTrainer_IdUserAndClient_IdUser(Long trainerId, Long clientId);

    // Usuwa relację trener-klient
    void deleteByTrainer_IdUserAndClient_IdUser(Long trainerId, Long clientId);


}
