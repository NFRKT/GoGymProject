package com.GoGym.controller;

import com.GoGym.module.Equipment;
import com.GoGym.repository.EquipmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/equipment")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminEquipmentController {

    private final EquipmentRepository equipmentRepository;

    public AdminEquipmentController(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @GetMapping("/new")
    public String newEquipmentForm(Model model) {
        model.addAttribute("equipment", new Equipment());
        return "admin-equipment-form";
    }

    @PostMapping("/new")
    public String createEquipment(@ModelAttribute("equipment") Equipment equipment) {
        equipmentRepository.save(equipment);
        return "redirect:/admin/panel";
    }

    @GetMapping("/list")
    public String listEquipments(Model model) {
        List<Equipment> equipments = equipmentRepository.findAll();
        model.addAttribute("equipments", equipments);
        return "admin-equipment-list";
    }

    @GetMapping("/edit/{id}")
    public String editEquipmentForm(@PathVariable Long id, Model model) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono sprzętu o ID: " + id));
        model.addAttribute("equipment", equipment);
        return "admin-equipment-form";
    }

    @PostMapping("/edit/{id}")
    public String updateEquipment(@PathVariable Long id, @ModelAttribute("equipment") Equipment equipment) {
        Equipment existing = equipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono sprzętu o ID: " + id));
        existing.setName(equipment.getName());
        equipmentRepository.save(existing);
        return "redirect:/admin/equipment/list";
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteEquipment(@PathVariable Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElse(null);
        Map<String, Object> response = new HashMap<>();
        if (equipment != null && equipment.getExercises() != null && !equipment.getExercises().isEmpty()) {
            response.put("success", false);
            response.put("message", "Nie można usunąć tego sprzętu, ponieważ jest przypisany do ćwiczeń.");
        } else {
            equipmentRepository.deleteById(id);
            response.put("success", true);
        }
        return ResponseEntity.ok(response);
    }
}
