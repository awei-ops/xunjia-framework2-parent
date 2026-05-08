package com.xunjia.pes.basicDataManage.controller;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.pes.basicDataManage.entity.RectificationMeasures;
import com.xunjia.pes.basicDataManage.service.RectificationMeasuresService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Api(value = "整改措施控制器")
@RestController
@RequestMapping("/energySavingMeasures")
public class RectificationMeasuresController {
    @Autowired
    private RectificationMeasuresService service;

    @ApiOperation(value = "跳转至列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("energySavingMeasures:list")
    public ModelAndView toList() {
        return new ModelAndView("bizData/rectificationMeasures/list");
    }

    @ApiOperation(value = "保存信息", httpMethod = "POST")
    @RequestMapping("/save")
    public ResponseData<Boolean> save(RectificationMeasures param,String myDate){
        try {
            param.setOriginalDataDate(DateUtils.parse(myDate, DateUtils.DATE_PATTERN));
        }catch (Exception ex){
            String err = ex.getMessage();
        }
        return service.save(param);
    }

    @ApiOperation(value = "更新信息", httpMethod = "POST")
    @RequestMapping("/update")
    public ResponseData<Boolean> update(RectificationMeasures param){
        return service.update(param);
    }

    @ApiOperation(value = "批量删除", httpMethod = "POST")
    @RequestMapping("/delete")
    public ResponseData<Boolean> deleteByIds(@RequestParam(name = "ids[]") String[] ids){
        List<String> list = Stream.of(ids).collect(Collectors.toList());
        return service.deleteByIds(list);
    }

    @ApiOperation(value = "根据给定id查询信息", httpMethod = "GET")
    @RequestMapping("/getByParam")
    public RectificationMeasures getByParam(String queryDate, String equipmentId){
        return service.getByParam(queryDate,equipmentId);
    }

    @ApiOperation(value = "查询分页数据", httpMethod = "GET")
    @RequestMapping("/getRectificationMeasures")
    @RequiresPermissions("energySavingMeasures:list")
    public PageVO<RectificationMeasures> getRectificationMeasures(String startDate, String endDate, String measuresTypeCode, int page, int rows){
        return service.getRectificationMeasures(startDate,endDate,measuresTypeCode,page,rows);
    }
}
