package com.xunjia.pes.bizData.waterInjection.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.framework.utils.excel.ExportUtils;
import com.xunjia.pes.basicDataManage.entity.Indicators;
import com.xunjia.pes.basicDataManage.service.IndicatorsService;
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.assessment.entity.MonitoringIndicatorNew;
import com.xunjia.pes.bizData.assessment.service.BenchmarkService;
import com.xunjia.pes.bizData.assessment.service.MonitoringIndicatorNewService;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSBRSJ;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSZRSJ;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_ZSZ;
import com.xunjia.pes.bizData.waterInjection.entity.Statistics_D_ZSZ;
import com.xunjia.pes.bizData.waterInjection.mapper.DMGC_S_D_ZSZRSJMapper;
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
public class DMGC_S_D_ZSZRSJService extends ServiceImpl<DMGC_S_D_ZSZRSJMapper, DMGC_S_D_ZSZRSJ> {

    @Autowired
    private DMGC_S_D_ZSZRSJMapper mapper;
    @Autowired
    private DMGC_S_ZSZService zszService;
    @Autowired
    private BenchmarkService benchmarkService;
    @Autowired
    private IndicatorsService indicatorsService;
    @Autowired
    private MonitoringIndicatorNewService monitoringIndicatorNewService;
    @Autowired
    private DMGC_S_D_ZSBRSJService dmgc_s_d_zsbrsjService;
    @Autowired
    @Lazy
    private ZSZ_JXPJService zszJxpjService;

