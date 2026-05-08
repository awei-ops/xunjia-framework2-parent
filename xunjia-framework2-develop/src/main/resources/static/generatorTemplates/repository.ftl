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

	Page<${entity.entityName}> findAll(Specification<${entity.entityName}> spec, Pageable pageable);

	Page<${entity.entityName}> findAll(Pageable pageable);
	
	@Modifying
	@Query("DELETE FROM ${entity.entityName} WHERE id IN (?1)")
	void deleteByIds(String[] ids);
	
	<#list entity.properties as p>
		<#if p.pkFlag == 0 && p.uniqueFlag == 1 && p.enableFlag == 0>
			${entity.entityName} findBy${p.propName?cap_first}(${p.type} ${p.propName});
		</#if>
	</#list>
	
	<#if entity.treeStructure == 1>
		List<${entity.entityName}> findByParent_id(String id);
			
		List<${entity.entityName}> findByParentIsNull();

		<#if enableFlag == 1>
		    List<${entity.entityName}> findByParent_idAndEnableState(String id, Integer enableState);

            List<${entity.entityName}> findByParentIsNullAndEnableState(Integer enableState);
		</#if>
	</#if>

	<#if enableFlag == 1>
    	@Modifying
    	@Query("UPDATE ${entity.entityName} SET enableState = ?1 WHERE id IN (?2)")
    	void updateEnableState(Integer enableState, String[] ids);

    	List<${entity.entityName}> findByEnableState(Integer enableState);
    </#if>
}
