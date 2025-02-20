package com.GoGym.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class GlobalExceptionHandler implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            model.addAttribute("status", statusCode);
            System.out.println("Błąd HTTP: " + statusCode); // 🔥 Debugowanie
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error/403"; // 🔥 Zwrot poprawnego widoku 403
            } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404"; // 🔥 Zwrot poprawnego widoku 404
            }
        }
        return "error/default"; // 🔥 Zwrot domyślnego błędu
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException() {
        return "redirect:/error/403"; // 🔥 Poprawne przekierowanie na 403
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalState(IllegalStateException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleSecurityException(SecurityException ex) {
        return Map.of("error", "Nie masz uprawnień do wykonania tej operacji");
    }

}
