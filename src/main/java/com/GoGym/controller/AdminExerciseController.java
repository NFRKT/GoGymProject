package com.GoGym.controller;

import com.GoGym.module.BodyPart;
import com.GoGym.module.Equipment;
import com.GoGym.module.Exercise;
import com.GoGym.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
@RequestMapping("/admin/exercise")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminExerciseController {

    private final ExerciseRepository exerciseRepository;
    private final BodyPartRepository bodyPartRepository;
    private final EquipmentRepository equipmentRepository;
    private final PlanExerciseRepository planExerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;

    public AdminExerciseController(ExerciseRepository exerciseRepository,
                                   BodyPartRepository bodyPartRepository,
                                   EquipmentRepository equipmentRepository, PlanExerciseRepository planExerciseRepository, WorkoutExerciseRepository workoutExerciseRepository) {
        this.exerciseRepository = exerciseRepository;
        this.bodyPartRepository = bodyPartRepository;
        this.equipmentRepository = equipmentRepository;
        this.planExerciseRepository = planExerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
    }

    // Formularz do stworzenia nowego ćwiczenia
    @GetMapping("/new")
    public String newExerciseForm(Model model) {
        model.addAttribute("exercise", new Exercise());

        List<BodyPart> bodyParts = bodyPartRepository.findAll();
        List<Equipment> equipments = equipmentRepository.findAll();
        model.addAttribute("equipments", equipments);

        model.addAttribute("bodyParts", bodyParts);
        model.addAttribute("equipments", equipments);
        model.addAttribute("difficulties", Exercise.Difficulty.values());
        model.addAttribute("exerciseTypes", Exercise.ExerciseType.values());

        return "admin-exercise-form";
    }




    // Przetwarzanie formularza tworzenia nowego ćwiczenia
    @PostMapping("/new")
    public String createExercise(@ModelAttribute("exercise") Exercise exercise,
                                 @RequestParam(required = false, name = "bodyPartIds") List<Long> bodyPartIds,
                                 @RequestParam(required = false, name = "equipmentIds") List<Long> equipmentIds,
                                 @RequestParam(name = "imageFile", required = false) MultipartFile imageFile) throws IOException {


        Set<BodyPart> selectedBodyParts = new HashSet<>(bodyPartRepository.findAllById(bodyPartIds));
        Set<Equipment> selectedEquipment = new HashSet<>(equipmentRepository.findAllById(equipmentIds));
        exercise.setBodyParts(selectedBodyParts);
        exercise.setEquipment(selectedEquipment);

        // Obsługa zdjęcia
        if (!imageFile.isEmpty()) {
            String fileName = exercise.getName().replaceAll("\\s+", "_").toLowerCase() + ".jpg"; // Nazwa na podstawie ćwiczenia
            String uploadDir = "src/main/resources/static/images/exercises/";
            Path uploadPath = Paths.get(uploadDir);

            // Tworzenie folderu jeśli nie istnieje
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Zapis pliku
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, imageFile.getBytes());

            // Ustawienie ścieżki do bazy danych
            exercise.setJpg("/images/exercises/" + fileName);
        }

        exerciseRepository.save(exercise);
        return "redirect:/admin/panel";
    }

    // Formularz edycji istniejącego ćwiczenia
    @GetMapping("/edit/{id}")
    public String editExerciseForm(@PathVariable Long id, Model model) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ćwiczenia o ID: " + id));

        boolean isUsedInPlans = planExerciseRepository.countByExerciseId(id) > 0;
        boolean isUsedInWorkouts = workoutExerciseRepository.countByExerciseId(id) > 0;
        boolean isTypeLocked = isUsedInPlans || isUsedInWorkouts;

        model.addAttribute("exercise", exercise);
        model.addAttribute("bodyParts", bodyPartRepository.findAll());
        model.addAttribute("equipments", equipmentRepository.findAll());
        model.addAttribute("difficulties", Exercise.Difficulty.values());
        model.addAttribute("exerciseTypes", Exercise.ExerciseType.values());
        model.addAttribute("isTypeLocked", isTypeLocked); // Dodajemy flagę do widoku

        return "admin-exercise-form";
    }


    // Aktualizacja ćwiczenia
    @PostMapping("/edit/{id}")
    public String updateExercise(@PathVariable Long id,
                                 @ModelAttribute("exercise") Exercise exercise,
                                 @RequestParam(required = false, name = "bodyPartIds") List<Long> bodyPartIds,
                                 @RequestParam(required = false, name = "equipmentIds") List<Long> equipmentIds,
                                 @RequestParam(name = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        Exercise existing = exerciseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ćwiczenia o ID: " + id));

        boolean isUsedInPlans = planExerciseRepository.countByExerciseId(id) > 0;
        boolean isUsedInWorkouts = workoutExerciseRepository.countByExerciseId(id) > 0;

        if (isUsedInPlans || isUsedInWorkouts) {
            exercise.setType(existing.getType());
        }

        existing.setName(exercise.getName());
        existing.setDescription(exercise.getDescription());
        existing.setDifficulty(exercise.getDifficulty());
        existing.setJpg(existing.getJpg()); // Domyślnie zachowujemy stare zdjęcie

        Set<BodyPart> selectedBodyParts = new HashSet<>(bodyPartRepository.findAllById(bodyPartIds));
        Set<Equipment> selectedEquipment = new HashSet<>(equipmentRepository.findAllById(equipmentIds));
        existing.setBodyParts(selectedBodyParts);
        existing.setEquipment(selectedEquipment);

// Obsługa zmiany zdjęcia
        if (!imageFile.isEmpty()) {
            String fileName = existing.getName().replaceAll("\\s+", "_").toLowerCase() + ".jpg";
            String uploadDir = "src/main/resources/static/images/exercises/";
            Path uploadPath = Paths.get(uploadDir);

            // Usunięcie starego pliku, jeśli istnieje
            File oldFile = new File(uploadPath + "/" + fileName);
            if (oldFile.exists()) {
                oldFile.delete();
            }

            // Zapis nowego pliku
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, imageFile.getBytes());

            // Aktualizacja ścieżki w bazie
            existing.setJpg("/images/exercises/" + fileName);
        }

        exerciseRepository.save(existing);
        return "redirect:/admin/panel";
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteExercise(@PathVariable Long id) {
        long usedInPlans = planExerciseRepository.countByExerciseId(id);
        long usedInWorkouts = workoutExerciseRepository.countByExerciseId(id);

        if (usedInPlans > 0 || usedInWorkouts > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Nie można usunąć ćwiczenia, ponieważ jest używane w planach treningowych lub workoutach.");
            return ResponseEntity.ok(response);
        }

        exerciseRepository.deleteById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/list")
    public String listExercises(Model model) {
        List<Exercise> exercises = exerciseRepository.findAll();
        // Dla każdego ćwiczenia ustalamy, czy jest używane w planach lub workoutach
        for (Exercise exercise : exercises) {
            boolean usedInPlans = planExerciseRepository.countByExerciseId(exercise.getIdExercise()) > 0;
            boolean usedInWorkouts = workoutExerciseRepository.countByExerciseId(exercise.getIdExercise()) > 0;
            // Załóżmy, że Exercise posiada transient pole "used" (lub setter, który nie jest zapisywany do bazy)
            exercise.setUsed(usedInPlans || usedInWorkouts);
        }
        model.addAttribute("exercises", exercises);
        return "admin-exercise-list";
    }




}
