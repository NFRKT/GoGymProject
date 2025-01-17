package com.GoGym.Module;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;
import java.time.LocalTime;

@Entity
@Table(name = "workouts")  // Nazwa tabeli w bazie danych
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_workout")
    private Long idWorkout;

    @Column(name = "workout_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate workoutDate;

    @Column(name = "intensity", nullable = false)
    @Enumerated(EnumType.STRING)
    private Intensity intensity;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private user user;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkoutExercise> workoutExercises;

    public enum Intensity {
        low, medium, high
    }

    // Konstruktor bezargumentowy
    public Workout() {}

    // Konstruktor z argumentami
    public Workout(Long idWorkout, LocalDate workoutDate, Intensity intensity, String notes,
                   LocalTime startTime, LocalTime endTime, user user, Set<WorkoutExercise> workoutExercises) {
        this.idWorkout = idWorkout;
        this.workoutDate = workoutDate;
        this.intensity = intensity;
        this.notes = notes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
        this.workoutExercises = workoutExercises;
    }

    // Gettery i Settery
    public Long getIdWorkout() {
        return idWorkout;
    }

    public void setIdWorkout(Long idWorkout) {
        this.idWorkout = idWorkout;
    }

    public LocalDate getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
    }

    public Intensity getIntensity() {
        return intensity;
    }

    public void setIntensity(Intensity intensity) {
        this.intensity = intensity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public user getUser() {
        return user;
    }

    public void setUser(user user) {
        this.user = user;
    }

    public Set<WorkoutExercise> getWorkoutExercises() {
        return workoutExercises;
    }

    public void setWorkoutExercises(Set<WorkoutExercise> workoutExercises) {
        this.workoutExercises = workoutExercises;
    }
}
