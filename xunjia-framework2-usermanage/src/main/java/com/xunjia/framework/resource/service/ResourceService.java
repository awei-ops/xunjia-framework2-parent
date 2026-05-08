package com.xunjia.framework.resource.service;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.xunjia.framework.usermanage.entity.Resource;
import com.xunjia.framework.utils.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.xunjia.framework.resource.repository.IResourceRepository;
import com.xunjia.framework.resourcePermission.repository.IResourcePermissionRepository;
import com.xunjia.framework.utils.FileUtils;
import com.xunjia.framework.utils.StringUtils;

@Service
@Transactional
public class ResourceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceService.class);
	
	@Autowired
	private IResourceRepository repo;

	@Autowired
	private IResourcePermissionRepository permRepo;

	@Value("${com.xunjia.framework.baseUploadFolder}")
	private String uploadFolder;
	
	public ResponseData<Boolean> save(Resource resource) {
		ResponseData<Boolean> resp = null;
		resource.setEnable(1);
		try {
			if (!StringUtils.isEmpty(resource.getCode())) {
				Resource existResource = repo.findByCode(resource.getCode());
				if (existResource == null) {
					resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_EXIST);
					return resp;
				}
			}
			
			repo.save(resource);
			resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("ResourceService.save方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}

	public ResponseData<Boolean> update(Resource resource) {
		ResponseData<Boolean> resp = null;
		try {
			if (!StringUtils.isEmpty(resource.getImgIcon())) {
				repo.save(resource);
			} else {
				Resource existResource = repo.findById(resource.getId()).get();
				if (StringUtils.isEmpty(resource.getCode())) {
					existResource.setCode(null);
				} else {
					Resource sameCodeResource = repo.findByCode(resource.getCode());
					if (sameCodeResource == null) {
						existResource.setCode(resource.getCode());
					} else {
						resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_EXIST);
						return resp;
					}
				}
				existResource.setAllowGrant(resource.getAllowGrant());
				existResource.setFontIcon(resource.getFontIcon());
				existResource.setIntegrateType(resource.getIntegrateType());
				existResource.setName(resource.getName());
				existResource.setOnclick(resource.getOnclick());
				existResource.setOrderNo(resource.getOrderNo());
				existResource.setPermissionCode(resource.getPermissionCode());
				existResource.setStyle(resource.getStyle());
				existResource.setType(resource.getType());
				existResource.setUrl(resource.getUrl());
				existResource.setParent(resource.getParent());
				existResource.setCategory(resource.getCategory());

				// 如果用户上传了新的图标，则删除原有图标文件
				if (!StringUtils.isEmpty(resource.getImgIcon())) {
					this.deleteImgIconFile(existResource.getImgIcon());
					existResource.setImgIcon(resource.getImgIcon());
				}

				repo.save(existResource);
			}
			resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("ResourceService.update方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}

	public ResponseData<Boolean> delete(String[] ids) {
		ResponseData<Boolean> resp = null;
		try {

			List<Resource> resources = repo.findByIdIn(ids);
			if (!ListUtils.isListEmpty(resources)){
				for (Resource r : resources){
					this.delete(r);
				}
			}

			resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("ResourceService.delete方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}

	private void delete(Resource resource){
		List<Resource> subResources = repo.findByParent_id(resource.getId());
		if (ListUtils.isListEmpty(subResources)){
			permRepo.deleteByResource(resource.getId());
			repo.delete(resource);
		} else {
			for (Resource subResource : subResources){
				this.delete(subResource);
			}
		}
	}

	public ResponseData<Boolean> updateState(int state, String[] ids) {
		ResponseData<Boolean> resp = null;
		try {
			repo.updateState(state, ids);
			resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("ResourceService.updateState方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}

	public ResponseData<Boolean> deleteImgIcon(String id) {
		Resource res = repo.findById(id).get();
		if (!StringUtils.isEmpty(res.getImgIcon())) {
			this.deleteImgIconFile(res.getImgIcon());
		}
		res.setImgIcon(null);
		repo.save(res);
		return ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
	}

	public List<Resource> findAllResources() {
		List<Resource> resources = repo.findAll();
		resources.sort(new Comparator<Resource>() {
			@Override
			public int compare(Resource o1, Resource o2) {
				return o1.getOrderNo() - o2.getOrderNo();
			}
		});
		return resources;
	}

	public List<Resource> findEnableResources() {
		return repo.findByEnableOrderByOrderNoAsc(1);
	}

	public List<Resource> findAllowGrantResources() {
		return repo.findByEnableAndAllowGrantOrderByOrderNoAsc(1, 1);
	}

	public List<Resource> findByIds(String[] ids) {
		return repo.findByIdInOrderByOrderNoAsc(ids);
	}

	public List<Resource> findEnableMenus() {
		return repo.findByTypeAndEnableOrderByOrderNoAsc("菜单", 1);
	}

	public Resource findById(String id) {
		return repo.findById(id).get();
	}
	
	public Resource findByUrl(String url) {
		return repo.findByUrl(url);
	}

	public Page<Resource> findResources(String name, String type, String parentId, int enable, int allowGrant, int page,
			int rows) {
		Specification<Resource> spec = new Specification<Resource>() {
			public Predicate toPredicate(Root<Resource> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicates = new LinkedList<Predicate>();
				if (!StringUtils.isEmpty(name)) {
					Predicate predicate = cb.like(root.get("name").as(String.class), "%" + name + "%");
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(type)) {
					Predicate predicate = cb.equal(root.get("type").as(String.class), type);
					predicates.add(predicate);
				}
				if (allowGrant != -1) {
					Predicate predicate = cb.equal(root.get("allowGrant").as(Integer.class), allowGrant);
					predicates.add(predicate);
				}
				if (enable != -1) {
					Predicate predicate = cb.equal(root.get("enable").as(Integer.class), enable);
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(parentId) && !parentId.equals("0")) {
					Predicate predicate = cb.equal(root.get("parent").get("id").as(String.class), parentId);
					predicates.add(predicate);
				} else {
					Predicate predicate = cb.isNull(root.get("parent"));
					predicates.add(predicate);
				}

				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		Sort sort = Sort.by(Direction.ASC, "orderNo");
		Pageable pageable = PageRequest.of(page - 1, rows, sort);
		Page<Resource> pageData = null;
		try {
			pageData = repo.findAll(spec, pageable);
		} catch (Exception e) {
			LOGGER.error("ResourceService.findResources方法异常。", e);
		}
		return pageData;
	}

	private void deleteImgIconFile(String savePath) {
		FileUtils.deleteFile(uploadFolder + savePath);
	}
}
