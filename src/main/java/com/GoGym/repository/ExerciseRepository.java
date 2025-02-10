package com.GoGym.repository;

import com.GoGym.module.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    @Query("SELECT DISTINCT e FROM Exercise e " +
            "LEFT JOIN e.bodyParts bp " +
            "LEFT JOIN e.equipment eq " +
            "WHERE (:difficulty IS NULL OR e.difficulty = :difficulty) " +
            "AND (:bodyPart IS NULL OR bp.name = :bodyPart) " +
            "AND (:equipment IS NULL OR eq.name = :equipment) " +
            "AND (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Exercise> filter(
            @Param("difficulty") Exercise.Difficulty difficulty,
            @Param("bodyPart") String bodyPart,
            @Param("equipment") String equipment,
            @Param("name") String name,
            Pageable pageable);


    Optional<Exercise> findById(Long idExercise);
}