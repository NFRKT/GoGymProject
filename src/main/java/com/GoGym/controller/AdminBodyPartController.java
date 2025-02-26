package com.GoGym.controller;

import com.GoGym.module.BodyPart;
import com.GoGym.repository.BodyPartRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/list")
    public String listBodyParts(Model model) {
        List<BodyPart> bodyParts = bodyPartRepository.findAll();
        model.addAttribute("bodyParts", bodyParts);
        return "admin-bodypart-list";
    }

    @GetMapping("/edit/{id}")
    public String editBodyPartForm(@PathVariable Long id, Model model) {
        BodyPart bodyPart = bodyPartRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono części ciała o ID: " + id));
        model.addAttribute("bodyPart", bodyPart);
        return "admin-bodypart-form";
    }

    @PostMapping("/edit/{id}")
    public String updateBodyPart(@PathVariable Long id, @ModelAttribute("bodyPart") BodyPart bodyPart) {
        BodyPart existing = bodyPartRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono części ciała o ID: " + id));
        existing.setName(bodyPart.getName());
        bodyPartRepository.save(existing);
        return "redirect:/admin/bodypart/list";
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteBodyPart(@PathVariable Long id) {
        BodyPart bodyPart = bodyPartRepository.findById(id)
                .orElse(null);
        Map<String, Object> response = new HashMap<>();
        if (bodyPart != null && bodyPart.getExercises() != null && !bodyPart.getExercises().isEmpty()) {
            response.put("success", false);
            response.put("message", "Nie można usunąć tej części ciała, ponieważ jest przypisana do ćwiczeń.");
        } else {
            bodyPartRepository.deleteById(id);
            response.put("success", true);
        }
        return ResponseEntity.ok(response);
    }
}
