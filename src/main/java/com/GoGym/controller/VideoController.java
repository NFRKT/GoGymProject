package com.GoGym.controller;

import com.GoGym.module.PlanExercise;
import com.GoGym.repository.PlanExerciseRepository;
import com.GoGym.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/video")
public class VideoController {
    private final CloudinaryService cloudinaryService;
    private final PlanExerciseRepository planExerciseRepository;

    public VideoController(CloudinaryService cloudinaryService, PlanExerciseRepository planExerciseRepository) {
        this.cloudinaryService = cloudinaryService;
        this.planExerciseRepository = planExerciseRepository;
    }

    @PostMapping("/upload/{exerciseId}")
    public ResponseEntity<?> uploadVideo(@PathVariable Long exerciseId, @RequestParam("file") MultipartFile file) {
        try {
            PlanExercise exercise = planExerciseRepository.findById(exerciseId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono ćwiczenia"));

            if (exercise.getVideoUrl() != null) {
                return ResponseEntity.badRequest().body("Możesz dodać tylko jedno nagranie.");
            }

            String videoUrl = cloudinaryService.uploadVideo(file);
            exercise.setVideoUrl(videoUrl);
            planExerciseRepository.save(exercise);

            return ResponseEntity.ok(videoUrl);
        } catch (MaxUploadSizeExceededException e) {
            return ResponseEntity.badRequest().body("Plik jest za duży! Maksymalny rozmiar to 100MB. Spróbuj dodać link do nagrania.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Błąd podczas przesyłania pliku.");
        }
    }

    @PostMapping("/link/{exerciseId}")
    public ResponseEntity<?> addVideoLink(@PathVariable Long exerciseId, @RequestParam("link") String link) {
        PlanExercise exercise = planExerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono ćwiczenia"));

        if (exercise.getVideoUrl() != null) {
            return ResponseEntity.badRequest().body("Możesz dodać tylko jedno nagranie.");
        }

        exercise.setVideoUrl(link);
        planExerciseRepository.save(exercise);

        return ResponseEntity.ok("Link do wideo został dodany.");
    }

    @DeleteMapping("/delete/{exerciseId}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long exerciseId) {
        PlanExercise exercise = planExerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono ćwiczenia"));

        if (exercise.getVideoUrl() == null) {
            return ResponseEntity.badRequest().body("To ćwiczenie nie ma przypisanego nagrania.");
        }

        try {
            if (exercise.getVideoUrl().contains("cloudinary.com")) {
                cloudinaryService.deleteVideo(exercise.getVideoUrl());
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Błąd podczas usuwania pliku z Cloudinary.");
        }

        // 🗑️ Usunięcie referencji w bazie danych
        exercise.setVideoUrl(null);
        planExerciseRepository.save(exercise);

        return ResponseEntity.ok("Nagranie zostało usunięte.");
    }
}
