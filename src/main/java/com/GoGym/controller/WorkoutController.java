package com.GoGym.controller;

import com.GoGym.dto.WorkoutDTO;
import com.GoGym.module.*;
import com.GoGym.repository.ExerciseRepository;
import com.GoGym.repository.TrainingPlanDayRepository;
import com.GoGym.repository.UserRepository;
import com.GoGym.repository.WorkoutExerciseRepository;
import com.GoGym.repository.WorkoutRepository;
import com.GoGym.service.ExerciseService;
import com.GoGym.service.WorkoutService;
import com.GoGym.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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
@Slf4j
@RequestMapping("/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;
    private final ExerciseService exerciseService;
    private final UserRepository userRepository;
    private final TrainingPlanDayRepository trainingPlanDayRepository;
    private final WorkoutRepository workoutRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutController(WorkoutService workoutService,
                             ExerciseService exerciseService,
                             UserRepository userRepository,
                             TrainingPlanDayRepository trainingPlanDayRepository,
                             WorkoutRepository workoutRepository,
                             WorkoutExerciseRepository workoutExerciseRepository,
                             ExerciseRepository exerciseRepository) {
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
        model.addAttribute("workout", new Workout());
        model.addAttribute("exercises", exercises);
        return "create-workout";
    }

    @PostMapping("/create")
    public String createWorkout(@ModelAttribute Workout workout,
                                @RequestParam List<Long> exerciseIds,
                                @RequestParam(required = false) List<Integer> strengthSets,
                                @RequestParam(required = false) List<Integer> reps,
                                @RequestParam(required = false) List<Double> weight,
                                @RequestParam(required = false) List<Integer> cardioSets,
                                @RequestParam(required = false) List<String> durations,
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
            Workout savedWorkout = workoutService.addWorkoutWithExercises(
                    workout, exerciseIds, strengthSets, reps, weight, cardioSets, durations, distances
            );
            return "redirect:/workouts/user-workouts/" + savedWorkout.getIdWorkout();
        } catch (Exception e) {
            log.error("Błąd przy tworzeniu treningu", e);
            model.addAttribute("error", "Nie udało się stworzyć workoutu: " + e.getMessage());
            return "create-workout";
        }
    }

    @GetMapping("/user-workouts/{id}")
    public String getWorkoutDetails(@PathVariable Long id, Model model, Principal principal) {
        Workout workout = workoutService.getWorkoutById(id);
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));

        if (!workout.getUser().equals(currentUser)) {
            throw new RuntimeException("Nie masz uprawnień do przeglądania tego treningu.");
        }

        model.addAttribute("workout", workout);
        return "workout-details";
    }

    @GetMapping("/user-workouts")
    public String getUserWorkouts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Pageable pageable = PageRequest.of(page, 20, Sort.by("workoutDate").descending());

        Page<Workout> workouts;
        if (date != null) {
            workouts = workoutService.getWorkoutsByUserAndDate(user, date, pageable);
        } else {
            workouts = workoutService.getWorkoutsByUserPage(user, pageable);
        }

        model.addAttribute("workouts", workouts);
        model.addAttribute("selectedDate", date);

        return "user-workouts";
    }

    @PostMapping("/add-workout-from-day")
    public ResponseEntity<?> addWorkoutFromDay(@RequestBody WorkoutDTO workoutDTO, Principal principal) {
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

        Set<WorkoutExercise> workoutExercises = day.getExercises().stream()
                .filter(planExercise -> planExercise.getStatus() == PlanExercise.Status.completed)
                .map(planExercise -> {
                    WorkoutExercise we = new WorkoutExercise();
                    we.setExercise(planExercise.getExercise());
                    we.setWorkout(workout);
                    if (planExercise.getExercise().getType() == Exercise.ExerciseType.STRENGTH) {
                        we.setSets(planExercise.getSets());
                        we.setReps(planExercise.getReps());
                        we.setWeight(planExercise.getWeight());
                    } else if (planExercise.getExercise().getType() == Exercise.ExerciseType.CARDIO) {
                        we.setSets(planExercise.getSets());
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
        TrainingPlanDay day = trainingPlanDayRepository.findById(dayId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono dnia treningowego"));
        Workout workout = workoutRepository.findByUserAndTrainingPlanDay(user, day)
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
            String allExercisesJson = objectMapper.writeValueAsString(allExercises);
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
                                @RequestParam(required = false) List<Integer> strengthSets,
                                @RequestParam(required = false) List<Integer> reps,
                                @RequestParam(required = false) List<Double> weight,
                                @RequestParam(required = false) List<Integer> cardioSets,
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
        existingWorkout.setWorkoutDate(workout.getWorkoutDate());
        existingWorkout.setIntensity(workout.getIntensity());
        existingWorkout.setStartTime(workout.getStartTime());
        existingWorkout.setEndTime(workout.getEndTime());
        existingWorkout.setNotes(workout.getNotes());

        if (deletedExerciseIds != null) {
            workoutExerciseRepository.deleteAllById(deletedExerciseIds);
        }

        // Deklarujemy liczniki raz – dla istniejących oraz nowych wierszy
        int strengthIndex = 0;
        int cardioIndex = 0;
        if (existingExerciseIds != null) {
            for (int i = 0; i < existingExerciseIds.size(); i++) {
                WorkoutExercise workoutExercise = workoutExerciseRepository.findById(existingExerciseIds.get(i))
                        .orElseThrow(() -> new RuntimeException("Nie znaleziono ćwiczenia"));
                Exercise exercise = exerciseRepository.findById(exerciseIds.get(i))
                        .orElseThrow(() -> new RuntimeException("Nie znaleziono ćwiczenia"));
                workoutExercise.setExercise(exercise);
                if (exercise.getType() == Exercise.ExerciseType.STRENGTH) {
                    workoutExercise.setSets(strengthSets.get(strengthIndex));
                    workoutExercise.setReps(reps.get(strengthIndex));
                    workoutExercise.setWeight(weight.get(strengthIndex));
                    workoutExercise.setDuration(null);
                    workoutExercise.setDistance(null);
                    strengthIndex++;
                } else if (exercise.getType() == Exercise.ExerciseType.CARDIO) {
                    workoutExercise.setSets(cardioSets.get(cardioIndex));
                    workoutExercise.setDuration(workoutService.parseDuration(durations.get(cardioIndex)));
                    workoutExercise.setDistance(distances.get(cardioIndex));
                    workoutExercise.setReps(null);
                    workoutExercise.setWeight(null);
                    cardioIndex++;
                }
                workoutExerciseRepository.save(workoutExercise);
            }
        }

        if (exerciseIds != null) {
            int existingSize = (existingExerciseIds != null) ? existingExerciseIds.size() : 0;
            // Nie resetujemy liczników – kontynuujemy od wartości po przetworzeniu istniejących wierszy
            for (int i = existingSize; i < exerciseIds.size(); i++) {
                Exercise exercise = exerciseRepository.findById(exerciseIds.get(i))
                        .orElseThrow(() -> new RuntimeException("Nie znaleziono ćwiczenia"));
                WorkoutExercise newExercise = new WorkoutExercise();
                newExercise.setWorkout(existingWorkout);
                newExercise.setExercise(exercise);
                if (exercise.getType() == Exercise.ExerciseType.STRENGTH) {
                    newExercise.setSets(strengthSets.get(strengthIndex));
                    newExercise.setReps(reps.get(strengthIndex));
                    newExercise.setWeight(weight.get(strengthIndex));
                    newExercise.setDuration(null);
                    newExercise.setDistance(null);
                    strengthIndex++;
                } else if (exercise.getType() == Exercise.ExerciseType.CARDIO) {
                    newExercise.setSets(cardioSets.get(cardioIndex));
                    newExercise.setDuration(workoutService.parseDuration(durations.get(cardioIndex)));
                    newExercise.setDistance(distances.get(cardioIndex));
                    newExercise.setReps(null);
                    newExercise.setWeight(null);
                    cardioIndex++;
                }
                workoutExerciseRepository.save(newExercise);
            }
        }
        workoutRepository.save(existingWorkout);
        return "redirect:/workouts/user-workouts/" + existingWorkout.getIdWorkout();
    }
}
