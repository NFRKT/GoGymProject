package com.GoGym.controller;

import com.GoGym.module.TrainingPlanDay;
import com.GoGym.module.User;
import com.GoGym.module.Workout;
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
import java.util.List;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class UserCalendarController {

    private final UserService userService;
    private final TrainingPlanService trainingPlanService;
    private final WorkoutService workoutService;

    @GetMapping("/calendar")
    public String getCalendar(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userService.findByEmail(userEmail);

        // Pobierz wszystkie dni treningowe użytkownika
        List<String> trainingDays = trainingPlanService.getTrainingDaysForUser(user.getIdUser())
                .stream()
                .filter(day -> day.getDayType() == TrainingPlanDay.DayType.training)
                .map(day -> day.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .collect(Collectors.toList());

        // Pobierz wszystkie dni regeneracyjne użytkownika
        List<String> restDays = trainingPlanService.getTrainingDaysForUser(user.getIdUser())
                .stream()
                .filter(day -> day.getDayType() == TrainingPlanDay.DayType.rest)
                .map(day -> day.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .collect(Collectors.toList());

        // Pobierz dni, w których użytkownik dodał workout
        List<String> workoutDays = workoutService.getWorkoutsForUser(user.getIdUser())
                .stream()
                .map(Workout::getWorkoutDate)
                .map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .collect(Collectors.toList());

        model.addAttribute("trainingDays", trainingDays);
        model.addAttribute("restDays", restDays);
        model.addAttribute("workoutDays", workoutDays);

        return "user-calendar";
    }
}
