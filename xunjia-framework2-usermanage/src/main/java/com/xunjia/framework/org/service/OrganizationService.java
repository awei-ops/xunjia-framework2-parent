package com.xunjia.framework.org.service;

import com.xunjia.framework.common.Context;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.org.repository.IOrganizationRepository;
import com.xunjia.framework.org.repository.IOrganizationTypeRepository;
import com.xunjia.framework.orgPermission.repository.IOrgPermissionRepository;
import com.xunjia.framework.user.repository.IUserRepository;
import com.xunjia.framework.usermanage.entity.Organization;
import com.xunjia.framework.usermanage.entity.OrganizationType;
import com.xunjia.framework.usermanage.entity.User;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.framework.utils.StringLetterUtils;
import com.xunjia.framework.utils.StringUtils;
//import com.spire.xls.ExcelVersion;
//import com.spire.xls.Workbook;
//import com.spire.xls.Worksheet;
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
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 组织机构业务服务
 * 2020年5月8日
 * @author 姜浩
 */
@Service
@Transactional
public class OrganizationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationService.class);
	
	@Autowired
	private IOrganizationRepository orgRepo;
	
	@Autowired
	private IOrganizationTypeRepository orgTypeRepo;
	
	@Autowired
	private IUserRepository userRepo;
	
	@Autowired
	private IOrgPermissionRepository rightRepo;
	
	/**
	 * 保存组织机构信息
	 * @param org
	 * @return
	 */
	public ResponseData<Boolean> save(Organization org){
		ResponseData<Boolean> resp;
		org.setPyCode(StringLetterUtils.getFirstLetter(org.getName()));
		org.setEnable(1);
		try {
			if (StringUtils.isNotEmpty(org.getCode())) {
				Organization sameCodeOrg = orgRepo.findByCodeAndDeleteFlag(org.getCode(), 0);
				if (sameCodeOrg != null) {
					resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_EXIST);
					return resp;
				}
			}
			
			if (org.getParent() == null || StringUtils.isEmpty(org.getParent().getId())) {
				org.setLevel(1);
			} else {
				Organization parentOrg = orgRepo.getOne(org.getParent().getId());
				org.setLevel(parentOrg.getLevel() + 1);
			}
			orgRepo.save(org);
			resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("OrganizationService.save方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 更新组织机构信息
	 * @param org
	 * @return
	 */
	public ResponseData<Boolean> update(Organization org, String originalCode){
		ResponseData<Boolean> resp;
		org.setPyCode(StringLetterUtils.getFirstLetter(org.getName()));
		if (org.getParent() == null || StringUtils.isEmpty(org.getParent().getId())) {
			org.setLevel(1);
		} else {
			Organization parentOrg = orgRepo.getOne(org.getParent().getId());
			org.setLevel(parentOrg.getLevel() + 1);
		}
		
		try {
			if (StringUtils.isNotEmpty(org.getCode())) {
				if (org.getCode().equals(originalCode)) {
					orgRepo.save(org);
					resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
				} else {
					Organization sameCodeOrg = orgRepo.findByCodeAndDeleteFlag(org.getCode(), 0);
					if (sameCodeOrg == null || sameCodeOrg.getId().equals(org.getId())) {
						orgRepo.save(org);
						resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
					} else {
						resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_EXIST);
					}
				}
			} else {
				orgRepo.save(org);
				resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
			}
		} catch (Exception e) {
			LOGGER.error("OrganizationService.update方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 批量删除组织信息
	 * @param ids
	 * @return
	 */
	public ResponseData<Boolean> deleteByIds(String[] ids){
		ResponseData<Boolean> resp;
		try {
			for (String id : ids) {
				List<Organization> childOrgs = orgRepo.findByParent_idAndDeleteFlagOrderByOrderNoAsc(id, 0);
				if (!ListUtils.isListEmpty(childOrgs)) {
					resp = ResponseData.getFail(ResponseMsg.DELETE_FAIL_SUB_ORG_EXIST);
					return resp;
				}
				List<User> childUsers = userRepo.findByOrg_idAndDeleteFlag(id, 0);
				if (!ListUtils.isListEmpty(childUsers)) {
					resp = ResponseData.getFail(ResponseMsg.DELETE_FAIL_SUB_USER_EXIST);
					return resp;
				}
			}
			rightRepo.deleteByOrgIds(ids);
			orgRepo.deleteByIds(ids);
			resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("OrganizationService.deleteByIds方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 批量更新组织机构可用状态
	 * @param enable
	 * @param ids
	 * @return
	 */
	public ResponseData<Boolean> updateEnableState(int enable, String[] ids){
		ResponseData<Boolean> resp;
		try {
			orgRepo.updateEnableState(enable, ids);
			resp = ResponseData.getSuccess(ResponseMsg.COMMON_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("OrganizationService.updateEnableState方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 根据给定id查询组织信息
	 * @param id
	 * @return
	 */
	public Organization findById(String id) {
		return orgRepo.findById(id).get();
	}

	public Organization findByCode(String code){return  orgRepo.findByCodeAndDeleteFlag(code,0);}
	
	/**
	 * 根据上级组织id查询组织信息
	 * @param parentId
	 * @return
	 */
	public List<Organization> findByParentId(String parentId){
		return StringUtils.isEmpty(parentId) 
				? orgRepo.findByParent_idIsNullAndDeleteFlagOrderByOrderNoAsc(0)
				: orgRepo.findByParent_idAndDeleteFlagOrderByOrderNoAsc(parentId, 0);
	}
	
	public List<Organization> findByParentWithCurrOrg(String orgId){
		List<Organization> orgList = StringUtils.isEmpty(orgId) 
				? orgRepo.findByParent_idIsNullAndDeleteFlagOrderByOrderNoAsc(0)
				: orgRepo.findByParent_idAndDeleteFlagOrderByOrderNoAsc(orgId, 0);
		if (!StringUtils.isEmpty(orgId)) {
			Organization org = orgRepo.findById(orgId).get();
			orgList.add(0, org);
		}
		return orgList;
	}
	
	/**
	 * 根据上级组织id查询可用组织
	 * @param parentId
	 * @return
	 */
	public List<Organization> findEnableOrgByParentId(String parentId){
		return StringUtils.isEmpty(parentId) 
				? orgRepo.findByEnableAndParent_idIsNullAndDeleteFlagOrderByOrderNoAsc(1, 0)
				: orgRepo.findByEnableAndParent_idAndDeleteFlagOrderByOrderNoAsc(1, parentId, 0);
	}
	
	public List<Organization> findEnableOrgByParentWithCurrOrg(String orgId){
		List<Organization> orgList = StringUtils.isEmpty(orgId) 
				? orgRepo.findByEnableAndParent_idIsNullAndDeleteFlagOrderByOrderNoAsc(1, 0)
				: orgRepo.findByEnableAndParent_idAndDeleteFlagOrderByOrderNoAsc(1, orgId, 0);
		if (!StringUtils.isEmpty(orgId)) {
			Organization org = orgRepo.findById(orgId).get();
			orgList.add(0, org);
		}
		return orgList;
	}
	
	/**
	 * 根据组织分类id查询组织信息
	 * @param typeId
	 * @return
	 */
	/*
	public List<Organization> findByTypeId(int typeId){
		return orgRepo.findByType_idOrderByOrderNoAsc(typeId);
	}
	*/
	
	/**
	 * 根据组织分类id查询组织信息
	 * @param ids
	 * @return
	 */
	public List<Organization> findByTypeIds(String[] ids, String parentId){
		return orgRepo.findByType_idInAndParent_idAndDeleteFlagOrderByOrderNoAsc(ids, parentId, 0);
	}
	
	/**
	 * 查询组织机构分页信息
	 * @param name
	 * @param pyCode
	 * @param parentId
	 * @param typeId
	 * @param pageIndex
	 * @param rows
	 * @return
	 */
	public Page<Organization> findOrganizations(String name, String code, String pyCode, String parentId, String typeId, int enabled, int pageIndex, int rows){
		Specification<Organization> spec = (Specification<Organization>) (root, query, cb) -> {

			List<Predicate> predicates = new LinkedList<Predicate>();
			Predicate deletePredicate = cb.equal(root.get("deleteFlag").as(Integer.class), 0);
			predicates.add(deletePredicate);
			if (!StringUtils.isEmpty(name)) {
				Predicate predicate = cb.like(root.get("name").as(String.class), "%" + name + "%");
				predicates.add(predicate);
			}
			if (!StringUtils.isEmpty(code)) {
				Predicate predicate = cb.equal(root.get("code").as(String.class), code);
				predicates.add(predicate);
			}
			if (!StringUtils.isEmpty(pyCode)) {
				Predicate predicate = cb.like(root.get("pyCode").as(String.class), pyCode + "%");
				predicates.add(predicate);
			}
			if (!StringUtils.isEmpty(parentId)) {
				Predicate predicate = cb.equal(root.get("parent").get("id").as(String.class), parentId);
				predicates.add(predicate);
			} else {
				Predicate predicate = cb.isNull(root.get("parent"));
				predicates.add(predicate);
			}
			if (!StringUtils.isEmpty(typeId)) {
				Predicate predicate = cb.equal(root.get("type").get("id").as(String.class), typeId);
				predicates.add(predicate);
			}
			if (enabled != -1) {
				Predicate predicate = cb.equal(root.get("enable").as(Integer.class), enabled);
				predicates.add(predicate);
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
		Sort sort = Sort.by(Direction.ASC, "orderNo");
		Pageable pageable = PageRequest.of(pageIndex - 1, rows, sort);
		Page<Organization> pageData = null;
		try {
			pageData = orgRepo.findAll(spec, pageable);
		} catch (Exception e) {
			LOGGER.error("OrganizationService.findOrganizations方法异常。", e);
		}
		return pageData;
	}


	public ResponseData<Boolean> importOrganizations(MultipartFile file){
		ResponseData<Boolean> resp=null;
//		User currentUser = Context.getCurrentUser();
//		Workbook workbook = new Workbook();
//		try (InputStream is = file.getInputStream()) {
//			workbook.loadFromStream(is, ExcelVersion.Version2013);
//			Worksheet sheet = workbook.getWorksheets().get(0);
//			int lastRowIndex = sheet.getLastRow();
//			if (lastRowIndex > 0) {
//				//分批次导入
//				int pageSize = 100;
//				int pageCount = lastRowIndex % pageSize == 0 ? lastRowIndex / pageSize : lastRowIndex / pageSize + 1;
//
//				for (int page = 0; page < pageCount; page++) {
//					int startPos = page * pageSize + 1;
//					int endPos = startPos + pageSize;
//					if (endPos > lastRowIndex + 1) {
//						endPos = lastRowIndex + 1;
//					}
//
//					// 第一行表头，起始pos需要加1
//					if (page == 0) {
//						startPos += 1;
//					}
//
//					Set<String> parentNames = new HashSet<>();
//					Set<String> orgTypeNames = new HashSet<>();
//					for (int i = startPos; i < endPos; i++){
//						String parentOrgName = sheet.get(i, 3).getValue().trim();
//						String orgType = sheet.get(i, 4).getValue().trim();
//						if (StringUtils.isEmpty(parentOrgName))
//							continue;
//						String[] parentOrgNameArr = parentOrgName.split("/");
//						Collections.addAll(parentNames, parentOrgNameArr);
//						orgTypeNames.add(orgType);
//					}
//					List<Organization> existOrgs = orgRepo.findByNameInAndDeleteFlag(parentNames.toArray(new String[0]), 0);
//					List<OrganizationType> existOrgTypes = orgTypeRepo.findByNameIn(orgTypeNames.toArray(new String[0]));
//
//					List<Organization> newOrgs = new ArrayList<>(endPos - startPos);
//					for (int i = startPos; i < endPos; i++) {
//						String name = sheet.get(i, 1).getValue().trim();
//						String code = sheet.get(i, 2).getValue().trim();
//						String parentOrgName = sheet.get(i, 3).getValue().trim();
//						String orgType = sheet.get(i, 4).getValue().trim();
//						String orderNoStr = sheet.get(i, 5).getValue().trim();
//
//						//如果未填写组织名称、上级组织或组织类型，则抛弃这一行数据
//						if (StringUtils.isEmpty(name) || StringUtils.isEmpty(parentOrgName) || StringUtils.isEmpty(orgType)) {
//							continue;
//						}
//
//						Optional<OrganizationType> orgTypeOptional = existOrgTypes.stream().filter(c -> c.getName().equals(orgType)).findFirst();
//						OrganizationType organizationType = null;
//						if (orgTypeOptional.isPresent()){
//							organizationType = orgTypeOptional.get();
//						} else {
//							continue;
//						}
//
//						String[] parentOrgNameArray = parentOrgName.split("/");
//						boolean isParentExist = false;
//						boolean isParentOrgAllowed = false;    //为true时，表示可以保存该组织下的组织机构
//						Organization parentOrg = null;
//						for (int k = 0; k < parentOrgNameArray.length; k++){
//							final int queryIndex = k;
//							List<Organization> currParentOrgs = existOrgs.stream().filter(c -> c.getName().equals(parentOrgNameArray[queryIndex])).collect(Collectors.toList());
//							if (ListUtils.isListEmpty(currParentOrgs)){
//								break;
//							}
//
//							if (currParentOrgs.size() == 1){
//								parentOrg = currParentOrgs.get(0);
//								if (!currentUser.getUsername().equals("admin") && currentUser.getOrg().getId().equals(parentOrg.getId())) {
//									isParentOrgAllowed = true;
//								}
//							} else {
//								if (parentOrg == null){
//									break;
//								} else {
//									final Organization queryOrg = parentOrg;
//									Optional<Organization> currParentOrg = currParentOrgs.stream().filter(c -> c.getParent() != null && c.getParent().getId().equals(queryOrg.getId())).findFirst();
//									if (currParentOrg.isPresent()){
//										parentOrg = currParentOrg.get();
//										if (!currentUser.getUsername().equals("admin") && currentUser.getOrg().getId().equals(parentOrg.getId())) {
//											isParentOrgAllowed = true;
//										}
//									} else {
//										break;
//									}
//								}
//							}
//
//							if (k == parentOrgNameArray.length - 1){
//								isParentExist = true;
//							}
//						}
//
//						if (!isParentExist){
//							continue;
//						}
//						if (!currentUser.getUsername().equals("admin") && !isParentOrgAllowed) {
//							continue;
//						}
//
//						Organization org = new Organization();
//						org.setName(name);
//						org.setCode(code);
//						org.setEnable(1);
//						org.setParent(parentOrg);
//						org.setDeleteFlag(0);
//						org.setType(organizationType);
//						org.setPyCode(StringLetterUtils.getFirstLetter(name));
//						org.setLevel(parentOrg.getLevel() + 1);
//						try {
//							org.setOrderNo(Integer.parseInt(orderNoStr));
//						} catch (NumberFormatException e) {
//							org.setOrderNo(100);
//						}
//						newOrgs.add(org);
//					}
//					if (newOrgs.size() > 0) {
//						orgRepo.saveAll(newOrgs);
//					}
//				}
//			}
//
//			resp = ResponseData.getSuccess(ResponseMsg.IMPORT_SUCCESS);
//		} catch (IOException e) {
//			LOGGER.error("OrganizationService.clear方法异常。", e);
//			resp = ResponseData.getError(e);
//		}
		return resp;
	}

	public Integer findNextOrderNo(String orgId){
		if (StringUtils.isEmpty(orgId)) return 1;
		Integer maxOrderNo = orgRepo.findMaxOrderNo(orgId);
		return maxOrderNo == null ? 1 : maxOrderNo + 1;
	}

}
