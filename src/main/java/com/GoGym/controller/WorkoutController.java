package com.GoGym.controller;

import com.GoGym.dto.WorkoutDTO;
import com.GoGym.module.*;
import com.GoGym.repository.TrainingPlanDayRepository;
import com.GoGym.repository.UserRepository;
import com.GoGym.repository.WorkoutRepository;
import com.GoGym.service.ExerciseService;
import com.GoGym.service.WorkoutService;
import com.GoGym.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;
    private final ExerciseService exerciseService;
    private final UserRepository userRepository;
    private final TrainingPlanDayRepository trainingPlanDayRepository;
    private final WorkoutRepository workoutRepository;


    @Autowired
    public WorkoutController(WorkoutService workoutService, ExerciseService exerciseService, UserRepository userRepository, TrainingPlanDayRepository trainingPlanDayRepository, WorkoutRepository workoutRepository) {
        this.workoutService = workoutService;
        this.exerciseService = exerciseService;
        this.userRepository = userRepository;
        this.trainingPlanDayRepository = trainingPlanDayRepository;
        this.workoutRepository = workoutRepository;
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
                                @RequestParam(required = false) List<String> durations, // 🚀 Format "hh:mm:ss" lub "mm:ss"
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

    @PostMapping("/add-workout-from-day")
    public ResponseEntity<?> addWorkoutFromDay(@RequestBody WorkoutDTO workoutDTO, Principal principal) {
        System.out.println("Otrzymane żądanie: " + workoutDTO);

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));

        TrainingPlanDay day = trainingPlanDayRepository.findById(workoutDTO.getDayId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono dnia treningowego"));

        Workout workout = new Workout();
        workout.setWorkoutDate(day.getDate());
        workout.setStartTime(workoutDTO.getStartTime());
        workout.setEndTime(workoutDTO.getEndTime());
        workout.setIntensity(Workout.Intensity.valueOf(workoutDTO.getIntensity()));
        workout.setNotes(workoutDTO.getNotes());
        workout.setUser(user);
        workout.setTrainingPlanDay(day);

        // 🔥 Filtrujemy tylko ćwiczenia z status == completed
        Set<WorkoutExercise> workoutExercises = day.getExercises().stream()
                .filter(planExercise -> planExercise.getStatus() == PlanExercise.Status.completed) // 👈 Filtrowanie
                .map(planExercise -> {
                    WorkoutExercise we = new WorkoutExercise();
                    we.setExercise(planExercise.getExercise());
                    we.setWorkout(workout);
                    if (planExercise.getExercise().getType() == Exercise.ExerciseType.STRENGTH) {
                        we.setSets(planExercise.getSets());
                        we.setReps(planExercise.getReps());
                        we.setWeight(planExercise.getWeight());
                    } else {
                        we.setDuration(planExercise.getDuration());
                        we.setDistance(planExercise.getDistance());
                    }
                    return we;
                }).collect(Collectors.toSet());

        if (workoutExercises.isEmpty()) {
            return ResponseEntity.badRequest().body("Brak ukończonych ćwiczeń do dodania!");
        }

        workout.setWorkoutExercises(workoutExercises);
        workoutRepository.save(workout);

        return ResponseEntity.ok("Workout został zapisany!");
    }
    @GetMapping("/check-workout-exists/{dayId}")
    public ResponseEntity<Boolean> checkWorkoutExists(@PathVariable Long dayId) {
        boolean exists = workoutRepository.existsByTrainingPlanDay_IdDay(dayId);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/remove-workout-from-day")
    public ResponseEntity<?> removeWorkoutFromDay(@RequestParam Long dayId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));

        Workout workout = workoutRepository.findByUserAndTrainingPlanDay(user, trainingPlanDayRepository.findById(dayId)
                        .orElseThrow(() -> new RuntimeException("Nie znaleziono dnia treningowego")))
                .orElse(null);

        if (workout == null) {
            return ResponseEntity.badRequest().body("Workout nie istnieje dla tego dnia.");
        }

        workoutRepository.delete(workout);
        return ResponseEntity.ok("Workout został usunięty!");
    }




}
