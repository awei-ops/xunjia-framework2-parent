package com.xunjia.framework.resourcePermission.util;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.xunjia.framework.usermanage.entity.Resource;
import com.xunjia.framework.usermanage.entity.ResourcePermission;
import com.xunjia.framework.usermanage.entity.UserRoleMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xunjia.framework.resource.repository.IResourceRepository;
import com.xunjia.framework.resourcePermission.repository.IResourcePermissionRepository;
import com.xunjia.framework.user.repository.IUserRoleMappingRepository;
import com.xunjia.framework.utils.ListUtils;

@Component
public class AuthorizedResourceUtil {
	
	@Autowired
	private IResourceRepository resRepo;
	
	@Autowired
	private IUserRoleMappingRepository urmRepo;
	
	@Autowired
	private IResourcePermissionRepository permRepo;
	
	public List<Resource> findAuthorizedResources(String userId, String username){
		List<Resource> authorizedResources = null;
		if (username.equals("admin")) {
			authorizedResources = this.findAuthorizedResourcesForAdmin();
		} else {
			authorizedResources = this.findAuthorizedResourcesForUser(userId);
		}
		return authorizedResources;
	}

	private List<Resource> findAuthorizedResourcesForAdmin(){
		List<Resource> resourceList = resRepo.findByCategory("WEB");
		resourceList = resourceList.stream().filter(c -> c.getEnable() == 1).collect(Collectors.toList());
		resourceList.sort(new Comparator<Resource>() {
			@Override
			public int compare(Resource o1, Resource o2) {
				return o1.getOrderNo() - o2.getOrderNo();
			}
		});
		return resourceList;
	}
	
	private List<Resource> findAuthorizedResourcesForUser(String userId){
		List<Resource> authorizedResources = this.findAuthcResources();
		if (authorizedResources == null) {
			authorizedResources = new LinkedList<Resource>();
		}
		
		//查询用户拥有的角色，并根据角色id查询有权资源
		List<UserRoleMapping> mappingList = urmRepo.findByUser_id(userId);
		if (!ListUtils.isListEmpty(mappingList)) {
			String[] roleIds = new String[mappingList.size()];
			for (int i = 0; i < mappingList.size(); i++) {
				roleIds[i] = mappingList.get(i).getRole().getId();
			}
			List<ResourcePermission> resourcePermissionList = permRepo.findByOwnerTypeAndOwnerIdIn("R", roleIds);
			if (!ListUtils.isListEmpty(resourcePermissionList)) {
				String[] resourceIds = new String[resourcePermissionList.size()];
				for (int i = 0; i < resourcePermissionList.size(); i++) {
					resourceIds[i] = resourcePermissionList.get(i).getResourceId();
				}
				List<Resource> roleAuthorizedResources = resRepo.findByIdInOrderByOrderNoAsc(resourceIds);
				if (!ListUtils.isListEmpty(roleAuthorizedResources)) {
					for (Resource r : roleAuthorizedResources) {
						Optional<Resource> existResourceOptional = authorizedResources.stream()
								.filter(c -> c.getId().equals(r.getId())).findFirst();
						if (!existResourceOptional.isPresent()) {
							authorizedResources.add(r);
						}
					}
				}
			}
		}
		
		//查询用户权限
		List<ResourcePermission> resourcePermissionList = permRepo.findByOwnerTypeAndOwnerId("U", userId);
		if (!ListUtils.isListEmpty(resourcePermissionList)) {
			String[] resourceIds = new String[resourcePermissionList.size()];
			for (int i = 0; i < resourcePermissionList.size(); i++) {
				resourceIds[i] = resourcePermissionList.get(i).getResourceId();
			}
			List<Resource> userAuthorizedResources = resRepo.findByIdInOrderByOrderNoAsc(resourceIds);
			if (!ListUtils.isListEmpty(userAuthorizedResources)) {
				for (Resource resource : userAuthorizedResources) {
					Optional<Resource> existResourceOptional = authorizedResources.stream()
							.filter(c -> c.getId().equals(resource.getId())).findFirst();
					if (!existResourceOptional.isPresent()) {
						authorizedResources.add(resource);
					}
				}
			}
		}
		
		authorizedResources = authorizedResources.stream().filter(c -> c.getEnable() == 1 && "WEB".equals(c.getCategory())).collect(Collectors.toList());
		authorizedResources.sort(new Comparator<Resource>() {
			@Override
			public int compare(Resource o1, Resource o2) {
				return o1.getOrderNo() - o2.getOrderNo();
			}
		});
		return authorizedResources;
	}
	
	/**
	 * 查询系统中所有权限码为“authc”的资源
	 * @return
	 */
	public List<Resource> findAuthcResources(){
		return resRepo.findByPermissionCodeAndCategory("authc", "WEB");
	}
}
