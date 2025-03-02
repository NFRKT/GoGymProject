package com.GoGym.controller;

import com.GoGym.module.PlanExercise;
import com.GoGym.repository.PlanExerciseRepository;
import com.GoGym.service.CloudinaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/video")
public class VideoController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);
    private final CloudinaryService cloudinaryService;
    private final PlanExerciseRepository planExerciseRepository;
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024; // 100MB

    public VideoController(CloudinaryService cloudinaryService, PlanExerciseRepository planExerciseRepository) {
        this.cloudinaryService = cloudinaryService;
        this.planExerciseRepository = planExerciseRepository;
    }

    @PostMapping("/upload/{exerciseId}")
    public ResponseEntity<?> uploadVideo(@PathVariable Long exerciseId, @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Plik nie może być pusty.");
            }

            if (file.getSize() > MAX_VIDEO_SIZE) {
                return ResponseEntity.badRequest().body("Plik jest za duży! Maksymalny rozmiar to 100MB. Spróbuj dodać link do nagrania.");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                return ResponseEntity.badRequest().body("Przesłany plik musi być nagraniem wideo.");
            }

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
            logger.error("Przekroczono maksymalny rozmiar pliku.", e);
            return ResponseEntity.badRequest().body("Plik jest za duży! Maksymalny rozmiar to 100MB. Spróbuj dodać link do nagrania.");
        } catch (IOException e) {
            logger.error("Błąd podczas przesyłania pliku.", e);
            return ResponseEntity.internalServerError().body("Błąd podczas przesyłania pliku.");
        } catch (Exception e) {
            logger.error("Nieoczekiwany błąd podczas uploadu wideo.", e);
            return ResponseEntity.internalServerError().body("Wystąpił błąd podczas przesyłania nagrania.");
        }
    }

    @PostMapping("/link/{exerciseId}")
    public ResponseEntity<?> addVideoLink(@PathVariable Long exerciseId, @RequestParam("link") String link) {
        try {
            PlanExercise exercise = planExerciseRepository.findById(exerciseId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono ćwiczenia"));

            if (exercise.getVideoUrl() != null) {
                return ResponseEntity.badRequest().body("Możesz dodać tylko jedno nagranie.");
            }

            exercise.setVideoUrl(link);
            planExerciseRepository.save(exercise);

            return ResponseEntity.ok(link);
        } catch (Exception e) {
            logger.error("Błąd podczas dodawania linku do nagrania.", e);
            return ResponseEntity.internalServerError().body("Błąd podczas dodawania linku do nagrania.");
        }
    }

    @DeleteMapping("/delete/{exerciseId}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long exerciseId) {
        try {
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
                logger.error("Błąd podczas usuwania pliku z Cloudinary.", e);
                return ResponseEntity.internalServerError().body("Błąd podczas usuwania pliku z Cloudinary.");
            }

            exercise.setVideoUrl(null);
            planExerciseRepository.save(exercise);

            return ResponseEntity.ok("Nagranie zostało usunięte.");
        } catch (Exception e) {
            logger.error("Błąd podczas usuwania nagrania.", e);
            return ResponseEntity.internalServerError().body("Wystąpił błąd podczas usuwania nagrania.");
        }
    }
}
