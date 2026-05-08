package com.xunjia.framework.appendix.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xunjia.framework.appendix.service.AppendixService;
import com.xunjia.framework.common.entity.Appendix;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appendix")
public class AppendixController {

	@Autowired
	private AppendixService service;
	
	@GetMapping("/getAppendixById")
	public Appendix getAppendixById(String id) {
		return service.findAppendixById(id);
	}
	
	@DeleteMapping("/delete")
	public ResponseData<Boolean> deleteAppendix(String id){
		return service.deleteAppendix(id);
	}
	
	@GetMapping("/download")
	public void download(String id, HttpServletResponse response, HttpServletRequest request) {
		Appendix appendix = service.findAppendixById(id);
		FileUtils.downloadFile(response, appendix.getDir() + "/" + appendix.getFileName(), appendix.getOriginalFileName());
	}
}
