package com.GoGym.service;

import com.GoGym.module.TrainerReview;
import com.GoGym.module.User;
import com.GoGym.repository.TrainerReviewRepository;
import com.GoGym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerReviewService {

    @Autowired
    private TrainerReviewRepository trainerReviewRepository;

    @Autowired
    private UserRepository userRepository;

    public List<TrainerReview> getReviewsForTrainer(Long trainerId) {
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trenera"));
        return trainerReviewRepository.findByTrainer(trainer);
    }

    public boolean hasClientReviewedTrainer(Long trainerId, Long clientId) {
        User trainer = userRepository.findById(trainerId).orElseThrow();
        User client = userRepository.findById(clientId).orElseThrow();
        return trainerReviewRepository.existsByTrainerAndClient(trainer, client);
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
            throw new IllegalStateException("Już dodałeś ocenę dla tego trenera");
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


}
