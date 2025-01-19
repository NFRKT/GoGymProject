package com.GoGym.module;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "User")  // Nazwa tabeli w bazie danych
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;

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

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Request> receivedRequests; // Zgłoszenia do trenera

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Request> sentRequests; // Zgłoszenia wysłane przez klienta

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TrainerClient> clients; // Klienci przypisani do trenera

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TrainerClient> trainers; // Trener przypisany do klienta
    public enum Gender {
        KOBIETA, MĘŻCZYZNA
    }

    public enum UserType {
        UŻYTKOWNIK, TRENER
    }

    public User() {
    }

    public User(Long idUser) {
        this.idUser = idUser;
    }

    public User(Long idUser, String email, String password, String firstName, String secondName,
                Date birthDate, Gender gender, UserType userType) {
        this.idUser = idUser;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.secondName = secondName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.userType = userType;

    }
    public Long getIdUser() {
        return idUser;
    }
    public void setIdUser(Long idUser) {
        this.idUser = idUser;
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

    public Set<Request> getReceivedRequests() {
        return receivedRequests;
    }

    public void setReceivedRequests(Set<Request> receivedRequests) {
        this.receivedRequests = receivedRequests;
    }

    public Set<Request> getSentRequests() {
        return sentRequests;
    }

    public void setSentRequests(Set<Request> sentRequests) {
        this.sentRequests = sentRequests;
    }

    public Set<TrainerClient> getClients() {
        return clients;
    }

    public void setClients(Set<TrainerClient> clients) {
        this.clients = clients;
    }

    public Set<TrainerClient> getTrainers() {
        return trainers;
    }

    public void setTrainers(Set<TrainerClient> trainers) {
        this.trainers = trainers;
    }

}
