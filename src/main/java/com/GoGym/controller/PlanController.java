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
import java.util.*;

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
    public PlanController(TrainingPlanService trainingPlanService, UserRepository userRepository, ExerciseRepository exerciseRepository, TrainingPlanRepository trainingPlanRepository, PlanExerciseRepository planExerciseRepository, TrainingPlanDayRepository trainingPlanDayRepository) {
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
        // Sortowanie: ukończone plany na końcu
        plans.sort(Comparator.comparing(plan -> plan.getStatus() == TrainingPlan.Status.completed));
        model.addAttribute("plans", plans);
        return "user-plans"; // Zwraca widok user-plans.html
    }

    @GetMapping("/trainer-plans")
    public String getTrainerPlans(@RequestParam Long idUser, Model model) {
        List<TrainingPlan> plans = trainingPlanService.findPlansByIdTrainer(idUser);
        plans.sort(Comparator.comparing(plan -> plan.getStatus() == TrainingPlan.Status.completed));
        model.addAttribute("plans", plans);
        model.addAttribute("idUser", idUser);
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

        // Dodaj plan do modelu
        model.addAttribute("plan", plan);



        // Dodaj listę ćwiczeń do modelu
        List<Exercise> exercises = exerciseRepository.findAll();
        model.addAttribute("exercises", exercises);

        return "edit-plan"; // Wyświetlenie widoku edit-plan.html
    }
