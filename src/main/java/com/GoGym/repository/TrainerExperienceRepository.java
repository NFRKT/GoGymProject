package com.GoGym.repository;

import com.GoGym.module.TrainerExperience;
import com.GoGym.module.TrainerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrainerExperienceRepository extends JpaRepository<TrainerExperience, Long> {
    List<TrainerExperience> findByTrainer(TrainerDetails trainer);
}
