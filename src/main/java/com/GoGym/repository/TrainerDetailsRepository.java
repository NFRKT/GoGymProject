package com.GoGym.repository;

import com.GoGym.module.TrainerDetails;
import com.GoGym.module.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerDetailsRepository extends JpaRepository<TrainerDetails, Long> {
    TrainerDetails findByUser(User user);
}
