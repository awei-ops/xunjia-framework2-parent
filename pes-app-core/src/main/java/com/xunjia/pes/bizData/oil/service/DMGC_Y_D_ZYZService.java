package com.xunjia.pes.bizData.oil.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.framework.utils.excel.ExportUtils;
import com.xunjia.pes.basicDataManage.entity.Indicators;
import com.xunjia.pes.basicDataManage.service.IndicatorsService;
import com.xunjia.pes.bizData.assessment.entity.MonitoringIndicatorNew;
import com.xunjia.pes.bizData.assessment.service.MonitoringIndicatorNewService;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_CSB;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_ZYZ;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_JB;
import com.xunjia.pes.bizData.oil.mapper.DMGC_Y_D_ZYZMapper;

import com.xunjia.pes.score.Calculation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DMGC_Y_D_ZYZService extends ServiceImpl<DMGC_Y_D_ZYZMapper, DMGC_Y_D_ZYZ> {

    @Autowired
    private DMGC_Y_D_ZYZMapper mapper;
    @Autowired
    private IndicatorsService indicatorsService;
    @Autowired
    private MonitoringIndicatorNewService monitoringIndicatorNewService;
    @Autowired
    @Lazy
    private ZYZ_JXPJService zyzJxpjService;

    public Boolean createData(String rq) {
        boolean result = true;
        try {
            Date now = new Date();
            String nowDateString = "";
            if (StringUtils.isNotEmpty(rq)) {
                nowDateString = rq;
            } else {
                nowDateString = DateUtils.format(now, DateUtils.DATE_PATTERN);
            }
            Date createDate = DateUtils.parse(nowDateString, DateUtils.DATE_PATTERN);
            LambdaQueryWrapper<DMGC_Y_D_ZYZ> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DMGC_Y_D_ZYZ::getRq, createDate);
            wrapper.eq(DMGC_Y_D_ZYZ::getZkEventId, "DE8D6CD076452D56E040007F01006724");
            List<DMGC_Y_D_ZYZ> yDZyzs = mapper.selectList(wrapper);
            if (yDZyzs.size() == 0) {
                DMGC_Y_D_ZYZ song2 = new DMGC_Y_D_ZYZ();
                song2.setEventId(UUID.randomUUID().toString());
                song2.setRq(createDate);
                song2.setZkEventId("DE8D6CD076452D56E040007F01006724");
                song2.setStationName("松2转油站");
                yDZyzs.add(song2);
                this.saveBatch(yDZyzs);
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
            result = false;
        }
        return result;
    }

    public Boolean saveData(String id, Double zhdh, Double dyhd, Double dyhq) {
        try {
            DMGC_Y_D_ZYZ record = mapper.selectById(id);
            record.setZhdh(zhdh);
            record.setDyhd(dyhd);
            record.setDyhq(dyhq);
            mapper.updateById(record);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Boolean auditData(String rq) {
        try {
            LambdaQueryWrapper<DMGC_Y_D_ZYZ> wrapper = new LambdaQueryWrapper<>();
            Date startDate = DateUtils.parse(rq, DateUtils.DATE_PATTERN);
            wrapper.ge(DMGC_Y_D_ZYZ::getRq, startDate);
            Date endDate = DateUtils.parse(rq + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
            wrapper.le(DMGC_Y_D_ZYZ::getRq, endDate);
            wrapper.isNotNull(DMGC_Y_D_ZYZ::getZhdh);
            wrapper.isNotNull(DMGC_Y_D_ZYZ::getDyhd);
            wrapper.isNotNull(DMGC_Y_D_ZYZ::getDyhq);
            wrapper.ne(DMGC_Y_D_ZYZ::getZhdh, 0);
            wrapper.ne(DMGC_Y_D_ZYZ::getDyhd, 0);
            wrapper.ne(DMGC_Y_D_ZYZ::getDyhq, 0);
            List<DMGC_Y_D_ZYZ> dmgcYDZyzList = mapper.selectList(wrapper);
            if (dmgcYDZyzList.size() != 0) {
                getCompleteData(dmgcYDZyzList);
                this.updateBatchById(dmgcYDZyzList);
                zyzJxpjService.updateData(rq);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public PageVO<DMGC_Y_D_ZYZ> getPageData(DMGC_Y_D_ZYZ example, String startDate, String endDate, int page, int size) {
        PageVO<DMGC_Y_D_ZYZ> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_Y_D_ZYZ> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            PageInfo<DMGC_Y_D_ZYZ> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e) {
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    public void exportData(DMGC_Y_D_ZYZ example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response) {

        Workbook workbook = ExportUtils.createWorkbook();
        try {
            List<DMGC_Y_D_ZYZ> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            Map<String, List<String>> headerNamesAndFieldsMap = ExportUtils.getHeaderNamesAndFields(DMGC_Y_D_ZYZ.class);
            List<String> headerNames = headerNamesAndFieldsMap.get("headerNames");
            List<String> fields = headerNamesAndFieldsMap.get("fields");
            ExportUtils.exportExcel("转油放水站生产动态日数据", headerNames, fields, 0, workbook, dataList);
            ExportUtils.responseWorkbook("转油放水站生产动态日数据", workbook, request, response);
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

    private LambdaQueryWrapper<DMGC_Y_D_ZYZ> buildQueryWrapper(DMGC_Y_D_ZYZ example, String startDate, String endDate) {
        LambdaQueryWrapper<DMGC_Y_D_ZYZ> queryWrapper = new LambdaQueryWrapper<>();
        if (example != null) {
            if (StringUtils.isNotEmpty(example.getStationName())) {
                queryWrapper.like(DMGC_Y_D_ZYZ::getStationName, example.getStationName());
            }
        }
        if (!StringUtils.isEmpty(startDate)) {
            try {
                Date date = DateUtils.parse(startDate, DateUtils.DATE_PATTERN);
                queryWrapper.ge(DMGC_Y_D_ZYZ::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!StringUtils.isEmpty(endDate)) {
            try {
                Date date = DateUtils.parse(endDate + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                queryWrapper.le(DMGC_Y_D_ZYZ::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        queryWrapper.orderByDesc(DMGC_Y_D_ZYZ::getRq);
        return queryWrapper;
    }

    private MonitoringIndicatorNew filterByQueryData(double queryData, List<MonitoringIndicatorNew> monitoringIndicatorNewList, String monitoringItem) {
        List<MonitoringIndicatorNew> temp = monitoringIndicatorNewList.stream().filter(c -> c.getMonitoringItem().equals(monitoringItem)).collect(Collectors.toList());
        if (temp.size() == 1) {
            return temp.get(0);
        }
        for (MonitoringIndicatorNew param : temp) {
            if (param.getValueMin() == null && param.getValueMax() != null && queryData <= param.getValueMax()) {
                return param;
            }
            if (param.getValueMin() != null && param.getValueMax() != null && queryData > param.getValueMin() && queryData <= param.getValueMax()) {
                return param;
            }
            if (param.getValueMax() == null && param.getValueMin() != null && queryData > param.getValueMin()) {
                return param;
            }
        }
        return null;
    }

    public Boolean updateData(String rq) {
        boolean result = true;
        try {
            Date now = new Date();
            String nowDateString = "";
            if (StringUtils.isNotEmpty(rq)) {
                nowDateString = rq;
            } else {
                nowDateString = DateUtils.format(now, DateUtils.DATE_PATTERN);
            }
            Date updateDate = DateUtils.parse(nowDateString, DateUtils.DATE_PATTERN);
            LambdaQueryWrapper<DMGC_Y_D_ZYZ> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DMGC_Y_D_ZYZ::getRq, updateDate);
            List<DMGC_Y_D_ZYZ> dataList = mapper.selectList(wrapper);
            if (dataList.size() != 0) {
                getCompleteData(dataList);
                this.updateBatchById(dataList);
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
            result = false;
        }
        return result;
    }

    public Boolean updateData(List<DMGC_Y_D_ZYZ> params) {
        return this.updateBatchById(params);
    }

    private List<DMGC_Y_D_ZYZ> getDataOfDay(String startDate, String endDate) {
        List<DMGC_Y_D_ZYZ> dataList = mapper.selectList(this.buildQueryWrapper(null, startDate, endDate));
        return dataList;
    }

    private void getCompleteData(List<DMGC_Y_D_ZYZ> partData) {
        if (partData.size() == 0) {
            return;
        }
        try {
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("转油站监测项目与指标要求", null);
            List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("zyfsz")).collect(Collectors.toList());
            for (DMGC_Y_D_ZYZ param : partData) {

                MonitoringIndicatorNew monitoringIndicatorNew = filterByQueryData(0, monitoringIndicatorNewList, "转油站综合能耗");
                param.setZhdhWeight(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level2") && d.getItemCode().equals("zyzzhdh")).collect(Collectors.toList()).get(0).getWeight());
                if (monitoringIndicatorNew != null) {
                    if (param.getZhdh() != null) {
                        param.setZhdhScore(Calculation.calculationOfUnitConsumption(param.getZhdh(), monitoringIndicatorNew));
                        param.setZhdhWeightScore(Calculation.getMultiplicationResult(param.getZhdhScore(), param.getZhdhWeight()));
                        param.setZhdhPj(Calculation.getUnitConsumptionComment(param.getZhdhScore(), monitoringIndicatorNew));
                    }
                }

                monitoringIndicatorNew = filterByQueryData(0, monitoringIndicatorNewList, "转油站吨液耗电");
                param.setDyhdWeight(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level2") && d.getItemCode().equals("zyzdyhd")).collect(Collectors.toList()).get(0).getWeight());
                if (monitoringIndicatorNew != null) {
                    if (param.getDyhd() != null) {
                        param.setDyhdScore(Calculation.calculationOfUnitConsumption(param.getDyhd(), monitoringIndicatorNew));
                        param.setDyhdWeightScore(Calculation.getMultiplicationResult(param.getDyhdScore(), param.getDyhdWeight()));
                        param.setDyhdPj(Calculation.getUnitConsumptionComment(param.getDyhdScore(), monitoringIndicatorNew));
                    }
                }

                monitoringIndicatorNew = filterByQueryData(0, monitoringIndicatorNewList, "转油站吨液耗气");
                param.setDyhqWeight(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level2") && d.getItemCode().equals("zyzdyhq")).collect(Collectors.toList()).get(0).getWeight());
                if (monitoringIndicatorNew != null) {
                    if (param.getDyhq() != null) {
                        param.setDyhqScore(Calculation.calculationOfUnitConsumption(param.getDyhq(), monitoringIndicatorNew));
                        param.setDyhqWeightScore(Calculation.getMultiplicationResult(param.getDyhqScore(), param.getDyhqWeight()));
                        param.setDyhqPj(Calculation.getUnitConsumptionComment(param.getDyhqScore(), monitoringIndicatorNew));
                    }
                }
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
    }

    private List<DMGC_Y_D_ZYZ> getDataOfMonth(String startDate, String endDate) {
        List<DMGC_Y_D_ZYZ> dataList = new ArrayList<>();
        String queryStart = startDate;
        String queryEnd = endDate;
        try {
            queryStart = DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getMonth(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-01";
            queryEnd = DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getMonth(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getDay(DateUtils.parse(DateUtils.getLastDay(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)), DateUtils.DATE_PATTERN));
            dataList = mapper.selectList(this.buildQueryWrapper(null, queryStart, queryEnd));
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return dataList;
    }

    private List<DMGC_Y_D_ZYZ> getDataOfYear(String startDate, String endDate) {
        List<DMGC_Y_D_ZYZ> dataList = new ArrayList<>();
        String queryStart = startDate;
        String queryEnd = endDate;
        try {
            queryStart = DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-01-01";
            queryEnd = DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-12-31";
            dataList = mapper.selectList(this.buildQueryWrapper(null, queryStart, queryEnd));
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return dataList;
    }

    public PageVO<DMGC_Y_D_ZYZ> getAssessment(String cycle, String assessmentDate, int page, int size) {
        PageVO<DMGC_Y_D_ZYZ> pageVO = null;
        List<DMGC_Y_D_ZYZ> tempResult = new ArrayList<>();
        try {
            Date sumStart = DateUtils.parse(assessmentDate, DateUtils.DATE_PATTERN);
            Date sumEnd = DateUtils.parse(assessmentDate + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
            switch (cycle) {
                case "日":
                    tempResult = getDataOfDay(assessmentDate, assessmentDate);
                    break;
                case "月":
                    sumStart = DateUtils.parse(DateUtils.getFirstDay(sumStart), DateUtils.DATE_PATTERN);
                    sumEnd = DateUtils.parse(DateUtils.getLastDay(sumStart) + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                    tempResult = getDataOfMonth(assessmentDate, assessmentDate);
                    break;
                case "年":
                    sumStart = DateUtils.parse(DateUtils.getYear(sumStart) + "-01-01", DateUtils.DATE_PATTERN);
                    sumEnd = DateUtils.parse(DateUtils.getYear(sumStart) + "-12-31 23:59:59", DateUtils.DATE_TIME_PATTERN);
                    tempResult = getDataOfYear(assessmentDate, assessmentDate);
                    break;
            }
            if (tempResult.size() == 0) {
                return new PageVO<>();
            }
            List<DMGC_Y_D_ZYZ> dataList = tempResult;
            if (cycle.equals("月") || cycle.equals("年")) {
                dataList = buildAssessment(tempResult, sumStart, sumEnd);
            }
            dataList = dataList.stream().sorted(Comparator.comparing(DMGC_Y_D_ZYZ::getStationName, Comparator.nullsLast(String::compareTo))).collect(Collectors.toList());
//            PageMethod.startPage(page, size);
            PageInfo<DMGC_Y_D_ZYZ> pageInfo = PageInfo.of(dataList);
            List<DMGC_Y_D_ZYZ> pageList = dataList.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
            pageVO = new PageVO<>(pageInfo.getTotal(), pageList);
            return pageVO;
        } catch (Exception ex) {
            String err = ex.getMessage();
            return new PageVO<>();
        }
    }

    public List<DMGC_Y_D_ZYZ> getAssessmentNoPage(String cycle, String assessmentDate) {
        List<DMGC_Y_D_ZYZ> result = new ArrayList<>();
        List<DMGC_Y_D_ZYZ> tempResult = new ArrayList<>();
        try {
            Date sumStart = DateUtils.parse(assessmentDate, DateUtils.DATE_PATTERN);
            Date sumEnd = DateUtils.parse(assessmentDate + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
            switch (cycle) {
                case "日":
                    tempResult = getDataOfDay(assessmentDate, assessmentDate);
                    break;
                case "月":
                    sumStart = DateUtils.parse(DateUtils.getFirstDay(sumStart), DateUtils.DATE_PATTERN);
                    sumEnd = DateUtils.parse(DateUtils.getLastDay(sumStart) + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                    tempResult = getDataOfMonth(assessmentDate, assessmentDate);
                    break;
                case "年":
                    sumStart = DateUtils.parse(DateUtils.getYear(sumStart) + "-01-01", DateUtils.DATE_PATTERN);
                    sumEnd = DateUtils.parse(DateUtils.getYear(sumStart) + "-12-31 23:59:59", DateUtils.DATE_TIME_PATTERN);
                    tempResult = getDataOfYear(assessmentDate, assessmentDate);
                    break;
            }
            if (tempResult.size() == 0) {
                return result;
            }
            result = tempResult;
            if (cycle.equals("月") || cycle.equals("年")) {
                result = buildAssessment(tempResult, sumStart, sumEnd);
            }
            result = result.stream().sorted(Comparator.comparing(DMGC_Y_D_ZYZ::getStationName, Comparator.nullsLast(String::compareTo))).collect(Collectors.toList());
            return result;
        } catch (Exception ex) {
            String err = ex.getMessage();
            return result;
        }
    }

    private List<DMGC_Y_D_ZYZ> buildAssessment(List<DMGC_Y_D_ZYZ> dmgcYDZYZList, Date sumStart, Date sumEnd) {
        if (dmgcYDZYZList.size() == 0) {
            return new ArrayList<>();
        }
        List<String> zmcList = dmgcYDZYZList.stream().map(c -> c.getStationName()).distinct().collect(Collectors.toList());
        List<DMGC_Y_D_ZYZ> dataList = new ArrayList<>();
        DMGC_Y_D_ZYZ dmgc_s_d_zszrsj;
        for (String zmc : zmcList) {
            dmgc_s_d_zszrsj = new DMGC_Y_D_ZYZ();
            dmgc_s_d_zszrsj.setRq(sumEnd);
            Date finalSumStart = sumStart;
            Date finalSumEnd = sumEnd;
            double zhydl = dmgcYDZYZList.stream()
                    .filter(c -> c.getStationName().equals(zmc) && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                    .mapToDouble(DMGC_Y_D_ZYZ::getZhdh).sum();


            dataList.add(dmgc_s_d_zszrsj);
        }
        double maxZhdh = dataList.stream().mapToDouble(DMGC_Y_D_ZYZ::getZhdh).max().getAsDouble();

        return dataList;
    }

    public List<DMGC_Y_D_ZYZ> getDataForZYQ(String cycle, Date assessmentDate) {
        LambdaQueryWrapper<DMGC_Y_D_ZYZ> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(DMGC_Y_D_ZYZ::getJxpjScore);
        wrapper.eq(DMGC_Y_D_ZYZ::getRq, assessmentDate);
        wrapper.and(item -> item.isNotNull(DMGC_Y_D_ZYZ::getZhdh)
                .or().isNotNull(DMGC_Y_D_ZYZ::getDyhd)
                .or().isNotNull(DMGC_Y_D_ZYZ::getDyhq)
                .or().ne(DMGC_Y_D_ZYZ::getZhdh, 0)
                .or().ne(DMGC_Y_D_ZYZ::getDyhd, 0)
                .or().ne(DMGC_Y_D_ZYZ::getDyhq, 0));
        return mapper.selectList(wrapper);
    }

    public List<DMGC_Y_D_ZYZ> getDataForPortrait(String zid, Date queryStart, Date queryEnd) {
        LambdaQueryWrapper<DMGC_Y_D_ZYZ> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(zid)) {
            wrapper.eq(DMGC_Y_D_ZYZ::getZkEventId, zid);
        }
        wrapper.isNotNull(DMGC_Y_D_ZYZ::getJxpjScore);
        wrapper.ge(DMGC_Y_D_ZYZ::getRq, queryStart);
        wrapper.le(DMGC_Y_D_ZYZ::getRq, queryEnd);
        return mapper.selectList(wrapper);
    }

    public List<DMGC_Y_D_ZYZ> getEffectiveDataOfDay(String queryDate) {
        List<DMGC_Y_D_ZYZ> result = new ArrayList<>();
        try {
            Date date = DateUtils.parse(queryDate, DateUtils.DATE_PATTERN);
            LambdaQueryWrapper<DMGC_Y_D_ZYZ> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DMGC_Y_D_ZYZ::getRq, date);
            wrapper.and(item -> item.isNotNull(DMGC_Y_D_ZYZ::getZhdh)
                    .or().isNotNull(DMGC_Y_D_ZYZ::getDyhd)
                    .or().isNotNull(DMGC_Y_D_ZYZ::getDyhq)
                    .or().ne(DMGC_Y_D_ZYZ::getZhdh, 0)
                    .or().ne(DMGC_Y_D_ZYZ::getDyhd, 0)
                    .or().ne(DMGC_Y_D_ZYZ::getDyhq, 0));
            result = mapper.selectList(wrapper);
            return result;
        } catch (Exception ex) {
            return result;
        }
    }

    public List<DMGC_Y_D_ZYZ> getEffectiveData(Date queryStartDate,Date queryEndDate) {
        List<DMGC_Y_D_ZYZ> result = new ArrayList<>();
        try {
            LambdaQueryWrapper<DMGC_Y_D_ZYZ> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(DMGC_Y_D_ZYZ::getRq, queryStartDate);
            wrapper.le(DMGC_Y_D_ZYZ::getRq, queryEndDate);
            wrapper.and(item -> item.isNotNull(DMGC_Y_D_ZYZ::getZhdh)
                    .or().isNotNull(DMGC_Y_D_ZYZ::getDyhd)
                    .or().isNotNull(DMGC_Y_D_ZYZ::getDyhq)
                    .or().ne(DMGC_Y_D_ZYZ::getZhdh, 0)
                    .or().ne(DMGC_Y_D_ZYZ::getDyhd, 0)
                    .or().ne(DMGC_Y_D_ZYZ::getDyhq, 0));
            result = mapper.selectList(wrapper);
            return result;
        } catch (Exception ex) {
            return result;
        }
    }
}
