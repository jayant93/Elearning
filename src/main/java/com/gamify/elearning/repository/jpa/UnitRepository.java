package com.gamify.elearning.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gamify.elearning.entity.Unit;

@Repository
public interface UnitRepository extends JpaRepository<Unit,String>{

}
