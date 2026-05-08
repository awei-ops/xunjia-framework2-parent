package com.xunjia.framework.generator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.xunjia.framework.generator.entity.CustomEntityProperty;

public interface ICustomEntityPropertyRepository extends JpaRepository<CustomEntityProperty, String> {

	public List<CustomEntityProperty> findByEntity_id(String entityId);
	
	@Modifying
	@Query("DELETE FROM CustomEntityProperty WHERE entity.id = ?1")
	public void deleteByEntityId(String entityId);
}
