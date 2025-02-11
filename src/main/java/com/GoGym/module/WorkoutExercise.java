package com.GoGym.module;

import jakarta.persistence.*;

import java.sql.Time;

@Entity
@Table(name = "workout_exercises")
public class WorkoutExercise {

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

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "distance")
    private Double distance;

    @ManyToOne
    @JoinColumn(name = "id_workout", nullable = false)
    private Workout workout;

    @ManyToOne
    @JoinColumn(name = "id_exercise", nullable = false)
    private Exercise exercise;

    // Konstruktor bezargumentowy
    public WorkoutExercise() {}

    // Konstruktor z argumentami
    public WorkoutExercise(Long id, Integer sets, Integer reps, Double weight, Integer duration, Double distance, Workout workout, Exercise exercise) {
        this.id = id;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.duration = duration;
        this.distance = distance;
        this.workout = workout;
        this.exercise = exercise;
    }

    // Gettery i Settery
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

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
        if (exercise.getType() == Exercise.ExerciseType.CARDIO) {
            this.sets = null;
            this.reps = null;
            this.weight = null;
        } else {
            this.duration = null;
            this.distance = null;
        }
    }
}
