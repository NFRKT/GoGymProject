package com.GoGym.module;

import jakarta.persistence.*;

@Entity
@Table(name = "treiners_clients")
public class TrainerClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_trainer", nullable = false)
    private User trainer;

    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    private User client;

    public TrainerClient() {
    }

    public TrainerClient(Long id, User trainer, User client) {
        this.id = id;
        this.trainer = trainer;
        this.client = client;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getTrainer() {
        return trainer;
    }

    public void setTrainer(User trainer) {
        this.trainer = trainer;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }
}

