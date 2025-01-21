package com.GoGym.module;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "training_plans")
public class TrainingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan")
    private Long idPlan;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    private String status; // Może być "active" lub "completed"

    @Column(name = "id_trainer", nullable = false)
    private Long idTrainer;

    @Column(name = "id_client", nullable = false)
    private Long idClient;

    @OneToMany(mappedBy = "trainingPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanExercise> exercises;

    @OneToMany(mappedBy = "trainingPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainingPlanDay> trainingPlanDays;

    public TrainingPlan() {
    }

    public TrainingPlan(Long idPlan, String name, String description, String status, Long idTrainer, Long idClient, List<PlanExercise> exercises, List<TrainingPlanDay> trainingPlanDays) {
        this.idPlan = idPlan;
        this.name = name;
        this.description = description;
        this.status = status;
        this.idTrainer = idTrainer;
        this.idClient = idClient;
        this.exercises = exercises;
        this.trainingPlanDays = trainingPlanDays;
    }

    public Long getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Long idPlan) {
        this.idPlan = idPlan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getIdTrainer() {
        return idTrainer;
    }

    public void setIdTrainer(Long idTrainer) {
        this.idTrainer = idTrainer;
    }

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }

    public List<PlanExercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<PlanExercise> exercises) {
        this.exercises = exercises;
    }

    public List<TrainingPlanDay> getTrainingPlanDays() {
        return trainingPlanDays;
    }

    public void setTrainingPlanDays(List<TrainingPlanDay> trainingPlanDays) {
        this.trainingPlanDays = trainingPlanDays;
    }
}

