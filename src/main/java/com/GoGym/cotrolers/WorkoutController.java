package com.GoGym.cotrolers;

import com.GoGym.Module.Exercise;
import com.GoGym.Module.Workout;
import com.GoGym.Module.user;
import com.GoGym.Service.ExerciseService;
import com.GoGym.Service.WorkoutService;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        model.addAttribute("workout", new Workout());
        model.addAttribute("exercises", exerciseService.getAllExercises());
        return "create-workout";
    }



    @PostMapping("/create")
    public String createWorkout(@ModelAttribute Workout workout,
                                @RequestParam List<Long> exerciseIds,
                                @RequestParam List<Integer> sets,
                                @RequestParam List<Integer> reps,
                                @RequestParam List<Integer> weight,
                                Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            user currentUser = customUserDetails.getUser();

            if (currentUser == null) {
                throw new RuntimeException("Brak zalogowanego użytkownika");
            }
            workout.setUser(currentUser);
            List<Exercise> exercises = exerciseService.getAllExercises();
            System.out.println("Loaded exercises: " + exercises); // Debugowanie
            model.addAttribute("workout", new Workout());
            model.addAttribute("exercises", exerciseService.getAllExercises());
            workoutService.addWorkoutWithExercises(workout, exerciseIds, sets, reps, weight);
            return "redirect:/workouts";
        } catch (Exception e) {
            model.addAttribute("error", "Nie udało się stworzyć workoutu: " + e.getMessage());
            return "create-workout";
        }
    }

}
