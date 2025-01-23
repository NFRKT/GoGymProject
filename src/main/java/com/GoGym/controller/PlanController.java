package com.GoGym.controller;

import com.GoGym.module.*;
import com.GoGym.repository.*;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.service.RequestService;
import com.GoGym.service.TrainerClientService;
import com.GoGym.service.TrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller

public class PlanController {

    @Autowired
    private TrainingPlanService trainingPlanService;
    private UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final TrainingPlanRepository trainingPlanRepository;
    private final PlanExerciseRepository planExerciseRepository;
    private final TrainingPlanDayRepository trainingPlanDayRepository;

    @Autowired
    public PlanController(TrainingPlanService trainingPlanService,UserRepository userRepository, ExerciseRepository exerciseRepository, TrainingPlanRepository trainingPlanRepository, PlanExerciseRepository planExerciseRepository, TrainingPlanDayRepository trainingPlanDayRepository) {
        this.trainingPlanService = trainingPlanService;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.trainingPlanRepository = trainingPlanRepository;
        this.planExerciseRepository = planExerciseRepository;
        this.trainingPlanDayRepository = trainingPlanDayRepository;
    }

    @GetMapping("/user-plans")
    public String getUserPlans(@RequestParam Long idUser, Model model) {
        List<TrainingPlan> plans = trainingPlanService.findPlansByIdClient(idUser);
        model.addAttribute("plans", plans);
        return "plans"; // Zwraca widok plans.html
    }

    @GetMapping("/trainer-plans")
    public String getTrainerPlans(@RequestParam Long idUser, Model model) {
        List<TrainingPlan> plans = trainingPlanService.findPlansByIdTrainer(idUser);
        model.addAttribute("plans", plans);
        return "plans"; // Zwraca widok plans.html
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
            @RequestParam List<Integer> sets,
            @RequestParam List<Integer> reps,
            @RequestParam(required = false) List<Integer> weight,
            @RequestParam List<Integer> exerciseDays, // Dni przypisane do ćwiczeń
            @RequestParam String startDate, // Dodano datę początkową
            Model model) {

        // Tworzenie planu
        TrainingPlan plan = new TrainingPlan();
        plan.setName(name);
        plan.setDescription(description);
        plan.setIdClient(clientId);
        plan.setIdTrainer(trainerId);
        plan.setStatus(TrainingPlan.Status.active);
        LocalDate start = LocalDate.parse(startDate);
        plan.setStartDate(start);

        List<TrainingPlanDay> days = new ArrayList<>();

        // Tworzenie dni planu z datami
        for (int i = 0; i < dayType.size(); i++) {
            TrainingPlanDay day = new TrainingPlanDay();
            day.setTrainingPlan(plan);
            day.setDayType(TrainingPlanDay.DayType.valueOf(dayType.get(i)));
            day.setNotes(notes != null && notes.size() > i ? notes.get(i) : null);
            day.setStatus(TrainingPlanDay.Status.notCompleted);
            day.setDate(start.plusDays(i)); // Ustawianie daty dnia

            List<PlanExercise> exercisesForDay = new ArrayList<>();

            // Jeśli dzień to "training", przypisz odpowiednie ćwiczenia
            if (day.getDayType() == TrainingPlanDay.DayType.training) {
                for (int j = 0; j < exerciseIds.size(); j++) {
                    // Sprawdź, czy ćwiczenie należy do tego dnia
                    if (exerciseDays.get(j) == i) {
                        PlanExercise exercise = new PlanExercise();
                        exercise.setExercise(new Exercise(exerciseIds.get(j)));
                        exercise.setSets(sets.get(j));
                        exercise.setReps(reps.get(j));
                        exercise.setWeight((weight != null && weight.size() > j) ? weight.get(j) : null);
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

        plan.setExercises(new ArrayList<>());
        plan.setTrainingPlanDays(days);

        // Obliczanie daty końcowej
        plan.setEndDate(start.plusDays(dayType.size() - 1));

        trainingPlanService.createTrainingPlan(plan);

        return "redirect:/trainer-clients/trainer-panel";
    }

    /**
     * Sprawdza, czy kolejne ćwiczenie powinno być przypisane do bieżącego dnia na podstawie dnia tygodnia.
     */
    private boolean isNextExerciseForCurrentDay(List<String> dayType, int currentDayIndex, int exerciseIndex) {
        // Sprawdza, czy kolejne ćwiczenie przypada na inny dzień treningowy
        int remainingDays = dayType.size() - currentDayIndex - 1;
        int remainingExercises = exerciseIndex;
        return remainingExercises >= remainingDays;
    }

    @PostMapping("/update-exercise-status/{exerciseId}")
    @ResponseBody
    public void updateExerciseStatus(@PathVariable Long exerciseId, @RequestParam boolean completed) {
        PlanExercise.Status newStatus = completed ? PlanExercise.Status.completed : PlanExercise.Status.notCompleted;
        trainingPlanService.updateExerciseStatus(exerciseId, newStatus);
    }

    @PostMapping("/update-day-status/{dayId}")
    @ResponseBody
    public void updateDayStatus(@PathVariable Long dayId) {
        trainingPlanService.updateDayStatus(dayId);
    }

    @PostMapping("/update-plan-status/{planId}")
    @ResponseBody
    public void updatePlanStatus(@PathVariable Long planId) {
        trainingPlanService.updatePlanStatus(planId);
    }

    @GetMapping("/plan-details/{idPlan}")
    public String getPlanDetails(@PathVariable Long idPlan, Model model) {
        TrainingPlan plan = trainingPlanRepository.findById(idPlan)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono planu o ID: " + idPlan));
        model.addAttribute("plan", plan);
        return "plan-details"; // Widok dla szczegółów planu
    }



}