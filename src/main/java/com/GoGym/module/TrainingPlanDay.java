package com.GoGym.module;

import com.GoGym.dto.TrainingPlanDayDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "training_plan_days")
@Data
public class TrainingPlanDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_day")
    private Long idDay;

    @ManyToOne
    @JoinColumn(name = "id_plan", nullable = false)
    @JsonIgnore // Zapobiega rekurencyjnym odwołaniom w serializacji JSON
    private TrainingPlan trainingPlan;

    @Column(name = "day_type", nullable = false)
    @Enumerated(EnumType.STRING)// "training" lub "rest"
    private DayType dayType;

    public enum DayType {
        training, rest
    }

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)// "not_completed" lub "completed"
    private Status status;
    public enum Status {
        notCompleted, completed
    }

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // Opcjonalne notatki do dnia odpoczynku

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "trainingPlanDay", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Zapobiega rekurencyjnym odwołaniom w serializacji JSON
    private List<PlanExercise> exercises;

    public TrainingPlanDay() {
    }

    public TrainingPlanDay(Long idDay, TrainingPlan trainingPlan, DayType dayType, Status status, String notes, LocalDate date, List<PlanExercise> exercises) {
        this.idDay = idDay;
        this.trainingPlan = trainingPlan;
        this.dayType = dayType;
        this.status = status;
        this.notes = notes;
        this.date = date;
        this.exercises = exercises;
    }

    public Long getIdDay() {
        return idDay;
    }

    public void setIdDay(Long idDay) {
        this.idDay = idDay;
    }

    public TrainingPlan getTrainingPlan() {
        return trainingPlan;
    }

    public void setTrainingPlan(TrainingPlan trainingPlan) {
        this.trainingPlan = trainingPlan;
    }

    public DayType getDayType() {
        return dayType;
    }

    public void setDayType(DayType dayType) {
        this.dayType = dayType;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<PlanExercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<PlanExercise> exercises) {
        this.exercises = exercises;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "TrainingPlanDay{" +
                "idDay=" + idDay +
                ", dayType=" + dayType +
                ", status=" + status +
                ", notes='" + notes + '\'' +
                ", date=" + date +
                ", exercises=" + (exercises != null ? exercises.size() + " exercises" : "none") +
                ", trainingPlanId=" + (trainingPlan != null ? trainingPlan.getIdPlan() : "none") +
                '}';
    }

    public static TrainingPlanDay updateTrainingPlanDay(TrainingPlanDay trainingPlanDay, TrainingPlanDayDTO dto) {
        trainingPlanDay.setDate(Objects.nonNull(dto.getDate()) ? dto.getDate() : trainingPlanDay.getDate());
        trainingPlanDay.setNotes(Objects.nonNull(dto.getNotes()) ? dto.getNotes() : trainingPlanDay.getNotes());
        trainingPlanDay.setDayType(Objects.nonNull(dto.getDayType()) ? DayType.valueOf(String.valueOf(dto.getDayType())) : trainingPlanDay.getDayType());

        return trainingPlanDay;
    }

}
