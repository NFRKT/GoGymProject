package com.GoGym.repository;

import com.GoGym.Module.Exercise;
import com.GoGym.Module.user;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    @Query("SELECT e FROM Exercise e " +
            "WHERE (:difficulty IS NULL OR e.difficulty = :difficulty) " +
            "AND (:bodyPart IS NULL OR :bodyPart MEMBER OF e.bodyParts)" +
            "AND (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Exercise> filter(
            @Param("difficulty") Exercise.Difficulty difficulty, // Zmieniono typ na Enum
            @Param("bodyPart") String bodyPart,
            @Param("name") String name,
            Pageable pageable);

    Optional<Exercise> findById(Integer idExercise);
}



