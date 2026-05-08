package com.xunjia.pes.bizData.waterTreatment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.framework.utils.excel.ExportUtils;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_JRL;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSBRSJ;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSZRSJ;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_D_SBYX;
import com.xunjia.pes.bizData.waterTreatment.mapper.DMGC_S_D_SBYXMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class DMGC_S_D_SBYXService {

    @Autowired
    private DMGC_S_D_SBYXMapper mapper;

    public PageVO<DMGC_S_D_SBYX> getPageData(DMGC_S_D_SBYX example, String startDate, String endDate, int page, int size){
        PageVO<DMGC_S_D_SBYX> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_S_D_SBYX> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            PageInfo<DMGC_S_D_SBYX> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e){
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    public void exportData(DMGC_S_D_SBYX example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response) {

        Workbook workbook = ExportUtils.createWorkbook();
        try {
            List<DMGC_S_D_SBYX> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            Map<String, List<String>> headerNamesAndFieldsMap = ExportUtils.getHeaderNamesAndFields(DMGC_S_D_SBYX.class);
            List<String> headerNames = headerNamesAndFieldsMap.get("headerNames");
            List<String> fields = headerNamesAndFieldsMap.get("fields");
            ExportUtils.exportExcel("水处理站设备运行日数据", headerNames, fields, 0, workbook, dataList);
            ExportUtils.responseWorkbook("水处理站设备运行日数据", workbook, request, response);
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

    private LambdaQueryWrapper<DMGC_S_D_SBYX> buildQueryWrapper(DMGC_S_D_SBYX example, String startDate, String endDate){
        LambdaQueryWrapper<DMGC_S_D_SBYX> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DMGC_S_D_SBYX::getSblx,"机泵");
        if(example != null){
            if(StringUtils.isNotEmpty(example.getSszm())){
                queryWrapper.like(DMGC_S_D_SBYX::getSszm,example.getSszm());
            }
        }
        if (!StringUtils.isEmpty(startDate)) {
            try {
                Date date = DateUtils.parse(startDate, DateUtils.DATE_PATTERN);
                queryWrapper.ge(DMGC_S_D_SBYX::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!StringUtils.isEmpty(endDate)){
            try {
                Date date = DateUtils.parse(endDate + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                queryWrapper.le(DMGC_S_D_SBYX::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        queryWrapper.orderByDesc(DMGC_S_D_SBYX::getRq);
        return queryWrapper;
    }

    public List<DMGC_S_D_SBYX> getEffectiveData(List<String> jbIds,Date queryStartDate,Date queryEndDate ){
        List<DMGC_S_D_SBYX> result = new ArrayList<>();
        try {
            LambdaQueryWrapper<DMGC_S_D_SBYX> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DMGC_S_D_SBYX::getYxzt, "01");
            queryWrapper.in(DMGC_S_D_SBYX::getSbid,jbIds);
            queryWrapper.ge(DMGC_S_D_SBYX::getRq, queryStartDate);
            queryWrapper.le(DMGC_S_D_SBYX::getRq, queryEndDate);
            result = mapper.selectList(queryWrapper);
            return result;

        }catch (Exception ex){
            return result;
        }
    }
}
