package com.GoGym.service;

import com.GoGym.module.PlanExercise;
import com.GoGym.module.TrainingPlan;
import com.GoGym.module.TrainingPlanDay;
import com.GoGym.repository.PlanExerciseRepository;
import com.GoGym.repository.TrainingPlanDayRepository;
import com.GoGym.repository.TrainingPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainingPlanService {

    private final TrainingPlanRepository trainingPlanRepository;
    private final PlanExerciseRepository planExerciseRepository;
    private final TrainingPlanDayRepository trainingPlanDayRepository;

    public TrainingPlanService(TrainingPlanRepository trainingPlanRepository, PlanExerciseRepository planExerciseRepository, TrainingPlanDayRepository trainingPlanDayRepository) {
        this.trainingPlanRepository = trainingPlanRepository;
        this.planExerciseRepository = planExerciseRepository;
        this.trainingPlanDayRepository = trainingPlanDayRepository;
    }


    public TrainingPlan createTrainingPlan(TrainingPlan plan) {
        trainingPlanRepository.save(plan);

        for (TrainingPlanDay day : plan.getTrainingPlanDays()) {
            day.setTrainingPlan(plan);
            trainingPlanDayRepository.save(day);

            for (PlanExercise exercise : day.getExercises()) {
                exercise.setTrainingPlanDay(day);
                planExerciseRepository.save(exercise);
            }
        }

        return plan;
    }

    public List<TrainingPlan> findPlansByIdClient(Long idUser) {
        return trainingPlanRepository.findByIdClient(idUser);
    }

    public List<TrainingPlan> findPlansByIdTrainer(Long idUser) {
        return trainingPlanRepository.findByIdTrainer(idUser);
    }

}