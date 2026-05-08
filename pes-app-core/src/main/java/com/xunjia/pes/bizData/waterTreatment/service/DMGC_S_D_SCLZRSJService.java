package com.xunjia.pes.bizData.waterTreatment.service;

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
import com.xunjia.pes.bizData.assessment.service.BenchmarkService;
import com.xunjia.pes.bizData.assessment.service.MonitoringIndicatorNewService;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_SYB;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_ZYZ;
import com.xunjia.pes.bizData.waterInjection.entity.Statistics_D_ZSZ;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_D_SCLZRSJ;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_SCLZ;
import com.xunjia.pes.bizData.waterTreatment.mapper.DMGC_S_D_SCLZRSJMapper;
import com.xunjia.pes.score.Calculation;
import lombok.Data;
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
public class DMGC_S_D_SCLZRSJService extends ServiceImpl<DMGC_S_D_SCLZRSJMapper, DMGC_S_D_SCLZRSJ> {

    @Autowired
    private DMGC_S_D_SCLZRSJMapper mapper;
    @Autowired
    private DMGC_S_SCLZService service;

    @Autowired
    private BenchmarkService benchmarkService;

    @Autowired
    private MonitoringIndicatorNewService monitoringIndicatorNewService;

    @Autowired
    private IndicatorsService indicatorsService;

    @Autowired
    @Lazy(true)
    private SCLZ_JXPJService sclzJxpjService;

