package com.GoGym.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TrainingPlanDTO {
    private Long idPlan;
    private String name;
    private String description;
    private LocalDate startDate;;
    private LocalDate endDate;
    private List<TrainingPlanDayDTO> trainingPlanDays;
}
