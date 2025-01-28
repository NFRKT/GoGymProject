package com.GoGym.repository;
import com.GoGym.module.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
    List<TrainingPlan> findByIdClient(Long idClient);
    List<TrainingPlan> findByIdTrainer(Long idTrainer);
}
