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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class TrainingService {

    private final TrainingPlanDayRepository trainingPlanDayRepository;
    private final TrainingPlanRepository  trainingPlanRepository;
    private final ExerciseRepository exerciseRepository;
    private final PlanExerciseRepository planExerciseRepository;


    public TrainingPlan updateTrainingPlan(Long trainingId, TrainingPlanDTO dto) {
        validateTrainingData(dto);

        TrainingPlan trainingPlan = trainingPlanRepository.findById(trainingId).orElseThrow(TrainingNotFoundException::new);
        List<TrainingPlanDay> planDay = trainingPlan.getTrainingPlanDays();
        Set<PlanExercise> exerciseList = new HashSet<>();
        trainingPlan.getTrainingPlanDays().forEach(x -> {
            exerciseList.addAll(x.getExercises());
        });

        //update tylko planu treningowego
        TrainingPlan updatedTrainingPlan = trainingPlanRepository.save(TrainingPlan.updateTrainingPlan(trainingPlan, dto));

        planDay.forEach(x -> {
            TrainingPlanDayDTO trainingPlanDayDTODTO = dto.getTrainingPlanDays().stream().filter(z -> z.getIdDay().equals(x.getIdDay())).findFirst().orElse(null);
            if (Objects.isNull(trainingPlanDayDTODTO))
                return;
            TrainingPlanDay updatedTraininPlanDay = trainingPlanDayRepository.save(TrainingPlanDay.updateTrainingPlanDay(x, trainingPlanDayDTODTO));
        });
        //jeśli jest róznica w ilosci to wchodzi w edycje dodawania
        if (dto.getTrainingPlanDays().size() != planDay.size()) {
            Set<Long> currentPlanDaysIds = planDay.stream().map(TrainingPlanDay::getIdDay).collect(Collectors.toSet());
            Set<TrainingPlanDayDTO> newPlanDays = dto.getTrainingPlanDays().stream().filter(x -> !currentPlanDaysIds.contains(x.getIdDay())).collect(Collectors.toSet());
            newPlanDays.forEach(x -> {
               TrainingPlanDay trainingPlanDay = new TrainingPlanDay();
               //todo zmienic date na ta z formularza jak będzie juz dostepna
               trainingPlanDay.setDate(LocalDate.now());
               trainingPlanDay.setNotes(x.getNotes());
               trainingPlanDay.setDayType(x.getDayType());
               //todo zmienic status na ten z formularza jka bedzie dostepny
               trainingPlanDay.setStatus(TrainingPlanDay.Status.notCompleted);
               trainingPlanDay.setTrainingPlan(trainingPlan);
               TrainingPlanDay updatedTrainingPlanDay = trainingPlanDayRepository.save(trainingPlanDay);
            });

        } else {
            dto.getTrainingPlanDays().forEach(x -> {
                //sprawdzenie czy doszlo jakies nowe cwiczenie
                TrainingPlanDay currentPlanDay = planDay.stream().filter(z -> z.getIdDay().equals(x.getIdDay())).findFirst().orElse(null);
                Set<Long> newExercisesIds = x.getExercises().stream().map(ExerciseDTO::getIdExercise).collect(Collectors.toSet());
                newExercisesIds.removeAll(currentPlanDay.getExercises().stream().map(z -> z.getExercise().getIdExercise()).collect(Collectors.toSet()));

                if (Objects.isNull(currentPlanDay))
                    return;

                if (currentPlanDay.getExercises().size() != x.getExercises().size()) {
                    Set<ExerciseDTO> exerciseDTO = x.getExercises().stream().filter(z -> newExercisesIds.contains(z.getIdExercise())).collect(Collectors.toSet());
                    exerciseDTO.forEach(z -> {
                        //todo dodac deydkowany wyjatek
                        Exercise exercise = exerciseRepository.findById(Long.valueOf(z.getIdExercise().toString())).orElseThrow(RuntimeException::new);
                        planExerciseRepository.save(PlanExercise.toPlanExercise(z, trainingPlan, currentPlanDay, exercise));
                    });
                } else {
                    Set<ExerciseDTO> exerciseDTO = x.getExercises().stream().filter(z -> !newExercisesIds.contains(z.getIdExercise())).collect(Collectors.toSet());
                    exerciseDTO.forEach(z -> {
                        PlanExercise currentPlanExercise = planExerciseRepository.findByExerciseIdExerciseAndTrainingPlanAndTrainingPlanDay(z.getIdExercise(), trainingPlan, currentPlanDay).orElseThrow(RuntimeException::new);
                        Exercise exercise = exerciseRepository.findById(Long.valueOf(z.getIdExercise().toString())).orElseThrow(RuntimeException::new);
                        planExerciseRepository.save(PlanExercise.updatePlanExercise(currentPlanExercise, exercise, z));
                    });
                }

            });


        }
        return trainingPlan;
    }

    public void validateTrainingData(TrainingPlanDTO dto) {
        //todo tutaj mozesz sobie zrobic jakas walidacje na dane wejsciowe, lub w DTO mozesz sobie zrobic adnotacje
    }
}