//@GetMapping("/trainer-plans/edit/{id}")
//public String editPlan(@PathVariable Long id, Model model) {
//    TrainingPlan plan = trainingPlanRepository.findById(id)
//            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono planu o ID: " + id));
//
//    List<Exercise> exercises = exerciseRepository.findAll();
//    List<TrainingPlanDay> trainingPlanDays = plan.getTrainingPlanDays();
//
//    // Wczytanie powiązanych ćwiczeń dla każdego dnia
//    for (TrainingPlanDay day : trainingPlanDays) {
//        List<PlanExercise> exercisesForDay = planExerciseRepository.findByTrainingPlanDay(day);
//        day.setExercises(exercisesForDay);
//    }
//
//    model.addAttribute("plan", plan);
//    model.addAttribute("exercises", exercises);
//    model.addAttribute("trainingPlanDays", trainingPlanDays); // Przekazanie dni z ćwiczeniami
//    return "edit-plan";
//}
//
//
//    @PostMapping("/trainer-plans/update/{id}")
//    public String editPlan(
//            @RequestParam Long planId,
//            @RequestParam String name,
//            @RequestParam String description,
//            @RequestParam String startDate,
//            @RequestParam List<String> dayType,
//            @RequestParam(required = false) List<String> notes,
//            @RequestParam(required = false) List<Long> exerciseIdsExisting,
//            @RequestParam(required = false) List<Long> exerciseIds,
//            @RequestParam(required = false) List<Integer> sets,
//            @RequestParam(required = false) List<Integer> reps,
//            @RequestParam(required = false) List<Integer> weight,
//            @RequestParam(required = false) List<Integer> exerciseDays,
//            @RequestParam(required = false) List<Long> dayIds) {
//
//
//        TrainingPlan plan = trainingPlanRepository.findById(planId)
//                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono planu o ID: " + planId));
//
//
//        // Aktualizacja głównych danych planu
//        plan.setName(name);
//        plan.setDescription(description);
//        plan.setStartDate(LocalDate.parse(startDate));
//
//        List<TrainingPlanDay> updatedDays = new ArrayList<>();
//
//        for (int i = 0; i < dayType.size(); i++) {
//            int finalI = i;
//            TrainingPlanDay day = dayIds != null && i < dayIds.size() && dayIds.get(i) != null
//                    ? trainingPlanDayRepository.findById(dayIds.get(i))
//                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono dnia o ID: " + dayIds.get(finalI)))
//                    : new TrainingPlanDay();
//
//            day.setTrainingPlan(plan);
//            day.setDayType(TrainingPlanDay.DayType.valueOf(dayType.get(i)));
//            day.setNotes(notes != null && notes.size() > i ? notes.get(i) : null);
//
//            // Ustawienie wartości domyślnej dla date, jeśli nie została podana
//            if (day.getDate() == null) {
//                day.setDate(plan.getStartDate().plusDays(i)); // Przykład: dzień po kolei od daty startowej planu
//            }
//
//            List<PlanExercise> updatedExercises = new ArrayList<>();
//            if (exerciseIds != null) {
//            for (int j = 0; j < exerciseIds.size(); j++) {
//                if (exerciseDays.get(j) == i) {
//                    PlanExercise exercise = new PlanExercise();
//                    exercise.setExercise(new Exercise(exerciseIds.get(j)));
//                    exercise.setSets(sets.get(j));
//                    exercise.setReps(reps.get(j));
//                    exercise.setWeight(weight != null && weight.size() > j ? weight.get(j) : null);
//                    exercise.setTrainingPlanDay(day);
//                    updatedExercises.add(exercise);
//                }
//            }
//            day.setExercises(updatedExercises);
//            updatedDays.add(day);
//        }}
//
//
//        plan.setTrainingPlanDays(updatedDays);
//        trainingPlanService.createTrainingPlan(plan);
//
//        return "redirect:/trainer-plans";
//    }

    @PostMapping("/trainer-plans/update/{id}")
    public String updatePlan(@PathVariable Long id,
                             @RequestParam String name,
                             @RequestParam String description,
                             @RequestParam List<String> dayType,
                             @RequestParam List<String> notes,
                             @RequestParam Map<Integer, List<Long>> exerciseIds,
                             @RequestParam Map<Integer, List<Integer>> sets,
                             @RequestParam Map<Integer, List<Integer>> reps,
                             @RequestParam(required = false) Map<Integer, List<Integer>> weight) {

        // Pobierz istniejący plan
        TrainingPlan plan = trainingPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono planu o ID: " + id));

        // Aktualizacja nazwy i opisu planu
        plan.setName(name);
        plan.setDescription(description);

        // Iteracja przez dni w planie
        for (int dayIndex = 0; dayIndex < dayType.size(); dayIndex++) {
            String dayTypeValue = dayType.get(dayIndex);
            String notesValue = notes.get(dayIndex);

            if (dayTypeValue == null || dayTypeValue.isEmpty()) {
                throw new IllegalArgumentException("Niepoprawny typ dnia: wartość jest pusta lub null dla indeksu " + dayIndex);
            }

            // Sprawdzanie istniejącego dnia lub tworzenie nowego
            TrainingPlanDay day = null;
            if (plan.getTrainingPlanDays().size() > dayIndex) {
                // Jeśli plan ma już dzień na tym indeksie, edytujemy go
                day = plan.getTrainingPlanDays().get(dayIndex);
                day.setDayType(TrainingPlanDay.DayType.valueOf(dayTypeValue));
                day.setNotes(notesValue);
            } else {
                // Tworzymy nowy dzień, jeśli go nie ma
                day = new TrainingPlanDay();
                day.setDayType(TrainingPlanDay.DayType.valueOf(dayTypeValue));
                day.setNotes(notesValue);

                // Ustawienie daty na dzień następny po ostatnim dniu w planie
                LocalDate lastDate = plan.getTrainingPlanDays().isEmpty() ? LocalDate.now() : plan.getTrainingPlanDays().get(plan.getTrainingPlanDays().size() - 1).getDate();
                day.setDate(lastDate.plusDays(1)); // Dodaj 1 dzień do ostatniej daty
            }

            // Ustawienie statusu dnia na 'notCompleted'
            day.setStatus(TrainingPlanDay.Status.notCompleted);
            day.setTrainingPlan(plan);

            // Lista ćwiczeń dla dnia
            List<PlanExercise> exercises = new ArrayList<>();
            List<Long> exerciseIdsForDay = exerciseIds.get(dayIndex);

            // Jeśli exerciseIdsForDay jest null, pomijamy dalszą logikę dodawania ćwiczeń
            if (exerciseIdsForDay != null) {
                // Usuwanie ćwiczeń, które już nie są w formularzu
                List<PlanExercise> exercisesToRemove = new ArrayList<>();
                for (PlanExercise existingExercise : day.getExercises()) {
                    if (!exerciseIdsForDay.contains(existingExercise.getExercise().getIdExercise())) {
                        exercisesToRemove.add(existingExercise);
                    }
                }
                day.getExercises().removeAll(exercisesToRemove); // Usuń ćwiczenia, które nie zostały przekazane w formularzu

                // Zaktualizuj lub dodaj ćwiczenia
                for (int exIndex = 0; exIndex < exerciseIdsForDay.size(); exIndex++) {
                    Long exerciseId = exerciseIdsForDay.get(exIndex);

                    // Sprawdzamy, czy ćwiczenie już istnieje w kolekcji ćwiczeń dla tego dnia
                    PlanExercise exercise = null;
                    for (PlanExercise existingExercise : day.getExercises()) {
                        if (existingExercise.getExercise().getIdExercise().equals(exerciseId)) {
                            exercise = existingExercise;
                            break;
                        }
                    }

                    // Jeśli ćwiczenie nie istnieje, tworzymy nowe
                    if (exercise == null) {
                        exercise = new PlanExercise();
                        exercise.setExercise(new Exercise(exerciseId));
                        exercise.setTrainingPlanDay(day);
                        day.getExercises().add(exercise);
                    }

                    // Zaktualizuj dane ćwiczenia
                    exercise.setSets(sets.get(dayIndex).get(exIndex));
                    exercise.setReps(reps.get(dayIndex).get(exIndex));

                    if (weight != null && weight.containsKey(dayIndex)) {
                        exercise.setWeight(weight.get(dayIndex).get(exIndex));
                    }

                    exercise.setStatus(PlanExercise.Status.notCompleted);
                }
            }

            // Dodaj lub zaktualizuj ćwiczenia w dniu
            day.setExercises(day.getExercises()); // Zaktualizowana lista ćwiczeń
            plan.getTrainingPlanDays().add(day); // Dodaj dzień do planu (jeśli jest nowy)
            System.out.println("Zaktualizowane ćwiczenia dla dnia " + dayIndex + ":");
            for (PlanExercise ex : day.getExercises()) {
                System.out.println("Ćwiczenie: " + ex.getExercise().getName() +
                        ", Sets: " + ex.getSets() +
                        ", Reps: " + ex.getReps() +
                        ", Weight: " + ex.getWeight());
            }

        }

        // Zapisz zaktualizowany plan
        trainingPlanRepository.save(plan);


        return "redirect:/trainer-plans?idUser=" + plan.getTrainer().getIdUser();
    }







}