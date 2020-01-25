package com.gamify.elearning.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gamify.elearning.entity.Curriculum;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, String> {

}
