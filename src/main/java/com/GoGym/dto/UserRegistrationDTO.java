package com.GoGym.dto;

import com.GoGym.module.User;
import com.GoGym.module.TrainerDetails;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserRegistrationDTO {
    private String email;
    private String password;
    private String firstName;
    private String secondName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;
    private String gender;
    private String userType;

    // Pola dla trenerów
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    private String phoneNumber;
    private String workArea;
    private List<String> specializations = new ArrayList<>();

    public UserRegistrationDTO() {}

    public UserRegistrationDTO(String email, String password, String firstName, String secondName,
                               Date birthDate, String gender, String userType, Date startDate, String phoneNumber, String workArea, List<String> specializations) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.secondName = secondName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.userType = userType;
        this.startDate = startDate;
        this.phoneNumber = phoneNumber;
        this.workArea = workArea;
        this.specializations = specializations;
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
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

    public List<String> getSpecializations() { return specializations; }
    public void setSpecializations(List<String> specializations) { this.specializations = specializations; }

}
