package com.GoGym.cotrolers;

import com.GoGym.Module.Exercise;
import com.GoGym.Service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashSet;
import java.util.List;

@Controller
public class ExerciseController {

    private final ExerciseService exerciseService;

    @Autowired
    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping("/exercises")
    public String getExercisesList(Model model) {
        List<Exercise> exercises = exerciseService.getAllExercises(); // Pobieramy ćwiczenia

        model.addAttribute("exercises", exercises); // Dodajemy ćwiczenia do modelu
        return "exercises-list"; // Zwracamy widok z listą ćwiczeń
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
