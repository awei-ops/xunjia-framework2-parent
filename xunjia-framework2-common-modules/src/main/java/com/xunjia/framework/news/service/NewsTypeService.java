package com.xunjia.framework.news.service;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.news.entity.NewsType;
import com.xunjia.framework.news.repository.INewsRepository;
import com.xunjia.framework.news.repository.INewsTypeRepository;
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

import com.xunjia.framework.utils.StringUtils;

@Service
@Transactional
@Slf4j
public class NewsTypeService {
	
	@Autowired
	private INewsTypeRepository repo;
	
	@Autowired
	private INewsRepository newsRepo;

	public ResponseData<Boolean> save(NewsType type){
		ResponseData<Boolean> resp = null;
		try {
			NewsType existType = repo.findByName(type.getName());
			if (existType == null) {
				repo.save(type);
				resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
			} else {
				resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_NAME_EXIST);
			}
		} catch (Exception e) {
			resp = ResponseData.getError(e);
			log.error(e.getMessage());
		}
		return resp;
	}
	
	public ResponseData<Boolean> update(NewsType type){
		ResponseData<Boolean> resp = null;
		try {
			NewsType existType = repo.findByName(type.getName());
			if (existType == null) {
				repo.save(type);
				resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
			} else if (existType.getId().equals(type.getId())) {
				repo.save(type);
				resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
			} else {
				resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_NAME_EXIST);
			}
		} catch (Exception e) {
			resp = ResponseData.getError(e);
			log.error(e.getMessage());
		}
		return resp;
	}
	
	public ResponseData<Boolean> delete(String[] ids){
		ResponseData<Boolean> resp = null;
		try {
			boolean newsExists = false;
			for (String id : ids) {
				int newsCount = newsRepo.countByType_id(id);
				if (newsCount > 0) {
					newsExists = true;
					break;
				}
			}
			
			if (newsExists) {
				resp = ResponseData.getFail(ResponseMsg.DELETE_FAIL_SUB_EXIST);
			} else {
				repo.deleteByIds(ids);
				resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
			}
		} catch (Exception e) {
			resp = ResponseData.getError(e);
			log.error(e.getMessage());
		}
		return resp;
	}
	
	public NewsType findById(String id) {
		return repo.findById(id).get();
	}
	
	public NewsType findByName(String name) {
		return repo.findByName(name);
	}
	
	public List<NewsType> findByParent(String parentId){
		if (StringUtils.isEmpty(parentId)) {
			return repo.findByParentIsNull();
		} else {
			return repo.findByParent_id(parentId);
		}
	}
	
	public Page<NewsType> findNewsTypes(String name, String parentId, int page, int rows) {
		Specification<NewsType> spec = new Specification<NewsType>() {
			public Predicate toPredicate(Root<NewsType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicates = new LinkedList<Predicate>();
				if (!StringUtils.isEmpty(name)) {
					Predicate predicate = cb.like(root.get("name").as(String.class), "%" + name + "%");
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(parentId)) {
					Predicate predicate = cb.equal(root.get("parentId").as(String.class), parentId);
					predicates.add(predicate);
				}

				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		Sort sort = Sort.by(Direction.ASC, "orderNo");
		Pageable pageable = PageRequest.of(page - 1, rows, sort);
		Page<NewsType> pageData = null;
		try {
			pageData = repo.findAll(spec, pageable);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return pageData;
	}
}
