package com.GoGym.module;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "User")  // Nazwa tabeli w bazie danych
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

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
    @JsonIgnore
    private Set<Request> receivedRequests; // Zgłoszenia do trenera

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Request> sentRequests; // Zgłoszenia wysłane przez klienta

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<TrainerClient> clients; // Klienci przypisani do trenera

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<TrainerClient> trainers; // Trener przypisany do klienta

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatRoom> chatRooms;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private TrainerDetails trainerDetails;

    public enum Gender {
        KOBIETA, MĘŻCZYZNA
    }

    public enum UserType {
        CLIENT, TRAINER, ADMIN
    }

    public User() {
    }

    public User(Long idUser) {
        this.idUser = idUser;
    }

    public User(Long idUser, String email, String password, String firstName, String lastName,
                Date birthDate, Gender gender, UserType userType) {
        this.idUser = idUser;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public TrainerDetails getTrainerDetails() {
        return trainerDetails;
    }

    public void setTrainerDetails(TrainerDetails trainerDetails) {
        this.trainerDetails = trainerDetails;
    }

}
