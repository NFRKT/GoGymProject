package com.GoGym.module;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_request")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_trainer", nullable = false)
    @JsonIgnore
    private User trainer;

    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    @JsonIgnore
    private User client;

    @Column(name = "status", nullable = true)
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    public enum RequestStatus {
        pending, accepted, rejected
    }

    @Column(name = "request_date")
    private Timestamp requestDate;

    public Request() {
    }

    public Request(Long id, User trainer, User client, RequestStatus requestStatus, Timestamp requestDate) {
        this.id = id;
        this.trainer = trainer;
        this.client = client;
        this.requestStatus = requestStatus;
        this.requestDate = requestDate;
    }

    // Gettery i settery
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

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }
}
