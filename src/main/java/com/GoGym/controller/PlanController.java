package com.GoGym.controller;

import com.GoGym.dto.ExerciseDTO;
import com.GoGym.dto.TrainingPlanDTO;
import com.GoGym.dto.TrainingPlanDayDTO;
import com.GoGym.module.*;
import com.GoGym.repository.*;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.service.RequestService;
import com.GoGym.service.TrainerClientService;
import com.GoGym.service.TrainingPlanService;
import com.GoGym.service.TrainingService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;


@Controller
@Slf4j
@AllArgsConstructor
public class PlanController {

    private TrainingPlanService trainingPlanService;
    private UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final TrainingPlanRepository trainingPlanRepository;
    private final PlanExerciseRepository planExerciseRepository;
    private final TrainingPlanDayRepository trainingPlanDayRepository;
    private final TrainingService trainingService;
    private final TrainerClientService trainerClientService;

    @GetMapping("/user-plans")
    public String getUserPlans(@RequestParam Long idUser, Model model) {
        List<TrainingPlan> plans = trainingPlanService.findPlansByIdClient(idUser);
        // Sortowanie: ukończone plany na końcu
        plans.sort(Comparator.comparing(plan -> plan.getStatus() == TrainingPlan.Status.completed));
        List<TrainerClient> clientTrainers = trainerClientService.getClientTrainers(idUser); // Pobranie klientów trenera
        List<User> trainers = clientTrainers.stream().map(TrainerClient::getTrainer).toList(); // Przekształcenie na listę użytkowników

        model.addAttribute("plans", plans);
        model.addAttribute("trainers", trainers);  // Dodanie klientów do modelu
        return "user-plans"; // Zwraca widok user-plans.html
    }

    @GetMapping("/trainer-plans")
    public String getTrainerPlans(@RequestParam Long idUser, Model model) {
        List<TrainingPlan> plans = trainingPlanService.findPlansByIdTrainer(idUser);
        plans.sort(Comparator.comparing(plan -> plan.getStatus() == TrainingPlan.Status.completed));

        List<TrainerClient> trainerClients = trainerClientService.getTrainerClients(idUser); // Pobranie klientów trenera
        List<User> clients = trainerClients.stream().map(TrainerClient::getClient).toList(); // Przekształcenie na listę użytkowników

        model.addAttribute("plans", plans);
        model.addAttribute("clients", clients);  // Dodanie klientów do modelu

        return "trainer-plans";
    }


    @GetMapping("/{id}/create-plan")
    public String createPlan(@PathVariable Long id, Model model, Authentication authentication) {
        // Pobierz dane klienta na podstawie ID
        User client = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta o ID: " + id));

        // Dodaj klienta do modelu
        model.addAttribute("client", client);

        // Dodaj listę dostępnych ćwiczeń
        List<Exercise> exercises = exerciseRepository.findAll();
        model.addAttribute("exercises", exercises);

        // Pobierz ID zalogowanego trenera
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();

        Long trainerId = loggedInUser.getIdUser();

        // Przekaż ID trenera do widoku
        model.addAttribute("trainerId", trainerId);

        return "create-plan"; // Wyświetlenie widoku create-plan.html
    }


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
            @RequestParam(required = false) List<String> duration,  // 🔥 Przyjmujemy jako String (hh:mm:ss lub mm:ss)
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
                        Exercise currentExercise = exerciseRepository.findById(exerciseIds.get(j)).orElseThrow(RuntimeException::new);
                        exercise.setExercise(currentExercise);

                        if (currentExercise.getType() == Exercise.ExerciseType.CARDIO) {
                            exercise.setSets(null);
                            exercise.setReps(null);
                            exercise.setWeight(null);
                            exercise.setDuration((duration != null && duration.size() > j) ? parseDuration(duration.get(j)) : null);
                            exercise.setDistance((distance != null && distance.size() > j) ? distance.get(j) : null);
                        } else {
                            exercise.setSets((sets != null && sets.size() > j) ? sets.get(j) : null);
                            exercise.setReps((reps != null && reps.size() > j) ? reps.get(j) : null);
                            exercise.setWeight((weight != null && weight.size() > j) ? weight.get(j) : null);
                            exercise.setDuration(null);
                            exercise.setDistance(null);
                        }

