//package com.GoGym.controller;
//
//import com.GoGym.module.Equipment;
//import com.GoGym.service.EquipmentService;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//@RestController
//@RequestMapping("/api/equipments")
//public class EquipmentController {
//
//    private final EquipmentService equipmentService;
//    public EquipmentController(EquipmentService equipmentService) {
//        this.equipmentService = equipmentService;
//    }
//    @GetMapping
//    public List<Equipment> getAllEquipments() {
//        return equipmentService.findAll();
//    }
//    @PostMapping
//    public Equipment createEquipment(@RequestBody Equipment equipment) {
//        return equipmentService.save(equipment);
//    }
//}
