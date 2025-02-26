package com.GoGym.service;

import com.GoGym.module.TrainerReview;
import com.GoGym.module.User;
import com.GoGym.repository.TrainerReviewRepository;
import com.GoGym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TrainerReviewService {

    private final TrainerReviewRepository trainerReviewRepository;
    private final UserRepository userRepository;

    @Autowired
    public TrainerReviewService(TrainerReviewRepository trainerReviewRepository, UserRepository userRepository) {
        this.trainerReviewRepository = trainerReviewRepository;
        this.userRepository = userRepository;
    }

    public List<TrainerReview> getReviewsForTrainer(Long trainerId) {
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trenera"));
        return trainerReviewRepository.findByTrainer(trainer);
    }

    public TrainerReview addReview(Long trainerId, Long clientId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Ocena musi być w zakresie 1-5");
        }
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trenera"));
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta"));

        if (trainerReviewRepository.existsByTrainerAndClient(trainer, client)) {
            throw new IllegalStateException("Dodałeś już opinię na temat tego trenera!");
        }

        TrainerReview review = new TrainerReview(trainer, client, rating, comment);
        return trainerReviewRepository.save(review);
    }


    public TrainerReview updateReview(Long reviewId, Long clientId, int rating, String comment) {
        TrainerReview review = trainerReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono opinii"));

        if (!review.getClient().getIdUser().equals(clientId)) {
            throw new SecurityException("Nie możesz edytować tej opinii");
        }

        if (review.getRating() == rating && Objects.equals(review.getComment(), comment)) {
            throw new IllegalStateException("Nie dokonano żadnych zmian");
        }

        review.setRating(rating);
        review.setComment(comment);
        return trainerReviewRepository.save(review);
    }

    public void deleteReview(Long reviewId, Long clientId) {
        TrainerReview review = trainerReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono opinii"));

        if (!review.getClient().getIdUser().equals(clientId)) {
            throw new SecurityException("Nie możesz usunąć tej opinii");
        }

        trainerReviewRepository.delete(review);
    }

    public void deleteReviewByAdmin(Long reviewId) {
        trainerReviewRepository.deleteById(reviewId);
    }

}
