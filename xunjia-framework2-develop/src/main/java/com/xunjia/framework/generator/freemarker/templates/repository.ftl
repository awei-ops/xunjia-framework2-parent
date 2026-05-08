package ${entity.packageName}.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import ${entity.packageName}.entity.${entity.entityName};

public interface I${entity.entityName?cap_first}Repository extends JpaRepository<${entity.entityName}, String> {

	public Page<${entity.entityName}> findAll(Specification<${entity.entityName}> spec, Pageable pageable);
	
	@Modifying
	@Query("DELETE FROM ${entity.entityName} WHERE id IN (?1)")
	public void deleteByIds(String[] ids);
	
	<#list entity.properties as p>
		<#if p.pkFlag == 0 && p.uniqueFlag == 1>
			public ${entity.entityName} findBy${p.propName?cap_first}(${p.type} ${p.propName});
		</#if>
		
		<#if p.enableFlag == 1>
			@Modifying
			@Query("UPDATE ${entity.entityName} SET ${p.propName} = ?1 WHERE id IN (?2)")
			public void updateEnableState(${p.type} ${p.propName}, String[] ids);
			
			public List<${entity.entityName}> findBy${p.propName?cap_first}(${p.type} ${p.propName});
		</#if>
		
	</#list>
	
	<#if entity.treeStructure == 1>
		public List<${entity.entityName}> findByParent_id(String id);
			
		public List<${entity.entityName}> findByParentIsNull();
	</#if>
}
