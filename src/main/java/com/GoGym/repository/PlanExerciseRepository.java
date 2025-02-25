package com.GoGym.repository;
import com.GoGym.module.PlanExercise;
import com.GoGym.module.TrainingPlan;
import com.GoGym.module.TrainingPlanDay;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlanExerciseRepository extends JpaRepository<PlanExercise, Long> {
    List<PlanExercise> findByTrainingPlanDayIdDay(Long idDay);

    // Alternatywnie, jeśli chcesz użyć JPQL
    @Query("SELECT pe FROM PlanExercise pe WHERE pe.trainingPlanDay.idDay = :idDay")
    List<PlanExercise> findExercisesByDayId(@Param("idDay") Long idDay);

    Optional<PlanExercise> findByExerciseIdExerciseAndTrainingPlanAndTrainingPlanDay(Long exerciseId, TrainingPlan trainingPlan, TrainingPlanDay trainingPlanDay);

    @Modifying
    @Transactional
    @Query("DELETE FROM PlanExercise pe WHERE pe.trainingPlanDay.idDay = :dayId")
    void deleteByDayId(@Param("dayId") Long dayId);
    @Modifying
    @Transactional
    @Query("UPDATE PlanExercise pe SET pe.videoUrl = :videoUrl WHERE pe.id = :exerciseId")
    void updateVideoUrl(@Param("exerciseId") Long exerciseId, @Param("videoUrl") String videoUrl);

    @Query("SELECT COUNT(pe) FROM PlanExercise pe WHERE pe.exercise.idExercise = :exerciseId")
    long countByExerciseId(@Param("exerciseId") Long exerciseId);


}
