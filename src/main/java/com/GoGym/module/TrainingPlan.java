package com.GoGym.module;

import jakarta.persistence.*;

import java.time.LocalDate;
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

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status; // Może być "active" lub "completed"
    public enum Status {
        active, completed
    }

    @Column(name = "id_trainer", nullable = false)
    private Long idTrainer;

    @Column(name = "id_client", nullable = false)
    private Long idClient;

    @OneToMany(mappedBy = "trainingPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanExercise> exercises;

    @OneToMany(mappedBy = "trainingPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainingPlanDay> trainingPlanDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client", insertable = false, updatable = false)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_trainer", insertable = false, updatable = false)
    private User trainer;

    public TrainingPlan() {
    }

    public TrainingPlan(Long idPlan, String name, LocalDate startDate, LocalDate endDate, String description, Status status, Long idTrainer, Long idClient, List<PlanExercise> exercises, List<TrainingPlanDay> trainingPlanDays, User client, User trainer) {
        this.idPlan = idPlan;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.status = status;
        this.idTrainer = idTrainer;
        this.idClient = idClient;
        this.exercises = exercises;
        this.trainingPlanDays = trainingPlanDays;
        this.client = client;
        this.trainer = trainer;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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
    public User getClient(){ return client;}
    public void setClient(User client){this.client=client;}
    public User getTrainer(){ return trainer;}
    public void setTrainer(User trainer){this.trainer=trainer;}
}

