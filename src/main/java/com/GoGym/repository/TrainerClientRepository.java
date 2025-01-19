package com.GoGym.repository;

import com.GoGym.module.Request;
import com.GoGym.module.TrainerClient;
import com.GoGym.module.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface TrainerClientRepository extends JpaRepository<TrainerClient, Long> {
    List<TrainerClient> findByTrainer_IdUser(Long trainerId);

    List<TrainerClient> findByClient_IdUser(Long clientId);

    @Query("SELECT DISTINCT tc.trainer FROM TrainerClient tc")
    List<User> findAllTrainers();

}
