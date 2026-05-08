package com.xunjia.pes.bizData.oil.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.framework.utils.excel.ExportUtils;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_JRL;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_ZSBYXSS;
import com.xunjia.pes.bizData.oil.mapper.DMGC_Y_D_ZSBYXSSMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class DMGC_Y_D_ZSBYXSSService {

    @Autowired
    private DMGC_Y_D_ZSBYXSSMapper mapper;

    public PageVO<DMGC_Y_D_ZSBYXSS> getPageData(DMGC_Y_D_ZSBYXSS example, String startDate, String endDate, int page, int size){
        PageVO<DMGC_Y_D_ZSBYXSS> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_Y_D_ZSBYXSS> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            PageInfo<DMGC_Y_D_ZSBYXSS> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e){
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    public void exportData(DMGC_Y_D_ZSBYXSS example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response) {

        Workbook workbook = ExportUtils.createWorkbook();
        try {
            List<DMGC_Y_D_ZSBYXSS> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            Map<String, List<String>> headerNamesAndFieldsMap = ExportUtils.getHeaderNamesAndFields(DMGC_Y_D_ZSBYXSS.class);
            List<String> headerNames = headerNamesAndFieldsMap.get("headerNames");
            List<String> fields = headerNamesAndFieldsMap.get("fields");
            ExportUtils.exportExcel("站设备运行时数动态日数据", headerNames, fields, 0, workbook, dataList);
            ExportUtils.responseWorkbook("站设备运行时数动态日数据", workbook, request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private LambdaQueryWrapper<DMGC_Y_D_ZSBYXSS> buildQueryWrapper(DMGC_Y_D_ZSBYXSS example, String startDate, String endDate){
        LambdaQueryWrapper<DMGC_Y_D_ZSBYXSS> queryWrapper = new LambdaQueryWrapper<>();
        if(example != null) {
            if(StringUtils.isNotEmpty(example.getSszkName())){
                queryWrapper.like(DMGC_Y_D_ZSBYXSS::getSszkName,example.getSszkName());
            }
            if (!StringUtils.isEmpty(example.getSbmc())) {
                queryWrapper.like(DMGC_Y_D_ZSBYXSS::getSbmc, "%" + example.getSbmc() + "%");
            }
            if (!StringUtils.isEmpty(example.getSbbh())) {
                queryWrapper.like(DMGC_Y_D_ZSBYXSS::getSbbh, "%" + example.getSbbh() + "%");
            }
        }
        if (!StringUtils.isEmpty(startDate)) {
            try {
                Date date = DateUtils.parse(startDate, DateUtils.DATE_PATTERN);
                queryWrapper.ge(DMGC_Y_D_ZSBYXSS::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!StringUtils.isEmpty(endDate)){
            try {
                Date date = DateUtils.parse(endDate + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                queryWrapper.le(DMGC_Y_D_ZSBYXSS::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        queryWrapper.orderByDesc(DMGC_Y_D_ZSBYXSS::getRq);
        return queryWrapper;
    }
}
