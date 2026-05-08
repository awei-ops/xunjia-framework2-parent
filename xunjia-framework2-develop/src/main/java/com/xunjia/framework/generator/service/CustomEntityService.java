package com.xunjia.framework.generator.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import com.xunjia.framework.resource.repository.IResourceRepository;
import com.xunjia.framework.usermanage.entity.Resource;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RequestMapping;

import com.xunjia.framework.common.Context;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.generator.entity.CustomEntity;
import com.xunjia.framework.generator.entity.CustomEntityProperty;
import com.xunjia.framework.generator.freemarker.Generator;
import com.xunjia.framework.generator.repository.ICustomEntityPropertyRepository;
import com.xunjia.framework.generator.repository.ICustomEntityRepository;
import com.xunjia.framework.utils.FileUtils;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.framework.utils.ZipUtils;

@Service
@Transactional
@Slf4j
public class CustomEntityService {

	@Autowired
	private ICustomEntityRepository entityRepo;
	
	@Autowired
	private ICustomEntityPropertyRepository propRepo;

	@Autowired
	private IResourceRepository resourceRepo;

	@Value("${com.xunjia.framework.baseUploadFolder}")
	private String uploadFolder;

	//@Autowired
	//private DocumentGenerator docGenerator;
	
	public ResponseData<Boolean> save(CustomEntity entity){
		ResponseData<Boolean> resp;
		entity.setCreateDate(new Date());
		entity.setAuthor(Context.getCurrentUser().getRealName());
		try {
			CustomEntity existEntity = entityRepo.findByPackageNameAndEntityName(entity.getPackageName(), entity.getEntityName());
			if (existEntity != null) {
				resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_NAME_EXIST);
			} else {
				entityRepo.save(entity);
				List<CustomEntityProperty> props = new ArrayList<>();
				for (CustomEntityProperty p : entity.getProperties()) {
					if (p.getPropName().equals("id")){
						p.setControlType("隐藏域");
					}
					if (!StringUtils.isEmpty(p.getPropName())) {
						p.setEntity(entity);
						props.add(p);
					}
					/*if (p.getControlType().equals("组织选择") || p.getControlType().equals("用户选择")){
						String propName = p.getPropName();
						String idPropName = this.generateIdPropName(entity.getProperties(), propName, 0);
						String idPropSuffix = idPropName.substring(propName.length() + 1);

						CustomEntityProperty newProp = new CustomEntityProperty();
						newProp.setEntity(entity);
						newProp.setColumnName(p.getColumnName() + "_" + idPropSuffix);
						newProp.setType("String");
						newProp.setControlType("隐藏域");
						newProp.setPropDescr(p.getPropDescr() + "Id");
						newProp.setOrderNo(p.getOrderNo());
						props.add(newProp);
					}*/
				}
				propRepo.saveAll(props);
				resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
			}
		} catch (Exception e) {
			log.error("CustomEntityService.save方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public ResponseData<Boolean> update(CustomEntity entity) {
		ResponseData<Boolean> resp;
		try {
			CustomEntity existEntity = entityRepo.findByPackageNameAndEntityName(entity.getPackageName(), entity.getEntityName());
			if (existEntity == null || existEntity.getId().equals(entity.getId())) {
				entity.setCreateDate(new Date());
				entity.setAuthor(Context.getCurrentUser().getRealName());
				existEntity = entity;
				entityRepo.save(existEntity);
				propRepo.deleteByEntityId(entity.getId());

				List<CustomEntityProperty> props = new ArrayList<CustomEntityProperty>();
				for (CustomEntityProperty p : entity.getProperties()) {
					if (p.getPropName().equals("id")){
						p.setControlType("隐藏域");
					}
					if (!StringUtils.isEmpty(p.getPropName())) {
						p.setEntity(existEntity);
						props.add(p);
					}
					/*if (p.getControlType().equals("组织选择") || p.getControlType().equals("用户选择")){
						String propName = p.getPropName();
						String idPropName = this.generateIdPropName(entity.getProperties(), propName, 0);
						String idPropSuffix = idPropName.substring(propName.length());

						CustomEntityProperty newProp = new CustomEntityProperty();
						newProp.setEntity(entity);
						newProp.setColumnName(p.getColumnName() + "_" + idPropSuffix);
						newProp.setType("String");
						newProp.setControlType("隐藏域");
						newProp.setPropDescr(p.getPropDescr() + "Id");
						newProp.setOrderNo(p.getOrderNo());
						props.add(newProp);
					}*/
				}
				propRepo.saveAll(props);
				resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
			} else {
				resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_NAME_EXIST);
			}
		} catch (Exception e) {
			log.error("CustomEntityService.update方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}

	/*private String generateIdPropName(List<CustomEntityProperty> props, String propName, int suffix){
		String idPropName = propName + "Id";
		if (suffix > 0){
			idPropName += suffix;
		}
		final String queryName = idPropName;
		if (props.stream().anyMatch(c -> c.getPropName().equals(queryName))){
			idPropName = this.generateIdPropName(props, propName, suffix + 1);
		}
		return idPropName;
	}*/
	
	public ResponseData<Boolean> delete(String[] ids){
		ResponseData<Boolean> resp;
		try {
			for (String id : ids) {
				propRepo.deleteByEntityId(id);
			}
			entityRepo.deleteByIds(ids);
			resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
		} catch (Exception e) {
			log.error("CustomEntityService.delete方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public ResponseData<Boolean> generate(String[] ids){
		ResponseData<Boolean> resp;
		String generatePath = uploadFolder + "/generator";
		File generateDirFile = new File(generatePath);
		FileUtils.deleteDirectory(generatePath);
		generateDirFile.mkdirs();
		try {
			for (String id : ids) {
				CustomEntity entity = entityRepo.findById(id).get();
				Generator generator = new Generator(entity, generatePath);
				generator.generateEntity();
				generator.generateRepository();
				generator.generateService();
				generator.generateController();
				
				if (entity.getTreeStructure() == 1) {
					generator.generateTreeListHtml();
				} else {
					generator.generateListHtml();
				}
				generator.generateAddHtml();
				generator.generateEditHtml();
			}
			
			resp = ResponseData.getSuccess(ResponseMsg.COMMON_SUCCESS);
		} catch (Exception e) {
			log.error("CustomEntityService.generate方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}

	/**
	 * 自动添加实体对应模块的资源信息
	 * 包含菜单和按钮
	 * @param ids
	 * @return
	 */
	public ResponseData<Boolean> saveModuleResources(String[] ids, String parentResourceId){
		ResponseData<Boolean> resp;
		try {
			List<CustomEntity> customEntities = entityRepo.findByIdIn(ids);
			Resource parentResource = new Resource();
			parentResource.setId(parentResourceId);
			for (CustomEntity entity : customEntities){
				Resource menu = new Resource();
				menu.setParent(parentResource);
				menu.setAllowGrant(1);
				menu.setEnable(1);
				menu.setIntegrateType("iframe");
				menu.setName(entity.getEntityDescr() + "管理");
				menu.setPermissionCode(StringUtils.lowerFirst(entity.getEntityName()) + ":list");
				menu.setType("菜单");
				menu.setUrl("/" + StringUtils.lowerFirst(entity.getEntityName() + "/toList"));
				resourceRepo.save(menu);

				String[] operations = null;
				if (StringUtils.isNotEmpty(entity.getOperations())){
					List<Resource> btnResources = new ArrayList<>(ids.length);
					operations = entity.getOperations().split(",");
					for (String operation : operations){
						Resource btnResource = new Resource();
						btnResource.setType("按钮");
						btnResource.setEnable(1);
						btnResource.setAllowGrant(1);
						btnResource.setParent(menu);
						switch(operation){
							case "添加":
								btnResource.setPermissionCode(StringUtils.lowerFirst(entity.getEntityName() + ":save"));
								btnResource.setOnclick("add" + StringUtils.upperFirst(entity.getEntityName() + "();"));
								btnResource.setOrderNo(1);
								btnResource.setStyle("info");
								btnResource.setFontIcon("fa fa-plus");
								btnResource.setName("添加");
								break;
							case "修改":
								btnResource.setPermissionCode(StringUtils.lowerFirst(entity.getEntityName() + ":update"));
								btnResource.setOnclick("edit" + StringUtils.upperFirst(entity.getEntityName()) + "();");
								btnResource.setOrderNo(2);
								btnResource.setStyle("info");
								btnResource.setFontIcon("fa fa-pencil");
								btnResource.setName("修改");
								break;
							case "删除":
								btnResource.setPermissionCode(StringUtils.lowerFirst(entity.getEntityName() + ":delete"));
								btnResource.setOnclick("dataRemove" + StringUtils.upperFirst(entity.getEntityName()) + "();");
								btnResource.setOrderNo(3);
								btnResource.setStyle("error");
								btnResource.setFontIcon("fa fa-remove");
								btnResource.setName("删除");
								break;
							case "启用":
								btnResource.setPermissionCode(StringUtils.lowerFirst(entity.getEntityName() + ":enable"));
								btnResource.setOnclick("setEnable" + StringUtils.upperFirst(entity.getEntityName()) + "();");
								btnResource.setOrderNo(4);
								btnResource.setStyle("info");
								btnResource.setFontIcon("fa fa-check");
								btnResource.setName("启用");
								break;
							case "禁用":
								btnResource.setPermissionCode(StringUtils.lowerFirst(entity.getEntityName() + ":disable"));
								btnResource.setOnclick("setDisable" + StringUtils.upperFirst(entity.getEntityName()) + "();");
								btnResource.setOrderNo(5);
								btnResource.setStyle("warning");
								btnResource.setFontIcon("fa fa-ban");
								btnResource.setName("禁用");
								break;
							case "导入":
								btnResource.setPermissionCode(StringUtils.lowerFirst(entity.getEntityName() + ":import"));
								btnResource.setOnclick("import" + StringUtils.upperFirst(entity.getEntityName()) + "();");
								btnResource.setOrderNo(6);
								btnResource.setStyle("info");
								btnResource.setFontIcon("fa fa-arrow-circle-o-up");
								btnResource.setName("导入");
								break;
							case "导出":
								btnResource.setPermissionCode(StringUtils.lowerFirst(entity.getEntityName() + ":export"));
								btnResource.setOnclick("export" + StringUtils.upperFirst(entity.getEntityName()) + "();");
								btnResource.setOrderNo(7);
								btnResource.setStyle("info");
								btnResource.setFontIcon("fa fa-arrow-circle-o-down");
								btnResource.setName("导出");
								break;
						}
						btnResources.add(btnResource);
					}
					resourceRepo.saveAll(btnResources);
				}
			}
			resp = ResponseData.getSuccess(ResponseMsg.COMMON_SUCCESS);
		} catch (Exception e){
			resp = ResponseData.getError(e);
			log.error(e.getMessage(), e);
		}
		return resp;
	}

	public CustomEntity findById(String id) {
		CustomEntity entity = entityRepo.findById(id).get();
		List<CustomEntityProperty> props = propRepo.findByEntity_id(id);
		entity.setProperties(props);
		return entity;
	}
	
	public List<CustomEntity> findAll(){
		Sort sort = Sort.by(Direction.ASC, "entityName");
		List<CustomEntity> entities = entityRepo.findAll(sort);
		return entities;
	}
	
	public Page<CustomEntity> findCustomEntities(String entityName, String author, int page, int rows){
		Specification<CustomEntity> spec = (Specification<CustomEntity>) (root, query, cb) -> {

			List<Predicate> predicates = new LinkedList<>();
			if (!StringUtils.isEmpty(entityName)) {
				Predicate predicate = cb.like(root.get("entityName").as(String.class), "%" + entityName + "%");
				predicates.add(predicate);
			}
			if (!StringUtils.isEmpty(author)) {
				Predicate predicate = cb.equal(root.get("author").as(String.class), author);
				predicates.add(predicate);
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
		Sort sort = Sort.by(Direction.DESC, "createDate");
		Pageable pageable = PageRequest.of(page - 1, rows, sort);
		Page<CustomEntity> entities = null;
		try {
			entities = entityRepo.findAll(spec, pageable);
		} catch (Exception e) {
			log.error("CustomEntityService.findCustomEntities方法异常。", e);
		}
		return entities;
	}

	public ResponseData<String> generateDocument(String projectName, String version, String[] entityIds){
		/*ResponseData<String> resp;
		try {
			org.springframework.core.io.Resource resource = new ClassPathResource("static/generatorDocumentTemplates/用户使用手册模板.docx");
			List<CustomEntity> customEntities = entityRepo.findByIdIn(entityIds);
			String docPath = docGenerator.generateUseDoc(projectName, version, customEntities, resource.getInputStream());
			resp = ResponseData.getSuccess(ResponseMsg.COMMON_SUCCESS, docPath);
		} catch (Exception e){
			e.printStackTrace();
			resp = ResponseData.getError(e);
		}
		return resp;*/
		return null;
	}

	@RequestMapping("/zipCodeFiles")
	public String zipCodeFiles() {
		String zipFilePath = uploadFolder + "/zip/generate.zip";
		File zipFile = new File(zipFilePath);
		if (zipFile.exists()) {
			zipFile.delete();
		}
		File zipDir = new File(uploadFolder + "/zip");
		if (!zipDir.exists()) {
			zipDir.mkdirs();
		}
		
		try {
			ZipUtils.zip(uploadFolder + "/generator", zipFilePath);
		} catch (Exception e) {
			log.error("CustomEntityService.save方法异常。", e);
		}
		return "/zip/generate.zip";
	}
}
