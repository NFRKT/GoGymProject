package com.GoGym.dto;

import lombok.Data;

@Data
public class ExerciseDTO {
    private Long idExercise;
    private Long idPlanExercise;
    private String name;
    private Integer sets;
    private Integer reps;
    private Double weight;
    private Integer duration;
    private Double distance;
    private String status;
    private String type;

    private boolean delete;

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

}

