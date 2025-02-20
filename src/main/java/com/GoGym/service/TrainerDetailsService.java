package com.GoGym.service;

import com.GoGym.module.TrainerDetails;
import com.GoGym.module.TrainerExperience;
import com.GoGym.module.User;
import com.GoGym.repository.TrainerDetailsRepository;
import com.GoGym.repository.TrainerExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerDetailsService {

    private final TrainerDetailsRepository trainerDetailsRepository;
    private final TrainerExperienceRepository trainerExperienceRepository;

    @Autowired
    public TrainerDetailsService(TrainerDetailsRepository trainerDetailsRepository,
                                 TrainerExperienceRepository trainerExperienceRepository) {
        this.trainerDetailsRepository = trainerDetailsRepository;
        this.trainerExperienceRepository = trainerExperienceRepository;
    }

    public TrainerDetails getTrainerDetails(User user) {
        return trainerDetailsRepository.findByUser(user);
    }

    public void saveTrainerDetails(TrainerDetails trainerDetails) {
        trainerDetailsRepository.save(trainerDetails);
    }

    public List<TrainerExperience> getTrainerExperience(Long trainerId) {
        TrainerDetails trainerDetails = trainerDetailsRepository.findById(trainerId).orElse(null);
        return trainerDetails != null ? trainerExperienceRepository.findByTrainer(trainerDetails) : List.of();
    }

    public void addTrainerExperience(TrainerExperience experience) {
        trainerExperienceRepository.save(experience);
    }

    public void deleteTrainerExperience(Long experienceId) {
        trainerExperienceRepository.deleteById(experienceId);
    }

    public Optional<TrainerExperience> findTrainerExperienceById(Long experienceId) {
        return trainerExperienceRepository.findById(experienceId);
    }

    public void updateTrainerExperience(TrainerExperience experience) {
        trainerExperienceRepository.save(experience);
    }
}
