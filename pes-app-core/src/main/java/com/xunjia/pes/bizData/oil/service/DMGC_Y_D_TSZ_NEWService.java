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
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.assessment.entity.MonitoringIndicatorNew;
import com.xunjia.pes.bizData.assessment.service.MonitoringIndicatorNewService;
import com.xunjia.pes.bizData.oil.entity.*;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_TSZ_NEW;
import com.xunjia.pes.bizData.oil.mapper.DMGC_Y_D_TSZ_NEWMapper;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_D_SCLZRSJ;
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
public class DMGC_Y_D_TSZ_NEWService extends ServiceImpl<DMGC_Y_D_TSZ_NEWMapper, DMGC_Y_D_TSZ_NEW> {

    @Autowired
    private DMGC_Y_D_TSZ_NEWMapper mapper;

    @Autowired
    private DMGC_Y_TSZ_NEWService dmgcYTszNewService;

    @Autowired
    private IndicatorsService indicatorsService;
    @Autowired
    private MonitoringIndicatorNewService monitoringIndicatorNewService;

    @Autowired
    @Lazy
    private TSZ_JXPJService tszJxpjService;

    public Boolean saveData(String id, Double zhdh, Double dyohd, Double dyohq, Double dyehd, Double dyehq) {
        try {
            DMGC_Y_D_TSZ_NEW record = mapper.selectById(id);
            record.setZhdh(zhdh);
            record.setDyohd(dyohd);
            record.setDyohq(dyohq);
            record.setDyehd(dyehd);
            record.setDyehq(dyehq);
            mapper.updateById(record);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Boolean auditData(String rq) {
        try {
            LambdaQueryWrapper<DMGC_Y_D_TSZ_NEW> wrapper = new LambdaQueryWrapper<>();
            Date startDate = DateUtils.parse(rq, DateUtils.DATE_PATTERN);
            wrapper.ge(DMGC_Y_D_TSZ_NEW::getRq, startDate);
            Date endDate = DateUtils.parse(rq + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
            wrapper.le(DMGC_Y_D_TSZ_NEW::getRq, endDate);
            wrapper.isNotNull(DMGC_Y_D_TSZ_NEW::getZhdh);
            wrapper.isNotNull(DMGC_Y_D_TSZ_NEW::getDyohd);
            wrapper.isNotNull(DMGC_Y_D_TSZ_NEW::getDyohq);
            wrapper.isNotNull(DMGC_Y_D_TSZ_NEW::getDyehd);
            wrapper.isNotNull(DMGC_Y_D_TSZ_NEW::getDyehq);
            wrapper.ne(DMGC_Y_D_TSZ_NEW::getZhdh, 0);
            wrapper.ne(DMGC_Y_D_TSZ_NEW::getDyohd, 0);
            wrapper.ne(DMGC_Y_D_TSZ_NEW::getDyohq, 0);
            wrapper.ne(DMGC_Y_D_TSZ_NEW::getDyehd, 0);
            wrapper.ne(DMGC_Y_D_TSZ_NEW::getDyehq, 0);
            List<DMGC_Y_D_TSZ_NEW> dmgcYDTszList = mapper.selectList(wrapper);
            if (dmgcYDTszList.size() != 0) {
                getCompleteData(dmgcYDTszList);
                this.updateBatchById(dmgcYDTszList);
                tszJxpjService.updateData(rq);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public PageVO<DMGC_Y_D_TSZ_NEW> getPageData(DMGC_Y_D_TSZ_NEW example, String startDate, String endDate, int page, int size) {
        PageVO<DMGC_Y_D_TSZ_NEW> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_Y_D_TSZ_NEW> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            PageInfo<DMGC_Y_D_TSZ_NEW> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e) {
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    public void exportData(DMGC_Y_D_TSZ_NEW example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response) {

        Workbook workbook = ExportUtils.createWorkbook();
        try {
            List<DMGC_Y_D_TSZ_NEW> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            Map<String, List<String>> headerNamesAndFieldsMap = ExportUtils.getHeaderNamesAndFields(DMGC_Y_D_TSZ_NEW.class);
            List<String> headerNames = headerNamesAndFieldsMap.get("headerNames");
            List<String> fields = headerNamesAndFieldsMap.get("fields");
            ExportUtils.exportExcel("脱水站生产动态日数据", headerNames, fields, 0, workbook, dataList);
            ExportUtils.responseWorkbook("脱水站生产动态日数据", workbook, request, response);
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

    private LambdaQueryWrapper<DMGC_Y_D_TSZ_NEW> buildQueryWrapper(DMGC_Y_D_TSZ_NEW example, String startDate, String endDate) {
        LambdaQueryWrapper<DMGC_Y_D_TSZ_NEW> queryWrapper = new LambdaQueryWrapper<>();
        if (example != null) {
            if (StringUtils.isNotEmpty(example.getStationName())) {
                queryWrapper.like(DMGC_Y_D_TSZ_NEW::getStationName, example.getStationName());
            }
        }
        if (!StringUtils.isEmpty(startDate)) {
            try {
                Date date = DateUtils.parse(startDate, DateUtils.DATE_PATTERN);
                queryWrapper.ge(DMGC_Y_D_TSZ_NEW::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!StringUtils.isEmpty(endDate)) {
            try {
                Date date = DateUtils.parse(endDate + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                queryWrapper.le(DMGC_Y_D_TSZ_NEW::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
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
            LambdaQueryWrapper<DMGC_Y_D_TSZ_NEW> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DMGC_Y_D_TSZ_NEW::getRq, updateDate);
            List<DMGC_Y_D_TSZ_NEW> dataList = mapper.selectList(wrapper);
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

    public Boolean updateData(List<DMGC_Y_D_TSZ_NEW> params) {
        return this.updateBatchById(params);
    }

    public List<DMGC_Y_D_TSZ_NEW> getDataOfDay(String startDate, String endDate) {
        List<DMGC_Y_D_TSZ_NEW> dataList = mapper.selectList(this.buildQueryWrapper(null, startDate, endDate));
        dataList = dataList.stream().sorted(Comparator.comparing(DMGC_Y_D_TSZ_NEW::getStationName).thenComparing(DMGC_Y_D_TSZ_NEW::getRq)).collect(Collectors.toList());
        return dataList;
    }

    private List<DMGC_Y_D_TSZ_NEW> getDataOfMonth(String startDate, String endDate) {
        List<DMGC_Y_D_TSZ_NEW> dataList = new ArrayList<>();
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
        dataList = dataList.stream().sorted(Comparator.comparing(DMGC_Y_D_TSZ_NEW::getStationName).thenComparing(DMGC_Y_D_TSZ_NEW::getRq)).collect(Collectors.toList());
        return dataList;
    }

    private List<DMGC_Y_D_TSZ_NEW> getDataOfYear(String startDate, String endDate) {
        List<DMGC_Y_D_TSZ_NEW> dataList = new ArrayList<>();
        String queryStart = startDate;
        String queryEnd = endDate;
        try {
            queryStart = DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-01-01";
            queryEnd = DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-12-31";
            dataList = mapper.selectList(this.buildQueryWrapper(null, queryStart, queryEnd));
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        dataList = dataList.stream().sorted(Comparator.comparing(DMGC_Y_D_TSZ_NEW::getStationName).thenComparing(DMGC_Y_D_TSZ_NEW::getRq)).collect(Collectors.toList());
        return dataList;
    }

    private void getCompleteData(List<DMGC_Y_D_TSZ_NEW> partData) {
        if (partData.size() == 0) {
            return;
        }
        try {
            List<String> zids = partData.stream().map(c -> c.getZid()).distinct().collect(Collectors.toList());
            List<DMGC_Y_TSZ_NEW> dmgcYTszNewList = dmgcYTszNewService.getByIds(zids);

            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("脱水站监测项目与指标要求", null);
            List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("tsz")).collect(Collectors.toList());
            for (DMGC_Y_D_TSZ_NEW param : partData) {
                Optional<DMGC_Y_TSZ_NEW> dmgcSZsz = dmgcYTszNewList.stream().filter(c -> c.getEventId().equals(param.getZid())).findFirst();
                dmgcSZsz.ifPresent(c -> {
                    param.setStationName(c.getMc());

                    MonitoringIndicatorNew monitoringIndicatorNew = filterByQueryData(0, monitoringIndicatorNewList, "脱水站综合能耗");
                    param.setZhdhWeight(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level2") && d.getItemCode().equals("tszzhdh")).collect(Collectors.toList()).get(0).getWeight());
                    if (monitoringIndicatorNew != null) {
                        if (param.getZhdh() != null) {
                            param.setZhdhScore(Calculation.calculationOfUnitConsumption(param.getZhdh(), monitoringIndicatorNew));
                            param.setZhdhWeightScore(Calculation.getMultiplicationResult(param.getZhdhScore(), param.getZhdhWeight()));
                            param.setZhdhPj(Calculation.getUnitConsumptionComment(param.getZhdhScore(), monitoringIndicatorNew));
                        }
                    }

                    monitoringIndicatorNew = filterByQueryData(0, monitoringIndicatorNewList, "脱水站吨油耗电");
                    param.setDyohdWeight(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level2") && d.getItemCode().equals("tszdyohd")).collect(Collectors.toList()).get(0).getWeight());
                    if (monitoringIndicatorNew != null) {
                        if (param.getDyohd() != null) {
                            param.setDyohdScore(Calculation.calculationOfUnitConsumption(param.getDyohd(), monitoringIndicatorNew));
                            param.setDyohdWeightScore(Calculation.getMultiplicationResult(param.getDyohdScore(), param.getDyohdWeight()));
                            param.setDyohdPj(Calculation.getUnitConsumptionComment(param.getDyohdScore(), monitoringIndicatorNew));
                        }
                    }

                    monitoringIndicatorNew = filterByQueryData(0, monitoringIndicatorNewList, "脱水站吨油耗气");
                    param.setDyohqWeight(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level2") && d.getItemCode().equals("tszdyohq")).collect(Collectors.toList()).get(0).getWeight());
                    if (monitoringIndicatorNew != null) {
                        if (param.getDyohq() != null) {
                            param.setDyohqScore(Calculation.calculationOfUnitConsumption(param.getDyohq(), monitoringIndicatorNew));
                            param.setDyohqWeightScore(Calculation.getMultiplicationResult(param.getDyohqScore(), param.getDyohqWeight()));
                            param.setDyohqPj(Calculation.getUnitConsumptionComment(param.getDyohqScore(), monitoringIndicatorNew));
                        }
                    }

                    monitoringIndicatorNew = filterByQueryData(0, monitoringIndicatorNewList, "脱水站吨液耗电");
                    param.setDyehdWeight(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level2") && d.getItemCode().equals("tszdyehd")).collect(Collectors.toList()).get(0).getWeight());
                    if (monitoringIndicatorNew != null) {
                        if (param.getDyehd() != null) {
                            param.setDyehdScore(Calculation.calculationOfUnitConsumption(param.getDyehd(), monitoringIndicatorNew));
                            param.setDyehdWeightScore(Calculation.getMultiplicationResult(param.getDyehdScore(), param.getDyehdWeight()));
                            param.setDyehdPj(Calculation.getUnitConsumptionComment(param.getDyehdScore(), monitoringIndicatorNew));
                        }
                    }

                    monitoringIndicatorNew = filterByQueryData(0, monitoringIndicatorNewList, "脱水站吨液耗气");
                    param.setDyehqWeight(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level2") && d.getItemCode().equals("tszdyehq")).collect(Collectors.toList()).get(0).getWeight());
                    if (monitoringIndicatorNew != null) {
                        if (param.getDyehq() != null) {
                            param.setDyehqScore(Calculation.calculationOfUnitConsumption(param.getDyehq(), monitoringIndicatorNew));
                            param.setDyehqWeightScore(Calculation.getMultiplicationResult(param.getDyehqScore(), param.getDyehqWeight()));
                            param.setDyehqPj(Calculation.getUnitConsumptionComment(param.getDyehqScore(), monitoringIndicatorNew));
                        }
                    }
                });
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
    }

    public PageVO<DMGC_Y_D_TSZ_NEW> getAssessment(String cycle, String assessmentDate, int page, int size) {
        PageVO<DMGC_Y_D_TSZ_NEW> pageVO = null;
        List<DMGC_Y_D_TSZ_NEW> tempResult = new ArrayList<>();
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
            List<DMGC_Y_D_TSZ_NEW> dataList = tempResult;
            if (cycle.equals("月") || cycle.equals("年")) {
                dataList = buildAssessment(tempResult, sumStart, sumEnd);
            }
            dataList = dataList.stream().sorted(Comparator.comparing(DMGC_Y_D_TSZ_NEW::getStationName, Comparator.nullsLast(String::compareTo))).collect(Collectors.toList());
//            PageMethod.startPage(page, size);
            PageInfo<DMGC_Y_D_TSZ_NEW> pageInfo = PageInfo.of(dataList);
            List<DMGC_Y_D_TSZ_NEW> pageList = dataList.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
            pageVO = new PageVO<>(pageInfo.getTotal(), pageList);
            return pageVO;
        } catch (Exception ex) {
            String err = ex.getMessage();
            return new PageVO<>();
        }
    }

    public List<DMGC_Y_D_TSZ_NEW> getAssessmentNoPage(String cycle, String assessmentDate) {
        List<DMGC_Y_D_TSZ_NEW> result = new ArrayList<>();
        List<DMGC_Y_D_TSZ_NEW> tempResult = new ArrayList<>();
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
            result = result.stream().sorted(Comparator.comparing(DMGC_Y_D_TSZ_NEW::getStationName, Comparator.nullsLast(String::compareTo))).collect(Collectors.toList());
            return result;
        } catch (Exception ex) {
            String err = ex.getMessage();
            return result;
        }
    }

    private List<DMGC_Y_D_TSZ_NEW> buildAssessment(List<DMGC_Y_D_TSZ_NEW> dmgcYDZYZList, Date sumStart, Date sumEnd) {
        if (dmgcYDZYZList.size() == 0) {
            return new ArrayList<>();
        }
        List<String> zmcList = dmgcYDZYZList.stream().map(c -> c.getStationName()).distinct().collect(Collectors.toList());
        List<DMGC_Y_D_TSZ_NEW> dataList = new ArrayList<>();
        DMGC_Y_D_TSZ_NEW dmgc_s_d_zszrsj;
        for (String zmc : zmcList) {
            dmgc_s_d_zszrsj = new DMGC_Y_D_TSZ_NEW();
            dmgc_s_d_zszrsj.setRq(sumEnd);
            Date finalSumStart = sumStart;
            Date finalSumEnd = sumEnd;
            double zhydl = dmgcYDZYZList.stream()
                    .filter(c -> c.getStationName().equals(zmc) && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                    .mapToDouble(DMGC_Y_D_TSZ_NEW::getZhdh).sum();

            dataList.add(dmgc_s_d_zszrsj);
        }
        double maxZhdh = dataList.stream().mapToDouble(DMGC_Y_D_TSZ_NEW::getZhdh).max().getAsDouble();

        return dataList;
    }

    public List<DMGC_Y_D_TSZ_NEW> getSumDataOfMonth(String startDate, String endDate) {
        List<DMGC_Y_D_TSZ_NEW> dataList = getDataOfMonth(startDate, endDate);
        List<String> zmcList = dataList.stream().map(c -> c.getStationName()).distinct().collect(Collectors.toList());
        List<DMGC_Y_D_TSZ_NEW> result = new ArrayList<>();
        try {
            Date beginMonth = DateUtils.parse(DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-"
                    + DateUtils.getMonth(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-01", DateUtils.DATE_PATTERN);
            Date endMonth = DateUtils.parse(DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-"
                    + DateUtils.getMonth(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-01", DateUtils.DATE_PATTERN);
            DMGC_Y_D_TSZ_NEW temp;
            for (String zmc : zmcList) {
                for (Date i = beginMonth; i.compareTo(endMonth) <= 0; i = DateUtils.addMonth(i, 1)) {
                    temp = new DMGC_Y_D_TSZ_NEW();
                    temp.setStationName(zmc);
                    Date sumStart = i;
                    Date sumEnd = DateUtils.parse(DateUtils.getLastDay(i) + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                    temp.setRq(sumEnd);
                    int syhdl = dataList.stream()
                            .filter(c -> c.getStationName().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                            .mapToInt(DMGC_Y_D_TSZ_NEW::getSyhdl).sum();
                    int wsyl = dataList.stream()
                            .filter(c -> c.getStationName().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                            .mapToInt(DMGC_Y_D_TSZ_NEW::getWsyl).sum();
                    int wgwsl = dataList.stream()
                            .filter(c -> c.getStationName().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                            .mapToInt(DMGC_Y_D_TSZ_NEW::getWgwsl).sum();
                    int hql = dataList.stream()
                            .filter(c -> c.getStationName().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                            .mapToInt(DMGC_Y_D_TSZ_NEW::getHql).sum();
                    int zhhdl = dataList.stream()
                            .filter(c -> c.getStationName().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                            .mapToInt(DMGC_Y_D_TSZ_NEW::getZhhdl).sum();
                    temp.setDyehd(Calculation.getDivisionResult(syhdl, wsyl + wgwsl));
                    temp.setDyohd(Calculation.getDivisionResult(syhdl, wsyl));
                    temp.setDyehq(Calculation.getDivisionResult(hql, wsyl + wgwsl));
                    temp.setDyohq(Calculation.getDivisionResult(hql, wsyl));
                    temp.setZhdh(Calculation.getDivisionResult(zhhdl, wsyl));
                    result.add(temp);
                }
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    public List<DMGC_Y_D_TSZ_NEW> getSumDataOfYear(String startDate, String endDate) {
        List<DMGC_Y_D_TSZ_NEW> dataList = getDataOfYear(startDate, endDate);
        List<String> zmcList = dataList.stream().map(c -> c.getStationName()).distinct().collect(Collectors.toList());
        List<DMGC_Y_D_TSZ_NEW> result = new ArrayList<>();
        try {
            int beginYear = DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN));
            int endYear = DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN));
            DMGC_Y_D_TSZ_NEW temp;
            for (String zmc : zmcList) {
                for (int i = beginYear; i <= endYear; i++) {
                    temp = new DMGC_Y_D_TSZ_NEW();
                    temp.setStationName(zmc);
                    Date sumStart = DateUtils.parse(i + "-01-01", DateUtils.DATE_PATTERN);
                    Date sumEnd = DateUtils.parse(i + "-12-31 23:59:59", DateUtils.DATE_TIME_PATTERN);
                    temp.setRq(sumEnd);
                    int syhdl = dataList.stream()
                            .filter(c -> c.getStationName().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                            .mapToInt(DMGC_Y_D_TSZ_NEW::getSyhdl).sum();
                    int wsyl = dataList.stream()
                            .filter(c -> c.getStationName().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                            .mapToInt(DMGC_Y_D_TSZ_NEW::getWsyl).sum();
                    int wgwsl = dataList.stream()
                            .filter(c -> c.getStationName().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                            .mapToInt(DMGC_Y_D_TSZ_NEW::getWgwsl).sum();
                    int hql = dataList.stream()
                            .filter(c -> c.getStationName().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                            .mapToInt(DMGC_Y_D_TSZ_NEW::getHql).sum();
                    int zhhdl = dataList.stream()
                            .filter(c -> c.getStationName().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                            .mapToInt(DMGC_Y_D_TSZ_NEW::getZhhdl).sum();
                    temp.setDyehd(Calculation.getDivisionResult(syhdl, wsyl + wgwsl));
                    temp.setDyohd(Calculation.getDivisionResult(syhdl, wsyl));
                    temp.setDyehq(Calculation.getDivisionResult(hql, wsyl + wgwsl));
                    temp.setDyohq(Calculation.getDivisionResult(hql, wsyl));
                    temp.setZhdh(Calculation.getDivisionResult(zhhdl, wsyl));
                    result.add(temp);
                }
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    public ChartOption getStatistics(String startDate, String endDate, String cycle, String type) {
        List<DMGC_Y_D_TSZ_NEW> dmgcYDTszNews = new ArrayList<>();
        switch (cycle) {
            case "日":
                dmgcYDTszNews = getDataOfDay(startDate, endDate);
                break;
            case "年":
                dmgcYDTszNews = getSumDataOfYear(startDate, endDate);
                break;
            case "月":
                dmgcYDTszNews = getSumDataOfMonth(startDate, endDate);
                break;
        }
        dmgcYDTszNews = dmgcYDTszNews.stream().sorted(Comparator.comparing(DMGC_Y_D_TSZ_NEW::getRq).thenComparing(DMGC_Y_D_TSZ_NEW::getStationName)).collect(Collectors.toList());
        ChartOption result = new ChartOption();
        result.setLegend(dmgcYDTszNews.stream().map(c -> c.getStationName()).distinct().collect(Collectors.toList()));
        Collections.sort(result.getLegend());
        result.setXAxis(dmgcYDTszNews.stream().map(c -> DateUtils.format(c.getRq(), DateUtils.DATE_PATTERN)).distinct().collect(Collectors.toList()));
        Collections.sort(result.getXAxis());
        if (type.equals("dyehd")) {
            result.setTitle("吨液耗电");
        }
        if (type.equals("dyohd")) {
            result.setTitle("吨油耗电");
        }
        if (type.equals("dyehq")) {
            result.setTitle("吨液耗气");
        }
        if (type.equals("dyohq")) {
            result.setTitle("吨油耗气");
        }
        if (type.equals("zhdh")) {
            result.setTitle("综合单耗");
        }

        try {
            for (String legend : result.getLegend()) {
                ChartOption.Serie mySerie = result.new Serie();
                mySerie.setName(legend);
                mySerie.setType("line");
                mySerie.setStack("总量");
                List<DMGC_Y_D_TSZ_NEW> temp = dmgcYDTszNews.stream().filter(c -> c.getStationName().equals(legend)).collect(Collectors.toList());
                for (String date : result.getXAxis()) {
                    Date d = DateUtils.parse(date, DateUtils.DATE_PATTERN);
                    Optional<DMGC_Y_D_TSZ_NEW> optional = temp.stream().filter(c -> {
                        try {
                            return DateUtils.parse(DateUtils.format(c.getRq(), DateUtils.DATE_PATTERN), DateUtils.DATE_PATTERN).getTime() == d.getTime();
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }).findFirst();
                    if (type.equals("dyehd")) {
                        optional.ifPresent(c -> mySerie.getData().add(c.getDyehd()));
                    }
                    if (type.equals("dyohd")) {
                        optional.ifPresent(c -> mySerie.getData().add(c.getDyohd()));
                    }
                    if (type.equals("dyehq")) {
                        optional.ifPresent(c -> mySerie.getData().add(c.getDyehq()));
                    }
                    if (type.equals("dyohq")) {
                        optional.ifPresent(c -> mySerie.getData().add(c.getDyohq()));
                    }
                    if (type.equals("zhdh")) {
                        optional.ifPresent(c -> mySerie.getData().add(c.getZhdh()));
                    }
                    if (!optional.isPresent()) {
                        mySerie.getData().add((double) 0);
                    }
                }
                result.getSeries().add(mySerie);
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    public List<DMGC_Y_D_TSZ_NEW> getDataForZYQ(String cycle, Date assessmentDate) {
        LambdaQueryWrapper<DMGC_Y_D_TSZ_NEW> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(DMGC_Y_D_TSZ_NEW::getJxpjScore);
        wrapper.eq(DMGC_Y_D_TSZ_NEW::getRq, assessmentDate);
        wrapper.and(item -> item.isNotNull(DMGC_Y_D_TSZ_NEW::getZhdh)
                .or().isNotNull(DMGC_Y_D_TSZ_NEW::getDyohd)
                .or().isNotNull(DMGC_Y_D_TSZ_NEW::getDyohq)
                .or().isNotNull(DMGC_Y_D_TSZ_NEW::getDyehd)
                .or().isNotNull(DMGC_Y_D_TSZ_NEW::getDyehq)
                .or().ne(DMGC_Y_D_TSZ_NEW::getZhdh, 0)
                .or().ne(DMGC_Y_D_TSZ_NEW::getDyohd, 0)
                .or().ne(DMGC_Y_D_TSZ_NEW::getDyohq, 0)
                .or().ne(DMGC_Y_D_TSZ_NEW::getDyehd, 0)
                .or().ne(DMGC_Y_D_TSZ_NEW::getDyehq, 0));
        return mapper.selectList(wrapper);
    }

    public List<DMGC_Y_D_TSZ_NEW> getDataForPortrait(String zid, Date queryStart, Date queryEnd) {
        LambdaQueryWrapper<DMGC_Y_D_TSZ_NEW> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(zid)) {
            wrapper.eq(DMGC_Y_D_TSZ_NEW::getZid, zid);
        }
        wrapper.isNotNull(DMGC_Y_D_TSZ_NEW::getJxpjScore);
        wrapper.ge(DMGC_Y_D_TSZ_NEW::getRq, queryStart);
        wrapper.le(DMGC_Y_D_TSZ_NEW::getRq, queryEnd);
        return mapper.selectList(wrapper);
    }

    public List<DMGC_Y_D_TSZ_NEW> getEffectiveDataOfDay(String queryDate) {
        List<DMGC_Y_D_TSZ_NEW> result = new ArrayList<>();
        try {
            Date date = DateUtils.parse(queryDate, DateUtils.DATE_PATTERN);
            LambdaQueryWrapper<DMGC_Y_D_TSZ_NEW> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DMGC_Y_D_TSZ_NEW::getRq, date);
            wrapper.and(item -> item.isNotNull(DMGC_Y_D_TSZ_NEW::getZhdh)
                    .or().isNotNull(DMGC_Y_D_TSZ_NEW::getDyohd)
                    .or().isNotNull(DMGC_Y_D_TSZ_NEW::getDyohq)
                    .or().isNotNull(DMGC_Y_D_TSZ_NEW::getDyehd)
                    .or().isNotNull(DMGC_Y_D_TSZ_NEW::getDyehq)
                    .or().ne(DMGC_Y_D_TSZ_NEW::getZhdh, 0)
                    .or().ne(DMGC_Y_D_TSZ_NEW::getDyohd, 0)
                    .or().ne(DMGC_Y_D_TSZ_NEW::getDyohq, 0)
                    .or().ne(DMGC_Y_D_TSZ_NEW::getDyehd, 0)
                    .or().ne(DMGC_Y_D_TSZ_NEW::getDyehq, 0));
            result = mapper.selectList(wrapper);
            return result;
        } catch (Exception ex) {
            return result;
        }
    }
}
