package com.GoGym.Module;
import jakarta.persistence.*;
import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Entity
@Table(name = "user")  // Nazwa tabeli w bazie danych
public class user {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id_user;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "second_name")
    private String secondName;

    @Column(name = "birth_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "user_type")
    @Enumerated(EnumType.STRING)
    private UserType userType;

    public enum Gender {
        KOBIETA, MĘŻCZYZNA
    }

    public enum UserType {
        UŻYTKOWNIK, TRENER
    }

    public user() {
    }

    public user(Long id_user, String email, String password, String firstName, String secondName,
                Date birthDate, Gender gender, UserType userType) {
        this.id_user = id_user;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.secondName = secondName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.userType = userType;

    }

    public void setId_user(Long id_user) {
        this.id_user = id_user;
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

}
