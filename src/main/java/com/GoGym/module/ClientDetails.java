package com.GoGym.module;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "client_details")
public class ClientDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    private Double weight;   // Waga w kg
    private Double height;   // Wzrost w cm
    private Double waist;    // Obwód talii
    private Double hips;     // Obwód bioder
    private Double chest;    // Obwód klatki piersiowej

    @Column(name = "profile_picture")
    private String profilePicture;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "city")
    private String city;
    @Temporal(TemporalType.DATE)
    private Date updateDate; // Data ostatniej aktualizacji danych

    public ClientDetails() {}

    public ClientDetails(Long id, User user, Double weight, Double height, Double waist, Double hips, Double chest, String profilePicture, String phoneNumber, String city, Date updateDate) {
        this.id = id;
        this.user = user;
        this.weight = weight;
        this.height = height;
        this.waist = waist;
        this.hips = hips;
        this.chest = chest;
        this.profilePicture = profilePicture;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.updateDate = updateDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Double getWaist() { return waist; }
    public void setWaist(Double waist) { this.waist = waist; }

    public Double getHips() { return hips; }
    public void setHips(Double hips) { this.hips = hips; }

    public Double getChest() { return chest; }
    public void setChest(Double chest) { this.chest = chest; }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getUpdateDate() { return updateDate; }
    public void setUpdateDate(Date updateDate) { this.updateDate = updateDate; }
}
