package com.GoGym.controller;

import com.GoGym.module.BodyPart;
import com.GoGym.repository.BodyPartRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/bodypart")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminBodyPartController {

    private final BodyPartRepository bodyPartRepository;

    public AdminBodyPartController(BodyPartRepository bodyPartRepository) {
        this.bodyPartRepository = bodyPartRepository;
    }

    @GetMapping("/new")
    public String newBodyPartForm(Model model) {
        model.addAttribute("bodyPart", new BodyPart());
        return "admin-bodypart-form";
    }

    @PostMapping("/new")
    public String createBodyPart(@ModelAttribute("bodyPart") BodyPart bodyPart) {
        bodyPartRepository.save(bodyPart);
        return "redirect:/admin/panel";
    }
}
