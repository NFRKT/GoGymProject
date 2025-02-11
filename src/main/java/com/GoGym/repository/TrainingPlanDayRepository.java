package com.GoGym.repository;

import com.GoGym.module.TrainingPlan;
import com.GoGym.module.TrainingPlanDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingPlanDayRepository extends JpaRepository<TrainingPlanDay, Long> {

    List<TrainingPlanDay> findByTrainingPlanOrderByDate(TrainingPlan trainingPlan);

}
