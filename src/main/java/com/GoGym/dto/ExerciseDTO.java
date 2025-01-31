package com.GoGym.dto;

import lombok.Data;

@Data
public class ExerciseDTO {
    private Long idExercise;
    private String name;
    private int sets;
    private int reps;
    private double weight;
    private String status;

}
