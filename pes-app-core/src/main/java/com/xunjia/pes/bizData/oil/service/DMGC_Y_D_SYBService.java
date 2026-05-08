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
import com.xunjia.pes.bizData.PieOption;
import com.xunjia.pes.bizData.assessment.entity.MonitoringIndicatorNew;
import com.xunjia.pes.bizData.assessment.service.MonitoringIndicatorNewService;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_SYB;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_JB;
import com.xunjia.pes.bizData.oil.mapper.DMGC_Y_D_SYBMapper;
import com.xunjia.pes.score.Calculation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DMGC_Y_D_SYBService extends ServiceImpl<DMGC_Y_D_SYBMapper, DMGC_Y_D_SYB> {

    @Autowired
    private DMGC_Y_D_SYBMapper mapper;

    @Autowired
    private DMGC_Y_JBService dmgcYJbService;

    @Autowired
    private MonitoringIndicatorNewService monitoringIndicatorNewService;

    @Autowired
    private IndicatorsService indicatorsService;

    public Boolean saveData(String id, String sbyxzk, Double sybxl, Double ckyl, Double bsshgyl, Integer pjll, Double hll) {
        try {
            DMGC_Y_D_SYB dmgcYDSyb = mapper.selectById(id);
            if (Integer.parseInt(sbyxzk) == 1) {
                sbyxzk = "01";
            }
            dmgcYDSyb.setSbyxzk(sbyxzk);
            dmgcYDSyb.setSybxl(sybxl);
            dmgcYDSyb.setCkyl(ckyl);
            dmgcYDSyb.setBsshgyl(bsshgyl);
            dmgcYDSyb.setPjll(pjll);
            dmgcYDSyb.setHll(hll);
            mapper.updateById(dmgcYDSyb);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Boolean auditData(String rq) {
        try {
            LambdaQueryWrapper<DMGC_Y_D_SYB> wrapper = new LambdaQueryWrapper<>();
            Date startDate = DateUtils.parse(rq, DateUtils.DATE_PATTERN);
            wrapper.ge(DMGC_Y_D_SYB::getRq, startDate);
            Date endDate = DateUtils.parse(rq + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
            wrapper.le(DMGC_Y_D_SYB::getRq, endDate);
            wrapper.eq(DMGC_Y_D_SYB::getSbyxzk, "01");
            wrapper.isNotNull(DMGC_Y_D_SYB::getSybxl);
            wrapper.isNotNull(DMGC_Y_D_SYB::getCkyl);
            wrapper.isNotNull(DMGC_Y_D_SYB::getBsshgyl);
            wrapper.isNotNull(DMGC_Y_D_SYB::getPjll);
            wrapper.isNotNull(DMGC_Y_D_SYB::getHll);
            wrapper.ne(DMGC_Y_D_SYB::getSybxl, 0);
            wrapper.ne(DMGC_Y_D_SYB::getCkyl, 0);
            wrapper.ne(DMGC_Y_D_SYB::getBsshgyl, 0);
            wrapper.ne(DMGC_Y_D_SYB::getPjll, 0);
            wrapper.ne(DMGC_Y_D_SYB::getHll, 0);
            List<DMGC_Y_D_SYB> dmgcYDSybList = mapper.selectList(wrapper);
//            for (DMGC_Y_D_SYB param : dmgcYDSybList) {
//                if (param.getHll() != null && param.getBsshgyl() != null && (param.getDataAlreadyAudited() == null || param.getDataAlreadyAudited().equals(false))) {
//                    param.setDataAlreadyAudited(true);
//                }
//            }
            if (dmgcYDSybList.size() != 0) {
                getCompleteData(dmgcYDSybList);
                this.updateBatchById(dmgcYDSybList);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Boolean getIfSomeDataNotInput(String rq) {
        try {
            LambdaQueryWrapper<DMGC_Y_D_SYB> wrapper = new LambdaQueryWrapper<>();
            Date queryDate = DateUtils.parse(rq, DateUtils.DATE_PATTERN);
            wrapper.eq(DMGC_Y_D_SYB::getRq, queryDate);
            wrapper.eq(DMGC_Y_D_SYB::getSbyxzk, "01");
            wrapper.and(item -> item.isNull(DMGC_Y_D_SYB::getSybxl)
                    .or().isNull(DMGC_Y_D_SYB::getCkyl)
                    .or().isNull(DMGC_Y_D_SYB::getBsshgyl)
                    .or().isNull(DMGC_Y_D_SYB::getPjll)
                    .or().isNull(DMGC_Y_D_SYB::getHll)
                    .or().eq(DMGC_Y_D_SYB::getSybxl, 0)
                    .or().eq(DMGC_Y_D_SYB::getCkyl, 0)
                    .or().eq(DMGC_Y_D_SYB::getBsshgyl, 0)
                    .or().eq(DMGC_Y_D_SYB::getPjll, 0)
                    .or().eq(DMGC_Y_D_SYB::getHll, 0));
            return (long) mapper.selectList(wrapper).size() > 0;
        } catch (Exception ex) {
            return false;
        }
    }

    public PageVO<DMGC_Y_D_SYB> getPageData(DMGC_Y_D_SYB example, String startDate, String endDate, int page, int size) {
        PageVO<DMGC_Y_D_SYB> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_Y_D_SYB> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            PageInfo<DMGC_Y_D_SYB> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e) {
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    public void exportData(DMGC_Y_D_SYB example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response) {

        Workbook workbook = ExportUtils.createWorkbook();
        try {
            List<DMGC_Y_D_SYB> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            Map<String, List<String>> headerNamesAndFieldsMap = ExportUtils.getHeaderNamesAndFields(DMGC_Y_D_SYB.class);
            List<String> headerNames = headerNamesAndFieldsMap.get("headerNames");
            List<String> fields = headerNamesAndFieldsMap.get("fields");
            ExportUtils.exportExcel("站输油泵生产动态日数据", headerNames, fields, 0, workbook, dataList);
            ExportUtils.responseWorkbook("站输油泵生产动态日数据", workbook, request, response);
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

    public void exportExamineData(String queryDate,
                                  HttpServletRequest request, HttpServletResponse response) {

        Workbook workbook = ExportUtils.createWorkbook();
        try {
            List<DMGC_Y_D_SYB> dataList = getAssessmentNoPage("日", queryDate).stream().filter(c -> c.getJbScore() != null).collect(Collectors.toList());
            Map<String, List<String>> headerNamesAndFieldsMap = ExportUtils.getExamineHeaderNamesAndFields(DMGC_Y_D_SYB.class);
            List<String> headerNames = headerNamesAndFieldsMap.get("headerNames");
            List<String> fields = headerNamesAndFieldsMap.get("fields");
            ExportUtils.exportExcel("集输系统输油泵日考核", headerNames, fields, 0, workbook, dataList);
            ExportUtils.responseWorkbook("集输系统输油泵日考核", workbook, request, response);
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

    private LambdaQueryWrapper<DMGC_Y_D_SYB> buildQueryWrapper(DMGC_Y_D_SYB example, String startDate, String endDate) {
        LambdaQueryWrapper<DMGC_Y_D_SYB> queryWrapper = new LambdaQueryWrapper<>();
        if (example != null) {
            if (StringUtils.isNotEmpty(example.getZm())) {
                queryWrapper.like(DMGC_Y_D_SYB::getZm, example.getZm());
            }
        }
        if (example != null) {
            if (!StringUtils.isEmpty(example.getSybbh())) {
                queryWrapper.eq(DMGC_Y_D_SYB::getSybbh, example.getSybbh());
            }
            if (StringUtils.isNotEmpty(example.getJbEventId())) {
                queryWrapper.eq(DMGC_Y_D_SYB::getJbEventId, example.getJbEventId());
            }
            if (StringUtils.isNotEmpty(example.getSszkEventId())) {
                queryWrapper.eq(DMGC_Y_D_SYB::getSszkEventId, example.getSszkEventId());
            }
        }
        if (!StringUtils.isEmpty(startDate)) {
            try {
                Date date = DateUtils.parse(startDate, DateUtils.DATE_PATTERN);
                queryWrapper.ge(DMGC_Y_D_SYB::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!StringUtils.isEmpty(endDate)) {
            try {
                Date date = DateUtils.parse(endDate + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                queryWrapper.le(DMGC_Y_D_SYB::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        queryWrapper.orderByDesc(DMGC_Y_D_SYB::getRq);
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

    public List<DMGC_Y_D_SYB> getDataOfDay(String startDate, String endDate) {
        List<DMGC_Y_D_SYB> dataList = mapper.selectList(this.buildQueryWrapper(null, startDate, endDate));
        return dataList;
    }

    public List<DMGC_Y_D_SYB> getDataOfMonth(String startDate, String endDate) {
        List<DMGC_Y_D_SYB> dataList = new ArrayList<>();
        String queryStart;
        String queryEnd;
        try {
            queryStart = DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getMonth(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-01";
            queryEnd = DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getMonth(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getDay(DateUtils.parse(DateUtils.getLastDay(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)), DateUtils.DATE_PATTERN));
            LambdaQueryWrapper<DMGC_Y_D_SYB> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(DMGC_Y_D_SYB::getRq, queryStart);
            wrapper.le(DMGC_Y_D_SYB::getRq, queryEnd);
            wrapper.eq(DMGC_Y_D_SYB::getSbyxzk, "01");
            wrapper.isNotNull(DMGC_Y_D_SYB::getSybxl);
            wrapper.isNotNull(DMGC_Y_D_SYB::getCkyl);
            wrapper.isNotNull(DMGC_Y_D_SYB::getBsshgyl);
            wrapper.isNotNull(DMGC_Y_D_SYB::getPjll);
            wrapper.isNotNull(DMGC_Y_D_SYB::getHll);
            wrapper.ne(DMGC_Y_D_SYB::getSybxl, 0);
            wrapper.ne(DMGC_Y_D_SYB::getCkyl, 0);
            wrapper.ne(DMGC_Y_D_SYB::getBsshgyl, 0);
            wrapper.ne(DMGC_Y_D_SYB::getPjll, 0);
            wrapper.ne(DMGC_Y_D_SYB::getHll, 0);
            wrapper.ne(DMGC_Y_D_SYB::getJbScore, 0);
            dataList = mapper.selectList(wrapper);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return dataList;
    }

    public List<DMGC_Y_D_SYB> getDataOfYear(String startDate, String endDate) {
        List<DMGC_Y_D_SYB> dataList = new ArrayList<>();
        String queryStart;
        String queryEnd;
        try {
            queryStart = DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-01-01";
            queryEnd = DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-12-31";
            LambdaQueryWrapper<DMGC_Y_D_SYB> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(DMGC_Y_D_SYB::getRq, queryStart);
            wrapper.le(DMGC_Y_D_SYB::getRq, queryEnd);
            wrapper.eq(DMGC_Y_D_SYB::getSbyxzk, "01");
            wrapper.isNotNull(DMGC_Y_D_SYB::getSybxl);
            wrapper.isNotNull(DMGC_Y_D_SYB::getCkyl);
            wrapper.isNotNull(DMGC_Y_D_SYB::getBsshgyl);
            wrapper.isNotNull(DMGC_Y_D_SYB::getPjll);
            wrapper.isNotNull(DMGC_Y_D_SYB::getHll);
            wrapper.ne(DMGC_Y_D_SYB::getSybxl, 0);
            wrapper.ne(DMGC_Y_D_SYB::getCkyl, 0);
            wrapper.ne(DMGC_Y_D_SYB::getBsshgyl, 0);
            wrapper.ne(DMGC_Y_D_SYB::getPjll, 0);
            wrapper.ne(DMGC_Y_D_SYB::getHll, 0);
            wrapper.ne(DMGC_Y_D_SYB::getJbScore, 0);
            dataList = mapper.selectList(wrapper);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return dataList;
    }

    private void getCompleteData(List<DMGC_Y_D_SYB> partData) {
        if (partData.size() == 0) {
            return;
        }
        List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("输油泵机组监测项目与指标要求", null);
        List<String> jbIds = partData.stream().map(c -> c.getJbEventId()).distinct().collect(Collectors.toList());
        List<DMGC_Y_JB> dmgcYJbList = dmgcYJbService.getByEventIds(jbIds);
        List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("zyfsz")).collect(Collectors.toList());
        for (DMGC_Y_D_SYB param : partData) {
            Optional<DMGC_Y_JB> jbOptional = dmgcYJbList.stream().filter(c -> c.getEventId().equals(param.getJbEventId())).findFirst();
            jbOptional.ifPresent(jb -> {
                param.setSybxlWeight(indicatorsList.stream().filter(c -> c.getLevelCode().equals("level5") && c.getItemCode().equals("efficiency")).collect(Collectors.toList()).get(0).getWeight());
                MonitoringIndicatorNew monitoringIndicatorNew = null;
                //无调整泵（电机变频器编号字段有无数据）
                if (jb.getDjbpqbh() == null) {
                    monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "机组效率(无调速)");
                }
                //有调速泵
                if (jb.getBlx().equals("02")) {
                    monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "机组效率(有调速)");
                }
                if (monitoringIndicatorNew != null) {
                    if (param.getSybxl() != null) {
                        param.setSybxlScore(Calculation.efficiency(param.getSybxl(), monitoringIndicatorNew));
                        param.setSybxlWeightScore(Calculation.getMultiplicationResult(param.getSybxlScore(), param.getSybxlWeight()));
                        param.setSybxlPj(Calculation.getEfficiencyComment(param.getSybxlScore(), monitoringIndicatorNew));
                    }
                }

                monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "节流损失率");
                param.setJlsslWeight(indicatorsList.stream().filter(c -> c.getLevelCode().equals("level5") && c.getItemCode().equals("loss")).collect(Collectors.toList()).get(0).getWeight());
                if (monitoringIndicatorNew != null && param.getBsshgyl() != null && param.getCkyl() != null) {
                    //节流损失率=（泵出口压力-泵所属汇管压力）/泵出口压力*100
                    double tempParam1 = Calculation.getReduceResult(param.getCkyl(), param.getBsshgyl());
                    double tempParam2 = Calculation.getDivisionResult(tempParam1, param.getCkyl());
                    param.setJlssl(Calculation.getMultiplicationResult(tempParam2, 100));
                    param.setJlsslScore(Calculation.calculationOfUnitConsumption(param.getJlssl(), monitoringIndicatorNew));
                    param.setJlsslWeightScore(Calculation.getMultiplicationResult(param.getJlsslScore(), param.getJlsslWeight()));
                    param.setJlsslPj(Calculation.getUnitConsumptionComment(param.getJlsslScore(), monitoringIndicatorNew));
                }

                monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "回流损失率");
                param.setHlRateWeight(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level5") && d.getItemCode().equals("hlssl")).collect(Collectors.toList()).get(0).getWeight());
                if (monitoringIndicatorNew != null && param.getHll() != null) {
                    //回流率=回流量/流量*100
                    param.setHlRate(Calculation.getMultiplicationResult(Calculation.getDivisionResult(param.getHll(), param.getPjll()), 100));
                    param.setHlRateScore(Calculation.calculationOfUnitConsumption(param.getHlRate(), monitoringIndicatorNew));
                    param.setHlRateWeightScore(Calculation.getMultiplicationResult(param.getHlRateScore(), param.getHlRateWeight()));
                    param.setHlRatePj(Calculation.getUnitConsumptionComment(param.getHlRateScore(), monitoringIndicatorNew));
                }

                monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "负荷率");
                param.setFhlWeight(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level5") && d.getItemCode().equals("fhl")).collect(Collectors.toList()).get(0).getWeight());
                if (monitoringIndicatorNew != null) {
                    //负荷率=流量/额定流量*100
                    param.setFhl(Calculation.getMultiplicationResult(Calculation.getDivisionResult(param.getPjll(), jb.getBedll()), 100));
                    param.setFhlScore(Calculation.specialFhl(param.getFhl(), monitoringIndicatorNew));
                    param.setFhlWeightScore(Calculation.getMultiplicationResult(param.getFhlScore(), param.getFhlWeight()));
                    param.setFhlPj(Calculation.specialFhlComment(param.getFhlScore(), monitoringIndicatorNew));
                }

                monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "绩效评价");
                if (monitoringIndicatorNew != null && param.getSybxlWeightScore() != null && param.getJlsslWeightScore() != null
                        && param.getHlRateWeightScore() != null && param.getFhlWeightScore() != null) {
                    param.setJbScore(Calculation.getPlusResult((param.getSybxlWeightScore() + param.getJlsslWeightScore() + param.getHlRateWeightScore() + param.getFhlWeightScore()), 0));
                    param.setJbPj(Calculation.getEfficiencyComment(param.getJbScore(), monitoringIndicatorNew));
                }
            });
        }
    }

    private List<DMGC_Y_D_SYB> buildAssessment(List<DMGC_Y_D_SYB> dmgcSYBList, Date sumStart, Date sumEnd) {
        if (dmgcSYBList.size() == 0) {
            return new ArrayList<>();
        }
        List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("输油泵机组监测项目与指标要求", null);
        List<String> jbIds = dmgcSYBList.stream().map(c -> c.getJbEventId()).distinct().collect(Collectors.toList());
        List<DMGC_Y_JB> dmgcYJbList = dmgcYJbService.getByEventIds(jbIds);
        List<DMGC_Y_D_SYB> resultList = new ArrayList<>();
        for (String jbId : jbIds) {
            if (resultList.stream().filter(c -> c.getJbEventId().equals(jbId)).count() != 0) {
                continue;
            }
            Date finalSumStart = sumStart;
            Date finalSumEnd = sumEnd;

            DMGC_Y_D_SYB result = new DMGC_Y_D_SYB();
            DMGC_Y_D_SYB temp = dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId)).findFirst().get();
            result.setJbEventId(jbId);
            result.setEquipName(temp.getEquipName());
            result.setEventId(temp.getEventId());
            result.setZm(temp.getZm());
            result.setRq(sumEnd);
            result.setSszkEventId(temp.getSszkEventId());
            result.setSybbh(temp.getSybbh());

            long jbIdCount = dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime()
                    && (c.getSybxl() != null || c.getFhl() != null || c.getJlssl() != null || c.getHlRate() != null)).count();
            Optional<DMGC_Y_JB> jbOptional = dmgcYJbList.stream().filter(c -> c.getEventId().equals(jbId)).findFirst();
            jbOptional.ifPresent(jb -> {
                MonitoringIndicatorNew monitoringIndicatorNew = null;
                //无调整泵（电机变频器编号字段有无数据）
                if (jb.getDjbpqbh() == null) {
                    monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "机组效率(无调速)");
                }
                //有调速泵
                if (jb.getBlx().equals("02")) {
                    monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "机组效率(有调速)");
                }
                if (monitoringIndicatorNew != null) {
//                    result.setSybxlWeight(temp.getSybxlWeight());
                    result.setSybxlScore(Calculation.getDivisionResult(dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getSybxl() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_SYB::getSybxlScore).sum(), jbIdCount));
//                    result.setSybxlWeightScore(Calculation.getMultiplicationResult(result.getSybxlScore(), result.getSybxlWeight()));
                    result.setSybxlWeightScore(Calculation.getDivisionResult(dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getSybxl() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_SYB::getSybxlWeightScore).sum(), jbIdCount));
                    result.setSybxlPj(Calculation.getEfficiencyComment(result.getSybxlScore(), monitoringIndicatorNew));
                }

                monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "节流损失率");
                if (monitoringIndicatorNew != null) {
//                    result.setJlsslWeight(temp.getJlsslWeight());
                    result.setJlsslScore(Calculation.getDivisionResult(dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getJlsslScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_SYB::getJlsslScore).sum(), jbIdCount));
//                    result.setJlsslWeightScore(Calculation.getMultiplicationResult(result.getJlsslScore(), result.getJlsslWeight()));
                    result.setJlsslWeightScore(Calculation.getDivisionResult(dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getJlsslScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_SYB::getJlsslWeightScore).sum(), jbIdCount));
                    result.setJlsslPj(Calculation.getUnitConsumptionComment(result.getJlsslScore(), monitoringIndicatorNew));
                }


                monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "回流损失率");
                if (monitoringIndicatorNew != null) {
//                    result.setHlRateWeight(temp.getHlRateWeight());
                    result.setHlRateScore(Calculation.getDivisionResult(dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getHlRateScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_SYB::getHlRateScore).sum(), jbIdCount));
//                    result.setHlRateWeightScore(Calculation.getMultiplicationResult(result.getHlRateScore(), result.getHlRateWeight()));
                    result.setHlRateWeightScore(Calculation.getDivisionResult(dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getHlRateScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_SYB::getHlRateWeightScore).sum(), jbIdCount));
                    result.setHlRatePj(Calculation.getUnitConsumptionComment(result.getHlRateScore(), monitoringIndicatorNew));
                }

                monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "负荷率");
                if (monitoringIndicatorNew != null) {
//                    result.setFhlWeight(temp.getFhlWeight());
                    result.setFhlScore(Calculation.getDivisionResult(dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getFhlScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_SYB::getFhlScore).sum(), jbIdCount));
//                    result.setFhlWeightScore(Calculation.getMultiplicationResult(result.getFhlScore(), result.getFhlWeight()));
                    result.setFhlWeightScore(Calculation.getDivisionResult(dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getFhlScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_SYB::getFhlWeightScore).sum(), jbIdCount));
                    result.setFhlPj(Calculation.specialFhlComment(result.getFhlScore(), monitoringIndicatorNew));
                }

                monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "绩效评价");
                if (monitoringIndicatorNew != null && result.getSybxlWeightScore() != null && result.getJlsslWeightScore() != null
                        && result.getHlRateWeightScore() != null && result.getFhlWeightScore() != null) {
                    result.setJbScore(Calculation.getPlusResult((result.getSybxlWeightScore() + result.getJlsslWeightScore() + result.getHlRateWeightScore() + result.getFhlWeightScore()), 0));
                    result.setJbPj(Calculation.getEfficiencyComment(result.getJbScore(), monitoringIndicatorNew));
                }
            });
            resultList.add(result);
        }
        return resultList;
    }

    public PageVO<DMGC_Y_D_SYB> getAssessment(String cycle, String assessmentDate, int page, int size, DMGC_Y_D_SYB example) {
        PageVO<DMGC_Y_D_SYB> pageVO = null;
        List<DMGC_Y_D_SYB> tempResult = new ArrayList<>();
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
            List<DMGC_Y_D_SYB> dataList = tempResult;
            if (cycle.equals("月") || cycle.equals("年")) {
                dataList = buildAssessment(tempResult, sumStart, sumEnd);
            }
            if (example != null) {
                if (StringUtils.isNotEmpty(example.getZm())) {
                    dataList = dataList.stream().filter(c -> c.getZm() != null && c.getZm().contains(example.getZm())).collect(Collectors.toList());
                }

                if (StringUtils.isNotEmpty(example.getEquipName())) {
                    dataList = dataList.stream().filter(c -> c.getEquipName() != null && c.getEquipName().contains(example.getEquipName())).collect(Collectors.toList());
                }

                if (StringUtils.isNotEmpty(example.getSybbh())) {
                    dataList = dataList.stream().filter(c -> c.getSybbh() != null && c.getSybbh().contains(example.getSybbh())).collect(Collectors.toList());
                }

                if (StringUtils.isNotEmpty(example.getJbPj())) {
                    if ("不合格".equals(example.getJbPj())) {
                        dataList = dataList.stream().filter(c -> (StringUtils.isEmpty(c.getSybxlPj()) || "不合格".equals(c.getSybxlPj()))
                                || (StringUtils.isEmpty(c.getJlsslPj()) || "不合格".equals(c.getJlsslPj()))
                                || (StringUtils.isEmpty(c.getHlRatePj()) || "不合格".equals(c.getHlRatePj()))
                                || (StringUtils.isEmpty(c.getFhlPj()) || "不合格".equals(c.getFhlPj()))
                                || (StringUtils.isEmpty(c.getJbPj()) || "不合格".equals(c.getJbPj()))).collect(Collectors.toList());
                    } else {
                        dataList = dataList.stream().filter(c -> (StringUtils.isNotEmpty(c.getSybxlPj()) && !"不合格".equals(c.getSybxlPj()))
                                && (StringUtils.isNotEmpty(c.getJlsslPj()) && !"不合格".equals(c.getJlsslPj()))
                                && (StringUtils.isNotEmpty(c.getHlRatePj()) && !"不合格".equals(c.getHlRatePj()))
                                && (StringUtils.isNotEmpty(c.getFhlPj()) && !"不合格".equals(c.getFhlPj()))
                                && (StringUtils.isNotEmpty(c.getJbPj()) && !"不合格".equals(c.getJbPj()))).collect(Collectors.toList());
                    }
                }
            }
            dataList = dataList.stream().sorted(Comparator.comparing(DMGC_Y_D_SYB::getZm)).collect(Collectors.toList());
            PageInfo<DMGC_Y_D_SYB> pageInfo = PageInfo.of(dataList);
            List<DMGC_Y_D_SYB> pageList = dataList.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
            pageVO = new PageVO<>(pageInfo.getTotal(), pageList);
        } catch (Exception e) {
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    public List<DMGC_Y_D_SYB> getAssessmentNoPage(String cycle, String assessmentDate) {
        List<DMGC_Y_D_SYB> result = new ArrayList<>();
        List<DMGC_Y_D_SYB> tempResult = new ArrayList<>();
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
            result = result.stream().sorted(Comparator.comparing(DMGC_Y_D_SYB::getZm).thenComparing(DMGC_Y_D_SYB::getEquipName)).collect(Collectors.toList());
            return result;
        } catch (Exception ex) {
            String err = ex.getMessage();
            return result;
        }
    }

    public List<PieOption> getAllStaticsOfPipe(String rq) {
        List<DMGC_Y_D_SYB> tempResult = getDataOfDay(rq, rq).stream().filter(c -> "01".equals(c.getSbyxzk())).collect(Collectors.toList());
        List<PieOption> result = new ArrayList<>();
        result.add(getStaticOfRunStatic(tempResult));
        result.add(getStaticOfJxpj(tempResult));
        result.add(getStatisticsOfRunningState(tempResult));
        result.add(getStatisticsOfThrottlingLoss(tempResult));
        result.add(getStaticOfHlRate(tempResult));
        result.add(getStaticOfFhl(tempResult));
        return result;
    }

    private PieOption getStaticOfRunStatic(List<DMGC_Y_D_SYB> tempResult) {
        PieOption result = new PieOption();
        try {
            long unqualified = tempResult.stream()
                    .filter(c -> "不合格".equals(c.getSybxlPj()) || StringUtils.isEmpty(c.getSybxlPj())
                            || "不合格".equals(c.getJlsslPj()) || StringUtils.isEmpty(c.getJlsslPj())
                            || "不合格".equals(c.getHlRatePj()) || StringUtils.isEmpty(c.getHlRatePj())
                            || "不合格".equals(c.getFhlPj()) || StringUtils.isEmpty(c.getFhlPj())
                            || "不合格".equals(c.getJbPj()) || StringUtils.isEmpty(c.getJbPj())).count();
            result.setTitle("输油泵综合运行评价");
            PieOption.PieData pieData = result.new PieData();

            pieData.setName("合格");
            pieData.setValue(tempResult.size() - unqualified);
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("不合格");
            pieData.setValue(unqualified);
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private PieOption getStaticOfJxpj(List<DMGC_Y_D_SYB> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("综合绩效评价");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("不合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getJbPj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getJbPj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("良好");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "良好".equals(c.getJbPj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private PieOption getStatisticsOfRunningState(List<DMGC_Y_D_SYB> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("机组效率");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("低效区");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getSybxlPj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("高效区");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "良好".equals(c.getSybxlPj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("合理区");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getSybxlPj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private PieOption getStatisticsOfThrottlingLoss(List<DMGC_Y_D_SYB> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("节流损失率");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("节流损失偏大");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getJlsslPj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("节流损失正常");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getJlsslPj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private PieOption getStaticOfHlRate(List<DMGC_Y_D_SYB> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("回流损失率");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("不合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getHlRatePj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getHlRatePj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("良好");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "良好".equals(c.getHlRatePj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private PieOption getStaticOfFhl(List<DMGC_Y_D_SYB> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("负荷率");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("不合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getFhlPj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getFhlPj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("良好");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "良好".equals(c.getFhlPj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    public ChartOption getStatistics(String startDate, String endDate, String cycle, String type, String jbId) {
        List<DMGC_Y_D_SYB> dmgcSDZsbrsjList = new ArrayList<>();
        switch (cycle) {
            case "日":
                dmgcSDZsbrsjList = getDataOfDay(startDate, endDate);
                break;
            case "月":
                dmgcSDZsbrsjList = getDataOfMonth(startDate, endDate);
                break;
            case "年":
                dmgcSDZsbrsjList = getDataOfYear(startDate, endDate);
                break;
        }
        dmgcSDZsbrsjList = dmgcSDZsbrsjList.stream().filter(c -> c.getJbEventId().equals(jbId)).collect(Collectors.toList());
        dmgcSDZsbrsjList = dmgcSDZsbrsjList.stream().sorted(Comparator.comparing(DMGC_Y_D_SYB::getRq).thenComparing(DMGC_Y_D_SYB::getJbEventId)).collect(Collectors.toList());
        ChartOption result = new ChartOption();
        result.setLegend(dmgcSDZsbrsjList.stream().map(c -> c.getEquipName()).distinct().collect(Collectors.toList()));
        Collections.sort(result.getLegend());
        result.setXAxis(dmgcSDZsbrsjList.stream().map(c -> DateUtils.format(c.getRq(), DateUtils.DATE_PATTERN)).distinct().collect(Collectors.toList()));
        Collections.sort(result.getXAxis());
        if (type.equals("绩效")) {
            result.setTitle("绩效曲线");
        }
        if (type.equals("泵效")) {
            result.setTitle("泵效曲线");
        }
        try {
            for (String legend : result.getLegend()) {
                ChartOption.Serie mySerie = result.new Serie();
                mySerie.setName(legend);
                mySerie.setType("line");
                mySerie.setStack("总量");
                List<DMGC_Y_D_SYB> temp = dmgcSDZsbrsjList.stream().filter(c -> c.getJbEventId().equals(jbId)).collect(Collectors.toList());
                for (String date : result.getXAxis()) {
                    Date d = DateUtils.parse(date, DateUtils.DATE_PATTERN);
                    Optional<DMGC_Y_D_SYB> optional = temp.stream().filter(c -> {
                        try {
                            return DateUtils.parse(DateUtils.format(c.getRq(), DateUtils.DATE_PATTERN), DateUtils.DATE_PATTERN).getTime() == d.getTime();
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }).findFirst();
                    if (type.equals("绩效")) {
                        optional.ifPresent(c -> mySerie.getData().add(c.getJbScore()));
                    }
                    if (type.equals("泵效")) {
                        optional.ifPresent(c -> mySerie.getData().add(c.getSybxl()));
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

    public List<DMGC_Y_D_SYB> getEffectiveDataOfDay(List<String> zids, String queryDate) {
        List<DMGC_Y_D_SYB> result = new ArrayList<>();
        try {
            Date date = DateUtils.parse(queryDate, DateUtils.DATE_PATTERN);
            LambdaQueryWrapper<DMGC_Y_D_SYB> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(DMGC_Y_D_SYB::getSszkEventId, zids);
            queryWrapper.eq(DMGC_Y_D_SYB::getSbyxzk, "01");
            queryWrapper.isNotNull(DMGC_Y_D_SYB::getSybxl);
            queryWrapper.isNotNull(DMGC_Y_D_SYB::getCkyl);
            queryWrapper.isNotNull(DMGC_Y_D_SYB::getBsshgyl);
            queryWrapper.isNotNull(DMGC_Y_D_SYB::getPjll);
            queryWrapper.isNotNull(DMGC_Y_D_SYB::getHll);
            queryWrapper.ne(DMGC_Y_D_SYB::getSybxl, 0);
            queryWrapper.ne(DMGC_Y_D_SYB::getCkyl, 0);
            queryWrapper.ne(DMGC_Y_D_SYB::getBsshgyl, 0);
            queryWrapper.ne(DMGC_Y_D_SYB::getPjll, 0);
            queryWrapper.ne(DMGC_Y_D_SYB::getHll, 0);
            queryWrapper.eq(DMGC_Y_D_SYB::getRq, date);
            result = mapper.selectList(queryWrapper);
            return result;
        } catch (Exception ex) {
            return result;
        }
    }

    public List<DMGC_Y_D_SYB> getEffectiveData(Date queryStartDate, Date queryEndDate) {
        List<DMGC_Y_D_SYB> result = new ArrayList<>();
        try {
            LambdaQueryWrapper<DMGC_Y_D_SYB> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DMGC_Y_D_SYB::getSbyxzk, "01");
            queryWrapper.isNotNull(DMGC_Y_D_SYB::getSybxl);
            queryWrapper.isNotNull(DMGC_Y_D_SYB::getCkyl);
            queryWrapper.isNotNull(DMGC_Y_D_SYB::getBsshgyl);
            queryWrapper.isNotNull(DMGC_Y_D_SYB::getPjll);
            queryWrapper.isNotNull(DMGC_Y_D_SYB::getHll);
            queryWrapper.ne(DMGC_Y_D_SYB::getSybxl, 0);
            queryWrapper.ne(DMGC_Y_D_SYB::getCkyl, 0);
            queryWrapper.ne(DMGC_Y_D_SYB::getBsshgyl, 0);
            queryWrapper.ne(DMGC_Y_D_SYB::getPjll, 0);
            queryWrapper.ne(DMGC_Y_D_SYB::getHll, 0);
            queryWrapper.ge(DMGC_Y_D_SYB::getRq, queryStartDate);
            queryWrapper.le(DMGC_Y_D_SYB::getRq, queryEndDate);
            result = mapper.selectList(queryWrapper);
            return result;
        } catch (Exception ex) {
            return result;
        }
    }
}
