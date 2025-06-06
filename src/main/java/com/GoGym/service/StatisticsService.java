package com.GoGym.service;

import com.GoGym.dto.WorkoutExerciseDTO;
import com.GoGym.module.Exercise;
import com.GoGym.module.User;
import com.GoGym.module.Workout;
import com.GoGym.module.WorkoutExercise;
import com.GoGym.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final WorkoutRepository workoutRepository;

    public StatisticsService(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public Map<String, Object> calculateStatistics(User user, int year, int month) {
        List<Workout> workouts = workoutRepository.findByUserOrderByWorkoutDateDesc(user);
        Map<String, Object> stats = new HashMap<>();

        // Najczęściej wykonywane ćwiczenia (ogólnie)
        Map<String, Long> exerciseFrequency = workouts.stream()
                .flatMap(w -> w.getWorkoutExercises().stream())
                .collect(Collectors.groupingBy(e -> e.getExercise().getName(), Collectors.counting()));

        List<Map.Entry<String, Long>> mostCommonExercises = exerciseFrequency.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry<String, Long>::getValue, Comparator.reverseOrder())
                        .thenComparing(Map.Entry::getKey))
                .limit(5)
                .toList();
        stats.put("mostCommonExercises", mostCommonExercises);

        // Najczęściej wykonywane ćwiczenia (w miesiącu)
        Map<String, Long> exerciseFrequencyInMonth = workouts.stream()
                .filter(w -> w.getWorkoutDate().getYear() == year && w.getWorkoutDate().getMonthValue() == month)
                .flatMap(w -> w.getWorkoutExercises().stream())
                .collect(Collectors.groupingBy(e -> e.getExercise().getName(), Collectors.counting()));

        List<List<String>> mostCommonExercisesInMonth = exerciseFrequencyInMonth.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry<String, Long>::getValue, Comparator.reverseOrder())
                        .thenComparing(Map.Entry::getKey))
                .limit(5)
                .map(entry -> List.of(entry.getKey(), String.valueOf(entry.getValue())))
                .toList();
        stats.put("mostCommonExercisesInMonth", mostCommonExercisesInMonth);

        // Największy ciężar dla ćwiczeń siłowych
        Map<String, Map.Entry<Double, LocalDate>> maxWeightExercises = new HashMap<>();
        for (Workout workout : workouts) {
            for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
                if (exercise.getExercise().getType() == Exercise.ExerciseType.STRENGTH) {
                    String exerciseName = exercise.getExercise().getName();
                    double weight = (exercise.getWeight() != null) ? exercise.getWeight() : 0.0;

                    if (!maxWeightExercises.containsKey(exerciseName) || maxWeightExercises.get(exerciseName).getKey() < weight) {
                        maxWeightExercises.put(exerciseName, Map.entry(weight, workout.getWorkoutDate()));
                    }
                }
            }
        }
        stats.put("maxWeightExercises", maxWeightExercises);

        // Najdłuższy trening cardio
        Optional<WorkoutExercise> longestCardio = workouts.stream()
                .flatMap(w -> w.getWorkoutExercises().stream())
                .filter(e -> e.getExercise().getType() == Exercise.ExerciseType.CARDIO)
                .max(Comparator.comparingInt(e -> (e.getDuration() != null) ? e.getDuration() : 0));
        longestCardio.ifPresent(e -> stats.put("longestCardioFormatted", formatDuration(e.getDuration())));
        stats.put("longestCardio", longestCardio.map(WorkoutExerciseDTO::new).orElse(null));

        // Ilość treningów w miesiącu
        long workoutsCount = workouts.stream()
                .filter(w -> w.getWorkoutDate().getYear() == year && w.getWorkoutDate().getMonthValue() == month)
                .count();
        stats.put("workoutsCount", workoutsCount);

        // ✅ Nowe statystyki:

        // Średnia długość treningu
        OptionalDouble avgWorkoutDuration = workouts.stream()
                .filter(w -> w.getWorkoutDate().getYear() == year && w.getWorkoutDate().getMonthValue() == month)
                .mapToInt(w -> w.getWorkoutExercises().stream()
                        .mapToInt(e -> (e.getDuration() != null) ? e.getDuration() : 0)
                        .sum()
                ).average();
        stats.put("averageWorkoutDuration", avgWorkoutDuration.isPresent() ? formatDuration((int) avgWorkoutDuration.getAsDouble()) : "Brak danych");

        OptionalDouble avgWorkoutDurationAll = workouts.stream()
                .mapToInt(w -> w.getWorkoutExercises().stream()
                        .mapToInt(e -> (e.getDuration() != null) ? e.getDuration() : 0)
                        .sum()
                ).average();
        stats.put("averageWorkoutDurationAll", avgWorkoutDurationAll.isPresent() ? formatDuration((int) avgWorkoutDurationAll.getAsDouble()) : "Brak danych");

