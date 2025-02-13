package com.GoGym.controller;

import com.GoGym.dto.WorkoutDTO;
import com.GoGym.module.*;
import com.GoGym.repository.*;
import com.GoGym.service.ExerciseService;
import com.GoGym.service.WorkoutService;
import com.GoGym.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final ExerciseRepository exerciseRepository;


    @Autowired
    public WorkoutController(WorkoutService workoutService, ExerciseService exerciseService, UserRepository userRepository, TrainingPlanDayRepository trainingPlanDayRepository, WorkoutRepository workoutRepository, WorkoutExerciseRepository workoutExerciseRepository, ExerciseRepository exerciseRepository) {
        this.workoutService = workoutService;
        this.exerciseService = exerciseService;
        this.userRepository = userRepository;
        this.trainingPlanDayRepository = trainingPlanDayRepository;
        this.workoutRepository = workoutRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.exerciseRepository = exerciseRepository;
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteWorkout(@PathVariable Long id, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono treningu o ID: " + id));

        if (!workout.getUser().equals(user)) {
            return ResponseEntity.status(403).body("Nie masz uprawnień do usunięcia tego treningu.");
        }

        workoutRepository.delete(workout);
        return ResponseEntity.ok("Trening został usunięty.");
    }

    @GetMapping("/edit/{id}")
    public String editWorkoutForm(@PathVariable Long id, Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono treningu o ID: " + id));

        if (!workout.getUser().equals(user)) {
            throw new RuntimeException("Nie masz uprawnień do edycji tego treningu.");
        }

        List<Exercise> allExercises = exerciseRepository.findAll();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String allExercisesJson = objectMapper.writeValueAsString(allExercises); // ✅ Konwersja na JSON
            model.addAttribute("allExercisesJson", allExercisesJson);
        } catch (Exception e) {
            throw new RuntimeException("Błąd serializacji ćwiczeń do JSON", e);
        }

        model.addAttribute("workout", workout);
        model.addAttribute("allExercises", allExercises);
        return "edit-workout";
    }


    @PostMapping("/update")
    public String updateWorkout(@ModelAttribute Workout workout,
                                @RequestParam(required = false) List<Long> existingExerciseIds,
                                @RequestParam(required = false) List<Long> deletedExerciseIds,
                                @RequestParam(required = false) List<Long> exerciseIds,
                                @RequestParam(required = false) List<Integer> sets,
                                @RequestParam(required = false) List<Integer> reps,
                                @RequestParam(required = false) List<Double> weight,
                                @RequestParam(required = false) List<String> durations,
                                @RequestParam(required = false) List<Double> distances,
                                Principal principal) {

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));

        Workout existingWorkout = workoutRepository.findById(workout.getIdWorkout())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono treningu o ID: " + workout.getIdWorkout()));

        if (!existingWorkout.getUser().equals(user)) {
            throw new RuntimeException("Nie masz uprawnień do edycji tego treningu.");
        }

        // ✅ 1. AKTUALIZACJA PODSTAWOWYCH DANYCH TRENINGU
        existingWorkout.setWorkoutDate(workout.getWorkoutDate());
        existingWorkout.setIntensity(workout.getIntensity());
        existingWorkout.setStartTime(workout.getStartTime());
        existingWorkout.setEndTime(workout.getEndTime());
        existingWorkout.setNotes(workout.getNotes());

        // ✅ 2. USUNIĘCIE ĆWICZEŃ, KTÓRE UŻYTKOWNIK USUNĄŁ
        if (deletedExerciseIds != null) {
            workoutExerciseRepository.deleteAllById(deletedExerciseIds);
        }

        // ✅ 3. AKTUALIZACJA ISTNIEJĄCYCH ĆWICZEŃ
        if (existingExerciseIds != null) {
            for (int i = 0; i < existingExerciseIds.size(); i++) {
                WorkoutExercise workoutExercise = workoutExerciseRepository.findById(existingExerciseIds.get(i))
                        .orElseThrow(() -> new RuntimeException("Nie znaleziono ćwiczenia"));

                Exercise exercise = exerciseRepository.findById(exerciseIds.get(i))
                        .orElseThrow(() -> new RuntimeException("Nie znaleziono ćwiczenia"));

                workoutExercise.setExercise(exercise); // ✅ ZAMIANA ĆWICZENIA NA NOWE

                if (exercise.getType() == Exercise.ExerciseType.STRENGTH) {
                    workoutExercise.setSets(sets.get(i));
                    workoutExercise.setReps(reps.get(i));
                    workoutExercise.setWeight(weight.get(i));
                    workoutExercise.setDuration(null);
                    workoutExercise.setDistance(null);
                } else if (exercise.getType() == Exercise.ExerciseType.CARDIO) {
                    workoutExercise.setDuration(workoutService.parseDuration(durations.get(i)));
                    workoutExercise.setDistance(distances.get(i));
                    workoutExercise.setSets(null);
                    workoutExercise.setReps(null);
                    workoutExercise.setWeight(null);
                }

                workoutExerciseRepository.save(workoutExercise);
            }
        }

        // ✅ 4. DODANIE NOWYCH ĆWICZEŃ
        if (exerciseIds != null) {
            int existingSize = (existingExerciseIds != null) ? existingExerciseIds.size() : 0;
            for (int i = 0; i < exerciseIds.size(); i++) {
                if (i < existingSize) continue; // Pomijamy już istniejące ćwiczenia

                Exercise exercise = exerciseRepository.findById(exerciseIds.get(i))
                        .orElseThrow(() -> new RuntimeException("Nie znaleziono ćwiczenia"));

                WorkoutExercise newExercise = new WorkoutExercise();
                newExercise.setWorkout(existingWorkout);
                newExercise.setExercise(exercise);

                if (exercise.getType() == Exercise.ExerciseType.STRENGTH) {
                    newExercise.setSets(sets.get(i));
                    newExercise.setReps(reps.get(i));
                    newExercise.setWeight(weight.get(i));
                    newExercise.setDuration(null);
                    newExercise.setDistance(null);
                } else if (exercise.getType() == Exercise.ExerciseType.CARDIO) {
                    newExercise.setDuration(workoutService.parseDuration(durations.get(i)));
                    newExercise.setDistance(distances.get(i));
                    newExercise.setSets(null);
                    newExercise.setReps(null);
                    newExercise.setWeight(null);
                }

                workoutExerciseRepository.save(newExercise);
            }
        }

        workoutRepository.save(existingWorkout);
        return "redirect:/workouts/" + workout.getIdWorkout();
    }

}
