package com.GoGym.exception;

public class TrainingNotFoundException extends RuntimeException {
    private static final String MSG = "Training not found";

    public TrainingNotFoundException() {
        super(MSG);
    }

    public TrainingNotFoundException(String message) {
        super(message);
    }
}
