package com.GoGym.repository;

import com.GoGym.module.TrainerDetails;
import com.GoGym.module.TrainerSpecialization;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainerSpecializationRepository extends JpaRepository<TrainerSpecialization, Long> {
    List<TrainerSpecialization> findByTrainer(TrainerDetails trainer);
    @Transactional
    void deleteAllByTrainer_IdTrainer(Long idTrainer);

}

