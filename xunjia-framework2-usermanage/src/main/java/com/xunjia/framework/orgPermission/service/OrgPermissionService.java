package com.xunjia.framework.orgPermission.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.criteria.Predicate;

import com.xunjia.framework.usermanage.entity.OrgPermission;
import com.xunjia.framework.usermanage.entity.Organization;
import com.xunjia.framework.usermanage.entity.UserRoleMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.xunjia.framework.org.repository.IOrganizationRepository;
import com.xunjia.framework.orgPermission.repository.IOrgPermissionRepository;
import com.xunjia.framework.user.repository.IUserRoleMappingRepository;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.framework.utils.StringUtils;

/**
 * 有权组织记录业务服务 2020年5月9日
 * 
 * @author 姜浩
 */
@Service
@Transactional
public class OrgPermissionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrgPermissionService.class);
	
	@Autowired
	private IOrgPermissionRepository repo;

	@Autowired
	private IOrganizationRepository orgRepo;

	@Autowired
	private IUserRoleMappingRepository urmRepo;

	/**
	 * 批量保存权限记录
	 * 
	 * @param records
	 * @return
	 */
	public ResponseData<Boolean> saveRecords(List<OrgPermission> records) {
		ResponseData<Boolean> resp ;
		String[] orgIds = records.stream().map(c -> c.getOrg().getId()).toArray(String[] :: new);
		try {
			// 根据给入的授权类型和菜单id，查询已有的权限记录
			List<OrgPermission> existRecords = repo.findByOwnerTypeAndOwnerIdAndOrg_idIn(records.get(0).getOwnerType(),
					records.get(0).getOwnerId(), orgIds);
			// 定义一个集合，保存新的权限记录
			List<OrgPermission> newRecords = new LinkedList<OrgPermission>();
			for (OrgPermission newRecord : records) {
				// 查询数据库中是否已存在传入的菜单记录
				Optional<OrgPermission> existRecordOptional = existRecords.stream()
						.filter(c -> c.getOrg().getId().equals(newRecord.getOrg().getId())).findFirst();
				if (!existRecordOptional.isPresent()) {
					// 如果数据库中不存在传入的菜单记录，则保存有权组织信息
					newRecords.add(newRecord);
				}
			}
			if (newRecords.size() > 0) {
				repo.saveAll(newRecords);
			}
			resp = ResponseData.getSuccess(ResponseMsg.COMMON_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("OrgPermissionService.saveRecords方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}

	/**
	 * 批量回收权限记录
	 * 
	 * @param ownerType     权限所有者类型
	 * @param ownerId       权限所有者id
	 * @param recoverOrgIds 组织id
	 * @return
	 */
	public ResponseData<Boolean> recover(String ownerType, String ownerId, String[] recoverOrgIds) {
		ResponseData<Boolean> resp;
		try {
			repo.deleteByOwner(ownerType, ownerId, recoverOrgIds);
			resp = ResponseData.getSuccess(ResponseMsg.COMMON_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("OrgPermissionService.recover方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}

	/**
	 * 根据权限所有者类型和所有者id批量删除权限记录
	 * 
	 * @param ownerType
	 * @param ownerIds
	 * @return
	 */
	public ResponseData<Boolean> deleteRecords(String ownerType, String[] ownerIds) {
		ResponseData<Boolean> resp = null;
		try {
			repo.deleteByOwner(ownerType, ownerIds);
			resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("OrgPermissionService.deleteRecords方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}

	/**
	 * 根据权限所有者类型和所有者id查询权限记录
	 * 
	 * @param ownerType
	 * @param ownerIds
	 * @return
	 */
	public List<OrgPermission> findRightRecords(String ownerType, String[] ownerIds, String parentOrgId) {
		List<OrgPermission> records = null;
		try {
			if (StringUtils.isEmpty(parentOrgId)) {
				records = repo.findByOwnerTypeAndOwnerIdInAndOrg_parentIsNull(ownerType, ownerIds);
			} else {
				records = repo.findByOwnerTypeAndOwnerIdInAndOrg_parent_id(ownerType, ownerIds, parentOrgId);
			}
			if (!ListUtils.isListEmpty(records)) {
				records = records.stream().filter(c -> c.getOrg().getEnable() == 1).collect(Collectors.toList());
				for (OrgPermission r : records) {
					r.getOrg().getName();
				}
			}
		} catch (Exception e) {
			LOGGER.error("OrgPermissionService.findRightRecords方法异常。", e);
		}
		return records;
	}

	/**
	 * 查询系统管理员权限记录
	 * @return
	 */
	/*
	public List<OrgPermission> findRightRecordsForAdmin() {
		List<OrgPermission> records = null;
		try {
			List<Organization> allOrgs = orgRepo.findAll(Sort.by(Direction.ASC, "orderNo"));
			allOrgs = allOrgs.stream().filter(c -> c.getEnable() == 1).collect(Collectors.toList());
			if (allOrgs != null && allOrgs.size() > 0) {
				records = new ArrayList<OrgPermission>(allOrgs.size());
				for (Organization o : allOrgs) {
					OrgPermission r = new OrgPermission();
					r.setOrg(o);
					records.add(r);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return records;
	}
	*/
	
	/**
	 * 查询用户有权组织（下级）
	 * @param userId
	 * @param username
	 * @param parentOrgId
	 * @return
	 */
	public List<Organization> findAuthorizedOrganizations(String userId, String username, String parentOrgId) {
		List<Organization> authorizedOrganizations;
		if (username.equals("admin")) {
			authorizedOrganizations = this.findAuthorizedOrganizationsForAdmin(parentOrgId);
		} else {
			authorizedOrganizations = this.findAuthorizedOrganizationsForUser(userId, parentOrgId);
		}
		return authorizedOrganizations;
	}
	
	public List<Organization> findAuthorizedOrganizationsWithoutParent(String userId, String username){
		List<Organization> authorizedOrganizations;
		if (username.equals("admin")) {
			authorizedOrganizations = this.findAuthorizedOrganizationsForAdmin(null);
		} else {
			authorizedOrganizations = this.findAuthorizedOrganizationsForUser(userId, null);
		}
		return authorizedOrganizations;
	}
	
	/**
	 * 查询用户有权组织（当前层级组织和下级组织）
	 * @param userId
	 * @param username
	 * @param currOrgId
	 * @return
	 */
	public List<Organization> findAuthorizedOrganizationsWithCurrOrg(String userId, String username, String currOrgId) {
		List<Organization> authorizedOrganizations = null;
		if (username.equals("admin")) {
			authorizedOrganizations = this.findAuthorizedOrganizationsForAdmin(currOrgId);
		} else {
			authorizedOrganizations = this.findAuthorizedOrganizationsForUser(userId, currOrgId);
			Organization currOrg = orgRepo.findById(currOrgId).get();
			authorizedOrganizations.add(0, currOrg);
		}
		return authorizedOrganizations;
	}
	
	private List<Organization> findAuthorizedOrganizationsForAdmin(String parentOrgId){
		List<Organization> authorizedOrganizations;
		if (StringUtils.isEmpty(parentOrgId)) {
			authorizedOrganizations = orgRepo.findByEnableAndParent_idIsNullAndDeleteFlagOrderByOrderNoAsc(1, 0);
		} else {
			authorizedOrganizations = orgRepo.findByEnableAndParent_idAndDeleteFlagOrderByOrderNoAsc(1, parentOrgId, 0);
		}
		return authorizedOrganizations;
	}

	private List<Organization> findAuthorizedOrganizationsForUser(String userId, String parentOrgId) {
		List<Organization> orgs = new LinkedList<Organization>();

		// 查询用户拥有的角色，并根据角色id查询有权组织
		List<UserRoleMapping> mappingList = urmRepo.findByUser_id(userId);
		if (ListUtils.isListEmpty(mappingList)) {
			String[] roleIds = new String[mappingList.size()];
			for (int i = 0; i < mappingList.size(); i++) {
				roleIds[i] = mappingList.get(i).getId();
				List<OrgPermission> roleOrgPermissions = repo.findByOwnerTypeAndOwnerIdIn("R", roleIds);
				if (!ListUtils.isListEmpty(roleOrgPermissions)) {
					for(OrgPermission op : roleOrgPermissions) {
						if (op.getOrg().getEnable() == 1) {
							Optional<Organization> existOrgOptional = orgs.stream().filter(c -> c.getId().equals(op.getOrg().getId())).findFirst();
							if (!existOrgOptional.isPresent()) {
								orgs.add(op.getOrg());
							}
						}
					}
				}
			}
		}
		
		// 用户有权组织
		List<OrgPermission> orgPermissions = repo.findByOwnerTypeAndOwnerId("U", userId);
		if (!ListUtils.isListEmpty(orgPermissions)) {
			for (OrgPermission op : orgPermissions) {
				if (op.getOrg().getEnable() == 1) {
					Optional<Organization> existOrgOptional = orgs.stream().filter(c -> c.getId().equals(op.getOrg().getId())).findFirst();
					if (!existOrgOptional.isPresent()) {
						orgs.add(op.getOrg());
					}
				}
			}
		}
		
		if (StringUtils.isEmpty(parentOrgId)) {
			int minLevel = Integer.MAX_VALUE;
			for (Organization org : orgs) {
				if (org.getLevel() < minLevel) {
					minLevel = org.getLevel();
				}
			}
			final int finalMinLevel = minLevel;
			orgs = orgs.stream().filter(c -> c.getLevel() == finalMinLevel && c.getEnable() == 1).collect(Collectors.toList());
		} else {
			orgs = orgs.stream().filter(c -> c.getParent() != null && c.getParent().getId().equals(parentOrgId) && c.getEnable() == 1).collect(Collectors.toList());
		}
		
		return orgs;
	}

	public Page<OrgPermission> findOrgPermissions(String ownerType, int ownerId, int page, int rows){
		Specification<OrgPermission> spec = (Specification<OrgPermission>) (root, query, cb) -> {

			List<Predicate> predicates = new LinkedList<>();
			Predicate typePredicate = cb.equal(root.get("ownerType").as(String.class), ownerType);
			Predicate idPredicate = cb.equal(root.get("ownerId").as(Integer.class), ownerId);
			predicates.add(typePredicate);
			predicates.add(idPredicate);

			return cb.and(predicates.toArray(new Predicate[0]));
		};
		Sort sort = Sort.by(Direction.ASC, "org.orderNo");
		Pageable pageable = PageRequest.of(page - 1, rows, sort);
		Page<OrgPermission> pageData = null;
		try {
			pageData = repo.findAll(spec, pageable);
		} catch (Exception e) {
			LOGGER.error("OrgPermissionService.findOrgPermissions方法异常。", e);
		}
		return pageData;
	}
}
