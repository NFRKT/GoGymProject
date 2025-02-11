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
    private final TrainingPlanRepository  trainingPlanRepository;
    private final ExerciseRepository exerciseRepository;
    private final PlanExerciseRepository planExerciseRepository;


    public TrainingPlan updateTrainingPlan(Long trainingId, TrainingPlanDTO dto) {
        validateTrainingData(dto);

        TrainingPlan trainingPlan = trainingPlanRepository.findById(trainingId)
                .orElseThrow(TrainingNotFoundException::new);

        List<TrainingPlanDay> existingDays = trainingPlan.getTrainingPlanDays();
        LocalDate lastDate = existingDays.isEmpty() ? LocalDate.now() :
                existingDays.stream()
                        .map(TrainingPlanDay::getDate)
                        .max(LocalDate::compareTo)
                        .orElse(LocalDate.now()); // 🔥 Pobranie ostatniego dnia z planu

        for (TrainingPlanDayDTO dayDTO : dto.getTrainingPlanDays()) {
            if (dayDTO.getIdDay() != null && dayDTO.isDelete()) {
                log.info("🔴 Usuwanie dnia treningowego ID: " + dayDTO.getIdDay());

                // Najpierw usuń powiązane ćwiczenia
                planExerciseRepository.deleteByDayId(dayDTO.getIdDay());

                // Usuń dzień z listy dni w planie
                trainingPlan.getTrainingPlanDays().removeIf(d -> d.getIdDay().equals(dayDTO.getIdDay()));

                // Teraz faktyczne usunięcie dnia
                trainingPlanDayRepository.deleteById(dayDTO.getIdDay());

                log.info("✅ Dzień treningowy ID: " + dayDTO.getIdDay() + " usunięty");
                continue;
            }



            TrainingPlanDay day;
            if (dayDTO.getIdDay() != null) {
                // 🔹 Aktualizacja istniejącego dnia
                day = trainingPlanDayRepository.findById(dayDTO.getIdDay())
                        .orElseThrow(() -> new RuntimeException("Nie znaleziono dnia treningowego"));
            } else {
                // 🔥 Tworzenie nowego dnia
                day = new TrainingPlanDay();
                day.setTrainingPlan(trainingPlan);
                day.setDate(lastDate.plusDays(1));
                day.setStatus(TrainingPlanDay.Status.notCompleted);
                lastDate = day.getDate();
            }

            day.setNotes(dayDTO.getNotes());
            day.setDayType(dayDTO.getDayType());

            if (day.getExercises() == null) {
                day.setExercises(new ArrayList<>());
            }

            trainingPlanDayRepository.save(day);

            for (ExerciseDTO exerciseDTO : dayDTO.getExercises()) {
                if (exerciseDTO.getIdPlanExercise() != null && exerciseDTO.isDelete()) {
                    planExerciseRepository.deleteById(exerciseDTO.getIdPlanExercise());
                    continue;
                }

                PlanExercise existingExercise = (exerciseDTO.getIdPlanExercise() != null)
                        ? planExerciseRepository.findById(exerciseDTO.getIdPlanExercise()).orElse(null)
                        : null;

                if (existingExercise != null) {
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
                    Exercise exercise = exerciseRepository.findById(exerciseDTO.getIdExercise())
                            .orElseThrow(() -> new RuntimeException("Ćwiczenie nie istnieje"));

                    PlanExercise newExercise = PlanExercise.toPlanExercise(exerciseDTO, trainingPlan, day, exercise);
                    planExerciseRepository.save(newExercise);
                }
            }
        }

        return trainingPlan;
    }





    public void validateTrainingData(TrainingPlanDTO dto) {
        //todo tutaj mozesz sobie zrobic jakas walidacje na dane wejsciowe, lub w DTO mozesz sobie zrobic adnotacje
    }
}
