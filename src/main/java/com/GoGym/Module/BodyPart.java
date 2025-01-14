package com.GoGym.Module;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class BodyPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bodypart")
    private Long idBodyPart;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "bodyParts")
    private Set<Exercise> exercises;

    public BodyPart() {
    }

    public BodyPart(Long idBodyPart, String name) {
        this.idBodyPart = idBodyPart;
        this.name = name;
    }

    public Long getIdBodyPart() {
        return idBodyPart;
    }

    public void setIdBodyPart(Long idBodyPart) {
        this.idBodyPart = idBodyPart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(Set<Exercise> exercises) {
        this.exercises = exercises;
    }

    @Override
    public String toString() {
        return this.name;  // Zwróci nazwę części ciała
    }

}
