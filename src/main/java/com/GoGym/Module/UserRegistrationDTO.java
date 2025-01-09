package com.GoGym.Module;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
public class UserRegistrationDTO {
    private String email;
    private String password;
    private String firstName;
    private String secondName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;
    private user.Gender gender;
    private user.UserType userType;

    public UserRegistrationDTO() {}

    public UserRegistrationDTO(String email, String password, String firstName, String secondName,
                               Date birthDate, user.Gender gender, user.UserType userType) {
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

    public user.Gender getGender() {
        return gender;
    }

    public void setGender(user.Gender gender) {
        this.gender = gender;
    }

    public user.UserType getUserType() {
        return userType;
    }

    public void setUserType(user.UserType userType) {
        this.userType = userType;
    }
}
