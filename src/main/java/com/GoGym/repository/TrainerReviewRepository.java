package com.GoGym.repository;

import com.GoGym.module.TrainerReview;
import com.GoGym.module.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrainerReviewRepository extends JpaRepository<TrainerReview, Long> {
    List<TrainerReview> findByTrainer(User trainer);
    boolean existsByTrainerAndClient(User trainer, User client);
    @Query("SELECT AVG(r.rating) FROM TrainerReview r WHERE r.trainer.idUser = :trainerId")
    Double getAverageRatingForTrainer(@Param("trainerId") Long trainerId);
}