    public Boolean saveData(String id, Double dh, Double rhdl, Double rwssl) {
        try {
            DMGC_S_D_SCLZRSJ record = mapper.selectById(id);
            record.setDh(dh);
            record.setRhdl(rhdl);
            record.setRwssl(rwssl);
            mapper.updateById(record);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Boolean auditData(String rq) {
        try {
            LambdaQueryWrapper<DMGC_S_D_SCLZRSJ> wrapper = new LambdaQueryWrapper<>();
            Date startDate = DateUtils.parse(rq, DateUtils.DATE_PATTERN);
            wrapper.ge(DMGC_S_D_SCLZRSJ::getRq, startDate);
            Date endDate = DateUtils.parse(rq + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
            wrapper.le(DMGC_S_D_SCLZRSJ::getRq, endDate);
            wrapper.isNotNull(DMGC_S_D_SCLZRSJ::getDh);
            wrapper.isNotNull(DMGC_S_D_SCLZRSJ::getRhdl);
            wrapper.isNotNull(DMGC_S_D_SCLZRSJ::getRwssl);
            wrapper.ne(DMGC_S_D_SCLZRSJ::getDh, 0);
            wrapper.ne(DMGC_S_D_SCLZRSJ::getRhdl, 0);
            wrapper.ne(DMGC_S_D_SCLZRSJ::getRwssl, 0);
            List<DMGC_S_D_SCLZRSJ> dmgcSDSclzList = mapper.selectList(wrapper);
            if (dmgcSDSclzList.size() != 0) {
                getCompleteData(dmgcSDSclzList);
                this.updateBatchById(dmgcSDSclzList);
                sclzJxpjService.updateData(rq);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public PageVO<DMGC_S_D_SCLZRSJ> getPageData(DMGC_S_D_SCLZRSJ example, String startDate, String endDate, int page, int size) {
        PageVO<DMGC_S_D_SCLZRSJ> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_S_D_SCLZRSJ> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            PageInfo<DMGC_S_D_SCLZRSJ> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e) {
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    private LambdaQueryWrapper<DMGC_S_D_SCLZRSJ> buildQueryWrapper(DMGC_S_D_SCLZRSJ example, String startDate, String endDate) {
        LambdaQueryWrapper<DMGC_S_D_SCLZRSJ> queryWrapper = new LambdaQueryWrapper<>();
        if (example != null) {
            if (StringUtils.isNotEmpty(example.getZmc())) {
                queryWrapper.like(DMGC_S_D_SCLZRSJ::getZmc, example.getZmc());
            }
        }
        if (!StringUtils.isEmpty(startDate)) {
            try {
                Date date = DateUtils.parse(startDate, DateUtils.DATE_PATTERN);
                queryWrapper.ge(DMGC_S_D_SCLZRSJ::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!StringUtils.isEmpty(endDate)) {
            try {
                Date date = DateUtils.parse(endDate + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                queryWrapper.le(DMGC_S_D_SCLZRSJ::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        queryWrapper.orderByDesc(DMGC_S_D_SCLZRSJ::getRq);
        return queryWrapper;
    }

    public void exportData(DMGC_S_D_SCLZRSJ example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response) {

        Workbook workbook = ExportUtils.createWorkbook();
        try {
            List<DMGC_S_D_SCLZRSJ> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
//            if (!ListUtils.isListEmpty(dataList)) {
//                List<String> eventIds = dataList.stream().map(DMGC_S_D_SCLZRSJ::getZkEventId).collect(Collectors.toList());
//                List<DMGC_S_SCLZ> zszDataList = service.getByEventIds(eventIds);
//                for (DMGC_S_D_SCLZRSJ rsj : dataList) {
//                    Optional<DMGC_S_SCLZ> zszOptional = zszDataList.stream().filter(c -> c.getEventId().equals(rsj.getZkEventId())).findFirst();
//                    zszOptional.ifPresent(c -> rsj.setZmc(c.getMc()));
//                }
//            }

            Map<String, List<String>> headerNamesAndFieldsMap = ExportUtils.getHeaderNamesAndFields(DMGC_S_D_SCLZRSJ.class);
            List<String> headerNames = headerNamesAndFieldsMap.get("headerNames");
            List<String> fields = headerNamesAndFieldsMap.get("fields");
            ExportUtils.exportExcel("注水站运行数据", headerNames, fields, 0, workbook, dataList);
            ExportUtils.responseWorkbook("注水站运行数据", workbook, request, response);
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

    public ChartOption getStatistics(String startDate, String endDate, String cycle, String type) {
        List<Statistics_D_ZSZ> statisticsDZszs = new ArrayList<>();
        switch (cycle) {
            case "年":
                statisticsDZszs = getStatisticsOfYear(startDate, endDate, type);
                break;
            case "月":
                statisticsDZszs = getStatisticsOfMonth(startDate, endDate, type);
                break;
        }
        ChartOption result = new ChartOption();
        if (type.equals("zhhdl")) {
            result.setTitle("综合耗电量");
        }
        if (type.equals("dh")) {
            result.setTitle("综合单耗");
        }
        if (type.equals("wswsdh")) {
            result.setTitle("污水单耗");
        }
        result.setLegend(statisticsDZszs.stream().map(c -> c.getStatisticsName()).distinct().collect(Collectors.toList()));
        result.setXAxis(statisticsDZszs.stream().map(c -> c.getStatisticsDate()).distinct().collect(Collectors.toList()));
        for (String legend : result.getLegend()) {
            ChartOption.Serie mySerie = result.new Serie();
            mySerie.setName(legend);
            mySerie.setType("line");
            mySerie.setStack("总量");
            List<Statistics_D_ZSZ> temp = statisticsDZszs.stream().filter(c -> c.getStatisticsName().equals(legend)).collect(Collectors.toList());
            mySerie.setData(temp.stream().map(c -> c.getStatisticsValue()).collect(Collectors.toList()));
            result.getSeries().add(mySerie);
        }
        return result;
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
            LambdaQueryWrapper<DMGC_S_D_SCLZRSJ> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DMGC_S_D_SCLZRSJ::getRq, updateDate);
            List<DMGC_S_D_SCLZRSJ> dataList = mapper.selectList(wrapper);
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

    public Boolean updateData(List<DMGC_S_D_SCLZRSJ> params) {
        return this.updateBatchById(params);
    }

    private List<DMGC_S_D_SCLZRSJ> getDataOfDay(String startDate, String endDate) {
        List<DMGC_S_D_SCLZRSJ> dataList = mapper.selectList(this.buildQueryWrapper(null, startDate, endDate));
        return dataList;
    }

    private void getCompleteData(List<DMGC_S_D_SCLZRSJ> partData) {
        if (partData.size() == 0) {
            return;
        }
        List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("水处理站监测项目与指标要求", null);
        List<String> zszIds = partData.stream().map(c -> c.getZkEventId()).distinct().collect(Collectors.toList());
        List<DMGC_S_SCLZ> sclzList = service.getByEventIds(zszIds);
        //水处理站指标权重
        List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("sclz")).collect(Collectors.toList());
        for (DMGC_S_D_SCLZRSJ param : partData) {
            Optional<DMGC_S_SCLZ> optional = sclzList.stream().filter(c -> c.getEventId().equals(param.getZkEventId())).findFirst();
            optional.ifPresent(zsz -> {
                param.setZmc(zsz.getMc());
                MonitoringIndicatorNew monitoringIndicatorNew = filterByQueryData(0, monitoringIndicatorNewList, "水处理站单耗");
                param.setDhWeight(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level2") && d.getItemCode().equals("sclzdh")).collect(Collectors.toList()).get(0).getWeight());
                if (monitoringIndicatorNew != null && param.getDh() != null) {
                    param.setDhScore(Calculation.calculationOfUnitConsumption(param.getDh(), monitoringIndicatorNew));
                    param.setDhWeightScore(Calculation.getMultiplicationResult(param.getDhScore(), param.getDhWeight()));
                    param.setDhPj(Calculation.getUnitConsumptionComment(param.getDhScore(), monitoringIndicatorNew));
                }

                monitoringIndicatorNew = filterByQueryData(0, monitoringIndicatorNewList, "外输污水单耗");
                param.setWswsdhWeight(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level2") && d.getItemCode().equals("wswsdh")).collect(Collectors.toList()).get(0).getWeight());
                if (monitoringIndicatorNew != null && param.getRhdl() != null && param.getRwssl() != null) {
                    param.setWswsdh(Calculation.getDivisionResult(param.getRhdl(), param.getRwssl()));
                    param.setWswsdhScore(Calculation.calculationOfUnitConsumption(param.getWswsdh(), monitoringIndicatorNew));
                    param.setWswsdhWeightScore(Calculation.getMultiplicationResult(param.getWswsdhScore(), param.getWswsdhWeight()));
                    param.setWswsdhPj(Calculation.getUnitConsumptionComment(param.getWswsdhScore(), monitoringIndicatorNew));
                }
            });
        }
    }

    private List<DMGC_S_D_SCLZRSJ> getDataOfMonth(String startDate, String endDate) {
        List<DMGC_S_D_SCLZRSJ> dataList = new ArrayList<>();
        String queryStart = startDate;
        String queryEnd = endDate;
        try {
            DMGC_S_SCLZ dmgc_s_zsz = new DMGC_S_SCLZ();
            List<DMGC_S_SCLZ> zszList = service.getPageData(dmgc_s_zsz, 1, 9999).getRows();
            queryStart = DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getMonth(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-01";
            queryEnd = DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getMonth(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getDay(DateUtils.parse(DateUtils.getLastDay(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)), DateUtils.DATE_PATTERN));
            dataList = mapper.selectList(this.buildQueryWrapper(null, queryStart, queryEnd));
            for (DMGC_S_D_SCLZRSJ param : dataList) {
                DMGC_S_SCLZ dmgcSZsz = zszList.stream().filter(c -> c.getEventId().equals(param.getZkEventId())).findFirst().get();
                if (dmgcSZsz != null) {
                    param.setZmc(dmgcSZsz.getMc());
                }
                double zhhdl = param.getRhdl();
                double rwssl = param.getRwssl();
                param.setWswsdh(Calculation.getDivisionResult(zhhdl, rwssl));
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return dataList;
    }

    private List<DMGC_S_D_SCLZRSJ> getDataOfYear(String startDate, String endDate) {
        List<DMGC_S_D_SCLZRSJ> dataList = new ArrayList<>();
        String queryStart = startDate;
        String queryEnd = endDate;
        try {
            DMGC_S_SCLZ dmgc_s_zsz = new DMGC_S_SCLZ();
            List<DMGC_S_SCLZ> zszList = service.getPageData(dmgc_s_zsz, 1, 9999).getRows();
            queryStart = DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-01-01";
            queryEnd = DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-12-31";
            dataList = mapper.selectList(this.buildQueryWrapper(null, queryStart, queryEnd));
            for (DMGC_S_D_SCLZRSJ param : dataList) {
                DMGC_S_SCLZ dmgcSZsz = zszList.stream().filter(c -> c.getEventId().equals(param.getZkEventId())).findFirst().get();
                if (dmgcSZsz != null) {
                    param.setZmc(dmgcSZsz.getMc());
                }
                double zhhdl = param.getRhdl();
                double rwssl = param.getRwssl();
                param.setWswsdh(Calculation.getDivisionResult(zhhdl, rwssl));
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return dataList;
    }

    private List<Statistics_D_ZSZ> getStatisticsOfYear(String startDate, String endDate, String type) {
        List<Statistics_D_ZSZ> series = new ArrayList<>();
        Statistics_D_ZSZ temp;
        try {
            List<DMGC_S_D_SCLZRSJ> dataList = getDataOfYear(startDate, endDate);
            List<String> zmcList = dataList.stream().map(c -> c.getZmc()).distinct().collect(Collectors.toList());
            int beginYear = DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN));
            int endYear = DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN));
            for (String zmc : zmcList) {
                for (int i = beginYear; i <= endYear; i++) {
                    Date sumStart = DateUtils.parse(i + "-01-01", DateUtils.DATE_PATTERN);
                    Date sumEnd = DateUtils.parse(i + "-12-31 23:59:59", DateUtils.DATE_TIME_PATTERN);
                    temp = new Statistics_D_ZSZ();
                    temp.setStatisticsName(zmc);
                    temp.setStatisticsDate(String.valueOf(i));
                    if (type.equals("zhhdl")) {
                        temp.setStatisticsValue(dataList.stream()
                                .filter(c -> c.getZmc().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                                .mapToDouble(DMGC_S_D_SCLZRSJ::getRhdl).sum());
                    }
                    if (type.equals("wswsdh")) {
                        double zhhdl = dataList.stream()
                                .filter(c -> c.getZmc().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                                .mapToDouble(DMGC_S_D_SCLZRSJ::getRhdl).sum();
                        double rwssl = dataList.stream()
                                .filter(c -> c.getZmc().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                                .mapToDouble(DMGC_S_D_SCLZRSJ::getRwssl).sum();
                        temp.setStatisticsValue(Calculation.getDivisionResult(zhhdl, rwssl));
                    }
                    if (type.equals("dh")) {
                        double zhhdl = dataList.stream()
                                .filter(c -> c.getZmc().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                                .mapToDouble(DMGC_S_D_SCLZRSJ::getRhdl).sum();
                        double clsl = dataList.stream()
                                .filter(c -> c.getZmc().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                                .mapToDouble(DMGC_S_D_SCLZRSJ::getRclsl).sum();
                        temp.setStatisticsValue(Calculation.getDivisionResult(zhhdl, clsl));
                    }
                    series.add(temp);
                }
                if (series.stream().filter(c -> c.getStatisticsName().equals(zmc)).mapToDouble(Statistics_D_ZSZ::getStatisticsValue).sum() == 0) {
                    series.removeAll(series.stream().filter(c -> c.getStatisticsName().equals(zmc)).collect(Collectors.toList()));
                }
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return series;
    }

    private List<Statistics_D_ZSZ> getStatisticsOfMonth(String startDate, String endDate, String type) {
        List<Statistics_D_ZSZ> series = new ArrayList<>();
        Statistics_D_ZSZ temp;
        try {
            List<DMGC_S_D_SCLZRSJ> dataList = getDataOfMonth(startDate, endDate);
            List<String> zmcList = dataList.stream().map(c -> c.getZmc()).distinct().collect(Collectors.toList());
            Date beginMonth = DateUtils.parse(DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-"
                    + DateUtils.getMonth(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-01", DateUtils.DATE_PATTERN);
            Date endMonth = DateUtils.parse(DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-"
                    + DateUtils.getMonth(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-01", DateUtils.DATE_PATTERN);
            for (String zmc : zmcList) {
                for (Date i = beginMonth; i.compareTo(endMonth) <= 0; i = DateUtils.addMonth(i, 1)) {
                    Date sumStart = i;
                    Date sumEnd = DateUtils.parse(DateUtils.getLastDay(i) + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                    temp = new Statistics_D_ZSZ();
                    temp.setStatisticsName(zmc);
                    temp.setStatisticsDate(DateUtils.format(i, DateUtils.MONTH_PATTERN));
                    Date finalI = i;
                    if (type.equals("zhhdl")) {
                        temp.setStatisticsValue(dataList.stream()
                                .filter(c -> c.getZmc().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                                .mapToDouble(DMGC_S_D_SCLZRSJ::getRhdl).sum());
                    }
                    if (type.equals("wswsdh")) {
                        double zhhdl = dataList.stream()
                                .filter(c -> c.getZmc().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                                .mapToDouble(DMGC_S_D_SCLZRSJ::getRhdl).sum();
                        double rwssl = dataList.stream()
                                .filter(c -> c.getZmc().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                                .mapToDouble(DMGC_S_D_SCLZRSJ::getRwssl).sum();
                        temp.setStatisticsValue(Calculation.getDivisionResult(zhhdl, rwssl));
                    }
                    if (type.equals("dh")) {
                        double zhhdl = dataList.stream()
                                .filter(c -> c.getZmc().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                                .mapToDouble(DMGC_S_D_SCLZRSJ::getRhdl).sum();
                        double clsl = dataList.stream()
                                .filter(c -> c.getZmc().equals(zmc) && c.getRq().getTime() >= sumStart.getTime() && c.getRq().getTime() <= sumEnd.getTime())
                                .mapToDouble(DMGC_S_D_SCLZRSJ::getRclsl).sum();
                        temp.setStatisticsValue(Calculation.getDivisionResult(zhhdl, clsl));
                    }
                    series.add(temp);
                }
                if (series.stream().filter(c -> c.getStatisticsName().equals(zmc)).mapToDouble(Statistics_D_ZSZ::getStatisticsValue).sum() == 0) {
                    series.removeAll(series.stream().filter(c -> c.getStatisticsName().equals(zmc)).collect(Collectors.toList()));
                }
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return series;
    }

    public PageVO<DMGC_S_D_SCLZRSJ> getAssessment(String cycle, String assessmentDate, int page, int size) {
        PageVO<DMGC_S_D_SCLZRSJ> pageVO = null;
        List<DMGC_S_D_SCLZRSJ> tempResult = new ArrayList<>();
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
            List<DMGC_S_D_SCLZRSJ> dataList = tempResult;
            if (cycle.equals("月") || cycle.equals("年")) {
//                dataList = buildAssessment(tempResult, sumStart, sumEnd);
            }

            dataList = dataList.stream().sorted(Comparator.comparing(DMGC_S_D_SCLZRSJ::getZmc)).collect(Collectors.toList());
//            PageMethod.startPage(page, size);
            PageInfo<DMGC_S_D_SCLZRSJ> pageInfo = PageInfo.of(dataList);
            List<DMGC_S_D_SCLZRSJ> pageList = dataList.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
            pageVO = new PageVO<>(pageInfo.getTotal(), pageList);
            return pageVO;
        } catch (Exception ex) {
            String err = ex.getMessage();
            return new PageVO<>();
        }
    }

    public List<DMGC_S_D_SCLZRSJ> getAssessmentNoPage(String cycle, String assessmentDate) {
        List<DMGC_S_D_SCLZRSJ> result = new ArrayList<>();
        List<DMGC_S_D_SCLZRSJ> tempResult = new ArrayList<>();
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

            result = tempResult;
            if (cycle.equals("月") || cycle.equals("年")) {
//                dataList = buildAssessment(tempResult, sumStart, sumEnd);
            }
            return result;
        } catch (Exception ex) {
            String err = ex.getMessage();
            return result;
        }
    }

    public List<DMGC_S_D_SCLZRSJ> getDataForZYQ(String cycle, Date assessmentDate) {
        LambdaQueryWrapper<DMGC_S_D_SCLZRSJ> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(DMGC_S_D_SCLZRSJ::getJxpjScore);
        wrapper.eq(DMGC_S_D_SCLZRSJ::getRq, assessmentDate);
        wrapper.and(item -> item.isNotNull(DMGC_S_D_SCLZRSJ::getDh)
                .or().isNotNull(DMGC_S_D_SCLZRSJ::getRhdl)
                .or().isNotNull(DMGC_S_D_SCLZRSJ::getRwssl)
                .or().ne(DMGC_S_D_SCLZRSJ::getDh, 0)
                .or().ne(DMGC_S_D_SCLZRSJ::getRhdl, 0)
                .or().ne(DMGC_S_D_SCLZRSJ::getRwssl, 0));
        return mapper.selectList(wrapper);
    }

    public List<DMGC_S_D_SCLZRSJ> getDataForPortrait(String zid, Date queryStart, Date queryEnd) {
        LambdaQueryWrapper<DMGC_S_D_SCLZRSJ> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(zid)) {
            wrapper.eq(DMGC_S_D_SCLZRSJ::getZkEventId, zid);
        }
        wrapper.isNotNull(DMGC_S_D_SCLZRSJ::getJxpjScore);
        wrapper.ge(DMGC_S_D_SCLZRSJ::getRq, queryStart);
        wrapper.le(DMGC_S_D_SCLZRSJ::getRq, queryEnd);
        return mapper.selectList(wrapper);
    }

    public List<DMGC_S_D_SCLZRSJ> getEffectiveDataOfDay(String queryDate) {
        List<DMGC_S_D_SCLZRSJ> result = new ArrayList<>();
        try {
            Date date = DateUtils.parse(queryDate, DateUtils.DATE_PATTERN);
            LambdaQueryWrapper<DMGC_S_D_SCLZRSJ> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DMGC_S_D_SCLZRSJ::getRq, date);
            wrapper.and(item -> item.isNotNull(DMGC_S_D_SCLZRSJ::getDh)
                    .or().isNotNull(DMGC_S_D_SCLZRSJ::getRhdl)
                    .or().isNotNull(DMGC_S_D_SCLZRSJ::getRwssl)
                    .or().ne(DMGC_S_D_SCLZRSJ::getDh, 0)
                    .or().ne(DMGC_S_D_SCLZRSJ::getRhdl, 0)
                    .or().ne(DMGC_S_D_SCLZRSJ::getRwssl, 0));
            result = mapper.selectList(wrapper);
            return result;
        } catch (Exception ex) {
            return result;
        }
    }

    public List<DMGC_S_D_SCLZRSJ> getEffectiveData(Date queryStartDate, Date queryEndDate) {
        List<DMGC_S_D_SCLZRSJ> result = new ArrayList<>();
        try {
            LambdaQueryWrapper<DMGC_S_D_SCLZRSJ> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(DMGC_S_D_SCLZRSJ::getRq, queryStartDate);
            wrapper.le(DMGC_S_D_SCLZRSJ::getRq, queryEndDate);
            wrapper.and(item -> item.isNotNull(DMGC_S_D_SCLZRSJ::getDh)
                    .or().isNotNull(DMGC_S_D_SCLZRSJ::getRhdl)
                    .or().isNotNull(DMGC_S_D_SCLZRSJ::getRwssl)
                    .or().ne(DMGC_S_D_SCLZRSJ::getDh, 0)
                    .or().ne(DMGC_S_D_SCLZRSJ::getRhdl, 0)
                    .or().ne(DMGC_S_D_SCLZRSJ::getRwssl, 0));
            result = mapper.selectList(wrapper);
            return result;
        } catch (Exception ex) {
            return result;
        }
    }
}
