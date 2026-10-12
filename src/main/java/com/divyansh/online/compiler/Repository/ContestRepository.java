package com.divyansh.online.compiler.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.divyansh.online.compiler.Entity.ContestEntity;

@Repository
public interface ContestRepository extends JpaRepository<ContestEntity, Long>{

	List<ContestEntity> findAll();
	
	@Query("select c from ContestEntity c where c.ContestID = :cid")
	List<ContestEntity> findByContestID(@Param("cid") Long ContestID);
	
}
