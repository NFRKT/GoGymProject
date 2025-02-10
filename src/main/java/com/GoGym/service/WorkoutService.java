package com.GoGym.service;

import com.GoGym.module.Exercise;
import com.GoGym.module.Workout;
import com.GoGym.module.WorkoutExercise;
import com.GoGym.module.User;
import com.GoGym.repository.ExerciseRepository;
import com.GoGym.repository.WorkoutRepository;
import com.GoGym.repository.WorkoutExerciseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;

    public WorkoutService(WorkoutRepository workoutRepository, ExerciseRepository exerciseRepository, WorkoutExerciseRepository workoutExerciseRepository) {
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
    }

    @Transactional
    public Workout addWorkoutWithExercises(Workout workout, List<Long> exerciseIds, List<Integer> sets, List<Integer> reps,
                                           List<Double> weight, List<String> durations, List<Double> distances) {
        Workout savedWorkout = workoutRepository.save(workout);

        for (int i = 0; i < exerciseIds.size(); i++) {
            Long exerciseId = exerciseIds.get(i);
            Exercise exercise = exerciseRepository.findById(exerciseId)
                    .orElseThrow(() -> new RuntimeException("Ćwiczenie nie istnieje"));

            WorkoutExercise workoutExercise = new WorkoutExercise();
            workoutExercise.setWorkout(savedWorkout);
            workoutExercise.setExercise(exercise);

            if (exercise.getType() == Exercise.ExerciseType.STRENGTH) {
                workoutExercise.setSets(sets.get(i));
                workoutExercise.setReps(reps.get(i));
                workoutExercise.setWeight(weight.get(i));
            } else if (exercise.getType() == Exercise.ExerciseType.CARDIO) {
                workoutExercise.setDuration(parseDurationToSeconds(durations.get(i)));
                workoutExercise.setDistance(distances.get(i));
            }

            workoutExerciseRepository.save(workoutExercise);
        }

        return savedWorkout;
    }

    // 🚀 Konwersja czasu "mm:ss" na sekundy
    private int parseDurationToSeconds(String duration) {
        String[] parts = duration.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return (minutes * 60) + seconds;
    }


    public Workout getWorkoutById(Long id) {
        return workoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trening o ID " + id + " nie istnieje."));
    }

    public List<Workout> getWorkoutsByUser(User user) {
        return workoutRepository.findByUserOrderByWorkoutDateDesc(user);
    }



}

