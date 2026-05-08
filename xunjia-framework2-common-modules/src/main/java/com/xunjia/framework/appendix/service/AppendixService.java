package com.xunjia.framework.appendix.service;

import java.util.List;

import javax.transaction.Transactional;

import com.xunjia.framework.appendix.repository.IAppendixRepository;
import com.xunjia.framework.common.entity.Appendix;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AppendixService {

	@Autowired
	private IAppendixRepository appendixRepo;
	
	public ResponseData<Boolean> addAppendixs(List<Appendix> appendixs) {
		ResponseData<Boolean> resp = null;
		try {
			appendixRepo.saveAll(appendixs);
			resp = ResponseData.getSuccess("保存成功。");
		} catch (Exception e) {
			resp = ResponseData.getError(e);
		}
		return resp;
	}

	public ResponseData<Boolean> deleteAppendix(String id) {
		ResponseData<Boolean> resp = null;
		try {
			appendixRepo.deleteById(id);
			resp = ResponseData.getSuccess("删除成功。");
		} catch (Exception e) {
			resp = ResponseData.getError(e);
		}
		return resp;
	}

	public ResponseData<Boolean> deleteAppendixs(String businessType, String businessId) {
		ResponseData<Boolean> resp = null;
		try {
			appendixRepo.deleteByBusinessTypeAndBusinessId(businessType, businessId);
			resp = ResponseData.getSuccess("删除成功。");
		} catch (Exception e) {
			resp = ResponseData.getError(e);
		}
		return resp;
	}

	public ResponseData<Boolean> deleteAppendixs(List<String> ids) {
		ResponseData<Boolean> resp = null;
		try {
			for(String id : ids) {
				Appendix appendix = appendixRepo.getOne(id);
				FileUtils.deleteFile(appendix.getDir() + "/" + appendix.getFileName());
			}
			appendixRepo.deleteByIds(ids);
			resp = ResponseData.getSuccess("删除成功。");
		} catch (Exception e) {
			resp = ResponseData.getError(e);
		}
		return resp;
	}

	public Appendix findAppendixById(String id) {
		Appendix appendix = appendixRepo.getOne(id);
		return appendix;
	}

	public List<Appendix> findAppendixs(String businessType, String businessId) {
		List<Appendix> list = appendixRepo.findByBusinessTypeAndBusinessId(businessType, businessId);
		return list;
	}

}
