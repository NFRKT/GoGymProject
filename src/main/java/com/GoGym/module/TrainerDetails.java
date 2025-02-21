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
    private Long idTrainer;

    @Column(name = "start_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "work_area")
    private String workArea;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "profile_picture")
    private String profilePicture;

    @OneToOne
    @JoinColumn(name = "idTrainer", referencedColumnName = "id_user")
    private User user;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainerSpecialization> specializations = new ArrayList<>();

    public TrainerDetails() {
    }

    public TrainerDetails(Long idTrainer, Date startDate, String phoneNumber, String workArea, String bio, String profilePicture, User user, List<TrainerSpecialization> specializations) {
        this.idTrainer = idTrainer;
        this.startDate = startDate;
        this.phoneNumber = phoneNumber;
        this.workArea = workArea;
        this.bio=bio;
        this.profilePicture = profilePicture;
        this.user = user;
        this.specializations = specializations;
    }

    public Long getIdTrainer() {
        return idTrainer;
    }

    public void setIdTrainer(Long idTrainer) {
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

    public String getBio(){return bio;}
    public void setBio(String bio) {this.bio = bio;}

    public String getProfilePicture(){return profilePicture;}

    public void setProfilePicture(String profilePicture) {this.profilePicture = profilePicture;}

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
