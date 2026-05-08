package com.xunjia.framework.role.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.Predicate;

import com.xunjia.framework.common.Context;
import com.xunjia.framework.common.vo.TreeVO;
import com.xunjia.framework.org.repository.IOrganizationRepository;
import com.xunjia.framework.usermanage.entity.Organization;
import com.xunjia.framework.usermanage.entity.Role;
import com.xunjia.framework.utils.ListUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.orgPermission.repository.IOrgPermissionRepository;
import com.xunjia.framework.resourcePermission.repository.IResourcePermissionRepository;
import com.xunjia.framework.role.repository.IRoleRepository;
import com.xunjia.framework.user.repository.IUserRoleMappingRepository;
import com.xunjia.framework.utils.StringLetterUtils;
import com.xunjia.framework.utils.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 角色信息业务服务
 * 2020年5月9日
 * @author 姜浩
 */
@Service
@Transactional
@Slf4j
public class RoleService {

	@Autowired
	private IRoleRepository repo;
	
	@Autowired
	private IUserRoleMappingRepository urmRepo;
	
	@Autowired
	private IResourcePermissionRepository resPermRepo;
	
	@Autowired
	private IOrgPermissionRepository orgPermRepo;

	@Autowired
	private IOrganizationRepository orgRepo;
	
	/**
	 * 保存角色信息
	 * @param role
	 * @return
	 */
	public ResponseData<Boolean> save(Role role){
		ResponseData<Boolean> resp;
		if (role.getOrganization() != null && StringUtils.isEmpty(role.getOrganization().getId())){
			role.setOrganization(null);
		}
		role.setEnable(1);
		role.setPyCode(StringLetterUtils.getFirstLetter(role.getName()));
		try {
			Role existRole = repo.findByName(role.getName());
			if (existRole == null) {
				repo.save(role);
				resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
			} else {
				resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_NAME_EXIST);
			}
		} catch (Exception e) {
			log.error("RoleService.save方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 更新角色信息
	 * @param role
	 * @return
	 */
	public ResponseData<Boolean> update(Role role){
		ResponseData<Boolean> resp;
		if (role.getOrganization() != null && StringUtils.isEmpty(role.getOrganization().getId())){
			role.setOrganization(null);
		}
		try {
			Role existRole = repo.findByName(role.getName());
			if (existRole == null) {
				role.setPyCode(StringLetterUtils.getFirstLetter(role.getName()));
				repo.save(role);
				resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
			} else if (existRole.getId().equals(role.getId())) {
				existRole.setName(role.getName());
				existRole.setOrderNo(role.getOrderNo());
				existRole.setPyCode(StringLetterUtils.getFirstLetter(role.getName()));
				existRole.setOrganization(role.getOrganization());
				repo.save(existRole);
				resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
			} else {
				resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_NAME_EXIST);
			}
		} catch (Exception e) {
			log.error("RoleService.update方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 根据id批量删除角色信息
	 * @param ids
	 * @return
	 */
	public ResponseData<Boolean> deleteByIds(String[] ids){
		ResponseData<Boolean> resp;
		try {
			orgPermRepo.deleteByOwner("R", ids);
			resPermRepo.deleteByOwner("R", ids);
			urmRepo.deleteByRoleIds(ids);
			repo.deleteByIds(ids);
			resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
		} catch (Exception e) {
			log.error("RoleService.deleteByIds方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 批量更新角色可用状态
	 * @param enable  角色可用状态
	 * @param ids  角色id
	 * @return
	 */
	public ResponseData<Boolean> updateEnableState(int enable, String[] ids){
		ResponseData<Boolean> resp;
		try {
			repo.updateEnableState(enable, ids);
			resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
		} catch (Exception e) {
			log.error("RoleService.updateEnableState方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 根据id查询角色信息
	 * @param id
	 * @return
	 */
	public Role findById(String id) {
		return repo.findById(id).get();
	}
	
	/**
	 * 查询可用角色信息
	 * @return
	 */
	public List<Role> findEnableRoles(String orgId){
		if (StringUtils.isEmpty(orgId)){
			return repo.findByEnableAndOrganizationIsNull(1);
		}
		return repo.findByEnableAndOrganization_id(1, orgId);
	}

	/**
	 * 根据组织机构id查询角色。id为空时，查询全局角色
	 * @param orgId 组织id
	 * @return
	 */
	public List<Role> findByOrganization(String orgId){
		if (StringUtils.isEmpty(orgId)){
			return repo.findByOrganization_id(orgId);
		}
		return repo.findByOrganizationIsNull();
	}

	/**
	 * 多参数查询角色分页信息
	 * @param name				角色名称
	 * @param pyCode			拼音码
	 * @param enable			可用状态
	 * @param pageIndex		页号
	 * @param rows				每页显示条数
	 * @return
	 */
	public Page<Role> findRoles(String name, String pyCode, int enable, String orgId, int pageIndex, int rows){
		Specification<Role> spec = (Specification<Role>) (root, query, cb) -> {

			List<Predicate> predicates = new LinkedList<>();
			if (!StringUtils.isEmpty(name)) {
				Predicate predicate = cb.like(root.get("name").as(String.class), "%" + name + "%");
				predicates.add(predicate);
			}
			if (!StringUtils.isEmpty(pyCode)) {
				Predicate predicate = cb.like(root.get("pyCode").as(String.class), pyCode + "%");
				predicates.add(predicate);
			}
			if (enable != -1) {
				Predicate predicate = cb.equal(root.get("enable").as(Integer.class), enable);
				predicates.add(predicate);
			}
			if (!StringUtils.isEmpty(orgId)){
				Predicate predicate = cb.equal(root.get("organization").get("id").as(String.class), orgId);
				predicates.add(predicate);
			} else {
				Predicate predicate = cb.isNull(root.get("organization"));
				predicates.add(predicate);
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
		Sort sort = Sort.by(Direction.ASC, "orderNo");
		Pageable pageable = PageRequest.of(pageIndex - 1, rows, sort);
		Page<Role> pageData = null;
		try {
			pageData = repo.findAll(spec, pageable);
		} catch (Exception e) {
			log.error("RoleService.findRoles方法异常。", e);
		}
		return pageData;
	}

	@RequestMapping("/getRoleTreeForAuthorize")
	public List<TreeVO> getRoleTreeForAuthorize(String id){
		List<Organization> orgs = null;
		List<Role> roles = null;
		if (!StringUtils.isEmpty(id)){
			orgs = orgRepo.findByEnableAndParent_idAndDeleteFlagOrderByOrderNoAsc(1, id, 0);
			roles = repo.findByEnableAndOrganization_id(1, id);
		} else if (Context.getCurrentUser().getUsername().equals("admin")){
			orgs = orgRepo.findByEnableAndParent_idIsNullAndDeleteFlagOrderByOrderNoAsc(1, 0);
			roles = repo.findByEnableAndOrganizationIsNull(1);
		} else {
			id = Context.getCurrentUser().getOrg().getId();
			orgs = orgRepo.findByEnableAndParent_idAndDeleteFlagOrderByOrderNoAsc(1, id, 0);
			roles = repo.findByEnableAndOrganization_id(1, id);
		}

		if (orgs == null && roles == null){
			return new ArrayList<>(0);
		}
		return this.buildRoleTree(orgs, roles, id);
	}

	private List<TreeVO> buildRoleTree(List<Organization> orgs, List<Role> roles, String parentOrgId){
		List<TreeVO> treeNodes = new ArrayList<>();
		List<Organization> subOrgs = null;
		List<Role> subRoles = null;
		if (StringUtils.isEmpty(parentOrgId)) {
			subOrgs = orgs.stream().filter(c -> c.getParent() == null).collect(Collectors.toList());
			subRoles = roles.stream().filter(c -> c.getOrganization() == null).collect(Collectors.toList());
		} else {
			subOrgs = orgs.stream().filter(c -> c.getParent() != null && c.getParent().getId().equals(parentOrgId)).collect(Collectors.toList());
			subRoles = roles.stream().filter(c -> c.getOrganization() != null && c.getOrganization().getId().equals(parentOrgId)).collect(Collectors.toList());
		}

		if (!ListUtils.isListEmpty(subOrgs)) {
			for (Organization org : subOrgs) {
				TreeVO orgNode = new TreeVO("org_" + org.getId(), org.getName(), TreeVO.CLOSED, org.getType().getIcon());
				orgNode.setChildren(this.buildRoleTree(orgs, roles, org.getId()));
				treeNodes.add(orgNode);
			}
		}
		if (!ListUtils.isListEmpty(subRoles)) {
			for (Role role : subRoles) {
				TreeVO roleNode = new TreeVO("role_" + role.getId(), role.getName(), TreeVO.OPEN, null);
				treeNodes.add(roleNode);
			}
		}
		return treeNodes;
	}
}
