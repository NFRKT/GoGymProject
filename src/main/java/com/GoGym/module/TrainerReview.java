package com.GoGym.module;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "trainer_reviews")
public class TrainerReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private User trainer;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @Column(nullable = false)
    private int rating; // Ocena w skali 1-5

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    public TrainerReview() {
        this.createdAt = new Date();
    }

    public TrainerReview(User trainer, User client, int rating, String comment) {
        this.trainer = trainer;
        this.client = client;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = new Date();
    }

    public Long getId() { return id; }
    public User getTrainer() { return trainer; }
    public User getClient() { return client; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public Date getCreatedAt() { return createdAt; }

    public void setTrainer(User trainer) { this.trainer = trainer; }
    public void setClient(User client) { this.client = client; }
    public void setRating(int rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }
}
