package com.GoGym.controller;

import com.GoGym.module.*;
import com.GoGym.service.TrainerClientService;
import com.GoGym.service.TrainingPlanService;
import com.GoGym.service.UserService;
import com.GoGym.service.WorkoutService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class UserCalendarController {

    private final UserService userService;
    private final TrainingPlanService trainingPlanService;
    private final WorkoutService workoutService;
    private final TrainerClientService trainerClientService;

    @GetMapping("/calendar")
    public String getCalendar(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userService.findByEmail(userEmail);
        Long userId = user.getIdUser();

        // Pobierz dni treningowe i regeneracyjne z przypisaniem do planów treningowych
        List<TrainingPlanDay> trainingPlanDays = trainingPlanService.getTrainingDaysForUser(userId);

        // Mapa do przechowywania przypisanych kolorów dla planów
        Map<Long, String> planColors = new HashMap<>();
        List<String> colors = Arrays.asList("#007bff", "#28a745", "#17a2b8", "#ff5733", "#b5602f", "#6f42c1", "#e83e8c");
        int colorIndex = 0;

        List<Map<String, String>> trainingEvents = new ArrayList<>();

        for (TrainingPlanDay day : trainingPlanDays) {
            TrainingPlan plan = day.getTrainingPlan();

            if (!planColors.containsKey(plan.getIdPlan())) {
                planColors.put(plan.getIdPlan(), colors.get(colorIndex % colors.size()));
                colorIndex++;
            }

            boolean isCompleted = day.getStatus() == TrainingPlanDay.Status.completed;

            String eventType = (day.getDayType() == TrainingPlanDay.DayType.training) ? "Dzień Treningowy" : "Dzień Regeneracyjny";
            trainingEvents.add(Map.of(
                    "id", String.valueOf(plan.getIdPlan()),
                    "title", eventType + " - " + plan.getName(),
                    "start", day.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    "color", planColors.get(plan.getIdPlan()),
                    "type", "trainingPlan",
                    "completed", String.valueOf(isCompleted)
            ));
        }

        // Pobierz workouty użytkownika
        List<Map<String, String>> workoutEvents = workoutService.getWorkoutsForUser(userId)
                .stream()
                .map(workout -> Map.of(
                        "id", String.valueOf(workout.getIdWorkout()),
                        "title", "Workout",
                        "start", workout.getWorkoutDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        "color", "#ffc107",
                        "type", "workout"
                ))
                .collect(Collectors.toList());

        model.addAttribute("trainingEvents", trainingEvents);
        model.addAttribute("workoutEvents", workoutEvents);
        model.addAttribute("userId", userId); // Dodanie userId do modelu

        return "user-calendar";
    }
    @GetMapping("/trainer-calendar")
    public String getTrainerCalendar(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String trainerEmail = authentication.getName();
        User trainer = userService.findByEmail(trainerEmail);
        Long trainerId = trainer.getIdUser();

        // Pobranie listy podopiecznych trenera
        List<User> clients = trainerClientService.getTrainerClients(trainerId)
                .stream()
                .map(TrainerClient -> TrainerClient.getClient())
                .collect(Collectors.toList());

        // Mapa kolorów dla każdego klienta i jego planów
        Map<Long, Map<Long, String>> clientPlanColors = new HashMap<>();
        List<String> colors = Arrays.asList("#007bff", "#28a745", "#17a2b8", "#ff5733", "#ffc107", "#6f42c1", "#e83e8c");
        int colorIndex = 0;

        List<Map<String, String>> trainingEvents = new ArrayList<>();

        for (User client : clients) {
            if (!clientPlanColors.containsKey(client.getIdUser())) {
                clientPlanColors.put(client.getIdUser(), new HashMap<>());
            }

            List<TrainingPlanDay> trainingPlanDays = trainingPlanService.getTrainingDaysForUser(client.getIdUser());
            for (TrainingPlanDay day : trainingPlanDays) {
                TrainingPlan plan = day.getTrainingPlan();

                if (!clientPlanColors.get(client.getIdUser()).containsKey(plan.getIdPlan())) {
                    clientPlanColors.get(client.getIdUser()).put(plan.getIdPlan(), colors.get(colorIndex % colors.size()));
                    colorIndex++;
                }

                boolean isCompleted = day.getStatus() == TrainingPlanDay.Status.completed;

                String eventType = (day.getDayType() == TrainingPlanDay.DayType.training) ? "Dzień Treningowy" : "Dzień Regeneracyjny";
                trainingEvents.add(Map.of(
                        "id", String.valueOf(plan.getIdPlan()),
                        "title", eventType + " - " + client.getFirstName() + " " + client.getLastName() + " (" + plan.getName() + ")",
                        "start", day.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        "color", clientPlanColors.get(client.getIdUser()).get(plan.getIdPlan()),
                        "type", "trainingPlan",
                        "clientId", String.valueOf(client.getIdUser()),
                        "completed", String.valueOf(isCompleted)
                ));
            }

        }

        model.addAttribute("trainingEvents", trainingEvents);
        model.addAttribute("trainerId", trainerId);

        return "trainer-calendar";
    }
}
