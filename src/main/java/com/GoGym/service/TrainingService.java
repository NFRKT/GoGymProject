package com.GoGym.service;

import com.GoGym.dto.ExerciseDTO;
import com.GoGym.dto.TrainingPlanDTO;
import com.GoGym.dto.TrainingPlanDayDTO;
import com.GoGym.exception.TrainingNotFoundException;
import com.GoGym.module.Exercise;
import com.GoGym.module.PlanExercise;
import com.GoGym.module.TrainingPlan;
import com.GoGym.module.TrainingPlanDay;
import com.GoGym.repository.ExerciseRepository;
import com.GoGym.repository.PlanExerciseRepository;
import com.GoGym.repository.TrainingPlanDayRepository;
import com.GoGym.repository.TrainingPlanRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
@Transactional
public class TrainingService {

    private final TrainingPlanDayRepository trainingPlanDayRepository;
    private final TrainingPlanRepository trainingPlanRepository;
    private final ExerciseRepository exerciseRepository;
    private final PlanExerciseRepository planExerciseRepository;


    private int parseDurationToSeconds(String duration) {
        if (duration == null || duration.isEmpty()) return 0;
        String[] parts = duration.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return (minutes * 60) + seconds;
    }

    private String formatSecondsToDuration(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
    public TrainingPlan updateTrainingPlan(Long trainingId, TrainingPlanDTO dto) {
        validateTrainingData(dto);

        TrainingPlan trainingPlan = trainingPlanRepository.findById(trainingId)
                .orElseThrow(TrainingNotFoundException::new);

        boolean planWasCompleted = trainingPlan.getStatus() == TrainingPlan.Status.completed; // 🔥 Czy plan był ukończony?

        // 🔴 1. Usuwanie dni oznaczonych do usunięcia
        List<Long> daysToDelete = dto.getTrainingPlanDays().stream()
                .filter(TrainingPlanDayDTO::isDelete)
                .map(TrainingPlanDayDTO::getIdDay)
                .toList();

        for (Long dayId : daysToDelete) {
            log.info("🔴 Usuwanie dnia treningowego ID: " + dayId);
            planExerciseRepository.deleteByDayId(dayId);
            trainingPlanDayRepository.deleteById(dayId);
            log.info("✅ Dzień treningowy ID: " + dayId + " usunięty");
        }

        // 🔄 2. Pobranie i aktualizacja pozostałych dni
        List<TrainingPlanDay> updatedDays = trainingPlanDayRepository.findByTrainingPlanOrderByDate(trainingPlan);

        LocalDate startDate = updatedDays.isEmpty() ? LocalDate.now() : updatedDays.get(0).getDate();
        for (int i = 0; i < updatedDays.size(); i++) {
            updatedDays.get(i).setDate(startDate.plusDays(i)); // 🔥 Aktualizacja daty na podstawie kolejności
        }
        trainingPlanDayRepository.saveAll(updatedDays); // Zapis aktualizacji dat

        boolean addedNewDay = false; // 🔥 Czy dodaliśmy nowy dzień?

        // 🔹 3. Przetwarzanie dni planu (nowe + istniejące)
        for (TrainingPlanDayDTO dayDTO : dto.getTrainingPlanDays()) {
            if (dayDTO.isDelete()) continue; // Pomiń usunięte dni

            TrainingPlanDay day;
            boolean isNewDay = false;

            if (dayDTO.getIdDay() != null) {
                // 🔹 Aktualizacja istniejącego dnia
                day = trainingPlanDayRepository.findById(dayDTO.getIdDay())
                        .orElseThrow(() -> new RuntimeException("Nie znaleziono dnia treningowego"));
            } else {
                // 🔥 Tworzenie nowego dnia
                LocalDate newDayDate = startDate.plusDays(updatedDays.size()); // Kolejna dostępna data
                day = new TrainingPlanDay();
                day.setTrainingPlan(trainingPlan);
                day.setDate(newDayDate);
                day.setStatus(TrainingPlanDay.Status.notCompleted);
                day.setExercises(new ArrayList<>()); // ✅ Ustawienie pustej listy ćwiczeń
                updatedDays.add(day);
                addedNewDay = true;
                isNewDay = true;
            }

            day.setNotes(dayDTO.getNotes());
            day.setDayType(dayDTO.getDayType());

            trainingPlanDayRepository.save(day);

            boolean addedNewExercise = false; // 🔥 Czy dodano nowe ćwiczenie?

            // 🔹 4. Aktualizacja ćwiczeń dla dnia
            if (dayDTO.getExercises() != null) { // ✅ Sprawdzamy, czy lista ćwiczeń istnieje
                for (ExerciseDTO exerciseDTO : dayDTO.getExercises()) {
                    if (exerciseDTO.getIdPlanExercise() != null && exerciseDTO.isDelete()) {
                        planExerciseRepository.deleteById(exerciseDTO.getIdPlanExercise());
                        continue;
                    }

                    PlanExercise existingExercise = (exerciseDTO.getIdPlanExercise() != null)
                            ? planExerciseRepository.findById(exerciseDTO.getIdPlanExercise()).orElse(null)
                            : null;

                    if (existingExercise != null) {
                        // 🔹 Aktualizacja ćwiczenia
                        Exercise newExercise = exerciseRepository.findById(exerciseDTO.getIdExercise())
                                .orElseThrow(() -> new RuntimeException("Ćwiczenie nie istnieje"));
                        existingExercise.setExercise(newExercise);
                        existingExercise.setSets(exerciseDTO.getSets());
                        existingExercise.setReps(exerciseDTO.getReps());
                        existingExercise.setWeight(exerciseDTO.getWeight());
                        existingExercise.setDuration(exerciseDTO.getDuration());
                        existingExercise.setDistance(exerciseDTO.getDistance());
                        planExerciseRepository.save(existingExercise);
                    } else {
                        // 🔥 Dodanie nowego ćwiczenia
                        Exercise exercise = exerciseRepository.findById(exerciseDTO.getIdExercise())
                                .orElseThrow(() -> new RuntimeException("Ćwiczenie nie istnieje"));
                        PlanExercise newExercise = PlanExercise.toPlanExercise(exerciseDTO, trainingPlan, day, exercise);
                        newExercise.setDuration(exerciseDTO.getDuration());
                        planExerciseRepository.save(newExercise);
                        addedNewExercise = true;
                    }
                }
            }

            // 🔥 Jeśli dodano nowe ćwiczenie do istniejącego dnia → ustaw status na `notCompleted`
            if (addedNewExercise && !isNewDay) {
                day.setStatus(TrainingPlanDay.Status.notCompleted);
                trainingPlanDayRepository.save(day);
            }
        }

        updatePlanEndDate(trainingPlan);

        // 🔥 Jeśli plan był ukończony, ale dodano nowy dzień lub ćwiczenie → ustaw status na `active`
        if (planWasCompleted && (addedNewDay || updatedDays.stream().anyMatch(day -> day.getStatus() == TrainingPlanDay.Status.notCompleted))) {
            trainingPlan.setStatus(TrainingPlan.Status.active);
            trainingPlanRepository.save(trainingPlan);
        }

        return trainingPlan;
    }



    public void validateTrainingData(TrainingPlanDTO dto) {
        // Możesz tutaj dodać walidację danych
    }
    public void updatePlanEndDate(TrainingPlan plan) {
        List<TrainingPlanDay> sortedDays = trainingPlanDayRepository.findByTrainingPlanOrderByDate(plan);

        if (!sortedDays.isEmpty()) {
            plan.setEndDate(sortedDays.get(sortedDays.size() - 1).getDate()); // Ostatni dzień = endDate
        } else {
            plan.setEndDate(plan.getStartDate()); // Jeśli brak dni, endDate = startDate
        }

        trainingPlanRepository.save(plan); // Zapisz zaktualizowany plan
    }


}
