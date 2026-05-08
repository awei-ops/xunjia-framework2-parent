package com.xunjia.framework.generator.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.usermanage.entity.DicContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.dictionary.service.DicContentService;
import com.xunjia.framework.generator.entity.CustomEntity;
import com.xunjia.framework.generator.entity.CustomEntityProperty;
import com.xunjia.framework.generator.service.CustomEntityService;
import com.xunjia.framework.utils.FileUtils;
import com.xunjia.framework.utils.ListUtils;

@RestController
@RequestMapping("/customEntity")
public class CustomEntityController {

	@Autowired
	private CustomEntityService service;
	
	@Autowired
	private DicContentService dcService;
	
	@Value("${com.xunjia.framework.baseUploadFolder}")
	private String uploadFolder;
	
	@RequestMapping("/toList")
	public ModelAndView toList() {
		return new ModelAndView("framework/generator/list");
	}
	
	@RequestMapping("/toAdd")
	public ModelAndView toAdd() {
		return new ModelAndView("framework/generator/add");
	}
	
	@RequestMapping("/toEdit")
	public ModelAndView toEdit(String id) {
		ModelAndView mav = new ModelAndView("framework/generator/edit");
		CustomEntity entity = service.findById(id);
		if (entity != null && entity.getProperties() != null) {
			for (CustomEntityProperty cep : entity.getProperties()) {
				cep.setEntity(null);
			}
		}
		mav.addObject("entity", entity);
		return mav;
	}

	@RequestMapping("/toSelectParentResource")
	public ModelAndView toSelectParentResource(){
		return new ModelAndView("framework/generator/selectParentResource");
	}

	@RequestMapping("/toWriteProject")
	public ModelAndView toWriteProject(){
		return new ModelAndView(("framework/generator/writeProject"));
	}

	@RequestMapping("/save")
	public ResponseData<Boolean> save(CustomEntity entity){
		return service.save(entity);
	}
	
	@RequestMapping("/update")
	public ResponseData<Boolean> update(CustomEntity entity){
		return service.update(entity);
	}
	
	@RequestMapping("/delete")
	public ResponseData<Boolean> delete(@RequestParam(name="ids[]") String[] ids){
		return service.delete(ids);
	}
	
	@RequestMapping("/generate")
	public ResponseData<Boolean> generate(@RequestParam(name="ids[]")String[] ids){
		ResponseData<Boolean> resp = service.generate(ids);
		return resp;
	}

	@RequestMapping("/saveModuleResources")
	public ResponseData<Boolean> saveModuleResources(@RequestParam(name="ids[]") String[] ids, String parentResourceId){
		return service.saveModuleResources(ids, parentResourceId);
	}

	@RequestMapping("/downloadZipFile")
	public void downloadZipFile(HttpServletResponse response) {
		String zipfilePath = service.zipCodeFiles();
		FileUtils.downloadFile(response, uploadFolder + zipfilePath, "generate.zip");
	}

	@RequestMapping("/generateDocuments")
	public void generateDocuments(String projectName, String version, String entityIds, HttpServletResponse response){
		ResponseData<String> resp = service.generateDocument(projectName, version, entityIds.split(","));
		FileUtils.downloadFile(response, resp.getData(), "用户使用手册.doc");
	}

	@RequestMapping("/findById")
	public CustomEntity findById(String id) {
		return service.findById(id);
	}
	
	@RequestMapping("/findDataTypes")
	public List<Map<String, String>> findDataTypes(){
		List<Map<String, String>> dataTypes = new LinkedList<>();
		List<DicContent> contents = dcService.findByTypeCode("BASIC_DATA_TYPE");
		List<CustomEntity> entities = service.findAll();
		if (!ListUtils.isListEmpty(contents)) {
			for (DicContent dc : contents) {
				Map<String, String> dataType = new HashMap<>();
				dataType.put("name", dc.getName());
				dataType.put("value", dc.getName());
				dataTypes.add(dataType);
			}
		}

		if (!ListUtils.isListEmpty(entities)) {
			for (CustomEntity ce : entities) {
				Map<String, String> dataType = new HashMap<>();
				dataType.put("name", ce.getEntityName());
				dataType.put("value", ce.getPackageName() + ".entity." + ce.getEntityName());
				dataTypes.add(dataType);

			}

			//用户和组织类型
			Map<String, String> userDataType = new HashMap<>();
			userDataType.put("name", "User");
			userDataType.put("value", "com.xunjia.framework.usermanage.entity.User");
			dataTypes.add(userDataType);

			Map<String, String> orgDataType = new HashMap<>();
			orgDataType.put("name", "Organization");
			orgDataType.put("value", "com.xunjia.framework.usermanage.entity.Organization");
			dataTypes.add(orgDataType);

			Map<String, String> currType = new HashMap<>();
			currType.put("name", "当前类型");
			currType.put("value", "当前类型");
			dataTypes.add(currType);

			for (CustomEntity ce : entities) {
				Map<String, String> dataType = new HashMap<>();
				dataType.put("name", "List〈"+ ce.getEntityName() + "〉");
				dataType.put("value", "List<"+ ce.getPackageName() + ".entity." + ce.getEntityName() + ">");
				dataTypes.add(dataType);
			}

			Map<String, String> currListType = new HashMap<>();
			currListType.put("name", "List〈当前类型〉");
			currListType.put("value", "List<当前类型>");
			dataTypes.add(currListType);
		}

		return dataTypes;
	}
	
	@RequestMapping("/findCustomEntities")
	public PageVO<CustomEntity> findCustomEntities(String entityName, String author, int page, int rows){
		Page<CustomEntity> pageData = service.findCustomEntities(entityName, author, page, rows);
		if (pageData != null && pageData.getContent() != null) {
			for (CustomEntity ce : pageData.getContent()) {
				ce.setProperties(null);
			}
		}
		return new PageVO<CustomEntity>(pageData);
	}
}
