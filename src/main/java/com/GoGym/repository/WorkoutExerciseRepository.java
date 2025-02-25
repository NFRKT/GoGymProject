    package com.GoGym.repository;

    import com.GoGym.module.WorkoutExercise;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;

    public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {
        @Query("SELECT COUNT(we) FROM WorkoutExercise we WHERE we.exercise.idExercise = :exerciseId")
        long countByExerciseId(@Param("exerciseId") Long exerciseId);
    }
