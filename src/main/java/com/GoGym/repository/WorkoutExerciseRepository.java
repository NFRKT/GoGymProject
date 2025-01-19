    package com.GoGym.repository;

    import com.GoGym.module.WorkoutExercise;
    import org.springframework.data.jpa.repository.JpaRepository;

    public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {
    }
