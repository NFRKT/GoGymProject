package com.GoGym.repository;

import com.GoGym.module.TrainerReview;
import com.GoGym.module.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrainerReviewRepository extends JpaRepository<TrainerReview, Long> {
    List<TrainerReview> findByTrainer(User trainer);
    boolean existsByTrainerAndClient(User trainer, User client);
}
