package com.xunjia.framework.appendix.repository;

import java.util.List;

import com.xunjia.framework.common.entity.Appendix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface IAppendixRepository extends JpaRepository<Appendix, String> {

	List<Appendix> findByBusinessTypeAndBusinessId(String businessType, String businessId);
	
	@Modifying
    @Query(value="DELETE FROM Appendix WHERE id IN (:ids)")
	void deleteByIds(@Param("ids")List<String> ids);
	
	@Modifying
	@Query(value="DELETE FROM Appendix WHERE businessType = :businessType AND businessId = :businessId")
	void deleteByBusinessTypeAndBusinessId(@Param("businessType")String businessType, @Param("businessId")String businessId);
}
