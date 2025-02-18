package com.GoGym.dto;

import com.GoGym.module.WorkoutExercise;

public class WorkoutExerciseDTO {
    private String exerciseName;
    private Integer sets;
    private Integer reps;
    private Double weight;
    private String formattedDuration; // hh:mm:ss

    public WorkoutExerciseDTO(WorkoutExercise exercise) {
        this.exerciseName = exercise.getExercise().getName();
        this.sets = (exercise.getSets() != null) ? exercise.getSets() : 0;
        this.reps = (exercise.getReps() != null) ? exercise.getReps() : 0;
        this.weight = (exercise.getWeight() != null) ? exercise.getWeight() : 0.0;
        this.formattedDuration = formatDuration(exercise.getDuration());
    }

    private String formatDuration(Integer seconds) {
        if (seconds == null || seconds <= 0) return "00:00:00";
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public Integer getSets() {
        return sets;
    }

    public Integer getReps() {
        return reps;
    }

    public Double getWeight() {
        return weight;
    }

    public String getFormattedDuration() {
        return formattedDuration;
    }
}
