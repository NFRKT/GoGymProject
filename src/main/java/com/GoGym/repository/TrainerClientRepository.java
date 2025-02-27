package com.GoGym.repository;

import com.GoGym.module.Request;
import com.GoGym.module.TrainerClient;
import com.GoGym.module.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface TrainerClientRepository extends JpaRepository<TrainerClient, Long> {
    List<TrainerClient> findByTrainer_IdUser(Long trainerId);

    List<TrainerClient> findByClient_IdUser(Long clientId);

    Optional<TrainerClient> findByTrainer_IdUserAndClient_IdUser(Long trainerId, Long clientId);

    boolean existsByTrainerAndClient(User trainer, User client);

    @Query("SELECT COUNT(tc) FROM TrainerClient tc WHERE tc.trainer.idUser = :trainerId")
    int countByTrainerId(@Param("trainerId") Long trainerId);
}