                        exercise.setStatus(PlanExercise.Status.notCompleted);
                        exercise.setTrainingPlan(plan);
                        exercise.setTrainingPlanDay(day);

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

        String[] parts = duration.split(":");
        if (parts.length == 2) {
            return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        } else if (parts.length == 3) {
            return Integer.parseInt(parts[0]) * 3600 + Integer.parseInt(parts[1]) * 60 + Integer.parseInt(parts[2]);
        }
        throw new IllegalArgumentException("Niepoprawny format czasu: " + duration);
    }

//    private boolean isNextExerciseForCurrentDay(List<String> dayType, int currentDayIndex, int exerciseIndex) {
//        // Sprawdza, czy kolejne ćwiczenie przypada na inny dzień treningowy
//        int remainingDays = dayType.size() - currentDayIndex - 1;
//        int remainingExercises = exerciseIndex;
//        return remainingExercises >= remainingDays;
//    }

    @PostMapping("/update-exercise-status/{exerciseId}")
    @ResponseBody
    public Map<String, Object> updateExerciseStatus(@PathVariable Long exerciseId, @RequestParam boolean completed) {
        PlanExercise.Status newStatus = completed ? PlanExercise.Status.completed : PlanExercise.Status.notCompleted;
        trainingPlanService.updateExerciseStatus(exerciseId, newStatus);

        // Przygotowanie odpowiedzi
        Map<String, Object> response = new HashMap<>();
        response.put("exerciseId", exerciseId);
        response.put("status", newStatus.name());
        return response;
    }


    @PostMapping("/update-day-status/{dayId}")
    @ResponseBody
    public Map<String, Object> updateDayStatus(@PathVariable Long dayId) {
        TrainingPlanDay day = trainingPlanDayRepository.findById(dayId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono dnia o ID: " + dayId));

        // Sprawdzenie, czy wszystkie ćwiczenia są ukończone
        boolean allExercisesCompleted = day.getExercises().stream()
                .allMatch(exercise -> exercise.getStatus() == PlanExercise.Status.completed);

        // Aktualizacja statusu dnia
        day.setStatus(allExercisesCompleted ? TrainingPlanDay.Status.completed : TrainingPlanDay.Status.notCompleted);
        trainingPlanDayRepository.save(day);

        // Przygotowanie odpowiedzi
        Map<String, Object> response = new HashMap<>();
        response.put("dayId", day.getIdDay());
        response.put("status", day.getStatus().name()); // Zwróć status jako string
        return response;
    }


    @PostMapping("/update-plan-status/{planId}")
    @ResponseBody
    public Map<String, Object> getPlanStatus(@PathVariable Long planId) {
        TrainingPlan plan = trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono planu o ID: " + planId));

        // Sprawdzenie, czy wszystkie dni są ukończone
        boolean allDaysCompleted = plan.getTrainingPlanDays().stream()
                .allMatch(day -> day.getStatus() == TrainingPlanDay.Status.completed);

        // Aktualizacja statusu planu
        plan.setStatus(allDaysCompleted ? TrainingPlan.Status.completed : TrainingPlan.Status.active);
        trainingPlanRepository.save(plan);

        // Przygotowanie odpowiedzi
        Map<String, Object> response = new HashMap<>();
        response.put("planId", plan.getIdPlan());
        response.put("status", plan.getStatus().name()); // Zwróć status jako string
        return response;
    }

    @PostMapping("/update-rest-day-status/{dayId}")
    @ResponseBody
    public Map<String, Object> updateRestDayStatus(@PathVariable Long dayId) {
        TrainingPlanDay day = trainingPlanDayRepository.findById(dayId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono dnia odpoczynku o ID: " + dayId));

        // Przełącz status dnia odpoczynku
        TrainingPlanDay.Status newStatus = day.getStatus() == TrainingPlanDay.Status.completed
                ? TrainingPlanDay.Status.notCompleted
                : TrainingPlanDay.Status.completed;

        day.setStatus(newStatus);
        trainingPlanDayRepository.save(day);

        // Przygotowanie odpowiedzi
        Map<String, Object> response = new HashMap<>();
        response.put("dayId", day.getIdDay());
        response.put("status", newStatus.name());
        return response;
    }

        @GetMapping("/trainer-plans/edit/{id}")
        public String editPlan(@PathVariable Long id, Model model) {
            // Pobierz plan treningowy na podstawie ID
            TrainingPlan plan = trainingPlanRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono planu o ID: " + id));

            // Utwórz DTO do przekazania do widoku
            TrainingPlanDTO planDTO = new TrainingPlanDTO();
            planDTO.setIdPlan(plan.getIdPlan());
            planDTO.setName(plan.getName());
            planDTO.setDescription(plan.getDescription());

            // Mapuj dni treningowe
            List<TrainingPlanDayDTO> trainingPlanDays = plan.getTrainingPlanDays().stream().map(day -> {
                TrainingPlanDayDTO dayDTO = new TrainingPlanDayDTO();
                dayDTO.setIdDay(day.getIdDay());
                dayDTO.setDayType(day.getDayType());
                dayDTO.setNotes(day.getNotes());

                // Mapuj ćwiczenia dnia
                List<ExerciseDTO> exercises = day.getExercises().stream().map(exercise -> {
                    ExerciseDTO exerciseDTO = new ExerciseDTO();
                    exerciseDTO.setIdExercise(exercise.getExercise().getIdExercise());
                    exerciseDTO.setIdPlanExercise(exercise.getId());
                    exerciseDTO.setName(exercise.getExercise().getName());
                    exerciseDTO.setSets(exercise.getSets());
                    exerciseDTO.setReps(exercise.getReps());
                    exerciseDTO.setWeight(exercise.getWeight());
                    exerciseDTO.setDuration(exercise.getDuration());
                    exerciseDTO.setDistance(exercise.getDistance());
                    exerciseDTO.setType(exercise.getExercise().getType().name()); // 🔥 Dodaj typ ćwiczenia
                    return exerciseDTO;
                }).toList();


                dayDTO.setExercises(exercises);
                return dayDTO;
            }).toList();

            planDTO.setTrainingPlanDays(trainingPlanDays);

            // Dodaj DTO do modelu
            model.addAttribute("plan", planDTO);

            // Dodaj listę ćwiczeń do modelu
            List<Exercise> exercises = exerciseRepository.findAll();
            model.addAttribute("exercises", exercises);

            return "edit-plan"; // Wyświetlenie widoku edit-plan.html
        }

    @PutMapping("/trainer-plans/update/{id}")
    public ResponseEntity<TrainingPlanDTO> updatePlan(@PathVariable Long id, @RequestBody TrainingPlanDTO trainingPlanDTO) {
        log.info("Otrzymano żądanie edycji planu ID: " + id);
        TrainingPlan updatedTrainingPlan = trainingService.updateTrainingPlan(id, trainingPlanDTO);

        // 🔥 Konwersja `updatedTrainingPlan` na `TrainingPlanDTO`
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
                exerciseDTO.setIdPlanExercise(exercise.getId()); // Ustawiamy poprawne ID!
                exerciseDTO.setIdExercise(exercise.getExercise().getIdExercise());
                return exerciseDTO;
            }).toList();

            dayDTO.setExercises(exercises);
            return dayDTO;
        }).toList();

        responseDTO.setTrainingPlanDays(trainingPlanDays);

        return ResponseEntity.ok(responseDTO); // 🔥 Zwrot nowo zapisanych ID!
    }

    @PostMapping("/trainer-plans/delete/{id}")
    public ResponseEntity<?> deletePlan(@PathVariable Long id) {
        try {
            trainingPlanService.deleteTrainingPlan(id);
            return ResponseEntity.ok().body("Plan został usunięty.");
        } catch (Exception e) {
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

        // Wymuszenie załadowania ćwiczeń
        Hibernate.initialize(day.getExercises());

        // Debugging: Sprawdź, czy ćwiczenia się ładują
        if (day.getExercises() == null || day.getExercises().isEmpty()) {
            System.out.println("Brak ćwiczeń dla dnia: " + dayId);
        } else {
            System.out.println("Ćwiczenia dla dnia " + dayId + ": " + day.getExercises().size());
        }

        return ResponseEntity.ok(day);
    }




}