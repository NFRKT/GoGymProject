package com.GoGym.service;

import com.GoGym.module.*;
import com.GoGym.repository.BadgeRepository;
import com.GoGym.repository.TrainingPlanRepository;
import com.GoGym.repository.UserBadgeRepository;
import com.GoGym.repository.WorkoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final TrainingPlanRepository trainingPlanRepository;
    private final WorkoutRepository workoutRepository;
    private final NotificationService notificationService;

    public BadgeService(BadgeRepository badgeRepository,
                        UserBadgeRepository userBadgeRepository,
                        TrainingPlanRepository trainingPlanRepository,
                        WorkoutRepository workoutRepository, NotificationService notificationService) {
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.trainingPlanRepository = trainingPlanRepository;
        this.workoutRepository = workoutRepository;
        this.notificationService = notificationService;
    }

    // Metoda przyznająca odznakę, jeśli warunek jest spełniony
    public void awardBadgeIfEligible(User user, int achieved, int required, String badgeName) {
        if (achieved >= required && !hasUserBadge(user, badgeName)) {
            Badge badge = badgeRepository.findByName(badgeName)
                    .orElseThrow(() -> new IllegalArgumentException("Odznaka nie istnieje: " + badgeName));
            UserBadge userBadge = new UserBadge();
            userBadge.setUser(user);
            userBadge.setBadge(badge);
            userBadge.setAwardedDate(LocalDate.now());
            userBadgeRepository.save(userBadge);
            log.info("Przyznano odznakę '{}' użytkownikowi {}", badgeName, user.getEmail());
            notificationService.createNotification(user, user, "badge_awarded", badgeName);
        }

    }

    public boolean hasUserBadge(User user, String badgeName) {
        return userBadgeRepository.findByUserAndBadge_Name(user, badgeName).isPresent();
    }

    public List<UserBadge> getBadgesForUser(User user) {
        return userBadgeRepository.findByUser(user);
    }

    // Nowa metoda: sprawdzaj i przyznawaj odznaki według określonych kryteriów
    public void checkAndAwardBadgesForUser(User user) {
        // Odznaka za 5 ukończonych planów treningowych
        long completedPlans = trainingPlanRepository.findByIdClient(user.getIdUser())
                .stream().filter(plan -> plan.getStatus() == TrainingPlan.Status.completed)
                .count();
        if (completedPlans >= 5) {
            awardBadgeIfEligible(user, (int) completedPlans, 5, "5 Plans Completed");
        }
        if (completedPlans >= 8) {
            awardBadgeIfEligible(user, (int) completedPlans, 8, "8 Plans Completed");
        }

        // Odznaka za 5 workoutów – liczymy wszystkie workouty
        long totalWorkouts = workoutRepository.findByUserOrderByWorkoutDateDesc(user).size();
        if (totalWorkouts >= 5) {
            awardBadgeIfEligible(user, (int) totalWorkouts, 5, "5 Workouts Completed");
        }

        // Odznaka za 10 ukończonych ćwiczeń planowych
        int completedPlanExercises = 0;
        List<TrainingPlan> plans = trainingPlanRepository.findByIdClient(user.getIdUser());
        for (TrainingPlan plan : plans) {
            if (plan.getTrainingPlanDays() != null) {
                for (TrainingPlanDay day : plan.getTrainingPlanDays()) {
                    if (day.getExercises() != null) {
                        for (PlanExercise exercise : day.getExercises()) {
                            if (exercise.getStatus() == PlanExercise.Status.completed) {
                                completedPlanExercises++;
                            }
                        }
                    }
                }
            }
        }
        if (completedPlanExercises >= 10) {
            awardBadgeIfEligible(user, completedPlanExercises, 10, "10 Plan Exercises Completed");
        }
    }

}
