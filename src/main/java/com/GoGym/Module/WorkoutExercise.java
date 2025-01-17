package com.GoGym.Module;

import jakarta.persistence.*;

@Entity
@Table(name = "workout_exercises")
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sets", nullable = false)
    private Integer sets;

    @Column(name = "reps", nullable = false)
    private Integer reps;

    @Column(name = "weight")
    private Integer weight;

    @ManyToOne
    @JoinColumn(name = "id_workout", nullable = false)
    private Workout workout;

    @ManyToOne
    @JoinColumn(name = "id_exercise", nullable = false)
    private Exercise exercise;

    // Konstruktor bezargumentowy
    public WorkoutExercise() {}

    // Konstruktor z argumentami
    public WorkoutExercise(Long id, Integer sets, Integer reps, Integer weight, Workout workout, Exercise exercise) {
        this.id = id;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
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

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
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
    }
}
