package com.GoGym.cotrolers;

import com.GoGym.Module.Exercise;
import com.GoGym.Service.ExerciseService;
import com.GoGym.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashSet;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@Controller
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final ExerciseRepository exerciseRepository;

    @Autowired
    public ExerciseController(ExerciseService exerciseService, ExerciseRepository exerciseRepository) {
        this.exerciseService = exerciseService;
        this.exerciseRepository = exerciseRepository;
    }

    @GetMapping("/exercises")
    public String getExercises(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 25; // Ilość elementów na stronie
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Exercise> exercisesPage = exerciseRepository.findAll(pageable);

        // Konwersja kolekcji w każdym obiekcie Exercise
        for (Exercise exercise : exercisesPage.getContent()) {
            if (exercise.getEquipment() != null) {
                exercise.setEquipment(new HashSet<>(exercise.getEquipment()));
            }
            if (exercise.getBodyParts() != null) {
                exercise.setBodyParts(new HashSet<>(exercise.getBodyParts()));
            }
        }

        model.addAttribute("exercisesPage", exercisesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", exercisesPage.getTotalPages());

        return "exercises-list";
    }



   // @GetMapping("/exercises")
   // public String getAllExercises(Model model) {
    //    List<Exercise> exercises = exerciseService.getAllExercises();
   //     model.addAttribute("exercises", exercises);
    //    return "exercises-list";
   // }

    @GetMapping
    public List<Exercise> getAllExercises() {
        return exerciseService.getAllExercises();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable Integer id) {
        return exerciseService.getExerciseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Exercise createExercise(@RequestBody Exercise exercise) {
        return exerciseService.createExercise(exercise);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Exercise> updateExercise(@PathVariable Integer id, @RequestBody Exercise updatedExercise) {
        try {
            return ResponseEntity.ok(exerciseService.updateExercise(id, updatedExercise));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Integer id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
}
