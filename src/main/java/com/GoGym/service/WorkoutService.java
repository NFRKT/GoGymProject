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
    public Workout addWorkoutWithExercises(Workout workout, List<Long> exerciseIds, List<Integer> sets, List<Integer> reps, List<Integer> weight) {
        Workout savedWorkout = workoutRepository.save(workout);

        for (int i = 0; i < exerciseIds.size(); i++) {
            Long exerciseId = exerciseIds.get(i);
            int setCount = sets.get(i);
            int repCount = reps.get(i);
            int weightValue = weight.get(i);

            Exercise exercise = exerciseRepository.findById(exerciseId.intValue())
                    .orElseThrow(() -> new RuntimeException("Ćwiczenie nie istnieje"));

            WorkoutExercise workoutExercise = new WorkoutExercise();
            workoutExercise.setWorkout(savedWorkout);
            workoutExercise.setExercise(exercise);
            workoutExercise.setSets(setCount);
            workoutExercise.setReps(repCount);
            workoutExercise.setWeight(weightValue);

            workoutExerciseRepository.save(workoutExercise);
        }

        return savedWorkout;
    }
    public Workout getWorkoutById(Long id) {
        return workoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trening o ID " + id + " nie istnieje."));
    }

    public List<Workout> getWorkoutsByUser(User user) {
        return workoutRepository.findByUserOrderByWorkoutDateDesc(user);
    }



}

