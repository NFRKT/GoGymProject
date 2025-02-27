package com.GoGym.controller;

import com.GoGym.dto.ExerciseDTO;
import com.GoGym.dto.TrainingPlanDTO;
import com.GoGym.dto.TrainingPlanDayDTO;
import com.GoGym.module.*;
import com.GoGym.repository.*;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.service.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
@Controller
@Slf4j
@AllArgsConstructor
public class PlanController {

    private final TrainingPlanService trainingPlanService;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final TrainingPlanRepository trainingPlanRepository;
    private final PlanExerciseRepository planExerciseRepository;
    private final TrainingPlanDayRepository trainingPlanDayRepository;
    private final TrainingService trainingService;
    private final TrainerClientService trainerClientService;
    private final NotificationService notificationService;
    private final BadgeService badgeService;

    @PreAuthorize("hasAuthority('CLIENT')")
    @GetMapping("/user-plans")
    public String getUserActivePlans(@RequestParam Long idUser, Model model) {
        List<TrainingPlan> plans = trainingPlanService.findPlansByIdClient(idUser);

        List<TrainerClient> clientTrainers = trainerClientService.getClientTrainers(idUser);
        List<Long> activeTrainerIds = clientTrainers.stream()
                .map(tc -> tc.getTrainer().getIdUser())
                .toList();

        List<TrainingPlan> activeTrainerPlans = plans.stream()
                .filter(plan -> activeTrainerIds.contains(plan.getTrainer().getIdUser()))
                .toList();

        List<User> trainers = clientTrainers.stream()
                .map(TrainerClient::getTrainer)
                .toList();

        model.addAttribute("activePlans", activeTrainerPlans);
        model.addAttribute("trainers", trainers);
        model.addAttribute("idUser", idUser);

        return "user-plans";
    }

    @PreAuthorize("hasAuthority('CLIENT')")
    @GetMapping("/user-plans-archived")
    public String getUserArchivedPlans(@RequestParam Long idUser, Model model) {
        List<TrainingPlan> plans = trainingPlanService.findPlansByIdClient(idUser);

        List<TrainerClient> clientTrainers = trainerClientService.getClientTrainers(idUser);
        List<Long> activeTrainerIds = clientTrainers.stream()
                .map(tc -> tc.getTrainer().getIdUser())
                .toList();

        List<TrainingPlan> archivedPlans = plans.stream()
                .filter(plan -> !activeTrainerIds.contains(plan.getTrainer().getIdUser()))
                .toList();

        model.addAttribute("archivedPlans", archivedPlans);
        model.addAttribute("idUser", idUser);

        return "user-plans-archived";
    }

    @GetMapping("/trainer-plans")
    public String getTrainerPlans(@RequestParam Long idUser, Model model) {
        List<TrainingPlan> allPlans = trainingPlanService.findPlansByIdTrainer(idUser);

        List<TrainerClient> activeTrainerClients = trainerClientService.getTrainerClients(idUser);
        List<Long> activeClientIds = activeTrainerClients.stream()
                .map(tc -> tc.getClient().getIdUser())
                .toList();

        List<TrainingPlan> activeClientPlans = allPlans.stream()
                .filter(plan -> activeClientIds.contains(plan.getIdClient()))
                .toList();

        model.addAttribute("activePlans", activeClientPlans);
        model.addAttribute("clients", activeTrainerClients.stream().map(TrainerClient::getClient).toList());
        model.addAttribute("idUser", idUser);

        return "trainer-plans";
    }
    @PreAuthorize("hasAuthority('TRAINER')")
    @GetMapping("/trainer-plans-archived")
    public String getArchivedTrainerPlans(@RequestParam Long idUser, Model model) {
        List<TrainingPlan> allPlans = trainingPlanService.findPlansByIdTrainer(idUser);

        List<TrainerClient> activeTrainerClients = trainerClientService.getTrainerClients(idUser);
        List<Long> activeClientIds = activeTrainerClients.stream()
                .map(tc -> tc.getClient().getIdUser())
                .toList();

        List<TrainingPlan> archivedClientPlans = allPlans.stream()
                .filter(plan -> !activeClientIds.contains(plan.getIdClient()))
                .toList();

        model.addAttribute("archivedPlans", archivedClientPlans);
        model.addAttribute("idUser", idUser);

        return "trainer-plans-archived";
    }

    @PreAuthorize("hasAuthority('TRAINER')")
    @GetMapping("/{id}/create-plan")
    public String createPlan(@PathVariable Long id, Model model, Authentication authentication) {
        User client = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta o ID: " + id));

        model.addAttribute("client", client);

        List<Exercise> exercises = exerciseRepository.findAll();
        model.addAttribute("exercises", exercises);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();
        model.addAttribute("trainerId", loggedInUser.getIdUser());

        return "create-plan";
    }

