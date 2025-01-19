package com.GoGym.service;

import com.GoGym.module.Equipment;
import com.GoGym.repository.EquipmentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public List<Equipment> findAll() {
        return equipmentRepository.findAll();
    }

    public Equipment save(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }
}
