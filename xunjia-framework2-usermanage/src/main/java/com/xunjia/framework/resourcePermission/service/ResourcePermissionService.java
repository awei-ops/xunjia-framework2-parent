package com.xunjia.framework.resourcePermission.service;

import java.util.ArrayList;
import java.util.List;

import com.xunjia.framework.usermanage.entity.ResourcePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.resourcePermission.repository.IResourcePermissionRepository;
import com.xunjia.framework.resourcePermission.util.AuthorizedResourceUtil;

@Service
@Transactional
public class ResourcePermissionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourcePermissionService.class);
	
	@Autowired
	private IResourcePermissionRepository permRepo;
	
	@Autowired
	private AuthorizedResourceUtil authorizedResourceUtil;
	
	public ResponseData<Boolean> saveResourcePermissions(String ownerType, String ownerId, String[] resourceIds){
		ResponseData<Boolean> resp = null;
		List<ResourcePermission> resources = new ArrayList<ResourcePermission>(resourceIds.length);
		for (String resourceId : resourceIds) {
			ResourcePermission rp = new ResourcePermission();
			rp.setOwnerType(ownerType);
			rp.setOwnerId(ownerId);
			rp.setResourceId(resourceId);
			resources.add(rp);
		}
		try {
			permRepo.deleteByOwner(ownerType, ownerId, resourceIds);
			permRepo.saveAll(resources);
			resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("ResourcePermissionService.saveResourcePermissions方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public ResponseData<Boolean> recoverResourcePermissions(String ownerType, String ownerId, String[] resourceIds){
		ResponseData<Boolean> resp = null;
		try {
			permRepo.deleteByOwner(ownerType, ownerId, resourceIds);
			resp = ResponseData.getSuccess(ResponseMsg.COMMON_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("ResourcePermissionService.recoverResourcePermissions方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/*public List<Resource> findAuthorizedResources(String userId, String username){
		return authorizedResourceUtil.findAuthorizedResources(userId, username);
	}*/

	public List<ResourcePermission> findByOwner(String ownerType, String ownerId){
		return permRepo.findByOwnerTypeAndOwnerId(ownerType, ownerId);
	}
}
