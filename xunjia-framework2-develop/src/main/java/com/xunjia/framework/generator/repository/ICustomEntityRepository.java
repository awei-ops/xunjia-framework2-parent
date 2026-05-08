package com.xunjia.framework.generator.repository;

import com.xunjia.framework.generator.entity.CustomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ICustomEntityRepository extends JpaRepository<CustomEntity, String> {

	Page<CustomEntity> findAll(Specification<CustomEntity> spec, Pageable pageable);
	
	CustomEntity findByPackageNameAndEntityName(String packageName, String entityName);
	
	@Modifying
	@Query("DELETE FROM CustomEntity WHERE id IN (?1)")
	void deleteByIds(String[] ids);

	List<CustomEntity> findByIdIn(String[] ids);
}
