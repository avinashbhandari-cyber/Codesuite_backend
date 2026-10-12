package com.divyansh.online.compiler.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.divyansh.online.compiler.Entity.ContestEntity;
import com.divyansh.online.compiler.Entity.ContestQnEntity;

@Repository
public interface ContestQnRepository extends JpaRepository<ContestQnEntity, Long>{

	List<ContestQnEntity> findAll();
	
	@Query("select c from ContestQnEntity c where c.Q_ContestID = :cid")
	List<ContestQnEntity> findQuestionByContestID(@Param("cid") Long ContestID);
	
	@Query("select c from ContestQnEntity c where c.Q_ContestID = :cid and c.Q_ID = :qid")
	List<ContestQnEntity> findQuestionByQuestionID(@Param("cid") Long ContestID, @Param("qid") Long QuestionID);
	
}
