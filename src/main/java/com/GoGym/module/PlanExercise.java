package com.GoGym.module;

import com.GoGym.dto.ExerciseDTO;
import com.GoGym.dto.TrainingPlanDayDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "plan_exercises")
@Data
@Builder
public class PlanExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sets")
    private Integer sets;

    @Column(name = "reps")
    private Integer reps;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "duration")  // Czas w minutach
    private Integer duration;

    @Column(name = "distance")  // Dystans w km
    private Double distance;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status; // "notCompleted" lub "completed"

    public enum Status {
        notCompleted, completed
    }

    @ManyToOne
    @JoinColumn(name = "id_plan", nullable = false)
  // Zapobiega rekurencyjnym odwołaniom w serializacji JSON
    private TrainingPlan trainingPlan;

    @ManyToOne
    @JoinColumn(name = "id_exercise", nullable = false)
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "id_day", nullable = false)
    @JsonBackReference
    private TrainingPlanDay trainingPlanDay;

    @Column(name = "video_url")
    private String videoUrl;

    public PlanExercise() {
    }

    public PlanExercise(Long id, Integer sets, Integer reps, Double weight,  Integer duration, Double distance, Status status, TrainingPlan trainingPlan, Exercise exercise, TrainingPlanDay trainingPlanDay, String videoUrl) {
        this.id = id;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.duration = duration;
        this.distance = distance;
        this.status = status;
        this.trainingPlan = trainingPlan;
        this.exercise = exercise;
        this.trainingPlanDay = trainingPlanDay;
        this.videoUrl = videoUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public static PlanExercise toPlanExercise(ExerciseDTO dto, TrainingPlan trainingPlan, TrainingPlanDay trainingPlanDay, Exercise exercise) {
        return PlanExercise.builder()
                .reps(dto.getReps())
                .sets(dto.getSets())
                .status(Status.notCompleted)
                .trainingPlan(trainingPlan)
                .trainingPlanDay(trainingPlanDay)
                .weight(dto.getWeight())
                .duration(dto.getDuration())
                .distance(dto.getDistance())
                .exercise(exercise)
                .build();
    }
    public static PlanExercise updatePlanExercise(PlanExercise planExercise, Exercise exercise, ExerciseDTO dto) {
        if (exercise.getType() == Exercise.ExerciseType.CARDIO) {
            planExercise.setDuration(dto.getDuration());
            planExercise.setDistance(dto.getDistance());
            planExercise.setSets(0);
            planExercise.setReps(0);
            planExercise.setWeight(null);
        } else {
            planExercise.setSets(dto.getSets());
            planExercise.setReps(dto.getReps());
            planExercise.setWeight(dto.getWeight());
            planExercise.setDuration(null);
            planExercise.setDistance(null);
        }

        planExercise.setExercise(exercise);
        planExercise.setStatus(Status.notCompleted);
        return planExercise;
    }

    public boolean isCloudinaryVideo() {
        return videoUrl != null && videoUrl.contains("cloudinary.com");
    }

}
