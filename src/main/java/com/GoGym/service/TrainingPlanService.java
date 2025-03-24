package com.GoGym.service;

import com.GoGym.dto.ExerciseDTO;
import com.GoGym.dto.TrainingPlanDTO;
import com.GoGym.dto.TrainingPlanDayDTO;
import com.GoGym.module.*;
import com.GoGym.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class TrainingPlanService {

    private final TrainingPlanRepository trainingPlanRepository;
    private final PlanExerciseRepository planExerciseRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final TrainingPlanDayRepository trainingPlanDayRepository;
    private final NotificationService notificationService;

    public TrainingPlanService(TrainingPlanRepository trainingPlanRepository,
                               PlanExerciseRepository planExerciseRepository,
                               TrainingPlanDayRepository trainingPlanDayRepository,
                               UserRepository userRepository,
                               NotificationService notificationService, ExerciseRepository exerciseRepository) {
        this.trainingPlanRepository = trainingPlanRepository;
        this.planExerciseRepository = planExerciseRepository;
        this.trainingPlanDayRepository = trainingPlanDayRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.exerciseRepository =  exerciseRepository;
    }

    @Transactional
    public TrainingPlan createTrainingPlan(TrainingPlan plan) {

        trainingPlanRepository.save(plan);
        log.info("Utworzono plan treningowy o ID: {}", plan.getIdPlan());

        if (plan.getTrainingPlanDays() != null) {
            for (TrainingPlanDay day : plan.getTrainingPlanDays()) {
                day.setTrainingPlan(plan);
                trainingPlanDayRepository.save(day);
                log.info("Zapisano dzień treningowy o ID: {}", day.getIdDay());

                if (day.getExercises() != null) {
                    for (PlanExercise exercise : day.getExercises()) {
                        exercise.setTrainingPlanDay(day);
                        planExerciseRepository.save(exercise);
                        log.info("Zapisano ćwiczenie w dniu o ID: {}", day.getIdDay());
                    }
                }
            }
        } else {
            log.warn("Plan treningowy o ID {} nie posiada dni treningowych.", plan.getIdPlan());
        }

        // Pobieramy trenera i klienta do powiadomienia
        User trainer = userRepository.findById(plan.getIdTrainer())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trenera o ID: " + plan.getIdTrainer()));
        User client = userRepository.findById(plan.getIdClient())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta o ID: " + plan.getIdClient()));

        // Tworzymy powiadomienie o nowym planie
        notificationService.createNotification(client, trainer, "new_plan", plan.getName());
        log.info("Wysłano powiadomienie o nowym planie: {} do trenera i klienta.", plan.getName());

        return plan;
    }

    public List<TrainingPlan> findPlansByIdClient(Long idUser) {
        return trainingPlanRepository.findByIdClient(idUser);
    }

    public List<TrainingPlan> findPlansByIdTrainer(Long idUser) {
        return trainingPlanRepository.findByIdTrainer(idUser);
    }

    public void updateExerciseStatus(Long exerciseId, PlanExercise.Status newStatus) {
        PlanExercise exercise = planExerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ćwiczenia o ID: " + exerciseId));

        // Jeśli status ćwiczenia się nie zmienia, nie wykonujemy aktualizacji
        if (exercise.getStatus() == newStatus) {
            log.info("Ćwiczenie o ID: {} już ma status {}. Nie aktualizuję ponownie.", exerciseId, newStatus);
            return;
        }

        exercise.setStatus(newStatus);
        planExerciseRepository.save(exercise);
        log.info("Zaktualizowano status ćwiczenia o ID: {} na {}", exerciseId, newStatus);

    }


    public void updateDayStatus(Long dayId) {
        TrainingPlanDay day = trainingPlanDayRepository.findById(dayId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono dnia o ID: " + dayId));

        boolean allExercisesCompleted = day.getExercises().stream()
                .allMatch(exercise -> exercise.getStatus() == PlanExercise.Status.completed);

        TrainingPlanDay.Status newStatus = allExercisesCompleted
                ? TrainingPlanDay.Status.completed
                : TrainingPlanDay.Status.notCompleted;

        // Sprawdź, czy status dnia się zmienił, zanim go zapiszesz
        if (day.getStatus() == newStatus) {
            log.info("Dzień o ID: {} już ma status {}. Nie aktualizuję ponownie.", dayId, newStatus);
            return;
        }

        TrainingPlanDay.Status previousStatus = day.getStatus();
        day.setStatus(newStatus);
        trainingPlanDayRepository.save(day);
        log.info("Dzień o ID: {} zmieniono ze statusu {} na {}", dayId, previousStatus, newStatus);

        // Aktualizujemy status planu tylko wtedy, gdy status dnia się zmienił
        updatePlanStatus(day.getTrainingPlan().getIdPlan());

        // Powiadomienie wysyłamy tylko raz, gdy dzień został ukończony po raz pierwszy
        if (previousStatus != TrainingPlanDay.Status.completed && newStatus == TrainingPlanDay.Status.completed) {
            TrainingPlan plan = day.getTrainingPlan();
            User trainer = userRepository.findById(plan.getIdTrainer())
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trenera o ID: " + plan.getIdTrainer()));
            User client = userRepository.findById(plan.getIdClient())
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta o ID: " + plan.getIdClient()));
            int dayNumber = plan.getTrainingPlanDays().indexOf(day) + 1;
            notificationService.createNotification(trainer, client, "day_updated", "Dzień " + dayNumber + " w planie: " + plan.getName());
            log.info("Wysłano powiadomienie o ukończeniu dnia treningowego: {}", dayNumber);
        }
    }


    public void updatePlanStatus(Long planId) {
        TrainingPlan plan = trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono planu o ID: " + planId));

        boolean allDaysCompleted = plan.getTrainingPlanDays().stream()
                .allMatch(day -> day.getStatus() == TrainingPlanDay.Status.completed);

        TrainingPlan.Status newStatus = allDaysCompleted ? TrainingPlan.Status.completed : TrainingPlan.Status.active;

        // Sprawdź, czy status rzeczywiście się zmienił, zanim zapiszesz i zalogujesz zmianę
        if (plan.getStatus() == newStatus) {
            log.info("Plan o ID: {} już ma status {}. Nie aktualizuję ponownie.", planId, newStatus);
            return;
        }

        plan.setStatus(newStatus);
        trainingPlanRepository.save(plan);
        log.info("Zaktualizowano status planu o ID: {} na {}", planId, newStatus);

        // Powiadomienie wysyłamy tylko raz, gdy plan zostanie ukończony
        if (newStatus == TrainingPlan.Status.completed) {
            User trainer = userRepository.findById(plan.getIdTrainer())
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trenera o ID: " + plan.getIdTrainer()));
            User client = userRepository.findById(plan.getIdClient())
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta o ID: " + plan.getIdClient()));

            notificationService.createNotification(trainer, client, "plan_completed", plan.getName());
            log.info("Wysłano powiadomienie o ukończeniu planu: {}", plan.getName());
        }
    }

    public void deleteTrainingPlan(Long planId) {
        TrainingPlan plan = trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono planu o ID: " + planId));

        // Usuwamy powiązane dni i ćwiczenia
        if (plan.getTrainingPlanDays() != null) {
            for (TrainingPlanDay day : plan.getTrainingPlanDays()) {
                planExerciseRepository.deleteAll(day.getExercises());
                trainingPlanDayRepository.delete(day);
                log.info("Usunięto dzień treningowy o ID: {} i powiązane ćwiczenia", day.getIdDay());
            }
        }
        trainingPlanRepository.delete(plan);
        log.info("Usunięto plan treningowy o ID: {}", planId);
    }
    public List<TrainingPlanDay> getTrainingDaysForUser(Long userId) {
        return trainingPlanDayRepository.findByTrainingPlan_Client_IdUser(userId);
    }

    public TrainingPlan updateTrainingPlan(Long trainingId, TrainingPlanDTO dto) {
        validateTrainingData(dto);

        TrainingPlan trainingPlan = trainingPlanRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono planu treningowego"));

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
