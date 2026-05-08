package com.xunjia.pes.basicDataManage.controller;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.pes.basicDataManage.entity.EnergySavingDetail;
import com.xunjia.pes.basicDataManage.entity.EnergySavingDetail;
import com.xunjia.pes.basicDataManage.service.EnergySavingDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Api(value = "节能措施控详细制器")
@RestController
@RequestMapping("/energySavingDetail")
public class EnergySavingDetailController {

    @Autowired
    private EnergySavingDetailService service;

    @ApiOperation(value = "跳转至添加页面", httpMethod = "GET")
    @RequestMapping("/toAdd")
    @RequiresPermissions("energySavingDetail:save")
    public ModelAndView toAdd() {
        return new ModelAndView("basicDataManage/energySavingDetail/add");
    }

    @ApiOperation(value = "跳转至编辑页面", httpMethod = "GET")
    @RequestMapping("/toEdit")
    @RequiresPermissions("energySavingDetail:update")
    public ModelAndView toEdit() {
        ModelAndView mav = new ModelAndView("basicDataManage/energySavingDetail/edit");
        return mav;
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("energySavingDetail:list")
    public ModelAndView toList() {
        return new ModelAndView("basicDataManage/energySavingDetail/list");
    }

    @ApiOperation(value = "查询数据", httpMethod = "GET")
    @RequestMapping("/getByClassificationCode")
    public List<EnergySavingDetail> getByClassificationCode(String classificationCode){
        return service.getByClassificationCode(classificationCode);
    }

    @ApiOperation(value = "保存信息", httpMethod = "POST")
    @RequestMapping("/save")
    @RequiresPermissions("energySavingDetail:save")
    public ResponseData<Boolean> save(
            @ApiParam(value = "装置类型") EnergySavingDetail param) {
        return service.save(param);
    }

    @ApiOperation(value = "更新信息", httpMethod = "POST")
    @RequestMapping("/update")
    @RequiresPermissions("energySavingDetail:update")
    public ResponseData<Boolean> update(
            @ApiParam(value = "装置类型") EnergySavingDetail param) {
        return service.update(param);
    }

    @ApiOperation(value = "批量删除", httpMethod = "POST")
    @RequestMapping("/delete")
    @RequiresPermissions("energySavingDetail:delete")
    public ResponseData<Boolean> deleteByIds(
            @ApiParam(value = "id数组") @RequestParam(name = "ids[]") String[] ids) {
        List<String> list = Stream.of(ids).collect(Collectors.toList());
        return service.deleteByIds(list);
    }

    @ApiOperation(value = "根据给定id查询信息", httpMethod = "GET")
    @RequestMapping("/findById")
    public EnergySavingDetail findById(String id) {
        return service.findById(id);
    }

    @ApiOperation(value = "查询分页数据", httpMethod = "GET")
    @RequestMapping("/getPageData")
    @RequiresPermissions("energySavingDetail:list")
    public PageVO<EnergySavingDetail> getPageData(String measuresTypeCode,String classificationCode, int page, int rows){
        return service.getPageData(measuresTypeCode,classificationCode,page,rows);
    }
}
