package com.xunjia.framework.news.service;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.news.entity.News;
import com.xunjia.framework.news.entity.NewsContent;
import com.xunjia.framework.news.repository.INewsContentRepository;
import com.xunjia.framework.news.repository.INewsRepository;
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

import com.xunjia.framework.common.Context;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.StringUtils;

@Service
@Transactional
@Slf4j
public class NewsService {

	@Autowired
	private INewsRepository repo;
	
	@Autowired
	private INewsContentRepository contentRepo;
	
	public ResponseData<Boolean> save(News news, String content){
		ResponseData<Boolean> resp = null;
		news.setAuthor(Context.getCurrentUser().getRealName());
		news.setPublishDate(new Date());
		try {
			repo.save(news);
			
			NewsContent newsContent = new NewsContent();
			newsContent.setContent(content);
			newsContent.setNews(news);
			contentRepo.save(newsContent);
			
			resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
		} catch (Exception e) {
			resp = ResponseData.getError(e);
			log.error(e.getMessage());
		}
		return resp;
	}
	
	public ResponseData<Boolean> update(News news, String content) {
		ResponseData<Boolean> resp = null;
		try {
			News existNews = repo.getOne(news.getId());
			existNews.setSubTitle(news.getSubTitle());
			existNews.setSubTitlePos(news.getSubTitlePos());
			existNews.setTitle(news.getTitle());
			existNews.setType(news.getType());
			repo.save(existNews);
			
			NewsContent newsContent = contentRepo.findByNews_id(news.getId());
			newsContent.setContent(content);
			contentRepo.save(newsContent);

			resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
		} catch (Exception e) {
			resp = ResponseData.getError(e);
			log.error(e.getMessage());
		}
		return resp;
	}
	
	public ResponseData<Boolean> delete(String[] ids) {
		ResponseData<Boolean> resp = null;
		try {
			contentRepo.deleteByNewsIds(ids);
			repo.deleteByIds(ids);
			resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
		} catch (Exception e) {
			resp = ResponseData.getError(e);
			log.error(e.getMessage());
		}
		return resp;
	}
	
	public ResponseData<Boolean> audit(int auditState, String[] ids){
		ResponseData<Boolean> resp = null;
		try {
			repo.updateAuditState(auditState, ids);
			resp = ResponseData.getSuccess(ResponseMsg.COMMON_SUCCESS);
		} catch (Exception e) {
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public NewsContent findContentById(String id) {
		return contentRepo.findByNews_id(id);
	}

	public NewsContent findContentByIdForRead(String id){
		NewsContent newsContent = contentRepo.findByNews_id(id);
		repo.updateReadCount(id);
		return newsContent;
	}

	public News findLatestNews(String typeName) {
		return repo.findFirstByAuditStateAndType_nameOrderByPublishDateDesc(1, typeName);
	}
	
	public void updateReadCount(String id) {
		repo.updateReadCount(id);
	}
	
	public Page<News> findNews(String title, String startDate, String endDate, 
			int auditState, String typeId, String author, int page, int rows){
		Specification<News> spec = new Specification<News>() {
			public Predicate toPredicate(Root<News> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicates = new LinkedList<Predicate>();
				if (!StringUtils.isEmpty(title)) {
					Predicate predicate = cb.like(root.get("title").as(String.class), "%" + title + "%");
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(typeId)) {
					Predicate predicate = cb.equal(root.get("type").get("id").as(String.class), typeId);
					predicates.add(predicate);
				}
				if (auditState != -1) {
					Predicate predicate = cb.equal(root.get("auditState").as(Integer.class), auditState);
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(startDate)) {
					Predicate predicate;
					try {
						predicate = cb.greaterThanOrEqualTo(root.get("publishDate").as(Date.class), 
								DateUtils.parse(startDate, "yyyy-MM-dd"));
						predicates.add(predicate);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				if (!StringUtils.isEmpty(endDate)) {
					Predicate predicate;
					try {
						predicate = cb.lessThanOrEqualTo(root.get("publishDate").as(Date.class), 
								DateUtils.parse(endDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
						predicates.add(predicate);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}

				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		Sort sort = Sort.by(Direction.DESC, "publishDate");
		Pageable pageable = PageRequest.of(page - 1, rows, sort);
		Page<News> pageData = null;
		try {
			pageData = repo.findAll(spec, pageable);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return pageData;
	}
}
