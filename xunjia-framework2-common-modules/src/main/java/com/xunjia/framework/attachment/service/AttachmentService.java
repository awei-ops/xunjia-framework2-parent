package com.xunjia.framework.attachment.service;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import com.xunjia.framework.attachment.entity.Attachment;
import com.xunjia.framework.attachment.repository.IAttachmentRepository;
import com.xunjia.framework.common.Context;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.FileUtils;

@Service
@Transactional
public class AttachmentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentService.class);
	
	@Autowired
	private IAttachmentRepository repo;
	
	@Value("${com.xunjia.framework.baseUploadFolder}")
	private String uploadFolder;
	
	public ResponseData<Boolean> save(Attachment attachment, MultipartFile attachmentFile){
		String originalFileName = attachmentFile.getOriginalFilename();
		String extendName = "";
		if (originalFileName.lastIndexOf('.') != -1) {
			extendName = originalFileName.substring(originalFileName.lastIndexOf('.'));
		}
		String contentType = attachmentFile.getContentType();
		String savePath = "/attachment";
		String fileName = FileUtils.copyFile(attachmentFile, uploadFolder + savePath);
		
		attachment.setContentType(contentType);
		attachment.setExtendName(extendName);
		attachment.setOriginalFileName(originalFileName);
		attachment.setUploadUser(Context.getCurrentUser());
		attachment.setUploadTime(new Date());
		attachment.setUploadOrg(Context.getCurrentUser().getOrg());
		attachment.setSavePath(savePath + "/" + fileName);
		
		ResponseData<Boolean> resp = null;
		try {
			repo.save(attachment);
			resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("AttachmentService.save方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public ResponseData<Boolean> update(Attachment attachment){
		ResponseData<Boolean> resp = null;
		try {
			Attachment existAttachment = repo.findById(attachment.getCode()).get();
			existAttachment.setTitle(attachment.getTitle());
			existAttachment.setDescription(attachment.getDescription());
			repo.save(existAttachment);
			resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("AttachmentService.update方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public ResponseData<Boolean> deleteByCodes(String[] codes){
		ResponseData<Boolean> resp = null;
		try {
			List<Attachment> attachments = repo.findByCodeIn(codes);
			if (attachments != null && attachments.size() > 0) {
				for (Attachment atta : attachments) {
					FileUtils.deleteFile(uploadFolder + atta.getSavePath());
				}
				repo.deleteByCodes(codes);
			}
			resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("AttachmentService.deleteByCodes方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public Attachment findByCode(String code) {
		return repo.findById(code).get();
	}


	public List<Attachment> findByBusinessAndBusinessId(String business, String businessId) {
		List<Attachment> alist=repo.findByBusinessAndBusinessId(business,businessId);
		return alist;
	}
	
	public Attachment findAndUpdateDownloadCount(String code) {
		Attachment attachment = repo.findById(code).get();
		repo.updateDownloadCountAuto(code);
		return attachment;
	}
	
	public List<Attachment> findByBusiness(String business, String businessId){
		return repo.findByBusinessAndBusinessId(business, businessId);
	}
	
	public List<Attachment> findByBusiness(String business, String businessSubType, String businessId){
		return repo.findByBusinessAndBusinessIdAndBusinessSubType(business, businessId, businessSubType);
	}
	
	public Page<Attachment> findBusinessAttachments(String business, String businessSubType, 
			String businessId, String startDate, String endDate, int page, int rows){
		Specification<Attachment> spec = new Specification<Attachment>() {
			public Predicate toPredicate(Root<Attachment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				
				List<Predicate> predicates = new LinkedList<Predicate>();
				
				if (!StringUtils.isEmpty(business)) {
					Predicate predicate = cb.equal(root.get("business").as(String.class), business);
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(businessSubType)) {
					Predicate predicate = cb.equal(root.get("businessSubType").as(String.class), businessSubType);
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(businessId)) {
					Predicate predicate = cb.equal(root.get("businessId").as(String.class), businessId);
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(startDate)) {
					try {
						Predicate predicate = cb.greaterThanOrEqualTo(
								root.get("uploadTime").as(Date.class),
								DateUtils.parse(startDate, "yyyy-MM-dd"));
						predicates.add(predicate);
					} catch (ParseException e) {
						LOGGER.error("AttachmentService.findBusinessAttachments方法异常。", e);
					}
				}
				if (!StringUtils.isEmpty(endDate)) {
					try {
						Predicate predicate = cb.lessThanOrEqualTo(
								root.get("uploadTime").as(Date.class), 
								DateUtils.parse(endDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
						predicates.add(predicate);
					} catch (ParseException e) {
						LOGGER.error("AttachmentService.findBusinessAttachments方法异常。", e);
					}
				}
				
				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		Sort sort = Sort.by(Direction.DESC, "uploadTime");
		Pageable pageable = PageRequest.of(page - 1, rows, sort);
		Page<Attachment> pageData = null;
		try {
			pageData = repo.findAll(spec, pageable);
		} catch (Exception e) {
			LOGGER.error("AttachmentService.findBusinessAttachments方法异常。", e);
		}
		return pageData;
	}
	
	public Page<Attachment> findAttachments(String title, String uploadUserId, String startDate, String endDate, int page, int rows){
		Specification<Attachment> spec = new Specification<Attachment>() {
			public Predicate toPredicate(Root<Attachment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				
				List<Predicate> predicates = new LinkedList<Predicate>();
				Predicate bizPredicate = cb.isNull(root.get("business").as(String.class));
				predicates.add(bizPredicate);
				
				if (!StringUtils.isEmpty(title)) {
					Predicate predicate = cb.like(root.get("title").as(String.class), "%" + title + "%");
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(uploadUserId)) {
					Predicate predicate = cb.equal(root.get("uploadUser").get("id").as(String.class), uploadUserId);
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(startDate)) {
					try {
						Predicate predicate = cb.greaterThanOrEqualTo(
								root.get("uploadTime").as(Date.class),
								DateUtils.parse(startDate, "yyyy-MM-dd"));
						predicates.add(predicate);
					} catch (ParseException e) {
						LOGGER.error("AttachmentService.findAttachments方法异常。", e);
					}
				}
				if (!StringUtils.isEmpty(endDate)) {
					try {
						Predicate predicate = cb.lessThanOrEqualTo(
								root.get("uploadTime").as(Date.class), 
								DateUtils.parse(endDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
						predicates.add(predicate);
					} catch (ParseException e) {
						LOGGER.error("AttachmentService.findAttachments方法异常。", e);
					}
				}
				
				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		Sort sort = Sort.by(Direction.DESC, "uploadTime");
		Pageable pageable = PageRequest.of(page - 1, rows, sort);
		Page<Attachment> pageData = null;
		try {
			pageData = repo.findAll(spec, pageable);
		} catch (Exception e) {
			LOGGER.error("AttachmentService.findAttachments方法异常。", e);
		}
		return pageData;
	}
}
