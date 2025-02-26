package com.GoGym.controller;

import com.GoGym.dto.TrainerReviewDTO;
import com.GoGym.module.TrainerReview;
import com.GoGym.module.User;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.service.TrainerReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trainer/reviews")
public class TrainerReviewController {

    private final TrainerReviewService trainerReviewService;

    @Autowired
    public TrainerReviewController(TrainerReviewService trainerReviewService) {
        this.trainerReviewService = trainerReviewService;
    }

    @GetMapping("/{trainerId}")
    public List<TrainerReviewDTO> getTrainerReviews(@PathVariable Long trainerId) {
        List<TrainerReview> reviews = trainerReviewService.getReviewsForTrainer(trainerId);
        return reviews.stream().map(TrainerReviewDTO::new).toList();
    }


    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestParam Long trainerId,
                                       @RequestParam int rating,
                                       @RequestParam(required = false) String comment,
                                       Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();

        try {
            trainerReviewService.addReview(trainerId, loggedInUser.getIdUser(), rating, comment);
            return ResponseEntity.ok(Map.of("message", "Opinia dodana pomyślnie"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Wystąpił błąd"));
        }
    }


    @PutMapping("/update")
    public Map<String, String> updateReview(@RequestParam Long reviewId,
                                            @RequestParam int rating,
                                            @RequestParam String comment,
                                            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();
        trainerReviewService.updateReview(reviewId, loggedInUser.getIdUser(), rating, comment);
        return Map.of("message", "Opinia została zaktualizowana");
    }

    @DeleteMapping("/delete/{reviewId}")
    public Map<String, String> deleteReview(@PathVariable Long reviewId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();

        if (loggedInUser.getUserType() == User.UserType.ADMIN) {
            trainerReviewService.deleteReviewByAdmin(reviewId);
            return Map.of("message", "Opinia została usunięta przez administratora");
        } else {
            trainerReviewService.deleteReview(reviewId, loggedInUser.getIdUser());
            return Map.of("message", "Opinia została usunięta");
        }
    }

}
