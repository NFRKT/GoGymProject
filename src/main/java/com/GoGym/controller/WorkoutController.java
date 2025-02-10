package com.GoGym.controller;

import com.GoGym.module.Exercise;
import com.GoGym.module.Workout;
import com.GoGym.module.User;
import com.GoGym.service.ExerciseService;
import com.GoGym.service.WorkoutService;
import com.GoGym.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;
    private final ExerciseService exerciseService;


    @Autowired
    public WorkoutController(WorkoutService workoutService, ExerciseService exerciseService) {
        this.workoutService = workoutService;
        this.exerciseService = exerciseService;
    }

    @GetMapping("/create")
    public String createWorkoutForm(Model model) {
        List<Exercise> exercises = exerciseService.getAllExercises();

        // ✅ Debugowanie: sprawdź, czy ćwiczenia są pobierane
        System.out.println("Załadowane ćwiczenia: " + exercises.size());

        model.addAttribute("workout", new Workout());
        model.addAttribute("exercises", exercises);
        return "create-workout";
    }

    @PostMapping("/create")
    public String createWorkout(@ModelAttribute Workout workout,
                                @RequestParam List<Long> exerciseIds,
                                @RequestParam(required = false) List<Integer> sets,
                                @RequestParam(required = false) List<Integer> reps,
                                @RequestParam(required = false) List<Double> weight,
                                @RequestParam(required = false) List<String> durations, // 🚀 Format "mm:ss"
                                @RequestParam(required = false) List<Double> distances,
                                Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            User currentUser = customUserDetails.getUser();
            if (currentUser == null) {
                throw new RuntimeException("Brak zalogowanego użytkownika");
            }

            workout.setUser(currentUser);
            workoutService.addWorkoutWithExercises(workout, exerciseIds, sets, reps, weight, durations, distances);
            return "redirect:/workouts/" + workout.getIdWorkout();
        } catch (Exception e) {
            model.addAttribute("error", "Nie udało się stworzyć workoutu: " + e.getMessage());
            return "create-workout";
        }
    }


    @GetMapping("/{id}")
    public String getWorkoutDetails(@PathVariable Long id, Model model) {
        Workout workout = workoutService.getWorkoutById(id);
        model.addAttribute("workout", workout);
        return "workout-details";
    }

    @GetMapping
    public String getUserWorkouts(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = customUserDetails.getUser();

        List<Workout> workouts = workoutService.getWorkoutsByUser(currentUser);
        model.addAttribute("workouts", workouts);
        return "user-workouts";
    }


}
