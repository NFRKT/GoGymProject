package com.GoGym.repository;

import com.GoGym.module.TrainingPlanDay;
import com.GoGym.module.User;
import com.GoGym.module.Workout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByUserOrderByWorkoutDateDesc(User user);
    Optional<Workout> findByUserAndTrainingPlanDay(User user, TrainingPlanDay trainingPlanDay);
    boolean existsByTrainingPlanDay_IdDay(Long trainingPlanDayId);
    Page<Workout> findByUser(User user, Pageable pageable);
    Page<Workout> findByUserAndWorkoutDate(User user, LocalDate workoutDate, Pageable pageable);
}

