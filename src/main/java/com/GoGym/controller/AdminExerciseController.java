package com.GoGym.controller;

import com.GoGym.module.BodyPart;
import com.GoGym.module.Equipment;
import com.GoGym.module.Exercise;
import com.GoGym.repository.BodyPartRepository;
import com.GoGym.repository.EquipmentRepository;
import com.GoGym.repository.ExerciseRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/exercise")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminExerciseController {

    private final ExerciseRepository exerciseRepository;
    private final BodyPartRepository bodyPartRepository;
    private final EquipmentRepository equipmentRepository;

    public AdminExerciseController(ExerciseRepository exerciseRepository,
                                   BodyPartRepository bodyPartRepository,
                                   EquipmentRepository equipmentRepository) {
        this.exerciseRepository = exerciseRepository;
        this.bodyPartRepository = bodyPartRepository;
        this.equipmentRepository = equipmentRepository;
    }

    // Formularz do stworzenia nowego ćwiczenia
    @GetMapping("/new")
    public String newExerciseForm(Model model) {
        model.addAttribute("exercise", new Exercise());

        List<BodyPart> bodyParts = bodyPartRepository.findAll();
        List<Equipment> equipmentList = equipmentRepository.findAll();
        if (equipmentList == null) {
            equipmentList = new ArrayList<>(); // Zapobiega null w Thymeleaf
        }
        model.addAttribute("equipmentList", equipmentList);


        System.out.println("BodyParts: " + bodyParts); // Debugowanie
        System.out.println("Equipment: " + equipmentList); // Debugowanie

        model.addAttribute("bodyParts", bodyParts);
        model.addAttribute("equipmentList", equipmentList);
        model.addAttribute("difficulties", Exercise.Difficulty.values());
        model.addAttribute("exerciseTypes", Exercise.ExerciseType.values());

        return "admin-exercise-form";
    }




    // Przetwarzanie formularza tworzenia nowego ćwiczenia
    @PostMapping("/new")
    public String createExercise(@ModelAttribute("exercise") Exercise exercise,
                                 @RequestParam(required = false, name = "bodyPartIds") List<Long> bodyPartIds,
                                 @RequestParam(required = false, name = "equipmentIds") List<Long> equipmentIds) {
        Set<BodyPart> selectedBodyParts = new HashSet<>(bodyPartRepository.findAllById(bodyPartIds));
        Set<Equipment> selectedEquipment = new HashSet<>(equipmentRepository.findAllById(equipmentIds));
        exercise.setBodyParts(selectedBodyParts);
        exercise.setEquipment(selectedEquipment);
        exerciseRepository.save(exercise);
        return "redirect:/admin/panel";
    }

    // Formularz edycji istniejącego ćwiczenia
    @GetMapping("/edit/{id}")
    public String editExerciseForm(@PathVariable Long id, Model model) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ćwiczenia o ID: " + id));
        model.addAttribute("exercise", exercise);
        model.addAttribute("bodyParts", bodyPartRepository.findAll());
        model.addAttribute("equipmentList", equipmentRepository.findAll());
        model.addAttribute("difficulties", Exercise.Difficulty.values());
        model.addAttribute("exerciseTypes", Exercise.ExerciseType.values());
        return "admin-exercise-form";
    }

    // Aktualizacja ćwiczenia
    @PostMapping("/edit/{id}")
    public String updateExercise(@PathVariable Long id,
                                 @ModelAttribute("exercise") Exercise exercise,
                                 @RequestParam(required = false, name = "bodyPartIds") List<Long> bodyPartIds,
                                 @RequestParam(required = false, name = "equipmentIds") List<Long> equipmentIds) {
        Exercise existing = exerciseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ćwiczenia o ID: " + id));
        existing.setName(exercise.getName());
        existing.setDescription(exercise.getDescription());
        existing.setDifficulty(exercise.getDifficulty());
        existing.setType(exercise.getType());
        existing.setJpg(exercise.getJpg());
        Set<BodyPart> selectedBodyParts = new HashSet<>(bodyPartRepository.findAllById(bodyPartIds));
        Set<Equipment> selectedEquipment = new HashSet<>(equipmentRepository.findAllById(equipmentIds));
        existing.setBodyParts(selectedBodyParts);
        existing.setEquipment(selectedEquipment);
        exerciseRepository.save(existing);
        return "redirect:/admin/panel";
    }
}
