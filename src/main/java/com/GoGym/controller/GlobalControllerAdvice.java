package com.GoGym.controller;

import com.GoGym.module.User;
import com.GoGym.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addGlobalAttributes(Model model, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            model.addAttribute("idUser", userDetails.getIdUser());

            User user = userDetails.getUser();
            if (user != null) {
                boolean isUser = user.getUserType() == User.UserType.CLIENT;
                boolean isTrainer = user.getUserType() == User.UserType.TRAINER;
                boolean isAdmin = user.getUserType() == User.UserType.ADMIN;
                model.addAttribute("isUser", isUser);
                model.addAttribute("isTrainer", isTrainer);
                model.addAttribute("isAdmin", isAdmin);
            }
        }
    }
}
