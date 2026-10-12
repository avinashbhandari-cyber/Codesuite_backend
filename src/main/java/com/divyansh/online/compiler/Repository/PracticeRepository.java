package com.divyansh.online.compiler.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.divyansh.online.compiler.Entity.ContestQnEntity;
import com.divyansh.online.compiler.Entity.PracticeEntity;

@Repository
public interface PracticeRepository extends JpaRepository<PracticeEntity, Long> {

	List<PracticeEntity> findAll();

	@Query("select c from PracticeEntity c where c.Q_ID = :qid")
	List<PracticeEntity> findQuestionByQID(@Param("qid") Long QID);
	
}