    @PreAuthorize("hasAuthority('TRAINER')")
    @PostMapping("/create-plan")
    public String createPlan(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Long clientId,
            @RequestParam Long trainerId,
            @RequestParam List<String> dayType,
            @RequestParam(required = false) List<String> notes,
            @RequestParam List<Long> exerciseIds,
            @RequestParam(required = false) List<Integer> sets,
            @RequestParam(required = false) List<Integer> reps,
            @RequestParam(required = false) List<Double> weight,
            @RequestParam(required = false) List<String> duration,
            @RequestParam(required = false) List<Double> distance,
            @RequestParam List<Integer> exerciseDays,
            @RequestParam String startDate,
            Model model) {

        TrainingPlan plan = new TrainingPlan();
        plan.setName(name);
        plan.setDescription(description);
        plan.setIdClient(clientId);
        plan.setIdTrainer(trainerId);
        plan.setStatus(TrainingPlan.Status.active);
        LocalDate start = LocalDate.parse(startDate);
        plan.setStartDate(start);

        List<TrainingPlanDay> days = new ArrayList<>();
        for (int i = 0; i < dayType.size(); i++) {
            TrainingPlanDay day = new TrainingPlanDay();
            day.setTrainingPlan(plan);
            day.setDayType(TrainingPlanDay.DayType.valueOf(dayType.get(i)));
            day.setNotes(notes != null && notes.size() > i ? notes.get(i) : null);
            day.setStatus(TrainingPlanDay.Status.notCompleted);
            day.setDate(start.plusDays(i));

            List<PlanExercise> exercisesForDay = new ArrayList<>();
            if (day.getDayType() == TrainingPlanDay.DayType.training) {
                for (int j = 0; j < exerciseIds.size(); j++) {
                    if (exerciseDays.get(j) == i) {
                        PlanExercise exercise = new PlanExercise();
                        Exercise currentExercise = exerciseRepository.findById(exerciseIds.get(j))
                                .orElseThrow(RuntimeException::new);
                        exercise.setExercise(currentExercise);
                        exercise.setStatus(PlanExercise.Status.notCompleted);
                        exercise.setTrainingPlan(plan);
                        exercise.setTrainingPlanDay(day);

                        if (currentExercise.getType() == Exercise.ExerciseType.CARDIO) {
                            exercise.setSets(null);
                            exercise.setReps(null);
                            exercise.setWeight(null);
                            exercise.setDistance((distance != null && distance.size() > j) ? distance.get(j) : null);
                            exercise.setDuration((duration != null && duration.size() > j && !duration.get(j).isEmpty())
                                    ? parseDuration(duration.get(j))
                                    : null);
                        } else {
                            exercise.setSets((sets != null && sets.size() > j) ? sets.get(j) : null);
                            exercise.setReps((reps != null && reps.size() > j) ? reps.get(j) : null);
                            exercise.setWeight((weight != null && weight.size() > j) ? weight.get(j) : null);
                            exercise.setDuration(null);
                            exercise.setDistance(null);
                        }

                        exercisesForDay.add(exercise);
                    }
                }
            }
            day.setExercises(exercisesForDay);
            days.add(day);
        }

        plan.setTrainingPlanDays(days);
        plan.setEndDate(start.plusDays(dayType.size() - 1));

        trainingPlanService.createTrainingPlan(plan);
        return "redirect:/trainer-panel";
    }


    /**
     * Parsuje czas trwania z formatu "hh:mm:ss" lub "mm:ss" na liczbę sekund.
     */
    private Integer parseDuration(String duration) {
        if (duration == null || duration.isEmpty()) return null;

        try {
            String[] parts = duration.split(":");
            if (parts.length == 2) {
                return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
            } else if (parts.length == 3) {
                return Integer.parseInt(parts[0]) * 3600 + Integer.parseInt(parts[1]) * 60 + Integer.parseInt(parts[2]);
            } else {
                log.warn("Niepoprawny format czasu: " + duration);
            }
        } catch (NumberFormatException e) {
            log.error("Błąd parsowania czasu: " + duration, e);
        }

        return null;
    }


    @PostMapping("/update-exercise-status/{exerciseId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateExerciseStatus(@PathVariable Long exerciseId, @RequestParam boolean completed) {
        try {
            PlanExercise.Status newStatus = completed ? PlanExercise.Status.completed : PlanExercise.Status.notCompleted;
            trainingPlanService.updateExerciseStatus(exerciseId, newStatus);
            Map<String, Object> response = new HashMap<>();
            response.put("exerciseId", exerciseId);
            response.put("status", newStatus.name());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Błąd aktualizacji statusu ćwiczenia", e);
            return ResponseEntity.status(500).body(Map.of("error", "Błąd aktualizacji statusu ćwiczenia"));
        }
    }

