package ${entity.packageName}.service;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xunjia.framework.common.Context;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import ${entity.packageName}.entity.${entity.entityName};
import ${entity.packageName}.repository.I${entity.entityName?cap_first}Repository;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.framework.utils.DateUtils;

@Service
@Transactional
@Slf4j
public class ${entity.entityName?cap_first}Service {

	@Autowired
	private I${entity.entityName?cap_first}Repository ${entity.entityName?uncap_first}Repo;
	
	<#if methodCode.saveMethod??>${methodCode.saveMethod}</#if>
	<#if methodCode.updateMethod??>${methodCode.updateMethod}</#if>
	<#if methodCode.deleteMethod??>${methodCode.deleteMethod}</#if>
	<#if methodCode.stateMethods??>${methodCode.stateMethods}</#if>
	
	/**
	 * 根据id查询${entity.entityDescr}信息
	 * @param id 主键
	 * @return
	 */
	public ${entity.entityName} findById(String id) {
		return ${entity.entityName?uncap_first}Repo.findById(id).get();
	}
	
	<#if entity.treeStructure == 1>
		public List<${entity.entityName}> findByParent(String parentId){
			if (StringUtils.isEmpty(parentId)){
				return ${entity.entityName?uncap_first}Repo.findByParentIsNull();
			} else {
				return ${entity.entityName?uncap_first}Repo.findByParent_id(parentId);
			}
		}

		<#if enableFlag == 1>
		public List<${entity.entityName}> findByParent(String parentId, Integer enableState){
			if (StringUtils.isEmpty(parentId)){
				return ${entity.entityName?uncap_first}Repo.findByParentIsNullAndEnableState(enableState);
			} else {
				return ${entity.entityName?uncap_first}Repo.findByParent_idAndEnableState(parentId, enableState);
			}
		}
		</#if>
	</#if>
	
	<#if methodCode.queryMethod??>${methodCode.queryMethod}</#if>
}