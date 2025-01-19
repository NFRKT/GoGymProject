package com.GoGym.repository;

import com.GoGym.module.User;
import com.GoGym.module.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findByUserOrderByWorkoutDateDesc(User user);

}
