package com.GoGym.dto;

import com.GoGym.module.TrainerReview;

public class TrainerReviewDTO {
    private Long id;
    private int rating;
    private String comment;
    private String createdAt;
    private Long clientId;
    private String clientFirstName;
    private String clientSecondName;

    public TrainerReviewDTO(TrainerReview review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.createdAt = review.getCreatedAt().toString();
        if (review.getClient() != null) {
            this.clientId = review.getClient().getIdUser();
            this.clientFirstName = review.getClient().getFirstName();
            this.clientSecondName = review.getClient().getSecondName();
        }
    }

    public Long getId() { return id; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }
    public Long getClientId() { return clientId; }
    public String getClientFirstName() { return clientFirstName; }
    public String getClientSecondName() { return clientSecondName; }
}