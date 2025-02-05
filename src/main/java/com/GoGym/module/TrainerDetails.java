package com.GoGym.module;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "trainer_details")
public class TrainerDetails {

    @Id
    @Column(name = "id_trainer", nullable = false)
    private Integer idTrainer;

    @Column(name = "start_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "work_area")
    private String workArea;

    @OneToOne
    @JoinColumn(name = "idTrainer", referencedColumnName = "id_user")
    private User user;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainerSpecialization> specializations = new ArrayList<>();

    public TrainerDetails() {
    }

    public TrainerDetails(Integer idTrainer, Date startDate, String phoneNumber, String workArea, User user, List<TrainerSpecialization> specializations) {
        this.idTrainer = idTrainer;
        this.startDate = startDate;
        this.phoneNumber = phoneNumber;
        this.workArea = workArea;
        this.user = user;
        this.specializations = specializations;
    }

    public Integer getIdTrainer() {
        return idTrainer;
    }

    public void setIdTrainer(Integer idTrainer) {
        this.idTrainer = idTrainer;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWorkArea() {
        return workArea;
    }

    public void setWorkArea(String workArea) {
        this.workArea = workArea;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<TrainerSpecialization> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(List<TrainerSpecialization> specializations) {
        this.specializations = specializations;
    }
}
