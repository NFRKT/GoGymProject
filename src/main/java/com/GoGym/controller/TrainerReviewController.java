package com.GoGym.controller;

import com.GoGym.module.TrainerReview;
import com.GoGym.module.User;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.service.TrainerReviewService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<TrainerReview> getTrainerReviews(@PathVariable Long trainerId) {
        return trainerReviewService.getReviewsForTrainer(trainerId);
    }

    @PostMapping("/add")
    public Map<String, String> addReview(@RequestParam Long trainerId,
                                         @RequestParam int rating,
                                         @RequestParam(required = false) String comment,
                                         Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();
        trainerReviewService.addReview(trainerId, loggedInUser.getIdUser(), rating, comment);
        return Map.of("message", "Opinia dodana pomyślnie");
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
        trainerReviewService.deleteReview(reviewId, loggedInUser.getIdUser());
        return Map.of("message", "Opinia została usunięta");
    }
}
