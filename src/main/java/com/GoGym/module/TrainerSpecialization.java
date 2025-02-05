package com.GoGym.module;

import jakarta.persistence.*;

@Entity
@Table(name = "trainer_specialization")
public class TrainerSpecialization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String specialization;

    @ManyToOne
    @JoinColumn(name = "id_trainer", nullable = false)
    private TrainerDetails trainer;

    public TrainerSpecialization() {}

    public TrainerSpecialization(String specialization, TrainerDetails trainer) {
        this.specialization = specialization;
        this.trainer = trainer;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public TrainerDetails getTrainer() { return trainer; }
    public void setTrainer(TrainerDetails trainer) { this.trainer = trainer; }
}

