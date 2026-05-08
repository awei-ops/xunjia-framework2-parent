package com.xunjia.framework.org.service;

import java.util.List;

import com.xunjia.framework.usermanage.entity.OrganizationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.org.repository.IOrganizationRepository;
import com.xunjia.framework.org.repository.IOrganizationTypeRepository;
import com.xunjia.framework.utils.StringUtils;

/**
 * 组织分类业务服务
 * 2020年5月8日
 * @author 姜浩
 */
@Service
@Transactional
public class OrganizationTypeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationTypeService.class);
	
	@Autowired
	private IOrganizationTypeRepository repo;
	
	@Autowired
	private IOrganizationRepository orgRepo;
	
	/**
	 * 添加或更新组织分类信息
	 * @param type
	 * @return
	 */
	public ResponseData<Boolean> saveOrUpdate(OrganizationType type){
		ResponseData<Boolean> resp;
		String msg = StringUtils.isEmpty(type.getId()) ? ResponseMsg.SAVE_SUCCESS : ResponseMsg.UPDATE_SUCCESS;
		try {
			OrganizationType existType = repo.findByName(type.getName());
			if (existType == null || existType.getId().equals(type.getId())) {
				repo.save(type);
				resp = ResponseData.getSuccess(msg);
			} else {
				resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_NAME_EXIST);
			}
		} catch (Exception e) {
			LOGGER.error("OrganizationTypeService.saveOrUpdate方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 批量删除组织分类
	 * @param ids
	 * @return
	 */
	public ResponseData<Boolean> deleteByIds(String[] ids){
		ResponseData<Boolean> resp;
		try {
			long subOrgCount = orgRepo.countByType_idInAndDeleteFlag(ids, 0);
			if (subOrgCount > 0) {
				resp = ResponseData.getFail(ResponseMsg.DELETE_FAIL_SUB_EXIST);
			} else {
				repo.deleteByIds(ids);
				resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
			}
		} catch (Exception e) {
			LOGGER.error("OrganizationTypeService.deleteByIds方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 根据id查询组织分类
	 * @param id
	 * @return
	 */
	public OrganizationType findById(String id) {
		return repo.findById(id).get();
	}
	
	/**
	 * 查询所有可用分类信息
	 * @return
	 */
	public List<OrganizationType> findAllEnableTypes(){
		List<OrganizationType> types = repo.findAll(Sort.by(Direction.ASC, "orderNo"));
		return types;
	}
	
	/**
	 * 查询组织分类分页信息
	 * @return
	 */
	public List<OrganizationType> findAll(){
		Sort sort = Sort.by(Direction.ASC, "orderNo");
		List<OrganizationType> types = null;
		try {
			types = repo.findAll(sort);
		} catch (Exception e) {
			LOGGER.error("OrganizationTypeService.findAll方法异常。", e);
		}
		return types;
	}
}
