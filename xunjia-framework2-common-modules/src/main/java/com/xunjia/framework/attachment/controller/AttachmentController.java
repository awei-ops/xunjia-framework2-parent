package com.xunjia.framework.attachment.controller;

import javax.servlet.http.HttpServletResponse;

import com.xunjia.framework.common.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.ModelAndView;
import com.xunjia.framework.common.Context;
import com.xunjia.framework.attachment.entity.Attachment;
import com.xunjia.framework.attachment.service.AttachmentService;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.utils.FileUtils;

import java.util.List;

@RestController
@RequestMapping("/attachment")
public class AttachmentController {

	@Autowired
	private AttachmentService service;
	
	@Value("${com.xunjia.framework.baseUploadFolder}")
	private String uploadFolder;
	
	@RequestMapping("/toList")
	public ModelAndView toList() {
		return new ModelAndView("framework/attachment/list");
	}
	
	@RequestMapping("/toAdd")
	public ModelAndView toAdd() {
		return new ModelAndView("framework/attachment/add");
	}
	
	@RequestMapping("/toEdit")
	public ModelAndView toEdit() {
		return new ModelAndView("framework/attachment/edit");
	}
	
	@RequestMapping("/toDownload")
	public ModelAndView toDownload() {
		return new ModelAndView("framework/attachment/download");
	}
	
	@RequestMapping("/save")
	public ResponseData<Boolean> save(Attachment attachment, MultipartRequest request){
		MultipartFile attachmentFile = request.getFile("attachmentFile");
		ResponseData<Boolean> resp = service.save(attachment, attachmentFile);
		return resp;
	}
	
	@RequestMapping("/update")
	public ResponseData<Boolean> update(Attachment attachment){
		ResponseData<Boolean> resp = service.update(attachment);
		return resp;
	}
	
	@RequestMapping("/delete")
	public ResponseData<Boolean> delete(@RequestParam(name="codes[]")String[] codes){
		ResponseData<Boolean> resp = service.deleteByCodes(codes);
		return resp;
	}
	
	@RequestMapping("/findByBusinessAndBusinessId")
	public List<Attachment> findByBusinessAndBusinessId(String businessId) {
		List<Attachment> attachment = service.findByBusinessAndBusinessId("排查人员信息",businessId);
		return attachment;
	}

	@RequestMapping("/findByCode")
	public Attachment findByCode(String code) {
		Attachment attachment = service.findByCode(code);
		return attachment;
	}
	
	@RequestMapping("/download")
	public void download(String code, HttpServletResponse response) {
		Attachment attachment = service.findAndUpdateDownloadCount(code);
		FileUtils.downloadFile(response, uploadFolder + attachment.getSavePath(), attachment.getOriginalFileName());
	}
	
	@RequestMapping("/findAttachments")
	public PageVO<Attachment> findAttachments(String title, String startDate, String endDate, String uid, int page, int rows){
		String uploadUserId = null;
		if (StringUtils.isEmpty(uid)) {
			uploadUserId = Context.getCurrentUser().getId();
		} else if (!uid.equals("all")) {
			uploadUserId = uid;
		}
		Page<Attachment> pageData = service.findAttachments(title, uploadUserId, startDate, endDate, page, rows);
		PageVO<Attachment> pageVo = new PageVO<Attachment>(pageData);
		return pageVo;
	}
	
	@RequestMapping("/findBusinessAttachments")
	public PageVO<Attachment> findBusinessAttachments(String business, String businessSubType, 
			String businessId, String startDate, String endDate, int page, int rows){
		Page<Attachment> pageData = service.findBusinessAttachments(business, businessSubType, 
				businessId, startDate, endDate, page, rows);
		PageVO<Attachment> pageVo = new PageVO<Attachment>(pageData);
		return pageVo;
	}
}
