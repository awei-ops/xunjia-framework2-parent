package com.xunjia.framework.news.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.xunjia.framework.news.bean.PublicMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.FileUtils;

@RestController
public class UEditorController {
	
	//private FileUtils fileUtils;
	
	@Value("${com.xunjia.framework.uploadAccessPath}")
	private String fileAccessPath;

	@RequestMapping(value="/ueditor")
    @ResponseBody
    public String ueditor(HttpServletRequest request) {

        return PublicMsg.UEDITOR_CONFIG;
    }

    @RequestMapping(value="/imgUpload")
    @ResponseBody
    public Map<String, Object> imgUpload(MultipartFile upfile) {
    	String dateStr = DateUtils.format(new Date(), "yyyyMMdd");
        String savePath = FileUtils.copyFile(upfile, "news/" + dateStr);
        savePath = fileAccessPath + savePath;
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("state", "SUCCESS");
        map.put("url", savePath);
        map.put("size", upfile.getSize());
        map.put("original", upfile.getOriginalFilename());
        map.put("type", upfile.getContentType());
        return map;
    }
}
