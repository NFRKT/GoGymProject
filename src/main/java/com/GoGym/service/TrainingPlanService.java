package com.GoGym.service;

import com.GoGym.module.PlanExercise;
import com.GoGym.module.TrainingPlan;
import com.GoGym.module.TrainingPlanDay;
import com.GoGym.module.User;
import com.GoGym.repository.PlanExerciseRepository;
import com.GoGym.repository.TrainingPlanDayRepository;
import com.GoGym.repository.TrainingPlanRepository;
import com.GoGym.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainingPlanService {

    private final TrainingPlanRepository trainingPlanRepository;
    private final PlanExerciseRepository planExerciseRepository;
    private final UserRepository userRepository;
    private final TrainingPlanDayRepository trainingPlanDayRepository;
    private final NotificationService notificationService;

    public TrainingPlanService(TrainingPlanRepository trainingPlanRepository, PlanExerciseRepository planExerciseRepository, TrainingPlanDayRepository trainingPlanDayRepository, UserRepository userRepository, NotificationService notificationService) {
        this.trainingPlanRepository = trainingPlanRepository;
        this.planExerciseRepository = planExerciseRepository;
        this.trainingPlanDayRepository = trainingPlanDayRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }


    @Transactional
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

        // Pobranie trenera i klienta do powiadomienia
        User trainer = userRepository.findById(plan.getIdTrainer())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trenera o ID: " + plan.getIdTrainer()));

        User client = userRepository.findById(plan.getIdClient())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta o ID: " + plan.getIdClient()));

        // Tworzenie powiadomienia
        notificationService.createNotification(client, trainer, "new_plan", plan.getName());

        return plan;
    }

    public List<TrainingPlan> findPlansByIdClient(Long idUser) {
        return trainingPlanRepository.findByIdClient(idUser);
    }

    public List<TrainingPlan> findPlansByIdTrainer(Long idUser) {
        return trainingPlanRepository.findByIdTrainer(idUser);
    }

    public void updateExerciseStatus(Long exerciseId, PlanExercise.Status newStatus) {
        PlanExercise exercise = planExerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ćwiczenia o ID: " + exerciseId));
        exercise.setStatus(newStatus);
        planExerciseRepository.save(exercise);

        updateDayStatus(exercise.getTrainingPlanDay().getIdDay());
    }

    public void updateDayStatus(Long dayId) {
        TrainingPlanDay day = trainingPlanDayRepository.findById(dayId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono dnia o ID: " + dayId));

        boolean allExercisesCompleted = day.getExercises().stream()
                .allMatch(exercise -> exercise.getStatus() == PlanExercise.Status.completed);

        TrainingPlanDay.Status newStatus = allExercisesCompleted
                ? TrainingPlanDay.Status.completed
                : TrainingPlanDay.Status.notCompleted;

        day.setStatus(newStatus);
        trainingPlanDayRepository.save(day);

        // Debug
        System.out.println("Zaktualizowano status dnia: " + dayId + " na " + newStatus);

        updatePlanStatus(day.getTrainingPlan().getIdPlan());
    }

    public void updatePlanStatus(Long planId) {
        TrainingPlan plan = trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono planu o ID: " + planId));

        boolean allDaysCompleted = plan.getTrainingPlanDays().stream()
                .allMatch(day -> day.getStatus() == TrainingPlanDay.Status.completed);

        plan.setStatus(allDaysCompleted ? TrainingPlan.Status.completed : TrainingPlan.Status.active);
        trainingPlanRepository.save(plan);
    }

    public void deleteTrainingPlan(Long planId) {
        TrainingPlan plan = trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono planu o ID: " + planId));

        // Usuń powiązane ćwiczenia i dni
        for (TrainingPlanDay day : plan.getTrainingPlanDays()) {
            planExerciseRepository.deleteAll(day.getExercises());  // Usuń ćwiczenia z danego dnia
            trainingPlanDayRepository.delete(day);  // Usuń dzień
        }

        trainingPlanRepository.delete(plan);  // Usuń plan
    }


}