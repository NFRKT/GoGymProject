package com.GoGym.controller;


import com.GoGym.dto.ExceptionDetailsDTO;
import com.GoGym.exception.TrainingNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvisor {

    @ExceptionHandler({TrainingNotFoundException.class})
    public ResponseEntity<?> handleNotFoundException(Exception exception, WebRequest request) {
        ExceptionDetailsDTO exceptionDetailsDTO = new ExceptionDetailsDTO(new Date(), exception.getMessage(), request.getDescription(false));
        return new ResponseEntity(exceptionDetailsDTO, HttpStatus.NOT_FOUND);
    }

}
