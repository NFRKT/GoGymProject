package com.GoGym.service;

import com.GoGym.module.Exercise;
import com.GoGym.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    @Autowired
    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public Optional<Exercise> getExerciseById(Long id) {
        return exerciseRepository.findById(id);
    }

    public Exercise createExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    public Exercise updateExercise(Long id, Exercise updatedExercise) {
        return exerciseRepository.findById(id)
                .map(exercise -> {
                    exercise.setName(updatedExercise.getName());
                    exercise.setDescription(updatedExercise.getDescription());
                    exercise.setDifficulty(updatedExercise.getDifficulty());
                    return exerciseRepository.save(exercise);
                })
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
    }

    public void deleteExercise(Long id) {
        exerciseRepository.deleteById(id);
    }

}
