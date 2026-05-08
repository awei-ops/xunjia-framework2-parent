package com.xunjia.framework.device.controller;

import java.util.ArrayList;
import java.util.List;

import com.xunjia.framework.common.Context;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.usermanage.entity.Organization;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.device.entity.Device;
import com.xunjia.framework.device.service.DeviceService;
import com.xunjia.framework.utils.StringUtils;

@RestController
@RequestMapping("/device")
public class DeviceController {

	@RequestMapping("/toAdd")
	public ModelAndView toAdd() {
		return new ModelAndView("framework/device/add");
	}

	/**
	 * 跳转到修改页 2020年8月31日
	 * 
	 * @author 姜浩
	 * @return
	 */
	@RequestMapping("/toEdit")
	public ModelAndView toEdit() {
		return new ModelAndView("framework/device/edit");
	}

	/**
	 * 跳转到数据列表页 2020年8月31日
	 * 
	 * @author 姜浩
	 * @return
	 */
	@RequestMapping("/toList")
	public ModelAndView toList() {
		return new ModelAndView("framework/device/list");
	}

	@Autowired
	private DeviceService service;

	@RequestMapping("/save")
	@RequiresPermissions("device:save")
	public ResponseData<Boolean> save(Device device,String parentId) {
		if (!StringUtils.isEmpty(parentId)) {
			Organization org = new Organization();
			org.setId(parentId);		
			device.setOrg(org);
		}
		return service.save(device);
	}

	@RequestMapping("/delete")
	@RequiresPermissions("device:delete")
	public ResponseData<Boolean> delete(@RequestParam(name = "ids[]") String[] ids) {
		return service.delete(ids);
	}

	@RequestMapping("/update")
	@RequiresPermissions("device:update")
	public ResponseData<Boolean> update(Device device,String parentId) {
		if (!StringUtils.isEmpty(parentId)) {
			Organization org = new Organization();
			org.setId(parentId);		
			device.setOrg(org);
		}
		return service.update(device);
	}

	/**
	 * 更新装置可用状态 2020年8月31日
	 * 
	 * @author 姜浩
	 * @param enabled 可用状态
	 * @param ids     装置id
	 * @return 操作响应信息
	 */
	@RequestMapping("/updateEnableState")
	public ResponseData<Boolean> updateEnabled(int enabled, @RequestParam(name = "ids[]") String[] ids) {
		return service.updateEnableState(enabled, ids);
	}

	/**
	 * 根据id查询装置信息 2020年8月31日
	 * 
	 * @author 姜浩
	 * @param id 装置id
	 * @return 装置信息
	 */
	@RequestMapping("/findById")
	public Device findById(String id) {
		return service.findById(id);
	}

	@RequestMapping("/findByOrg")
	public List<Device> findByOrg(String orgId) {
		if (StringUtils.isEmpty(orgId)) {
			return new ArrayList<Device>(0);
		}
		return service.findByOrg(orgId);
	}

	/**
	 * 查询装置信息分页数据 2020年8月31日
	 * 
	 * @author 姜浩
	 * @param name    装置名称
	 * @param code    装置编码
	 * @param orgId   所属组织id
	 * @param enabled 可用状态
	 * @param page    当前页号
	 * @param rows    每页条数
	 * @return 装置信息分页对象
	 */
	@RequestMapping("/findDevices")
	public PageVO<Device> findDevices(String name, String code, String orgId, Integer enabled, int page, int rows) {
		if (enabled == null) {
			enabled = -1;
		}
		if (StringUtils.isEmpty(orgId) && !Context.getCurrentUser().getUsername().equals("admin")) {
			orgId = Context.getCurrentUser().getOrg().getId();
		}
		Page<Device> pageData = service.findDevices(name, code, orgId, enabled, page, rows);
		PageVO<Device> pageVo = new PageVO<Device>(pageData);
		return pageVo;
	}

	/**
	 * 根据当前人的岗位(车间)取装置下拉框 2020年10月30日
	 * 
	 * @author 杨慧
	 */
	@RequestMapping("/findByCurrentOrg")
	public List<Device> findByCurrentOrg() {
		String orgId = Context.getCurrentUser().getOrg().getId();
		List<Device> mappings = service.findByOrg(orgId);
		return mappings;
	}

}
