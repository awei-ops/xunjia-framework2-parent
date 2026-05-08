package com.xunjia.framework.dictionary.service;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.dictionary.repository.IDicContentRepository;
import com.xunjia.framework.dictionary.repository.IDicTypeRepository;
import com.xunjia.framework.usermanage.entity.DicContent;
import com.xunjia.framework.usermanage.entity.DicType;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 字典内容服务
 * 2020年5月8日
 * @author 姜浩
 */
@Service
@Transactional
public class DicContentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DicContentService.class);
	
	@Autowired
	private IDicTypeRepository typeRepo;
	
	@Autowired
	private IDicContentRepository repo;
	
	/**
	 * 保存字典内容
	 * @param dc
	 * @return
	 */
	public ResponseData<Boolean> save(DicContent dc){
		ResponseData<Boolean> resp = null;
		try {
			DicContent existContent = repo.findByCode(dc.getCode());
			if (existContent == null) {
				if (dc.getParentContent() != null) {
					dc.setParentContent(this.getParentContent(dc.getParentContent().getCode()));
				}
				dc.setPyCode(StringLetterUtils.getFirstLetter(dc.getName()));
				repo.save(dc);
				resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
			} else {
				resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_EXIST);
			}
		} catch (Exception e) {
			LOGGER.error("DicContentService.save方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 更新字典内容
	 * @param dc
	 * @return
	 */
	public ResponseData<Boolean> update(DicContent dc){
		ResponseData<Boolean> resp = null;
		try {
			DicContent existType = repo.findByCode(dc.getCode());
			if (existType == null) {
				if (dc.getParentContent() != null) {
					dc.setParentContent(this.getParentContent(dc.getParentContent().getCode()));
				}
				dc.setPyCode(StringLetterUtils.getFirstLetter(dc.getName()));
				repo.save(dc);
				resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
			} else if (existType != null && existType.getId().equals(dc.getId())) {
				if (dc.getParentContent() != null) {
					existType.setParentContent(this.getParentContent(dc.getParentContent().getCode()));
				}
				existType.setName(dc.getName());
				existType.setCode(dc.getCode());
				existType.setOrderNo(dc.getOrderNo());
				existType.setPyCode(StringLetterUtils.getFirstLetter(dc.getName()));
				repo.save(existType);
				resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
			} else {
				resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_EXIST);
			}
		} catch (Exception e) {
			LOGGER.error("DicContentService.update方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 根据给定id批量删除字典内容
	 * @param ids
	 * @return
	 */
	public ResponseData<Boolean> delete(String[] ids){
		ResponseData<Boolean> resp = null;
		try {
			repo.deleteByIds(ids);
			resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("DicContentService.delete方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public ResponseData<Boolean> importContents(MultipartFile file){
		ResponseData<Boolean> resp = null;
//		Workbook workbook = new Workbook();
//		try (InputStream is = file.getInputStream()) {
//			workbook.loadFromStream(is, ExcelVersion.Version2013);
//			Worksheet sheet = workbook.getWorksheets().get(0);
//			int lastRowIndex = sheet.getLastRow();
//			StringBuffer repeatedCodeSb = new StringBuffer();
//			if (lastRowIndex > 0) {
//				//分批次导入
//				int pageSize = 100;
//				int rowCount = lastRowIndex;
//				int pageCount = rowCount % pageSize == 0 ? rowCount / pageSize : rowCount / pageSize + 1;
//
//				for (int page = 0; page < pageCount; page++) {
//					int startPos = page * pageSize + 1;
//					int endPos = startPos + pageSize;
//					if (endPos > rowCount + 1) {
//						endPos = rowCount + 1;
//					}
//
//					if (page == 0) {
//						startPos += 1;
//					}
//
//					//取得当前批次字典内容的编码，查询数据库中是否存在重复
//					//查询所属字典分类是否存在
//					List<String> contentCodes = new ArrayList<String>(endPos - startPos);
//					Set<String> typeCodes = new HashSet<String>();
//					for (int i = startPos; i < endPos; i++) {
//						String contentCode = sheet.get(i, 2).getValue().trim();
//						String typeCode = sheet.get(i, 4).getValue().trim();
//						contentCodes.add(contentCode);
//						typeCodes.add(typeCode);
//					}
//					List<DicContent> sameCodeContents = repo.findByCodeIn(contentCodes.toArray(new String[0]));
//					List<DicType> parentTypes = typeRepo.findByCodeIn(typeCodes.toArray(new String[0]));
//
//					List<DicContent> dicContents = new ArrayList<DicContent>(endPos - startPos);
//					for (int i = startPos; i < endPos; i++) {
//						String contentName = sheet.get(i, 1).getValue().trim();
//						String contentCode = sheet.get(i, 2).getValue().trim();
//						String typeCode = sheet.get(i, 4).getValue().trim();
//						String orderNoStr = sheet.get(i, 5).getValue().trim();
//
//						//如果未填写分类名称和编码，则抛弃这一行数据
//						if (StringUtils.isEmpty(contentCode) || StringUtils.isEmpty(contentName)) {
//							continue;
//						}
//
//						//如果这一行的编码在数据库中已存在，则抛弃这一行数据
//						if (!ListUtils.isListEmpty(sameCodeContents)) {
//							Optional<DicContent> sameCodeTypeOptional = sameCodeContents.stream()
//									.filter(c -> c.getCode().equals(contentCode)).findFirst();
//							if (sameCodeTypeOptional.isPresent()) {
//								repeatedCodeSb.append(contentCode).append(",");
//								continue;
//							}
//						}
//
//						//如果所属分类不存在，则抛弃这一行数据
//						DicType parentType = null;
//						if (!ListUtils.isListEmpty(parentTypes)) {
//							Optional<DicType> parentTypeOptional = parentTypes.stream()
//									.filter(c -> c.getCode().equals(typeCode)).findFirst();
//							if (parentTypeOptional.isPresent()) {
//								parentType = parentTypeOptional.get();
//							} else {
//								continue;
//							}
//						}
//
//						//排序号转换与合法性校验
//						int orderNo = 100;
//						try {
//							orderNo = Integer.parseInt(orderNoStr);
//						} catch (NumberFormatException nfe) {
//							orderNo = 100;
//						}
//
//						DicContent dicContent = new DicContent();
//						dicContent.setName(contentName);
//						dicContent.setCode(contentCode);
//						dicContent.setOrderNo(orderNo);
//						dicContent.setPyCode(StringLetterUtils.getFirstLetter(contentName));
//						dicContent.setType(parentType);
//						dicContents.add(dicContent);
//					}
//					if (dicContents.size() > 0) {
//						repo.saveAll(dicContents);
//					}
//				}
//
//				//第二次分页迭代，目的是找到上级字典内容
//				for (int page = 0; page < pageCount; page++) {
//					int startPos = page * pageSize + 1;
//					int endPos = startPos + pageSize;
//					if (endPos > rowCount + 1) {
//						endPos = rowCount + 1;
//					}
//
//					if (page == 0) {
//						startPos += 1;
//					}
//
//					//找到设置了上级字典内容的行，读取这些信息
//					Set<String> parentContentCodes = new HashSet<String>();
//					Map<String, String> updateContentCodeMapper = new HashMap<String, String>();
//					for (int i = startPos; i < endPos; i++) {
//						String contentCode = sheet.get(i, 2).getValue().trim();
//						String parentContentCode = sheet.get(i, 3).getValue().trim();
//						if (!StringUtils.isEmpty(parentContentCode)) {
//							parentContentCodes.add(parentContentCode);
//							updateContentCodeMapper.put(contentCode, parentContentCode);
//						}
//					}
//					List<DicContent> parentDicContents = repo.findByCodeIn(parentContentCodes.toArray(new String[0]));
//					List<DicContent> updateDicContents = repo.findByCodeIn(updateContentCodeMapper.keySet().toArray(new String[0]));
//					if (updateDicContents.size() > 0) {
//						for (DicContent dc : updateDicContents) {
//							Optional<DicContent> parentContentOptional = parentDicContents.stream()
//									.filter(c -> c.getCode().equals(updateContentCodeMapper.get(dc.getCode())))
//									.findFirst();
//							if (parentContentOptional.isPresent()) {
//								dc.setParentContent(parentContentOptional.get());
//							}
//						}
//						repo.saveAll(updateDicContents);
//					}
//				}
//			}
//
//			resp = ResponseData.getSuccess(ResponseMsg.IMPORT_SUCCESS);
//			if (repeatedCodeSb.length() > 0) {
//				String repeatedCodeStr = repeatedCodeSb.substring(0, repeatedCodeSb.length() - 1);
//				resp.setMsg(resp.getMsg() + "存在重复的字典内容编码：" + repeatedCodeStr);
//			}
//		} catch (IOException e) {
//			LOGGER.error("DicContentService.importContents方法异常。", e);
//			resp = ResponseData.getError(e);
//		}
		return resp;
	}
	
	/**
	 * 根据给定id查询字典内容
	 * @param id
	 * @return
	 */
	public DicContent findById(String id) {
		return repo.findById(id).get();
	}
	
	/**
	 * 根据拼音码查询字典内容
	 * @param pyCode
	 * @return
	 */
	public List<DicContent> findByPyCode(String pyCode){
		List<DicContent> list = null;
		try {
			list = repo.findByPyCodeLikeOrderByOrderNoAsc(pyCode);
		} catch (Exception e){
			LOGGER.error("DicContentService.findByPyCode方法异常。", e);
		}
		return list;
	}
	
	public List<DicContent> findByTypeCode(String typeCode){
		return repo.findByType_codeOrderByOrderNoAsc(typeCode);
	}
	
	public List<DicContent> findByParentContentCode(String parentContentCode){
		return repo.findByParentContent_codeOrderByOrderNoAsc(parentContentCode);
	}
	
	/**
	 * 查询字典内容分页数据
	 * @param name
	 * @param code
	 * @param typeId
	 * @param page
	 * @param rows
	 * @return
	 */
	public Page<DicContent> findDicContents(String name, String code, String typeId, int page, int rows){
		Specification<DicContent> spec = new Specification<DicContent>() {
			public Predicate toPredicate(Root<DicContent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				
				List<Predicate> predicates = new LinkedList<Predicate>();
				if (!StringUtils.isEmpty(name)) {
					Predicate predicate = cb.like(root.get("name").as(String.class), "%" + name + "%");
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(code)) {
					Predicate predicate = cb.equal(root.get("code").as(String.class), code);
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(typeId) && !typeId.equals("0")) {
					Predicate predicate = cb.equal(root.get("type").get("id").as(String.class), typeId);
					predicates.add(predicate);
				}
				
				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		Sort sort = Sort.by(Direction.ASC, "orderNo");
		Pageable pageable = PageRequest.of(page - 1, rows, sort);
		Page<DicContent> pageData = null;
		try {
			pageData = repo.findAll(spec, pageable);
		} catch (Exception e) {
			LOGGER.error("DicContentService.findDicContents方法异常。", e);
		}
		return pageData;
	}
	
	/**
	 * 根据给定的内容编码，查询该字典内容的上级信息
	 * @param code
	 * @return
	 * @throws Exception
	 */
	private DicContent getParentContent(String code) throws Exception {
		if (StringUtils.isEmpty(code)) {
			return null;
		}
		DicContent parentContent = repo.findByCode(code);
		if (parentContent == null) {
			throw new Exception("未找到上级字典内容。");
		}
		return parentContent;
	}
}
