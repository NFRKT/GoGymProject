package com.GoGym.service;

import com.GoGym.module.Exercise;
import com.GoGym.module.Workout;
import com.GoGym.module.WorkoutExercise;
import com.GoGym.module.User;
import com.GoGym.repository.ExerciseRepository;
import com.GoGym.repository.WorkoutExerciseRepository;
import com.GoGym.repository.WorkoutRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;

    public WorkoutService(WorkoutRepository workoutRepository,
                          ExerciseRepository exerciseRepository,
                          WorkoutExerciseRepository workoutExerciseRepository) {
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
    }

    @Transactional
    public Workout addWorkoutWithExercises(Workout workout, List<Long> exerciseIds, List<Integer> strengthSets, List<Integer> reps,
                                           List<Double> weight, List<Integer> cardioSets, List<String> durations, List<Double> distances) {
        log.info("Dodawanie treningu dla użytkownika: {}", workout.getUser().getIdUser());
        Workout savedWorkout = workoutRepository.save(workout);
        int strengthIndex = 0;
        int cardioIndex = 0;
        for (int i = 0; i < exerciseIds.size(); i++) {
            Long exerciseId = exerciseIds.get(i);
            Exercise exercise = exerciseRepository.findById(exerciseId)
                    .orElseThrow(() -> new RuntimeException("Ćwiczenie nie istnieje, ID: " + exerciseId));
            WorkoutExercise workoutExercise = new WorkoutExercise();
            workoutExercise.setWorkout(savedWorkout);
            workoutExercise.setExercise(exercise);
            if (exercise.getType() == Exercise.ExerciseType.STRENGTH) {
                workoutExercise.setSets(strengthSets.get(strengthIndex));
                workoutExercise.setReps(reps.get(strengthIndex));
                workoutExercise.setWeight(weight.get(strengthIndex));
                strengthIndex++;
            } else if (exercise.getType() == Exercise.ExerciseType.CARDIO) {
                workoutExercise.setSets(cardioSets.get(cardioIndex));
                workoutExercise.setDuration(durations.get(cardioIndex) != null ? parseDuration(durations.get(cardioIndex)) : null);
                workoutExercise.setDistance(distances.get(cardioIndex));
                cardioIndex++;
            }
            workoutExerciseRepository.save(workoutExercise);
            log.info("Dodano ćwiczenie o ID: {} do treningu", exerciseId);
        }
        return savedWorkout;
    }

    public Integer parseDuration(String duration) {
        if (duration == null || duration.isEmpty()) return null;
        try {
            // Spróbuj sparsować jako liczbę – jeśli to sekundy
            return Integer.parseInt(duration);
        } catch (NumberFormatException e) {
            String[] parts = duration.split(":");
            if (parts.length == 2) { // Format mm:ss
                return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
            } else if (parts.length == 3) { // Format hh:mm:ss
                return Integer.parseInt(parts[0]) * 3600 + Integer.parseInt(parts[1]) * 60 + Integer.parseInt(parts[2]);
            }
            throw new IllegalArgumentException("Niepoprawny format czasu: " + duration);
        }
    }

    public List<Workout> getWorkoutsByUser(User user) {
        return workoutRepository.findByUserOrderByWorkoutDateDesc(user);
    }
    public Page<Workout> getWorkoutsByUserPage(User user, Pageable pageable) {
        return workoutRepository.findByUser(user, pageable);
    }

    public Page<Workout> getWorkoutsByUserAndDate(User user, LocalDate date, Pageable pageable) {
        return workoutRepository.findByUserAndWorkoutDate(user, date, pageable);
    }

    public Workout getWorkoutById(Long id) {
        return workoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trening o ID " + id + " nie istnieje."));
    }
    public List<Workout> getWorkoutsForUser(Long userId) {
        return workoutRepository.findByUser_IdUser(userId);
    }
}
