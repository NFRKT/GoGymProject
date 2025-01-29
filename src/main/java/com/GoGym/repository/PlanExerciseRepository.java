package com.GoGym.repository;
import com.GoGym.module.PlanExercise;
import com.GoGym.module.TrainingPlanDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlanExerciseRepository extends JpaRepository<PlanExercise, Long> {
    List<PlanExercise> findByTrainingPlanDayIdDay(Long idDay);

    // Alternatywnie, jeśli chcesz użyć JPQL
    @Query("SELECT pe FROM PlanExercise pe WHERE pe.trainingPlanDay.idDay = :idDay")
    List<PlanExercise> findExercisesByDayId(@Param("idDay") Long idDay);
}
