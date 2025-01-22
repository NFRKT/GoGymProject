package com.GoGym.controller;

import com.GoGym.module.TrainingPlan;
import com.GoGym.service.TrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/user-plans")
public class UserPlanController {

    @Autowired
    private TrainingPlanService trainingPlanService;

    @GetMapping
    public String getUserPlans(@RequestParam Long idUser, Model model) {
        List<TrainingPlan> plans = trainingPlanService.findPlansByIdUser(idUser);
        model.addAttribute("plans", plans);
        return "plans"; // Zwraca widok plans.html
    }
}
