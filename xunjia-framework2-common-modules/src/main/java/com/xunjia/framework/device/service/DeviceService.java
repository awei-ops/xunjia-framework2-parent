package com.xunjia.framework.device.service;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.device.entity.Device;
import com.xunjia.framework.device.repository.IDeviceRepository;
import com.xunjia.framework.utils.StringUtils;

@Service
@Transactional
public class DeviceService {

	@Autowired
	private IDeviceRepository repo;

	/**
	 * 保存装置信息 2020年8月31日
	 * 
	 * @author 姜浩
	 * @param equipment 装置实体对象
	 * @return 操作响应信息
	 */
	public ResponseData<Boolean> save(Device device) {
		ResponseData<Boolean> resp = null;
		device.setEnabled(1);
		if (device.getOrg() != null && device.getOrg().getId().equals("")) {
			device.setOrg(null);
		}
		try {
			repo.save(device);
			resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			resp = ResponseData.getSuccess(ResponseMsg.SAVE_FAIL);
			resp.setMsg(e.getMessage());
		}
		return resp;
	}
	
	public ResponseData<Boolean> delete(String[] ids)
	{
		ResponseData<Boolean> resp = null;
		try {
			repo.delete(ids);
			resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			resp = ResponseData.getSuccess(ResponseMsg.UPDATE_FAIL);
			resp.setMsg(e.getMessage());
		}
		return resp;
	}

	public ResponseData<Boolean> update(Device device) {
		ResponseData<Boolean> resp = null;
		if (device.getOrg() != null && device.getOrg().getId().equals("")) {
			device.setOrg(null);
		}
		try {
			repo.save(device);
			resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			resp = ResponseData.getSuccess(ResponseMsg.UPLOAD_FAIL);
			resp.setMsg(e.getMessage());
		}
		return resp;
	}

	public ResponseData<Boolean> updateEnableState(int enabled, String[] ids) {
		ResponseData<Boolean> resp = null;
		try {
			
			repo.updateEnable(enabled, ids);
			resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			resp = ResponseData.getSuccess(ResponseMsg.UPDATE_FAIL);
			resp.setMsg(e.getMessage());
		}
		return resp;
	}

	public Device findById(String id) {
		return repo.findById(id).get();
	}

	public List<Device> findByOrg(String orgId) {
		return repo.findByOrg_idOrderByOrderNoAsc(orgId);
	}
	
	

	public Page<Device> findDevices(String name, String code, String orgId, int enabled, int page, int rows) {
		Specification<Device> spec = new Specification<Device>() {
			public Predicate toPredicate(Root<Device> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicates = new LinkedList<Predicate>();
				if (!StringUtils.isEmpty(name)) {
					Predicate predicate = cb.like(root.get("name").as(String.class), "%" + name + "%");
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(code)) {
					Predicate predicate = cb.like(root.get("code").as(String.class), code + "%");
					predicates.add(predicate);
				}
				if (!StringUtils.isEmpty(orgId)) {
					Predicate predicate = cb.equal(root.get("org").get("id").as(String.class), orgId);
					predicates.add(predicate);
				}
				if (enabled != -1) {
					Predicate predicate = cb.equal(root.get("enabled").as(Integer.class), enabled);
					predicates.add(predicate);
				}

				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		Sort sort = Sort.by(Direction.ASC, "orderNo");
		Pageable pageable = PageRequest.of(page - 1, rows, sort);
		Page<Device> pageData = null;
		try {
			pageData = repo.findAll(spec, pageable);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageData;
	}

	public Integer ComputNewOrder(String orgId) {
		Integer result = 1;
		List<Device> tempDevices = findByOrg(orgId);
		if (tempDevices.size() != 0) {
			result = tempDevices.get(tempDevices.size() - 1).getOrderNo() + 1;
		}
		return result;
	}
}
