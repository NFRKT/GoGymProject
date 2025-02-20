package com.GoGym.module;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badge badge;

    private LocalDate awardedDate;

    public UserBadge() {
    }

    public UserBadge(Long id, User user, Badge badge, LocalDate awardedDate) {
        this.id = id;
        this.user = user;
        this.badge = badge;
        this.awardedDate = awardedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public LocalDate getAwardedDate() {
        return awardedDate;
    }

    public void setAwardedDate(LocalDate awardedDate) {
        this.awardedDate = awardedDate;
    }
}
