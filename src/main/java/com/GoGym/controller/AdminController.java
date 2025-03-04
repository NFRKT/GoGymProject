package com.GoGym.controller;

import com.GoGym.module.User;
import com.GoGym.repository.*;
import com.GoGym.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final TrainingPlanRepository trainingPlanRepository;
    private final TrainerClientRepository trainerClientRepository;

    public AdminController(UserService userService,
                           UserRepository userRepository,
                           WorkoutRepository workoutRepository,
                           ExerciseRepository exerciseRepository,
                           TrainingPlanRepository trainingPlanRepository, TrainerClientRepository trainerClientRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        this.trainingPlanRepository = trainingPlanRepository;
        this.trainerClientRepository = trainerClientRepository;
    }

    @GetMapping("/panel")
    public String adminPanel(Model model) {
        List<User> clients = userService.getUsersByType(User.UserType.CLIENT);
        List<User> trainers = userService.getUsersByType(User.UserType.TRAINER);

        long userCount = userRepository.count();
        long trainerCount = userRepository.findAll()
                .stream()
                .filter(u -> u.getUserType() != null && u.getUserType().toString().equals("TRAINER"))
                .count();
        long workoutCount = workoutRepository.count();
        long exerciseCount = exerciseRepository.count();
        long trainingPlanCount = trainingPlanRepository.count();
        long collaborationCount = trainerClientRepository.count();

        model.addAttribute("userCount", userCount);
        model.addAttribute("trainerCount", trainerCount);
        model.addAttribute("workoutCount", workoutCount);
        model.addAttribute("exerciseCount", exerciseCount);
        model.addAttribute("trainingPlanCount", trainingPlanCount);
        model.addAttribute("collaborationCount", collaborationCount);
        model.addAttribute("clients", clients);
        model.addAttribute("trainers", trainers);
        return "admin-panel";
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable Long id) {
        boolean success = userService.deleteUserById(id);
        return ResponseEntity.ok(Map.of("success", success));
    }

    @GetMapping("/clients/list")
    public String listClients(Model model) {
        List<User> clients = userService.getUsersByType(User.UserType.CLIENT);
        model.addAttribute("clients", clients);
        return "admin-clients-list";
    }

    @GetMapping("/trainers/list")
    public String listTrainers(Model model) {
        List<User> trainers = userService.getUsersByType(User.UserType.TRAINER);
        model.addAttribute("trainers", trainers);
        return "admin-trainers-list";
    }

}
