package com.xunjia.framework.user.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.xunjia.framework.usermanage.entity.Role;
import com.xunjia.framework.usermanage.entity.User;
import com.xunjia.framework.usermanage.entity.UserRoleMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.user.repository.IUserRoleMappingRepository;
import com.xunjia.framework.utils.ListUtils;

/**
 * 用户角色关系对照业务服务
 * 2020年5月9日
 * @author 姜浩
 */
@Service
@Transactional
public class UserRoleMappingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleMappingService.class);
	
	@Autowired
	private IUserRoleMappingRepository repo;
	
	/**
	 * 保存对照关系
	 * @param roleId
	 * @param userIds
	 * @return
	 */
	public ResponseData<Boolean> save(String roleId, String[] userIds){
		ResponseData<Boolean> resp = null;
		List<UserRoleMapping> urmList = new ArrayList<UserRoleMapping>(userIds.length);
		for (String userId : userIds) {
			UserRoleMapping urm = new UserRoleMapping();
			User user = new User();
			user.setId(userId);
			Role role = new Role();
			role.setId(roleId);
			urm.setUser(user);
			urm.setRole(role);
			urmList.add(urm);
		}
		
		try {
			repo.deleteEntity(roleId, userIds);
			repo.saveAll(urmList);

			resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("UserRoleMappingService.save方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public ResponseData<Boolean> save(String[] roleIds, String userId){
		ResponseData<Boolean> resp = null;
		List<UserRoleMapping> urmList = new ArrayList<UserRoleMapping>(roleIds.length);
		for (String roleId : roleIds) {
			UserRoleMapping urm = new UserRoleMapping();
			User user = new User();
			user.setId(userId);
			Role role = new Role();
			role.setId(roleId);
			urm.setUser(user);
			urm.setRole(role);
			urmList.add(urm);
		}
		
		try {
			repo.deleteEntity(roleIds, userId);
			repo.saveAll(urmList);
			resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("UserRoleMappingService.save方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}

	/**
	 * 删除指定用户的对照关系
	 * @param userIds
	 * @return
	 */
	public ResponseData<Boolean> deleteByUserIds(String[] userIds){
		ResponseData<Boolean> resp = null;
		try {
			repo.deleteByUserIds(userIds);
			resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("UserRoleMappingService.deleteByUserIds方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 删除指定角色的对照关系
	 * @param roleIds
	 * @return
	 */
	public ResponseData<Boolean> deleteByRoleIds(String[] roleIds){
		ResponseData<Boolean> resp = null;
		try {
			repo.deleteByRoleIds(roleIds);
			resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("UserRoleMappingService.deleteByRoleIds方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}

	public ResponseData<Boolean> deleteMappings(String roleId, String[] userIds){
		ResponseData<Boolean> resp = null;
		try {
			repo.deleteEntity(roleId, userIds);
			resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("UserRoleMappingService.deleteMappings方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public ResponseData<Boolean> deleteMappings(String[] roleIds, String userId){
		ResponseData<Boolean> resp = null;
		try {
			repo.deleteEntity(roleIds, userId);
			resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("UserRoleMappingService.deleteMappings方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 查询指定用户的对照关系
	 * @param userId
	 * @return
	 */
	public List<Role> findRolesByUser(String userId){
		List<Role> roles = null;
		try {
			List<UserRoleMapping> mappings = repo.findByUser_id(userId);
			if (!ListUtils.isListEmpty(mappings)) {
				roles = new ArrayList<Role>(mappings.size());
				for (UserRoleMapping m : mappings) {
					//过滤掉被禁用的角色
					if (m.getRole().getEnable() == 1) {	
						roles.add(m.getRole());
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("UserRoleMappingService.findRolesByUser方法异常。", e);
		}
		return roles;
	}
	
	/**
	 * 查询用户角色对照关系分页数据
	 * @param roleId
	 * @param username
	 * @param realNamePyCode
	 * @param pageIndex
	 * @param rows
	 * @return
	 */
	public Page<UserRoleMapping> findMappings(String roleId, String username, String realNamePyCode, int pageIndex, int rows){
		Specification<UserRoleMapping> spec = new Specification<UserRoleMapping>() {
			public Predicate toPredicate(Root<UserRoleMapping> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				
				List<Predicate> predicates = new LinkedList<Predicate>();
				if (!StringUtils.isEmpty(roleId)) {
					Predicate predicate = cb.equal(root.get("role").get("id").as(String.class), roleId);
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(username)) {
					Predicate predicate = cb.like(root.get("user").get("username").as(String.class), "%" + username + "%");
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(realNamePyCode)) {
					Predicate predicate = cb.like(root.get("user").get("realNamePyCode").as(String.class), realNamePyCode + "%");
					predicates.add(predicate);
				}
				
				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		Pageable pageable = PageRequest.of(pageIndex - 1, rows);
		
		Page<UserRoleMapping> pageData = null;
		try {
			pageData = repo.findAll(spec, pageable);
		} catch (Exception e) {
			LOGGER.error("UserRoleMappingService.findMappings方法异常。", e);
		}
		return pageData;
	}
}
