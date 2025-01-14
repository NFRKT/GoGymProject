package com.GoGym.Module;

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

    // Konstruktor bezargumentowy
    public Exercise() {
    }

    // Konstruktor z argumentami
    public Exercise(Long idExercise, String name, String description, Difficulty difficulty,
                    Set<BodyPart> bodyParts, Set<Equipment> equipment) {
        this.idExercise = idExercise;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.bodyParts = bodyParts;
        this.equipment = equipment;
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

    public Set<BodyPart> getBodyParts() {
        return bodyParts;
    }

    public void setBodyParts(Set<BodyPart> bodyParts) {
        this.bodyParts = bodyParts;
    }

    public Set<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(Set<Equipment> equipment) {
        this.equipment = equipment;
    }
}
