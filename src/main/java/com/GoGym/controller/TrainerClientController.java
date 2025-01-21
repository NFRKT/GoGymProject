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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/trainer-clients")
public class TrainerClientController {
    private TrainerClientService trainerClientService;
    private RequestService requestService;
    private TrainerClientRepository trainerClientRepository;
    private UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final TrainingPlanService trainingPlanService;
    private final TrainingPlanRepository trainingPlanRepository;
    private final PlanExerciseRepository planExerciseRepository;


    @Autowired
    public TrainerClientController(TrainerClientService trainerClientService, RequestService requestService, UserRepository userRepository, TrainerClientRepository trainerClientRepository, ExerciseRepository exerciseRepository, TrainingPlanService trainingPlanService, TrainingPlanRepository trainingPlanRepository, PlanExerciseRepository planExerciseRepository) {
        this.trainerClientService = trainerClientService;
        this.requestService = requestService;
        this.userRepository = userRepository;
        this.trainerClientRepository = trainerClientRepository;
        this.exerciseRepository = exerciseRepository;
        this.trainingPlanService = trainingPlanService;
        this.trainingPlanRepository = trainingPlanRepository;
        this.planExerciseRepository = planExerciseRepository;
    }

    @GetMapping("/trainer/{trainerId}")
    public List<TrainerClient> getTrainerClients(@PathVariable Long trainerId) {
        return trainerClientService.getTrainerClients(trainerId);
    }

    @GetMapping("/client/{clientId}")
    public List<TrainerClient> getClientTrainers(@PathVariable Long clientId) {
        return trainerClientService.getClientTrainers(clientId);
    }


    @GetMapping("/client-panel")
    public String clientPanel(Model model, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();
        Long clientId = loggedInUser.getIdUser();

        // Pobierz listę obecnych trenerów klienta
        List<TrainerClient> clientTrainers = trainerClientService.getClientTrainers(clientId);
        List<Long> currentTrainerIds = clientTrainers.stream()
                .map(tc -> tc.getTrainer().getIdUser())
                .toList();

        // Lista trenerów z oczekującymi zapytaniami
        List<Long> pendingRequestTrainerIds = requestService.getPendingRequestsByClient(clientId).stream()
                .map(request -> request.getTrainer().getIdUser())
                .toList();

        // Pobierz listę wszystkich dostępnych trenerów i odfiltruj obecnych
        List<User> availableTrainers = trainerClientService.getAllTrainers().stream()
                .filter(trainer -> !currentTrainerIds.contains(trainer.getIdUser()))
                .toList();

        model.addAttribute("clientTrainers", clientTrainers);
        model.addAttribute("trainers", availableTrainers);
        model.addAttribute("clientId", clientId);
        model.addAttribute("pendingTrainerIds", pendingRequestTrainerIds); // ID trenerów z oczekującymi zapytaniami
        model.addAttribute("currentTrainerIds", currentTrainerIds); // ID obecnych trenerów

        // Dodanie listy wysłanych zapytań
        List<Request> requests = requestService.getRequestsByClientId(clientId);
        model.addAttribute("requests", requests);

        List<TrainerClient> trainerClients = trainerClientService.getClientTrainers(clientId);
        model.addAttribute("clientTrainers", clientTrainers);

        return "client-panel"; // Załaduj widok Thymeleaf
    }





    @GetMapping("/trainer-panel")
    public String trainerPanel(Authentication authentication, Model model) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInTrainer = userDetails.getUser(); // Pobranie użytkownika z CustomUserDetails
        Long trainerId = loggedInTrainer.getIdUser(); // Pobranie ID trenera

        List<TrainerClient> trainerClients = trainerClientService.getTrainerClients(trainerId);
        model.addAttribute("trainerClients", trainerClients);
        model.addAttribute("requests", requestService.getRequestsForTrainer(trainerId));
        return "trainer-panel";
    }

    @PostMapping("/rejectTrainer")
    public String rejectTrainer(@RequestParam Long trainerId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();

        // Znajdź relację klient-trener
        TrainerClient trainerClient = trainerClientRepository.findByTrainer_IdUserAndClient_IdUser(trainerId, loggedInUser.getIdUser())
                .orElseThrow(() -> new IllegalArgumentException("Nie masz przypisanego tego trenera"));

        // Usuń relację
        trainerClientRepository.delete(trainerClient);

        return "redirect:/trainer-clients/client-panel";
    }

    @PostMapping("/rejectClient")
    public String rejectClient(@RequestParam Long clientId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();

        // Znajdź relację klient-trener
        TrainerClient trainerClient = trainerClientRepository.findByTrainer_IdUserAndClient_IdUser(loggedInUser.getIdUser(), clientId)
                .orElseThrow(() -> new IllegalArgumentException("Nie masz przypisanego tego klienta"));

        // Usuń relację
        trainerClientRepository.delete(trainerClient);

        return "redirect:/trainer-clients/trainer-panel";
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
            Model model) {

        // Tworzenie planu
        TrainingPlan plan = new TrainingPlan();
        plan.setName(name);
        plan.setDescription(description);
        plan.setIdClient(clientId);
        plan.setIdTrainer(trainerId);
        plan.setStatus("active");

        List<TrainingPlanDay> days = new ArrayList<>();
        int exerciseIndex = 0; // Indeks do iterowania po ćwiczeniach

        // Tworzenie dni planu
        for (int i = 0; i < dayType.size(); i++) {
            TrainingPlanDay day = new TrainingPlanDay();
            day.setTrainingPlan(plan);
            day.setDayType(TrainingPlanDay.DayType.valueOf(dayType.get(i)));
            day.setNotes(notes != null && notes.size() > i ? notes.get(i) : null);
            day.setStatus(TrainingPlanDay.Status.notCompleted);

            // Przypisywanie ćwiczeń do dnia
            List<PlanExercise> exercisesForDay = new ArrayList<>();
            while (exerciseIndex < exerciseIds.size()) {
                // Dostosuj logikę grupowania na podstawie danych z formularza
                Long exerciseId = exerciseIds.get(exerciseIndex);
                Integer setCount = sets.get(exerciseIndex);
                Integer repCount = reps.get(exerciseIndex);
                Integer exerciseWeight = (weight != null && weight.size() > exerciseIndex) ? weight.get(exerciseIndex) : null;

                // Tworzenie nowego ćwiczenia
                PlanExercise exercise = new PlanExercise();
                exercise.setExercise(new Exercise(exerciseId));
                exercise.setSets(setCount);
                exercise.setReps(repCount);
                exercise.setWeight(exerciseWeight);
                exercise.setStatus(PlanExercise.Status.notCompleted);
                exercise.setTrainingPlan(plan);
                exercise.setTrainingPlanDay(day);

                exercisesForDay.add(exercise);

                exerciseIndex++; // Przejdź do następnego ćwiczenia
                // Logika zatrzymania pętli dla konkretnego dnia
                if (exerciseIndex % dayType.size() == 0) {
                    break; // Zatrzymaj przypisywanie ćwiczeń do bieżącego dnia
                }
            }
            day.setExercises(exercisesForDay);
            days.add(day);
        }

        plan.setExercises(new ArrayList<>()); // Pusty, bo ćwiczenia przypisywane są do dni
        plan.setTrainingPlanDays(days);

        trainingPlanService.createTrainingPlan(plan);

        return "redirect:/trainer-clients/trainer-panel";
    }



}