    @PostMapping("/update-day-status/{dayId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateDayStatus(@PathVariable Long dayId) {
        try {
            TrainingPlanDay day = trainingPlanDayRepository.findById(dayId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono dnia o ID: " + dayId));
            boolean allExercisesCompleted = day.getExercises().stream()
                    .allMatch(exercise -> exercise.getStatus() == PlanExercise.Status.completed);
            TrainingPlanDay.Status previousStatus = day.getStatus();
            TrainingPlanDay.Status newStatus = allExercisesCompleted ? TrainingPlanDay.Status.completed : TrainingPlanDay.Status.notCompleted;
            day.setStatus(newStatus);
            trainingPlanDayRepository.save(day);
            trainingPlanService.updatePlanStatus(day.getTrainingPlan().getIdPlan());
            if (previousStatus != TrainingPlanDay.Status.completed && newStatus == TrainingPlanDay.Status.completed) {
                TrainingPlan plan = day.getTrainingPlan();
                User trainer = userRepository.findById(plan.getIdTrainer())
                        .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trenera o ID: " + plan.getIdTrainer()));
                User client = userRepository.findById(plan.getIdClient())
                        .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta o ID: " + plan.getIdClient()));
                int dayNumber = plan.getTrainingPlanDays().indexOf(day) + 1;
                notificationService.createNotification(trainer, client, "day_updated", "Dzień " + dayNumber + " w planie: " + plan.getName());
            }
            Map<String, Object> response = new HashMap<>();
            response.put("dayId", day.getIdDay());
            response.put("status", day.getStatus().name());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Błąd aktualizacji statusu dnia", e);
            return ResponseEntity.status(500).body(Map.of("error", "Błąd aktualizacji statusu dnia"));
        }
    }

    @PostMapping("/update-plan-status/{planId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePlanStatus(@PathVariable Long planId) {

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));
            trainingPlanService.updatePlanStatus(planId);
            TrainingPlan plan = trainingPlanRepository.findById(planId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono planu o ID: " + planId));
            Map<String, Object> response = new HashMap<>();
            response.put("planId", plan.getIdPlan());
            response.put("status", plan.getStatus().name());
            badgeService.checkAndAwardBadgesForUser(currentUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Błąd aktualizacji statusu planu", e);
            return ResponseEntity.status(500).body(Map.of("error", "Błąd aktualizacji statusu planu"));
        }


    }

