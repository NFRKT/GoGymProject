package com.GoGym.module;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Set;

@Entity
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_exercise")
    private Long idExercise;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "difficulty")
    @Enumerated(EnumType.STRING) // Dodajemy to, aby upewnić się, że enum jest zapisany jako tekst
    private Difficulty difficulty;

    @Column(name = "type")
    @Enumerated(EnumType.STRING) // Dodajemy to, aby upewnić się, że enum jest zapisany jako tekst
    private ExerciseType type;

    @Column(name = "jpg")
    private String jpg;
    @ManyToMany
    @JoinTable(
            name = "exercise_bodypart",
            joinColumns = @JoinColumn(name = "id_exercise"),
            inverseJoinColumns = @JoinColumn(name = "id_bodypart")
    )
    private Set<BodyPart> bodyParts;

    @ManyToMany
    @JoinTable(
            name = "exercise_equipment",
            joinColumns = @JoinColumn(name = "id_exercise"),
            inverseJoinColumns = @JoinColumn(name = "id_equipment")
    )
    private Set<Equipment> equipment;

    // Definicja ENUM Difficulty, które będzie mapowane na tekst
    public enum Difficulty {
        beginner, intermediate, advanced
    }
    public enum ExerciseType {
        STRENGTH, CARDIO
    }

    // Konstruktor bezargumentowy
    public Exercise() {
    }
    public Exercise(Long idExercise) {
        this.idExercise = idExercise;
    }

    // Konstruktor z argumentami
    public Exercise(Long idExercise, String name, String description, Difficulty difficulty, ExerciseType type,
                    Set<BodyPart> bodyParts, Set<Equipment> equipment, String jpg) {
        this.idExercise = idExercise;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.type = type;
        this.bodyParts = bodyParts;
        this.equipment = equipment;
        this.jpg = jpg;
    }

    public Long getIdExercise() {
        return idExercise;
    }

    public void setIdExercise(Long idExercise) {
        this.idExercise = idExercise;
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

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public ExerciseType getType() {
        return type;
    }

    public void setType(ExerciseType type) {
        this.type = type;
    }

    @JsonIgnore
    public Set<BodyPart> getBodyParts() {
        return bodyParts;
    }

    public void setBodyParts(Set<BodyPart> bodyParts) {
        this.bodyParts = bodyParts;
    }
    @JsonIgnore
    public Set<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(Set<Equipment> equipment) {
        this.equipment = equipment;
    }

    public String getJpg() {
        return jpg;
    }

    public void setJpg(String jpg) {
        this.jpg = jpg;
    }

    @Transient
    private boolean used;

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
