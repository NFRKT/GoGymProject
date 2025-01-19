package com.GoGym.dto;

import com.GoGym.module.User;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
public class UserRegistrationDTO {
    private String email;
    private String password;
    private String firstName;
    private String secondName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;
    private User.Gender gender;
    private User.UserType userType;

    public UserRegistrationDTO() {}

    public UserRegistrationDTO(String email, String password, String firstName, String secondName,
                               Date birthDate, User.Gender gender, User.UserType userType) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.secondName = secondName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.userType = userType;
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

    public User.Gender getGender() {
        return gender;
    }

    public void setGender(User.Gender gender) {
        this.gender = gender;
    }

    public User.UserType getUserType() {
        return userType;
    }

    public void setUserType(User.UserType userType) {
        this.userType = userType;
    }
}