    public Boolean saveData(String id, Double zhdh) {
        try {
            DMGC_S_D_ZSZRSJ record = mapper.selectById(id);
            record.setZhdh(zhdh);
            mapper.updateById(record);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Boolean auditData(String rq) {
        try {
            LambdaQueryWrapper<DMGC_S_D_ZSZRSJ> wrapper = new LambdaQueryWrapper<>();
            Date startDate = DateUtils.parse(rq, DateUtils.DATE_PATTERN);
            wrapper.ge(DMGC_S_D_ZSZRSJ::getRq, startDate);
            Date endDate = DateUtils.parse(rq + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
            wrapper.le(DMGC_S_D_ZSZRSJ::getRq, endDate);
            wrapper.isNotNull(DMGC_S_D_ZSZRSJ::getZhdh);
            wrapper.ne(DMGC_S_D_ZSZRSJ::getZhdh, 0);
            List<DMGC_S_D_ZSZRSJ> dmgcSDZszList = mapper.selectList(wrapper);
            if (dmgcSDZszList.size() != 0) {
                getCompleteData(dmgcSDZszList);
                this.updateBatchById(dmgcSDZszList);
                zszJxpjService.updateData(rq);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public PageVO<DMGC_S_D_ZSZRSJ> getPageData(DMGC_S_D_ZSZRSJ example, String startDate, String endDate, int page, int size) {
        PageVO<DMGC_S_D_ZSZRSJ> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_S_D_ZSZRSJ> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            if (!ListUtils.isListEmpty(dataList)) {
                List<String> eventIds = dataList.stream().map(DMGC_S_D_ZSZRSJ::getZid).collect(Collectors.toList());
                List<DMGC_S_ZSZ> zszDataList = zszService.getByEventIds(eventIds);
                for (DMGC_S_D_ZSZRSJ rsj : dataList) {
                    Optional<DMGC_S_ZSZ> zszOptional = zszDataList.stream().filter(c -> c.getEventId().equals(rsj.getZid())).findFirst();
                    zszOptional.ifPresent(c -> rsj.setZmc(c.getMc()));
                }
            }
            PageInfo<DMGC_S_D_ZSZRSJ> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e) {
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    public void exportData(DMGC_S_D_ZSZRSJ example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response) {

        Workbook workbook = ExportUtils.createWorkbook();
        try {
            List<DMGC_S_D_ZSZRSJ> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            if (!ListUtils.isListEmpty(dataList)) {
                List<String> eventIds = dataList.stream().map(DMGC_S_D_ZSZRSJ::getZid).collect(Collectors.toList());
                List<DMGC_S_ZSZ> zszDataList = zszService.getByEventIds(eventIds);
                for (DMGC_S_D_ZSZRSJ rsj : dataList) {
                    Optional<DMGC_S_ZSZ> zszOptional = zszDataList.stream().filter(c -> c.getEventId().equals(rsj.getZid())).findFirst();
                    zszOptional.ifPresent(c -> rsj.setZmc(c.getMc()));
                }
            }

            Map<String, List<String>> headerNamesAndFieldsMap = ExportUtils.getHeaderNamesAndFields(DMGC_S_D_ZSZRSJ.class);
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

    private LambdaQueryWrapper<DMGC_S_D_ZSZRSJ> buildQueryWrapper(DMGC_S_D_ZSZRSJ example, String startDate, String endDate) {
        LambdaQueryWrapper<DMGC_S_D_ZSZRSJ> queryWrapper = new LambdaQueryWrapper<>();
        if (example != null) {
            if (StringUtils.isNotEmpty(example.getZmc())) {
                queryWrapper.like(DMGC_S_D_ZSZRSJ::getZmc, example.getZmc());
            }
        }
        if (!StringUtils.isEmpty(startDate)) {
            try {
                Date date = DateUtils.parse(startDate, DateUtils.DATE_PATTERN);
                queryWrapper.ge(DMGC_S_D_ZSZRSJ::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!StringUtils.isEmpty(endDate)) {
            try {
                Date date = DateUtils.parse(endDate + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                queryWrapper.le(DMGC_S_D_ZSZRSJ::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        queryWrapper.orderByDesc(DMGC_S_D_ZSZRSJ::getRq);
        return queryWrapper;
    }

    public ChartOption getStatistics(String startDate, String endDate, String cycle, String type) {
        List<Statistics_D_ZSZ> statisticsDZszs = new ArrayList<>();
        ChartOption result = new ChartOption();
        if (type.equals("zhydl")) {
            result.setTitle("综合耗电量");
        } else {
            result.setTitle("综合单耗");
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

    private void getCompleteData(List<DMGC_S_D_ZSZRSJ> partData) {
        if (partData.size() == 0) {
            return;
        }
        try {
            List<String> zszIds = partData.stream().map(c -> c.getZid()).distinct().collect(Collectors.toList());
            List<DMGC_S_ZSZ> zszList = zszService.getByEventIds(zszIds);
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("注水泵机组监测项目与指标要求", null);
            List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("zsz")).collect(Collectors.toList());
            for (DMGC_S_D_ZSZRSJ param : partData) {
                Optional<DMGC_S_ZSZ> dmgcSZsz = zszList.stream().filter(c -> c.getEventId().equals(param.getZid())).findFirst();
                dmgcSZsz.ifPresent(c -> {
                    param.setZmc(c.getMc());
                    MonitoringIndicatorNew monitoringIndicatorNew = filterByQueryData(0, monitoringIndicatorNewList, "注水站综合单耗");
                    param.setZhdhWeight(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level2") && d.getItemCode().equals("zszzhdh")).collect(Collectors.toList()).get(0).getWeight());
                    if (monitoringIndicatorNew != null) {
                        if (param.getZhdh() != null) {
                            param.setZhdhScore(Calculation.calculationOfUnitConsumption(param.getZhdh(), monitoringIndicatorNew));
                            param.setZhdhWeightScore(Calculation.getMultiplicationResult(param.getZhdhScore(), param.getZhdhWeight()));
                            param.setZhdhPj(Calculation.getUnitConsumptionComment(param.getZhdhScore(), monitoringIndicatorNew));
                        }
                    }
                });
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
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
            LambdaQueryWrapper<DMGC_S_D_ZSZRSJ> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DMGC_S_D_ZSZRSJ::getRq, updateDate);
            List<DMGC_S_D_ZSZRSJ> dataList = mapper.selectList(wrapper);
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

    public Boolean updateData(List<DMGC_S_D_ZSZRSJ> params) {
        return this.updateBatchById(params);
    }

    private List<DMGC_S_D_ZSZRSJ> getDataOfDay(String startDate, String endDate) {
        List<DMGC_S_D_ZSZRSJ> dataList = mapper.selectList(this.buildQueryWrapper(null, startDate, endDate));
        return dataList;
    }

    private List<DMGC_S_D_ZSZRSJ> getDataOfMonth(String startDate, String endDate) {
        List<DMGC_S_D_ZSZRSJ> dataList = new ArrayList<>();
        String queryStart = startDate;
        String queryEnd = endDate;
        List<DMGC_S_D_ZSBRSJ> zsbrsjList = dmgc_s_d_zsbrsjService.getDataOfMonth(queryStart, queryEnd);
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

    private List<DMGC_S_D_ZSZRSJ> getDataOfYear(String startDate, String endDate) {
        List<DMGC_S_D_ZSZRSJ> dataList = new ArrayList<>();
        String queryStart = startDate;
        String queryEnd = endDate;
        List<DMGC_S_D_ZSBRSJ> zsbrsjList = dmgc_s_d_zsbrsjService.getDataOfYear(queryStart, queryEnd);
        try {
            queryStart = DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-01-01";
            queryEnd = DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-12-31";
            dataList = mapper.selectList(this.buildQueryWrapper(null, queryStart, queryEnd));
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return dataList;
    }


    public PageVO<DMGC_S_D_ZSZRSJ> getAssessment(String cycle, String assessmentDate, int page, int size) {
        PageVO<DMGC_S_D_ZSZRSJ> pageVO = null;
        List<DMGC_S_D_ZSZRSJ> tempResult = new ArrayList<>();
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
            List<DMGC_S_D_ZSZRSJ> dataList = tempResult;
            if (cycle.equals("月") || cycle.equals("年")) {
                dataList = buildAssessment(tempResult, sumStart, sumEnd);
            }
            dataList = dataList.stream().sorted(Comparator.comparing(DMGC_S_D_ZSZRSJ::getZmc, Comparator.nullsLast(String::compareTo))).collect(Collectors.toList());
//            PageMethod.startPage(page, size);
            PageInfo<DMGC_S_D_ZSZRSJ> pageInfo = PageInfo.of(dataList);
            List<DMGC_S_D_ZSZRSJ> pageList = dataList.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
            pageVO = new PageVO<>(pageInfo.getTotal(), pageList);
            return pageVO;
        } catch (Exception ex) {
            String err = ex.getMessage();
            return new PageVO<>();
        }
    }

    public List<DMGC_S_D_ZSZRSJ> getAssessmentNoPage(String cycle, String assessmentDate) {
        List<DMGC_S_D_ZSZRSJ> result = new ArrayList<>();
        List<DMGC_S_D_ZSZRSJ> tempResult = new ArrayList<>();
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
            result = result.stream().sorted(Comparator.comparing(DMGC_S_D_ZSZRSJ::getZmc, Comparator.nullsLast(String::compareTo))).collect(Collectors.toList());
            return result;
        } catch (Exception ex) {
            String err = ex.getMessage();
            return result;
        }
    }

    private List<DMGC_S_D_ZSZRSJ> buildAssessment(List<DMGC_S_D_ZSZRSJ> dmgcSDZszrsjList, Date sumStart, Date sumEnd) {
        if (dmgcSDZszrsjList.size() == 0) {
            return new ArrayList<>();
        }
        List<String> zmcList = dmgcSDZszrsjList.stream().map(c -> c.getZmc()).distinct().collect(Collectors.toList());
        List<DMGC_S_D_ZSZRSJ> dataList = new ArrayList<>();
        DMGC_S_D_ZSZRSJ dmgc_s_d_zszrsj;
        for (String zmc : zmcList) {
            dmgc_s_d_zszrsj = new DMGC_S_D_ZSZRSJ();
            dmgc_s_d_zszrsj.setZid(dmgcSDZszrsjList.stream().filter(c -> c.getZmc().equals(zmc)).collect(Collectors.toList()).get(0).getZid());
            dmgc_s_d_zszrsj.setZmc(zmc);
            dmgc_s_d_zszrsj.setRq(sumEnd);
            dmgc_s_d_zszrsj.setSsdwdm(dmgcSDZszrsjList.stream().filter(c -> c.getZmc().equals(zmc)).collect(Collectors.toList()).get(0).getSsdwdm());
            dmgc_s_d_zszrsj.setSsdwName(dmgcSDZszrsjList.stream().filter(c -> c.getZmc().equals(zmc)).collect(Collectors.toList()).get(0).getSsdwName());
            Date finalSumStart = sumStart;
            Date finalSumEnd = sumEnd;
            double zhydl = dmgcSDZszrsjList.stream()
                    .filter(c -> c.getZmc().equals(zmc) && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                    .mapToDouble(DMGC_S_D_ZSZRSJ::getZhydl).sum();


            dataList.add(dmgc_s_d_zszrsj);
        }
        double maxZhdh = dataList.stream().mapToDouble(DMGC_S_D_ZSZRSJ::getZhdh).max().getAsDouble();

        return dataList;
    }

    public List<DMGC_S_D_ZSZRSJ> getDataForZYQ(String cycle, Date assessmentDate) {
        LambdaQueryWrapper<DMGC_S_D_ZSZRSJ> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(DMGC_S_D_ZSZRSJ::getJxpjScore);
        wrapper.isNotNull(DMGC_S_D_ZSZRSJ::getZhdh);
        wrapper.ne(DMGC_S_D_ZSZRSJ::getZhdh, 0);
        wrapper.eq(DMGC_S_D_ZSZRSJ::getRq, assessmentDate);
        return mapper.selectList(wrapper);
    }

    public List<DMGC_S_D_ZSZRSJ> getDataForPortrait(String zid, Date queryStart, Date queryEnd) {
        LambdaQueryWrapper<DMGC_S_D_ZSZRSJ> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(zid)) {
            wrapper.eq(DMGC_S_D_ZSZRSJ::getZid, zid);
        }
        wrapper.isNotNull(DMGC_S_D_ZSZRSJ::getJxpjScore);
        wrapper.ge(DMGC_S_D_ZSZRSJ::getRq, queryStart);
        wrapper.le(DMGC_S_D_ZSZRSJ::getRq, queryEnd);
        return mapper.selectList(wrapper);
    }

    public List<DMGC_S_D_ZSZRSJ> getEffectiveDataOfDay(String queryDate) {
        List<DMGC_S_D_ZSZRSJ> result = new ArrayList<>();
        try {
            Date date = DateUtils.parse(queryDate, DateUtils.DATE_PATTERN);
            LambdaQueryWrapper<DMGC_S_D_ZSZRSJ> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DMGC_S_D_ZSZRSJ::getRq, date);
            wrapper.isNotNull(DMGC_S_D_ZSZRSJ::getZhdh);
            wrapper.ne(DMGC_S_D_ZSZRSJ::getZhdh, 0);
            result = mapper.selectList(wrapper);
            return result;
        } catch (Exception ex) {
            return result;
        }
    }

    public List<DMGC_S_D_ZSZRSJ> getEffectiveData(Date queryStartDate,Date queryEndDate) {
        List<DMGC_S_D_ZSZRSJ> result = new ArrayList<>();
        try {
            LambdaQueryWrapper<DMGC_S_D_ZSZRSJ> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(DMGC_S_D_ZSZRSJ::getRq, queryStartDate);
            wrapper.le(DMGC_S_D_ZSZRSJ::getRq, queryEndDate);
            wrapper.isNotNull(DMGC_S_D_ZSZRSJ::getZhdh);
            wrapper.ne(DMGC_S_D_ZSZRSJ::getZhdh, 0);
            result = mapper.selectList(wrapper);
            return result;
        } catch (Exception ex) {
            return result;
        }
    }
}