// Całkowity czas treningów (dla wybranego miesiąca)

        // Największa liczba powtórzeń w jednym ćwiczeniu
        // Liczenie największej liczby powtórzeń (serie * powtórzenia)
        Map<String, String> maxRepsExercises = new HashMap<>();

        for (Workout workout : workouts) {
            for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
                if (exercise.getReps() != null && exercise.getSets() != null) {
                    String exerciseName = exercise.getExercise().getName();
                    int totalReps = exercise.getReps() * exercise.getSets();
                    String formattedReps = totalReps + " powtórzeń (" + exercise.getSets() + " serii po " + exercise.getReps() + " powtórzeń)";

                    if (!maxRepsExercises.containsKey(exerciseName) || totalReps > Integer.parseInt(maxRepsExercises.get(exerciseName).split(" ")[0])) {
                        maxRepsExercises.put(exerciseName, formattedReps);
                    }
                }
            }
        }

        stats.put("maxRepsExercises", maxRepsExercises);


        // Najbardziej trenowane dni tygodnia
        Map<DayOfWeek, String> dniTygodniaMap = Map.of(
                DayOfWeek.MONDAY, "Poniedziałek",
                DayOfWeek.TUESDAY, "Wtorek",
                DayOfWeek.WEDNESDAY, "Środa",
                DayOfWeek.THURSDAY, "Czwartek",
                DayOfWeek.FRIDAY, "Piątek",
                DayOfWeek.SATURDAY, "Sobota",
                DayOfWeek.SUNDAY, "Niedziela"
        );

        Map<DayOfWeek, Long> treningiDniTygodnia = workouts.stream()
                .collect(Collectors.groupingBy(w -> w.getWorkoutDate().getDayOfWeek(), Collectors.counting()));

        List<Map.Entry<String, Long>> najczesciejDni = treningiDniTygodnia.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry<DayOfWeek, Long>::getValue, Comparator.reverseOrder()))
                .map(entry -> Map.entry(dniTygodniaMap.get(entry.getKey()), entry.getValue()))
                .toList();

        stats.put("mostFrequentWorkoutDays", najczesciejDni);

        int totalWorkoutTime = workouts.stream()
                .filter(w -> w.getWorkoutDate().getYear() == year && w.getWorkoutDate().getMonthValue() == month)
                .mapToInt(w -> w.getWorkoutExercises().stream()
                        .mapToInt(e -> (e.getDuration() != null) ? e.getDuration() : 0)
                        .sum()
                ).sum();
        stats.put("totalWorkoutTime", formatDuration(totalWorkoutTime));

        int totalWorkoutTimeAll = workouts.stream()
                .mapToInt(w -> w.getWorkoutExercises().stream()
                        .mapToInt(e -> (e.getDuration() != null) ? e.getDuration() : 0)
                        .sum()
                ).sum();
        stats.put("totalWorkoutTimeAll", formatDuration(totalWorkoutTimeAll));

        return stats;
    }

    private String formatDuration(Integer seconds) {
        if (seconds == null || seconds <= 0) return "00:00:00";
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }
}
