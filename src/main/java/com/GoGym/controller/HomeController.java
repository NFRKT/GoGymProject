package com.GoGym.controller;

import com.GoGym.module.*;
import com.GoGym.repository.BadgeRepository;
import com.GoGym.repository.WorkoutRepository;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.service.BadgeService;
import com.GoGym.service.TrainingPlanService;
import com.GoGym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
@ComponentScan({ "com.GoGym.*" })
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkoutRepository workoutRepository;


    @Autowired
    private TrainingPlanService trainingPlanService;

    @Autowired
    private BadgeService badgeService;

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));
        Long idUser = currentUser.getIdUser();

        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("CLIENT"));
        boolean isTrainer = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("TRAINER"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        model.addAttribute("isUser", isUser);
        model.addAttribute("isTrainer", isTrainer);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("idUser", idUser);
        model.addAttribute("username", username);

        if (isUser) {
            List<TrainingPlan> plans = trainingPlanService.findPlansByIdClient(idUser);
            List<TrainingPlan> activePlans = plans.stream()
                    .filter(plan -> plan.getStatus() == TrainingPlan.Status.active)
                    .collect(Collectors.toList());

            Map<Long, Integer> progressMap = new HashMap<>();
            for (TrainingPlan plan : activePlans) {
                int totalExercises = 0;
                int completedExercises = 0;
                if (plan.getTrainingPlanDays() != null) {
                    for (TrainingPlanDay day : plan.getTrainingPlanDays()) {
                        if (day.getExercises() != null) {
                            totalExercises += day.getExercises().size();
                            for (PlanExercise exercise : day.getExercises()) {
                                if (exercise.getStatus() == PlanExercise.Status.completed) {
                                    completedExercises++;
                                }
                            }
                        }
                    }
                }
                int progress = (totalExercises > 0) ? (completedExercises * 100 / totalExercises) : 0;
                progressMap.put(plan.getIdPlan(), progress);
            }
            model.addAttribute("activePlans", activePlans);
            model.addAttribute("progressMap", progressMap);

            badgeService.checkAndAwardBadgesForUser(currentUser);
            model.addAttribute("badges", badgeService.getBadgesForUser(currentUser));


        }
        return "home";
    }


    @GetMapping({"/", "/gogym"})
    public String landingPage(Model model) {
        // Pobierz liczbę wszystkich użytkowników
        long userCount = userRepository.count();

        // Liczba trenerów – przyjmujemy, że UserType jest przechowywany jako String w polu userType
        long trainerCount = userRepository.findAll()
                .stream()
                .filter(u -> u.getUserType() != null && u.getUserType().toString().equals("TRAINER"))
                .count();

        // Liczba treningów (workoutów)
        long workoutCount = workoutRepository.count();

        model.addAttribute("userCount", userCount);
        model.addAttribute("trainerCount", trainerCount);
        model.addAttribute("workoutCount", workoutCount);

        return "gogym";
    }

}
