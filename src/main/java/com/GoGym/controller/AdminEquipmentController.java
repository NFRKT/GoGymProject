package com.GoGym.controller;

import com.GoGym.module.Equipment;
import com.GoGym.repository.EquipmentRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
}
