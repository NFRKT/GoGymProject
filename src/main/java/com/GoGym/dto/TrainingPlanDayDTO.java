package com.GoGym.dto;

import com.GoGym.module.TrainingPlanDay;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class TrainingPlanDayDTO {
    private Long idDay;
    private TrainingPlanDay.DayType dayType;
    private String notes;
    private List<ExerciseDTO> exercises;
    private LocalDate date;
}
