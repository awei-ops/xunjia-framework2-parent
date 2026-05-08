package com.xunjia.framework.dictionary.service;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.dictionary.repository.IDicContentRepository;
import com.xunjia.framework.dictionary.repository.IDicTypeRepository;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * 字典分类服务
 * 2020年5月8日
 * @author 姜浩
 */
@Service
@Transactional
public class DicTypeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DicTypeService.class);
	
	@Autowired
	private IDicTypeRepository repo;
	
	@Autowired
	private IDicContentRepository contentRepo; 
	
	/**
	 * 保存字典分类
	 * @param type
	 * @return
	 */
	public ResponseData<Boolean> save(DicType type){
		ResponseData<Boolean> resp = null;
		try {
			DicType existType = repo.findByCode(type.getCode());
			if (existType == null) {
				type.setPyCode(StringLetterUtils.getFirstLetter(type.getName()));
				repo.save(type);
				resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
			} else {
				resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_EXIST);
			}
		} catch (Exception e) {
			LOGGER.error("DicTypeService.save方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 更新字典分类
	 * @param type
	 * @return
	 */
	public ResponseData<Boolean> update(DicType type){
		ResponseData<Boolean> resp = null;
		try {
			DicType existType = repo.findByCode(type.getCode());
			if (existType == null) {
				type.setPyCode(StringLetterUtils.getFirstLetter(type.getName()));
				repo.save(type);
				resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
			} else if (existType != null && existType.getId().equals(type.getId())) {
				existType.setName(type.getName());
				existType.setCode(type.getCode());
				existType.setOrderNo(type.getOrderNo());
				existType.setPyCode(StringLetterUtils.getFirstLetter(type.getName()));
				repo.save(existType);
				resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
			} else {
				resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_EXIST);
			}
		} catch (Exception e) {
			LOGGER.error("DicTypeService.update方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 根据给定id批量删除字典分类信息
	 * @param ids
	 * @return
	 */
	public ResponseData<Boolean> deleteByIds(String[] ids) {
		ResponseData<Boolean> resp = null;
		try {
			contentRepo.deleteByTypes(ids);
			repo.deleteByIds(ids);
			resp = ResponseData.getSuccess(ResponseMsg.DELETE_FAIL);
		} catch (Exception e) {
			LOGGER.error("DicTypeService.deleteByIds方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public ResponseData<Boolean> importTypes(MultipartFile file){
		ResponseData<Boolean> resp =null;
//		Workbook workbook = new Workbook();
//		try (InputStream is = file.getInputStream()) {
//			workbook.loadFromStream(is, ExcelVersion.Version2013);
//			Worksheet sheet = workbook.getWorksheets().get(0);
//			int lastRowIndex = sheet.getLastRow();
//			StringBuffer repeatedCodeSb = new StringBuffer();
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
//					if (page == 0) {
//						startPos += 1;
//					}
//
//					//取得当前批次分类信息的编码，查询数据库中是否存在重复
//					List<String> typeCodes = new ArrayList<String>(endPos - startPos);
//					for (int i = startPos; i < endPos; i++) {
//						String typeCode = sheet.get(i, 2).getValue().trim();
//						typeCodes.add(typeCode);
//					}
//					List<DicType> sameCodeTypes = repo.findByCodeIn(typeCodes.toArray(new String[0]));
//
//					List<DicType> dicTypes = new ArrayList<DicType>(endPos - startPos);
//					for (int i = startPos; i < endPos; i++) {
//						String typeCode = sheet.get(i, 2).getValue().trim();
//						String typeName = sheet.get(i, 1).getValue().trim();
//						String orderNoStr = sheet.get(i, 3).getValue().trim();
//
//						//如果未填写分类名称和编码，则抛弃这一行数据
//						if (StringUtils.isEmpty(typeCode) || StringUtils.isEmpty(typeName)) {
//							continue;
//						}
//
//						//如果这一行的编码在数据库中已存在，则抛弃这一行数据
//						if (!ListUtils.isListEmpty(sameCodeTypes)) {
//							Optional<DicType> sameCodeTypeOptional = sameCodeTypes.stream()
//									.filter(c -> c.getCode().equals(typeCode)).findFirst();
//							if (sameCodeTypeOptional.isPresent()) {
//								repeatedCodeSb.append(typeCode).append(",");
//								continue;
//							}
//						}
//
//						//排序号转换与合法性校验
//						int orderNo = 100;
//						try {
//							orderNo = Integer.parseInt(orderNoStr);
//						} catch (NumberFormatException ignored) { }
//
//						DicType type = new DicType();
//						type.setName(typeName);
//						type.setCode(typeCode);
//						type.setPyCode(StringLetterUtils.getFirstLetter(typeName));
//						type.setOrderNo(orderNo);
//						dicTypes.add(type);
//					}
//					if (dicTypes.size() > 0) {
//						repo.saveAll(dicTypes);
//					}
//				}
//			}
//
//			resp = ResponseData.getSuccess(ResponseMsg.IMPORT_SUCCESS);
//			if (repeatedCodeSb.length() > 0) {
//				String repeatedCodeStr = repeatedCodeSb.substring(0, repeatedCodeSb.length() - 1);
//				resp.setMsg(resp.getMsg() + "存在重复的分类编码：" + repeatedCodeStr);
//			}
//		} catch (IOException e) {
//			LOGGER.error("DicTypeService.importTypes方法异常。", e);
//			resp = ResponseData.getError(e);
//		}
		return resp;
	}
	
	/**
	 * 根据给定id查询字典分类
	 * @param id
	 * @return
	 */
	public DicType findById(String id) {
		return repo.findById(id).get();
	}
	
	/**
	 * 根据拼音码查询字典分类
	 * @param pyCode
	 * @return
	 */
	public List<DicType> findByPyCode(String pyCode){
		List<DicType> list = null;
		try {
			list = repo.findByPyCodeLikeOrderByOrderNoAsc(pyCode);
		} catch (Exception e){
			LOGGER.error("DicTypeService.findByPyCode方法异常。", e);
		}
		return list;
	}
	
	/**
	 * 查询字典分类分页数据
	 * @param name
	 * @param code
	 * @param page
	 * @param rows
	 * @return
	 */
	public Page<DicType> findDicTypes(String name, String code, int page, int rows){
		Specification<DicType> spec = new Specification<DicType>() {
			public Predicate toPredicate(Root<DicType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				
				List<Predicate> predicates = new LinkedList<Predicate>();
				if (!StringUtils.isEmpty(name)) {
					Predicate predicate = cb.like(root.get("name").as(String.class), "%" + name + "%");
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(code)) {
					Predicate predicate = cb.equal(root.get("code").as(String.class), code);
					predicates.add(predicate);
				}
				
				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		Sort sort = Sort.by(Direction.ASC, "orderNo");
		Pageable pageable = PageRequest.of(page - 1, rows, sort);
		Page<DicType> pageData = null;
		try {
			pageData = repo.findAll(spec, pageable);
		} catch (Exception e) {
			LOGGER.error("DicTypeService.findDicTypes方法异常。", e);
		}
		return pageData;
	}
}
