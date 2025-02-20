package com.GoGym.controller;

import com.GoGym.module.*;
import com.GoGym.repository.BadgeRepository;
import com.GoGym.repository.UserRepository;
import com.GoGym.service.BadgeService;
import com.GoGym.service.TrainingPlanService;
import com.GoGym.service.WorkoutService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class BadgesController {

    public final UserRepository userRepository;
    public final BadgeRepository badgeRepository;
    public final BadgeService badgeService;
    public final WorkoutService workoutService;
    public final TrainingPlanService trainingPlanService;


    @GetMapping("/badges")
    public String getBadgesPage(Model model, Authentication authentication) {
        // Pobieramy zalogowanego użytkownika
        String username = authentication.getName();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));

        // Pobierz wszystkie dostępne odznaki
        List<Badge> allBadges = badgeRepository.findAll();

        // Pobierz odznaki, które użytkownik już zdobył
        List<UserBadge> userBadges = badgeService.getBadgesForUser(currentUser);

        // Pseudologika liczenia postępów – załóżmy, że mamy:
        //  - dla "5 Workouts Completed": target = 5, progress = liczba workoutów użytkownika
        //  - dla "5 Plans Completed": target = 5, progress = liczba ukończonych planów treningowych
        //  - dla "10 Plan Exercises Completed": target = 10, progress = liczba ukończonych ćwiczeń planowych
        int workoutCount = workoutService.getWorkoutsByUser(currentUser).size();
        int completedPlans = (int) trainingPlanService.findPlansByIdClient(currentUser.getIdUser())
                .stream().filter(plan -> plan.getStatus() == TrainingPlan.Status.completed)
                .count();
        int completedPlanExercises = 0;
        List<TrainingPlan> plans = trainingPlanService.findPlansByIdClient(currentUser.getIdUser());
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

        // Wylicz postępy dla każdej odznaki
        Map<Long, String> badgeProgress = new HashMap<>();
        for (Badge badge : allBadges) {
            int target = 0;
            int progress = 0;
            if ("5 Workouts Completed".equals(badge.getName())) {
                target = 5;
                progress = Math.min(workoutCount, target);
            } else if ("5 Plans Completed".equals(badge.getName())) {
                target = 5;
                progress = Math.min(completedPlans, target);
            } else if ("10 Plan Exercises Completed".equals(badge.getName())) {
                target = 10;
                progress = Math.min(completedPlanExercises, target);
            }
            badgeProgress.put(badge.getId(), progress + "/" + target);
        }

        model.addAttribute("allBadges", allBadges);
        model.addAttribute("userBadges", userBadges);
        model.addAttribute("badgeProgress", badgeProgress);

        return "badges"; // Widok badges.html
    }

}
