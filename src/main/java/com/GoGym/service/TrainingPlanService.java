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
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Transactional
@Slf4j
public class TrainingPlanService {

    private final TrainingPlanRepository trainingPlanRepository;
    private final PlanExerciseRepository planExerciseRepository;
    private final UserRepository userRepository;
    private final TrainingPlanDayRepository trainingPlanDayRepository;
    private final NotificationService notificationService;

    public TrainingPlanService(TrainingPlanRepository trainingPlanRepository,
                               PlanExerciseRepository planExerciseRepository,
                               TrainingPlanDayRepository trainingPlanDayRepository,
                               UserRepository userRepository,
                               NotificationService notificationService) {
        this.trainingPlanRepository = trainingPlanRepository;
        this.planExerciseRepository = planExerciseRepository;
        this.trainingPlanDayRepository = trainingPlanDayRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public TrainingPlan createTrainingPlan(TrainingPlan plan) {
        // Zapisujemy plan, aby wygenerować jego ID
        trainingPlanRepository.save(plan);
        log.info("Utworzono plan treningowy o ID: {}", plan.getIdPlan());

        if (plan.getTrainingPlanDays() != null) {
            for (TrainingPlanDay day : plan.getTrainingPlanDays()) {
                day.setTrainingPlan(plan);
                trainingPlanDayRepository.save(day);
                log.info("Zapisano dzień treningowy o ID: {}", day.getIdDay());

                if (day.getExercises() != null) {
                    for (PlanExercise exercise : day.getExercises()) {
                        exercise.setTrainingPlanDay(day);
                        planExerciseRepository.save(exercise);
                        log.info("Zapisano ćwiczenie w dniu o ID: {}", day.getIdDay());
                    }
                }
            }
        } else {
            log.warn("Plan treningowy o ID {} nie posiada dni treningowych.", plan.getIdPlan());
        }

        // Pobieramy trenera i klienta do powiadomienia
        User trainer = userRepository.findById(plan.getIdTrainer())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trenera o ID: " + plan.getIdTrainer()));
        User client = userRepository.findById(plan.getIdClient())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta o ID: " + plan.getIdClient()));

        // Tworzymy powiadomienie o nowym planie
        notificationService.createNotification(client, trainer, "new_plan", plan.getName());
        log.info("Wysłano powiadomienie o nowym planie: {} do trenera i klienta.", plan.getName());

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
        log.info("Zaktualizowano status ćwiczenia o ID: {} na {}", exerciseId, newStatus);

        // Aktualizujemy status dnia, do którego należy ćwiczenie
        updateDayStatus(exercise.getTrainingPlanDay().getIdDay());
    }

    public void updateDayStatus(Long dayId) {
        TrainingPlanDay day = trainingPlanDayRepository.findById(dayId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono dnia o ID: " + dayId));

        boolean allExercisesCompleted = day.getExercises().stream()
                .allMatch(exercise -> exercise.getStatus() == PlanExercise.Status.completed);
        TrainingPlanDay.Status previousStatus = day.getStatus();
        TrainingPlanDay.Status newStatus = allExercisesCompleted
                ? TrainingPlanDay.Status.completed
                : TrainingPlanDay.Status.notCompleted;

        day.setStatus(newStatus);
        trainingPlanDayRepository.save(day);
        log.info("Dzień o ID: {} zmieniono ze statusu {} na {}", dayId, previousStatus, newStatus);

        updatePlanStatus(day.getTrainingPlan().getIdPlan());

        // Wyślij powiadomienie tylko, gdy dzień został ukończony po raz pierwszy
        if (previousStatus != TrainingPlanDay.Status.completed && newStatus == TrainingPlanDay.Status.completed) {
            TrainingPlan plan = day.getTrainingPlan();
            User trainer = userRepository.findById(plan.getIdTrainer())
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trenera o ID: " + plan.getIdTrainer()));
            User client = userRepository.findById(plan.getIdClient())
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta o ID: " + plan.getIdClient()));
            int dayNumber = plan.getTrainingPlanDays().indexOf(day) + 1;
            notificationService.createNotification(trainer, client, "day_updated", "Dzień " + dayNumber + " w planie: " + plan.getName());
            log.info("Wysłano powiadomienie o ukończeniu dnia treningowego: {}", dayNumber);
        }
    }

    public void updatePlanStatus(Long planId) {
        TrainingPlan plan = trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono planu o ID: " + planId));

        boolean allDaysCompleted = plan.getTrainingPlanDays().stream()
                .allMatch(day -> day.getStatus() == TrainingPlanDay.Status.completed);

        plan.setStatus(allDaysCompleted ? TrainingPlan.Status.completed : TrainingPlan.Status.active);
        trainingPlanRepository.save(plan);
        log.info("Zaktualizowano status planu o ID: {} na {}", planId, plan.getStatus());

        if (allDaysCompleted) {
            // Pobieramy trenera i klienta
            User trainer = userRepository.findById(plan.getIdTrainer())
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trenera o ID: " + plan.getIdTrainer()));
            User client = userRepository.findById(plan.getIdClient())
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta o ID: " + plan.getIdClient()));
            notificationService.createNotification(trainer, client, "plan_completed", plan.getName());
            log.info("Wysłano powiadomienie o ukończeniu planu: {}", plan.getName());
        }
    }

    public void deleteTrainingPlan(Long planId) {
        TrainingPlan plan = trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono planu o ID: " + planId));

        // Usuwamy powiązane dni i ćwiczenia
        if (plan.getTrainingPlanDays() != null) {
            for (TrainingPlanDay day : plan.getTrainingPlanDays()) {
                planExerciseRepository.deleteAll(day.getExercises());
                trainingPlanDayRepository.delete(day);
                log.info("Usunięto dzień treningowy o ID: {} i powiązane ćwiczenia", day.getIdDay());
            }
        }
        trainingPlanRepository.delete(plan);
        log.info("Usunięto plan treningowy o ID: {}", planId);
    }
}
