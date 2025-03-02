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
import java.util.Set;
import java.util.stream.Collectors;

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
        String username = authentication.getName();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));

        List<Badge> allBadges = badgeRepository.findAll();

        List<UserBadge> userBadges = badgeService.getBadgesForUser(currentUser);

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

        Set<Long> ownedBadgeIds = userBadges.stream()
                .map(ub -> ub.getBadge().getId())
                .collect(Collectors.toSet());

        model.addAttribute("allBadges", allBadges);
        model.addAttribute("userBadges", userBadges);
        model.addAttribute("badgeProgress", badgeProgress);
        model.addAttribute("ownedBadgeIds", ownedBadgeIds);

        return "badges";
    }


}
