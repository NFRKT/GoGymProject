package com.GoGym.dto;

public class RequestDTO {
    private Long id;
    private Long clientId;
    private String clientFirstName;
    private String clientSecondName;
    private String clientEmail;

    public RequestDTO() {
    }

    // Konstruktor, gettery, settery
    public RequestDTO(Long id, Long clientId, String clientFirstName, String clientSecondName, String clientEmail) {
        this.id = id;
        this.clientId = clientId;
        this.clientFirstName = clientFirstName;
        this.clientSecondName = clientSecondName;
        this.clientEmail = clientEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientFirstName() {
        return clientFirstName;
    }

    public void setClientFirstName(String clientFirstName) {
        this.clientFirstName = clientFirstName;
    }

    public String getClientSecondName() {
        return clientSecondName;
    }

    public void setClientSecondName(String clientSecondName) {
        this.clientSecondName = clientSecondName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
// Gettery i settery
}

