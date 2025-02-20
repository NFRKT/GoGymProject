package com.GoGym.service;

import com.GoGym.dto.ExerciseDTO;
import com.GoGym.dto.TrainingPlanDTO;
import com.GoGym.dto.TrainingPlanDayDTO;
import com.GoGym.exception.TrainingNotFoundException;
import com.GoGym.module.Exercise;
import com.GoGym.module.PlanExercise;
import com.GoGym.module.TrainingPlan;
import com.GoGym.module.TrainingPlanDay;
import com.GoGym.module.User;
import com.GoGym.repository.ExerciseRepository;
import com.GoGym.repository.PlanExerciseRepository;
import com.GoGym.repository.TrainingPlanDayRepository;
import com.GoGym.repository.TrainingPlanRepository;
import com.GoGym.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public TrainingPlan updateTrainingPlan(Long trainingId, TrainingPlanDTO dto) {
        // Przykładowa walidacja danych – możesz dodać więcej reguł
        validateTrainingData(dto);

        TrainingPlan trainingPlan = trainingPlanRepository.findById(trainingId)
                .orElseThrow(TrainingNotFoundException::new);

        log.info("Aktualizacja planu treningowego ID {}: zmiana nazwy na '{}' oraz opisu", trainingId, dto.getName());
        trainingPlan.setName(dto.getName());
        trainingPlan.setDescription(dto.getDescription());

        boolean planWasCompleted = trainingPlan.getStatus() == TrainingPlan.Status.completed;

        // 1. Usuwanie dni oznaczonych do usunięcia
        List<Long> daysToDelete = dto.getTrainingPlanDays().stream()
                .filter(TrainingPlanDayDTO::isDelete)
                .map(TrainingPlanDayDTO::getIdDay)
                .toList();

        for (Long dayId : daysToDelete) {
            log.info("🔴 Usuwanie dnia treningowego ID: {}", dayId);
            planExerciseRepository.deleteByDayId(dayId);
            trainingPlanDayRepository.deleteById(dayId);
            log.info("✅ Dzień treningowy ID: {} usunięty", dayId);
        }

        // 2. Pobranie i aktualizacja pozostałych dni
        List<TrainingPlanDay> updatedDays = trainingPlanDayRepository.findByTrainingPlanOrderByDate(trainingPlan);
        LocalDate startDate = updatedDays.isEmpty() ? LocalDate.now() : updatedDays.get(0).getDate();
        for (int i = 0; i < updatedDays.size(); i++) {
            updatedDays.get(i).setDate(startDate.plusDays(i));
        }
        trainingPlanDayRepository.saveAll(updatedDays);

        boolean addedNewDay = false;

        // 3. Przetwarzanie dni planu (nowe + istniejące)
        for (TrainingPlanDayDTO dayDTO : dto.getTrainingPlanDays()) {
            if (dayDTO.isDelete()) continue; // pomiń dni do usunięcia

            TrainingPlanDay day;
            boolean isNewDay = false;
            if (dayDTO.getIdDay() != null) {
                day = trainingPlanDayRepository.findById(dayDTO.getIdDay())
                        .orElseThrow(() -> new RuntimeException("Nie znaleziono dnia treningowego"));
            } else {
                // Tworzenie nowego dnia – ustawiamy datę na podstawie liczby już istniejących dni
                LocalDate newDayDate = startDate.plusDays(updatedDays.size());
                day = new TrainingPlanDay();
                day.setTrainingPlan(trainingPlan);
                day.setDate(newDayDate);
                day.setStatus(TrainingPlanDay.Status.notCompleted);
                day.setExercises(new ArrayList<>());
                updatedDays.add(day);
                addedNewDay = true;
                isNewDay = true;
                log.info("Dodano nowy dzień treningowy o dacie: {}", newDayDate);
            }

            day.setNotes(dayDTO.getNotes());
            day.setDayType(dayDTO.getDayType());
            trainingPlanDayRepository.save(day);

            boolean addedNewExercise = false;
            // 4. Przetwarzanie ćwiczeń dla dnia
            if (dayDTO.getExercises() != null) {
                for (ExerciseDTO exerciseDTO : dayDTO.getExercises()) {
                    if (exerciseDTO.getIdPlanExercise() != null && exerciseDTO.isDelete()) {
                        planExerciseRepository.deleteById(exerciseDTO.getIdPlanExercise());
                        log.info("Usunięto ćwiczenie o ID planowym: {}", exerciseDTO.getIdPlanExercise());
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
                        log.info("Zaktualizowano ćwiczenie o ID planowym: {}", exerciseDTO.getIdPlanExercise());
                    } else {
                        Exercise exercise = exerciseRepository.findById(exerciseDTO.getIdExercise())
                                .orElseThrow(() -> new RuntimeException("Ćwiczenie nie istnieje"));
                        PlanExercise newExercise = PlanExercise.toPlanExercise(exerciseDTO, trainingPlan, day, exercise);
                        newExercise.setDuration(exerciseDTO.getDuration());
                        planExerciseRepository.save(newExercise);
                        addedNewExercise = true;
                        log.info("Dodano nowe ćwiczenie '{}' do dnia o ID: {}", exercise.getName(), day.getIdDay());
                    }
                }
            }

            if (addedNewExercise && !isNewDay) {
                day.setStatus(TrainingPlanDay.Status.notCompleted);
                trainingPlanDayRepository.save(day);
                log.info("Ustawiono status dnia o ID {} na 'notCompleted' z powodu dodania nowego ćwiczenia", day.getIdDay());
            }
        }

        updatePlanEndDate(trainingPlan);

        if (planWasCompleted && (addedNewDay || updatedDays.stream().anyMatch(day -> day.getStatus() == TrainingPlanDay.Status.notCompleted))) {
            trainingPlan.setStatus(TrainingPlan.Status.active);
            trainingPlanRepository.save(trainingPlan);
            log.info("Plan treningowy ID {} zmieniono status z 'completed' na 'active'", trainingPlan.getIdPlan());
        }

        // Powiadomienie o edycji planu
        User trainer = userRepository.findById(trainingPlan.getIdTrainer())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trenera o ID: " + trainingPlan.getIdTrainer()));
        User client = userRepository.findById(trainingPlan.getIdClient())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta o ID: " + trainingPlan.getIdClient()));
        notificationService.createNotification(client, trainer, "updated_plan", trainingPlan.getName());
        log.info("Powiadomienie o edycji planu wysłane dla planu: {}", trainingPlan.getName());

        return trainingPlan;
    }

    public void validateTrainingData(TrainingPlanDTO dto) {
        // Przykładowa walidacja – możesz dodać więcej reguł
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nazwa planu nie może być pusta");
        }
        if (dto.getTrainingPlanDays() == null || dto.getTrainingPlanDays().isEmpty()) {
            throw new IllegalArgumentException("Plan musi zawierać przynajmniej jeden dzień treningowy");
        }
    }

    public void updatePlanEndDate(TrainingPlan plan) {
        List<TrainingPlanDay> sortedDays = trainingPlanDayRepository.findByTrainingPlanOrderByDate(plan);
        if (!sortedDays.isEmpty()) {
            plan.setEndDate(sortedDays.get(sortedDays.size() - 1).getDate());
        } else {
            plan.setEndDate(plan.getStartDate());
        }
        trainingPlanRepository.save(plan);
        log.info("Zaktualizowano datę zakończenia planu o ID: {} na {}", plan.getIdPlan(), plan.getEndDate());
    }
}
