package com.GoGym.module;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "plan_exercises")
public class PlanExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sets", nullable = false)
    private int sets;

    @Column(name = "reps", nullable = false)
    private int reps;

    @Column(name = "weight")
    private Integer weight; // Może być null

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status; // "notCompleted" lub "completed"

    public enum Status {
        notCompleted, completed
    }

    @ManyToOne
    @JoinColumn(name = "id_plan", nullable = false)
    @JsonIgnore // Zapobiega rekurencyjnym odwołaniom w serializacji JSON
    private TrainingPlan trainingPlan;

    @ManyToOne
    @JoinColumn(name = "id_exercise", nullable = false)
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "id_day", nullable = false)
    @JsonIgnore // Zapobiega rekurencyjnym odwołaniom w serializacji JSON
    private TrainingPlanDay trainingPlanDay;


    public PlanExercise() {
    }

    public PlanExercise(Long id, int sets, int reps, Integer weight, Status status, TrainingPlan trainingPlan, Exercise exercise, TrainingPlanDay trainingPlanDay) {
        this.id = id;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.status = status;
        this.trainingPlan = trainingPlan;
        this.exercise = exercise;
        this.trainingPlanDay = trainingPlanDay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public TrainingPlan getTrainingPlan() {
        return trainingPlan;
    }

    public void setTrainingPlan(TrainingPlan trainingPlan) {
        this.trainingPlan = trainingPlan;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TrainingPlanDay getTrainingPlanDay() {
        return trainingPlanDay;
    }

    public void setTrainingPlanDay(TrainingPlanDay trainingPlanDay) {
        this.trainingPlanDay = trainingPlanDay;
    }
}