    @PostMapping("/update-rest-day-status/{dayId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateRestDayStatus(@PathVariable Long dayId) {
        try {
            TrainingPlanDay day = trainingPlanDayRepository.findById(dayId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono dnia odpoczynku o ID: " + dayId));
            TrainingPlanDay.Status previousStatus = day.getStatus();
            TrainingPlanDay.Status newStatus = day.getStatus() == TrainingPlanDay.Status.completed
                    ? TrainingPlanDay.Status.notCompleted
                    : TrainingPlanDay.Status.completed;
            day.setStatus(newStatus);
            trainingPlanDayRepository.save(day);
            if (previousStatus != TrainingPlanDay.Status.completed && newStatus == TrainingPlanDay.Status.completed) {
                TrainingPlan plan = day.getTrainingPlan();
                User trainer = userRepository.findById(plan.getIdTrainer())
                        .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trenera o ID: " + plan.getIdTrainer()));
                User client = userRepository.findById(plan.getIdClient())
                        .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta o ID: " + plan.getIdClient()));
                int dayNumber = plan.getTrainingPlanDays().indexOf(day) + 1;
                notificationService.createNotification(trainer, client, "rest_day_completed", "Dzień " + dayNumber + " (Regeneracja) w planie: " + plan.getName());
            }
            Map<String, Object> response = new HashMap<>();
            response.put("dayId", day.getIdDay());
            response.put("status", newStatus.name());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Błąd aktualizacji statusu dnia odpoczynku", e);
            return ResponseEntity.status(500).body(Map.of("error", "Błąd aktualizacji statusu dnia odpoczynku"));
        }
    }

    @PreAuthorize("hasAuthority('TRAINER')")
    @GetMapping("/trainer-plans/edit/{id}")
    public String editPlan(@PathVariable Long id, Model model, Authentication authentication) {
        TrainingPlan plan = trainingPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono planu o ID: " + id));

        // Pobieramy zalogowanego użytkownika
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();

        // Sprawdzamy, czy trener edytuje swój własny plan
        if (!plan.getIdTrainer().equals(loggedInUser.getIdUser())) {
            log.warn("Użytkownik {} próbował edytować cudzy plan treningowy (ID: {})", loggedInUser.getIdUser(), id);
            return "redirect:/home";
        }

        TrainingPlanDTO planDTO = new TrainingPlanDTO();
        planDTO.setIdPlan(plan.getIdPlan());
        planDTO.setName(plan.getName());
        planDTO.setDescription(plan.getDescription());

        List<TrainingPlanDayDTO> trainingPlanDays = plan.getTrainingPlanDays().stream().map(day -> {
            TrainingPlanDayDTO dayDTO = new TrainingPlanDayDTO();
            dayDTO.setIdDay(day.getIdDay());
            dayDTO.setDayType(day.getDayType());
            dayDTO.setNotes(day.getNotes());
            List<ExerciseDTO> exercises = day.getExercises().stream().map(exercise -> {
                ExerciseDTO exerciseDTO = new ExerciseDTO();
                exerciseDTO.setIdPlanExercise(exercise.getId());
                exerciseDTO.setIdExercise(exercise.getExercise().getIdExercise());
                exerciseDTO.setName(exercise.getExercise().getName());
                exerciseDTO.setSets(exercise.getSets());
                exerciseDTO.setReps(exercise.getReps());
                exerciseDTO.setWeight(exercise.getWeight());
                exerciseDTO.setDuration(exercise.getDuration());
                exerciseDTO.setDistance(exercise.getDistance());
                exerciseDTO.setType(exercise.getExercise().getType().name());
                return exerciseDTO;
            }).toList();
            dayDTO.setExercises(exercises);
            return dayDTO;
        }).toList();

        planDTO.setTrainingPlanDays(trainingPlanDays);
        model.addAttribute("plan", planDTO);
        List<Exercise> exercises = exerciseRepository.findAll();
        model.addAttribute("exercises", exercises);

        return "edit-plan";
    }

    @PreAuthorize("hasAuthority('TRAINER')")
    @PutMapping("/trainer-plans/update/{id}")
    public ResponseEntity<TrainingPlanDTO> updatePlan(@PathVariable Long id, @RequestBody TrainingPlanDTO trainingPlanDTO) {
        try {
            log.info("Otrzymano żądanie edycji planu ID: " + id);
            TrainingPlan updatedTrainingPlan = trainingService.updateTrainingPlan(id, trainingPlanDTO);

            TrainingPlanDTO responseDTO = new TrainingPlanDTO();
            responseDTO.setIdPlan(updatedTrainingPlan.getIdPlan());
            responseDTO.setName(updatedTrainingPlan.getName());
            responseDTO.setDescription(updatedTrainingPlan.getDescription());

            List<TrainingPlanDayDTO> trainingPlanDays = updatedTrainingPlan.getTrainingPlanDays().stream().map(day -> {
                TrainingPlanDayDTO dayDTO = new TrainingPlanDayDTO();
                dayDTO.setIdDay(day.getIdDay());
                dayDTO.setDayType(day.getDayType());
                dayDTO.setNotes(day.getNotes());
                List<ExerciseDTO> exercises = day.getExercises().stream().map(exercise -> {
                    ExerciseDTO exerciseDTO = new ExerciseDTO();
                    exerciseDTO.setIdPlanExercise(exercise.getId());
                    exerciseDTO.setIdExercise(exercise.getExercise().getIdExercise());
                    return exerciseDTO;
                }).toList();
                dayDTO.setExercises(exercises);
                return dayDTO;
            }).toList();

            responseDTO.setTrainingPlanDays(trainingPlanDays);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Błąd przy aktualizacji planu", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PreAuthorize("hasAuthority('TRAINER')")
    @PostMapping("/trainer-plans/delete/{id}")
    public ResponseEntity<?> deletePlan(@PathVariable Long id) {
        try {
            trainingPlanService.deleteTrainingPlan(id);
            return ResponseEntity.ok().body("Plan został usunięty.");
        } catch (Exception e) {
            log.error("Błąd podczas usuwania planu", e);
            return ResponseEntity.status(500).body("Błąd podczas usuwania planu: " + e.getMessage());
        }
    }

    @GetMapping("/get-training-day/{dayId}")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> getTrainingDay(@PathVariable Long dayId) {
        Optional<TrainingPlanDay> dayOpt = trainingPlanDayRepository.findById(dayId);
        if (dayOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        TrainingPlanDay day = dayOpt.get();
        Hibernate.initialize(day.getExercises());
        if (day.getExercises() == null || day.getExercises().isEmpty()) {
            log.info("Brak ćwiczeń dla dnia: " + dayId);
        } else {
            log.info("Ćwiczenia dla dnia " + dayId + ": " + day.getExercises().size());
        }
        return ResponseEntity.ok(day);
    }
}
