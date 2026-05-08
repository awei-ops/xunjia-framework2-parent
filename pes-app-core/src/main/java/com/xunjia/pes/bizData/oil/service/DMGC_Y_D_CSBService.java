package com.xunjia.pes.bizData.oil.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
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
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_CSB;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_JB;
import com.xunjia.pes.bizData.oil.mapper.DMGC_Y_D_CSBMapper;
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
public class DMGC_Y_D_CSBService extends ServiceImpl<DMGC_Y_D_CSBMapper, DMGC_Y_D_CSB> {
    @Autowired
    private DMGC_Y_D_CSBMapper mapper;

    @Autowired
    private DMGC_Y_JBService jbService;

    @Autowired
    private MonitoringIndicatorNewService monitoringIndicatorNewService;

    @Autowired
    private IndicatorsService indicatorsService;

    public Boolean saveData(String id, String yxzt, Double bxl, Double jlssl, Double hlRate, Double fhl) {
        try {
            DMGC_Y_D_CSB record = mapper.selectById(id);
            record.setBxl(bxl);
            record.setJlssl(jlssl);
            record.setHlRate(hlRate);
            record.setFhl(fhl);
            if (Integer.parseInt(yxzt) == 1) {
                yxzt = "01";
            }
            record.setYxzt(yxzt);
            mapper.updateById(record);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Boolean auditData(String rq) {
        try {
            LambdaQueryWrapper<DMGC_Y_D_CSB> wrapper = new LambdaQueryWrapper<>();
            Date startDate = DateUtils.parse(rq, DateUtils.DATE_PATTERN);
            wrapper.ge(DMGC_Y_D_CSB::getRq, startDate);
            Date endDate = DateUtils.parse(rq + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
            wrapper.le(DMGC_Y_D_CSB::getRq, endDate);
            wrapper.eq(DMGC_Y_D_CSB::getYxzt, "01");
            wrapper.isNotNull(DMGC_Y_D_CSB::getBxl);
            wrapper.isNotNull(DMGC_Y_D_CSB::getJlssl);
            wrapper.isNotNull(DMGC_Y_D_CSB::getHlRate);
            wrapper.isNotNull(DMGC_Y_D_CSB::getFhl);
            wrapper.ne(DMGC_Y_D_CSB::getBxl, 0);
            wrapper.ne(DMGC_Y_D_CSB::getJlssl, 0);
            wrapper.ne(DMGC_Y_D_CSB::getHlRate, 0);
            wrapper.ne(DMGC_Y_D_CSB::getFhl, 0);
            List<DMGC_Y_D_CSB> dmgcYDSybList = mapper.selectList(wrapper);
//            for (DMGC_Y_D_CSB param : dmgcYDSybList) {
//                if (param.getBxl() != null && param.getJlssl() != null
//                        && param.getHlRate() != null && param.getFhl() != null
//                        && (param.getDataAlreadyAudited() == null || param.getDataAlreadyAudited().equals(false))) {
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
            LambdaQueryWrapper<DMGC_Y_D_CSB> wrapper = new LambdaQueryWrapper<>();
            Date queryDate = DateUtils.parse(rq, DateUtils.DATE_PATTERN);
            wrapper.eq(DMGC_Y_D_CSB::getRq, queryDate);
            wrapper.eq(DMGC_Y_D_CSB::getYxzt, "01");
            wrapper.and(item -> item.isNull(DMGC_Y_D_CSB::getBxl)
                    .or().isNull(DMGC_Y_D_CSB::getJlssl)
                    .or().isNull(DMGC_Y_D_CSB::getHlRate)
                    .or().isNull(DMGC_Y_D_CSB::getFhl)
                    .or().eq(DMGC_Y_D_CSB::getBxl, 0)
                    .or().eq(DMGC_Y_D_CSB::getJlssl, 0)
                    .or().eq(DMGC_Y_D_CSB::getHlRate, 0)
                    .or().eq(DMGC_Y_D_CSB::getFhl, 0));
            return (long) mapper.selectList(wrapper).size() > 0;
        } catch (Exception ex) {
            return false;
        }
    }

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
            LambdaQueryWrapper<DMGC_Y_D_CSB> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DMGC_Y_D_CSB::getRq, createDate);
            List<DMGC_Y_D_CSB> dmgcYDCsbs = mapper.selectList(wrapper);
            if (dmgcYDCsbs.size() == 0) {
                List<DMGC_Y_JB> jbList = jbService.getByName("掺水泵");
                for (DMGC_Y_JB param : jbList) {
                    DMGC_Y_D_CSB csb = new DMGC_Y_D_CSB();
                    csb.setEventId(UUID.randomUUID().toString());
                    csb.setEquipName(param.getMc());
                    csb.setJbEventId(param.getEventId());
                    csb.setRq(createDate);
                    csb.setSszkEventId(param.getSszkEventId());
                    csb.setZm(param.getSszkName());
                    csb.setZnbh(param.getZnbh());
                    dmgcYDCsbs.add(csb);
                }
                this.saveBatch(dmgcYDCsbs);
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
            result = false;
        }
        return result;
    }

    private LambdaQueryWrapper<DMGC_Y_D_CSB> buildQueryWrapper(DMGC_Y_D_CSB example, String startDate, String endDate) {
        LambdaQueryWrapper<DMGC_Y_D_CSB> queryWrapper = new LambdaQueryWrapper<>();
        if (example != null) {
            if (StringUtils.isNotEmpty(example.getJbEventId())) {
                queryWrapper.eq(DMGC_Y_D_CSB::getJbEventId, example.getJbEventId());
            }
            if (StringUtils.isNotEmpty(example.getSszkEventId())) {
                queryWrapper.eq(DMGC_Y_D_CSB::getSszkEventId, example.getSszkEventId());
            }
        }
        if (!StringUtils.isEmpty(startDate)) {
            try {
                Date date = DateUtils.parse(startDate, DateUtils.DATE_PATTERN);
                queryWrapper.ge(DMGC_Y_D_CSB::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!StringUtils.isEmpty(endDate)) {
            try {
                Date date = DateUtils.parse(endDate + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                queryWrapper.le(DMGC_Y_D_CSB::getRq, date);
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

    public List<DMGC_Y_D_CSB> getDataOfDay(String startDate, String endDate) {
        List<DMGC_Y_D_CSB> dataList = mapper.selectList(this.buildQueryWrapper(null, startDate, endDate));
        return dataList;
    }

    public List<DMGC_Y_D_CSB> getDataOfMonth(String startDate, String endDate) {
        List<DMGC_Y_D_CSB> dataList = new ArrayList<>();
        String queryStart;
        String queryEnd;
        try {
            queryStart = DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getMonth(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-01";
            queryEnd = DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getMonth(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getDay(DateUtils.parse(DateUtils.getLastDay(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)), DateUtils.DATE_PATTERN));
            LambdaQueryWrapper<DMGC_Y_D_CSB> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DMGC_Y_D_CSB::getYxzt, "01");
            queryWrapper.ge(DMGC_Y_D_CSB::getRq, queryStart);
            queryWrapper.le(DMGC_Y_D_CSB::getRq, queryEnd);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getBxl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getJlssl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getHlRate);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getFhl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getBxl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getJlssl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getHlRate);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getFhl);
            queryWrapper.ne(DMGC_Y_D_CSB::getBxl, 0);
            queryWrapper.ne(DMGC_Y_D_CSB::getJlssl, 0);
            queryWrapper.ne(DMGC_Y_D_CSB::getHlRate, 0);
            queryWrapper.ne(DMGC_Y_D_CSB::getFhl, 0);
            queryWrapper.ne(DMGC_Y_D_CSB::getJbScore, 0);
            dataList = mapper.selectList(queryWrapper);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return dataList;
    }

    public List<DMGC_Y_D_CSB> getDataOfYear(String startDate, String endDate) {
        List<DMGC_Y_D_CSB> dataList = new ArrayList<>();
        String queryStart;
        String queryEnd;
        try {
            queryStart = DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-01-01";
            queryEnd = DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-12-31";
            LambdaQueryWrapper<DMGC_Y_D_CSB> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DMGC_Y_D_CSB::getYxzt, "01");
            queryWrapper.ge(DMGC_Y_D_CSB::getRq, queryStart);
            queryWrapper.le(DMGC_Y_D_CSB::getRq, queryEnd);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getBxl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getJlssl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getHlRate);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getFhl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getBxl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getJlssl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getHlRate);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getFhl);
            queryWrapper.ne(DMGC_Y_D_CSB::getBxl, 0);
            queryWrapper.ne(DMGC_Y_D_CSB::getJlssl, 0);
            queryWrapper.ne(DMGC_Y_D_CSB::getHlRate, 0);
            queryWrapper.ne(DMGC_Y_D_CSB::getFhl, 0);
            queryWrapper.ne(DMGC_Y_D_CSB::getJbScore, 0);
            dataList = mapper.selectList(queryWrapper);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return dataList;
    }

    private void getCompleteData(List<DMGC_Y_D_CSB> partData) {
        if (partData.size() == 0) {
            return;
        }
        List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("掺水泵机组监测项目与指标要求", null);
        List<String> jbIds = partData.stream().map(c -> c.getJbEventId()).distinct().collect(Collectors.toList());
        List<DMGC_Y_JB> dmgcYJbList = jbService.getByEventIds(jbIds);
        List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("tsz")).collect(Collectors.toList());
        for (DMGC_Y_D_CSB param : partData) {
            Optional<DMGC_Y_JB> jbOptional = dmgcYJbList.stream().filter(c -> c.getEventId().equals(param.getJbEventId())).findFirst();
            jbOptional.ifPresent(jb -> {
                if (indicatorsList.size() > 0) {
                    param.setBxlWeight(indicatorsList.stream().filter(c -> c.getLevelCode().equals("level5") && c.getItemCode().equals("efficiency")).collect(Collectors.toList()).get(0).getWeight());
                    MonitoringIndicatorNew monitoringIndicatorNew = null;
                    //无调整泵（电机变频器编号字段有无数据）
                    if (jb.getDjbpqbh() == null) {
                        monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "机组效率(无调速)");
                    }
                    //有调速泵
                    if (jb.getBlx().equals("02")) {
                        monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "机组效率(有调速)");
                    }
                    if (monitoringIndicatorNew != null && param.getBxl() != null) {
                        param.setBxlScore(Calculation.efficiency(param.getBxl(), monitoringIndicatorNew));
                        param.setBxlWeightScore(Calculation.getMultiplicationResult(param.getBxlScore(), param.getBxlWeight()));
                        param.setBxlPj(Calculation.getEfficiencyComment(param.getBxlScore(), monitoringIndicatorNew));
                    }

                    monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "节流损失率");
                    param.setJlsslWeight(indicatorsList.stream().filter(c -> c.getLevelCode().equals("level5") && c.getItemCode().equals("loss")).collect(Collectors.toList()).get(0).getWeight());
                    if (monitoringIndicatorNew != null && param.getJlssl() != null) {
                        //节流损失率=（泵出口压力-泵所属汇管压力）/泵出口压力*100
                        param.setJlsslScore(Calculation.calculationOfUnitConsumption(param.getJlssl(), monitoringIndicatorNew));
                        param.setJlsslWeightScore(Calculation.getMultiplicationResult(param.getJlsslScore(), param.getJlsslWeight()));
                        param.setJlsslPj(Calculation.getUnitConsumptionComment(param.getJlsslScore(), monitoringIndicatorNew));
                    }

                    monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "回流损失率");
                    param.setHlRateWeight(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level5") && d.getItemCode().equals("hlssl")).collect(Collectors.toList()).get(0).getWeight());
                    if (monitoringIndicatorNew != null && param.getHlRate() != null) {
                        //回流率=回流量/流量*100
                        param.setHlRateScore(Calculation.calculationOfUnitConsumption(param.getHlRate(), monitoringIndicatorNew));
                        param.setHlRateWeightScore(Calculation.getMultiplicationResult(param.getHlRateScore(), param.getHlRateWeight()));
                        param.setHlRatePj(Calculation.getUnitConsumptionComment(param.getHlRateScore(), monitoringIndicatorNew));
                    }

                    monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "负荷率");
                    param.setFhlWeight(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level5") && d.getItemCode().equals("fhl")).collect(Collectors.toList()).get(0).getWeight());
                    if (monitoringIndicatorNew != null && param.getFhl() != null) {
                        //负荷率=流量/额定流量*100
                        param.setFhlScore(Calculation.specialFhl(param.getFhl(), monitoringIndicatorNew));
                        param.setFhlWeightScore(Calculation.getMultiplicationResult(param.getFhlScore(), param.getFhlWeight()));
                        param.setFhlPj(Calculation.specialFhlComment(param.getFhlScore(), monitoringIndicatorNew));
                    }

                    monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "绩效评价");
                    if (monitoringIndicatorNew != null && param.getBxlWeightScore() != null && param.getJlsslWeightScore() != null
                            && param.getHlRateWeightScore() != null && param.getFhlWeightScore() != null) {
                        param.setJbScore(Calculation.getPlusResult((param.getBxlWeightScore() + param.getJlsslWeightScore() + param.getHlRateWeightScore() + param.getFhlWeightScore()), 0));
                        param.setJbPj(Calculation.getEfficiencyComment(param.getJbScore(), monitoringIndicatorNew));
                    }
                }
            });
        }
    }

    private List<DMGC_Y_D_CSB> buildAssessment(List<DMGC_Y_D_CSB> dmgcSYBList, Date sumStart, Date sumEnd) {
        if (dmgcSYBList.size() == 0) {
            return new ArrayList<>();
        }
        List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("掺水泵机组监测项目与指标要求", null);
        List<String> jbIds = dmgcSYBList.stream().map(c -> c.getJbEventId()).distinct().collect(Collectors.toList());
        List<DMGC_Y_JB> dmgcYJbList = jbService.getByEventIds(jbIds);
        List<DMGC_Y_D_CSB> resultList = new ArrayList<>();
        for (String jbId : jbIds) {
            if (resultList.stream().filter(c -> c.getJbEventId().equals(jbId)).count() != 0) {
                continue;
            }
            Date finalSumStart = sumStart;
            Date finalSumEnd = sumEnd;

            DMGC_Y_D_CSB result = new DMGC_Y_D_CSB();
            DMGC_Y_D_CSB temp = dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId)).findFirst().get();
            result.setJbEventId(jbId);
            result.setEquipName(temp.getEquipName());
            result.setEventId(temp.getEventId());
            result.setZm(temp.getZm());
            result.setRq(sumEnd);
            result.setSszkEventId(temp.getSszkEventId());
            result.setZnbh(temp.getZnbh());

            long jbIdCount = dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime()
                    && (c.getBxl() != null || c.getFhl() != null || c.getJlssl() != null || c.getHlRate() != null)).count();
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
//                    result.setBxlWeight(temp.getBxlWeight());
                    result.setBxlScore(Calculation.getDivisionResult(dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getBxl() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_CSB::getBxlScore).sum(), jbIdCount));
//                    result.setBxlWeightScore(Calculation.getMultiplicationResult(result.getBxlScore(), result.getBxlWeight()));
                    result.setBxlWeightScore(Calculation.getDivisionResult(dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getBxl() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_CSB::getBxlWeightScore).sum(), jbIdCount));
                    result.setBxlPj(Calculation.getEfficiencyComment(result.getBxlScore(), monitoringIndicatorNew));
                }

                monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "节流损失率");
                if (monitoringIndicatorNew != null) {
//                    result.setJlsslWeight(temp.getJlsslWeight());
                    result.setJlsslScore(Calculation.getDivisionResult(dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getJlsslScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_CSB::getJlsslScore).sum(), jbIdCount));
//                    result.setJlsslWeightScore(Calculation.getMultiplicationResult(result.getJlsslScore(), result.getJlsslWeight()));
                    result.setJlsslWeightScore(Calculation.getDivisionResult(dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getJlsslScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_CSB::getJlsslWeightScore).sum(), jbIdCount));
                    result.setJlsslPj(Calculation.getUnitConsumptionComment(result.getJlsslScore(), monitoringIndicatorNew));
                }


                monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "回流损失率");
                if (monitoringIndicatorNew != null) {
//                    result.setHlRateWeight(temp.getHlRateWeight());
                    result.setHlRateScore(Calculation.getDivisionResult(dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getHlRateScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_CSB::getHlRateScore).sum(), jbIdCount));
//                    result.setHlRateWeightScore(Calculation.getMultiplicationResult(result.getHlRateScore(), result.getHlRateWeight()));
                    result.setHlRateWeightScore(Calculation.getDivisionResult(dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getHlRateScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_CSB::getHlRateWeightScore).sum(), jbIdCount));
                    result.setHlRatePj(Calculation.getUnitConsumptionComment(result.getHlRateScore(), monitoringIndicatorNew));
                }

                monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "负荷率");
                if (monitoringIndicatorNew != null) {
//                    result.setFhlWeight(temp.getFhlWeight());
                    result.setFhlScore(Calculation.getDivisionResult(dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getFhlScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_CSB::getFhlScore).sum(), jbIdCount));
//                    result.setFhlWeightScore(Calculation.getMultiplicationResult(result.getFhlScore(), result.getFhlWeight()));
                    result.setFhlWeightScore(Calculation.getDivisionResult(dmgcSYBList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getFhlScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_CSB::getFhlWeightScore).sum(), jbIdCount));
                    result.setFhlPj(Calculation.specialFhlComment(result.getFhlScore(), monitoringIndicatorNew));
                }

                monitoringIndicatorNew = filterByQueryData(jb.getBedll(), monitoringIndicatorNewList, "绩效评价");
                if (monitoringIndicatorNew != null && result.getBxlWeightScore() != null && result.getJlsslWeightScore() != null
                        && result.getHlRateWeightScore() != null && result.getFhlWeightScore() != null) {
                    result.setJbScore(Calculation.getPlusResult((result.getBxlWeightScore() + result.getJlsslWeightScore() + result.getHlRateWeightScore() + result.getFhlWeightScore()), 0));
                    result.setJbPj(Calculation.getEfficiencyComment(result.getJbScore(), monitoringIndicatorNew));
                }
            });
            resultList.add(result);
        }
        return resultList;
    }

    public PageVO<DMGC_Y_D_CSB> getAssessment(String cycle, String assessmentDate, int page, int size,DMGC_Y_D_CSB example) {
        PageVO<DMGC_Y_D_CSB> pageVO = null;
        List<DMGC_Y_D_CSB> tempResult = new ArrayList<>();
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
            List<DMGC_Y_D_CSB> dataList = tempResult;
            if (cycle.equals("月") || cycle.equals("年")) {
                dataList = buildAssessment(tempResult, sumStart, sumEnd);
            }
            if(example != null){
                if(StringUtils.isNotEmpty(example.getZm())){
                    dataList = dataList.stream().filter(c->c.getZm() != null && c.getZm().contains(example.getZm())).collect(Collectors.toList());
                }

                if(StringUtils.isNotEmpty(example.getEquipName())){
                    dataList = dataList.stream().filter(c->c.getEquipName() != null && c.getEquipName().contains(example.getEquipName())).collect(Collectors.toList());
                }

                if(StringUtils.isNotEmpty(example.getZnbh())){
                    dataList = dataList.stream().filter(c->c.getZnbh() != null && c.getZnbh().contains(example.getZnbh())).collect(Collectors.toList());
                }

                if (StringUtils.isNotEmpty(example.getJbPj())) {
                    if ("不合格".equals(example.getJbPj())) {
                        dataList = dataList.stream().filter(c -> (StringUtils.isEmpty(c.getBxlPj()) || "不合格".equals(c.getBxlPj()))
                                || (StringUtils.isEmpty(c.getJlsslPj()) || "不合格".equals(c.getJlsslPj()))
                                || (StringUtils.isEmpty(c.getHlRatePj()) || "不合格".equals(c.getHlRatePj()))
                                || (StringUtils.isEmpty(c.getFhlPj()) || "不合格".equals(c.getFhlPj()))
                                || (StringUtils.isEmpty(c.getJbPj()) || "不合格".equals(c.getJbPj()))).collect(Collectors.toList());
                    } else {
                        dataList = dataList.stream().filter(c -> (StringUtils.isNotEmpty(c.getBxlPj()) && !"不合格".equals(c.getBxlPj()))
                                && (StringUtils.isNotEmpty(c.getJlsslPj()) && !"不合格".equals(c.getJlsslPj()))
                                && (StringUtils.isNotEmpty(c.getHlRatePj()) && !"不合格".equals(c.getHlRatePj()))
                                && (StringUtils.isNotEmpty(c.getFhlPj()) && !"不合格".equals(c.getFhlPj()))
                                && (StringUtils.isNotEmpty(c.getJbPj()) && !"不合格".equals(c.getJbPj()))).collect(Collectors.toList());
                    }
                }
            }
            dataList = dataList.stream().sorted(Comparator.comparing(DMGC_Y_D_CSB::getZm)).collect(Collectors.toList());
//            PageMethod.startPage(page, size);
            PageInfo<DMGC_Y_D_CSB> pageInfo = PageInfo.of(dataList);
            List<DMGC_Y_D_CSB> pageList = dataList.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
            pageVO = new PageVO<>(pageInfo.getTotal(), pageList);
        } catch (Exception e) {
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    public List<DMGC_Y_D_CSB> getAssessmentNoPage(String cycle, String assessmentDate) {
        List<DMGC_Y_D_CSB> result = new ArrayList<>();
        List<DMGC_Y_D_CSB> tempResult = new ArrayList<>();
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
            result = result.stream().sorted(Comparator.comparing(DMGC_Y_D_CSB::getZm).thenComparing(DMGC_Y_D_CSB::getEquipName)).collect(Collectors.toList());
            return result;
        } catch (Exception ex) {
            String err = ex.getMessage();
            return result;
        }
    }

    public List<PieOption> getAllStaticsOfPipe(String rq) {
        List<DMGC_Y_D_CSB> tempResult = getDataOfDay(rq, rq).stream().filter(c->"01".equals(c.getYxzt())).collect(Collectors.toList());
        List<PieOption> result = new ArrayList<>();
        result.add(getStaticOfRunStatic(tempResult));
        result.add(getStaticOfJxpj(tempResult));
        result.add(getStatisticsOfRunningState(tempResult));
        result.add(getStatisticsOfThrottlingLoss(tempResult));
        result.add(getStaticOfHlRate(tempResult));
        result.add(getStaticOfFhl(tempResult));
        return result;
    }

    private PieOption getStaticOfRunStatic(List<DMGC_Y_D_CSB> tempResult) {
        PieOption result = new PieOption();
        try {
            long unqualified = tempResult.stream()
                    .filter(c -> "不合格".equals(c.getBxlPj()) || StringUtils.isEmpty(c.getBxlPj())
                            || "不合格".equals(c.getJlsslPj()) || StringUtils.isEmpty(c.getJlsslPj())
                            || "不合格".equals(c.getHlRatePj()) || StringUtils.isEmpty(c.getHlRatePj())
                            || "不合格".equals(c.getFhlPj()) || StringUtils.isEmpty(c.getFhlPj())
                            || "不合格".equals(c.getJbPj()) || StringUtils.isEmpty(c.getJbPj())).count();
            result.setTitle("掺水泵综合运行评价");
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

    private PieOption getStaticOfJxpj(List<DMGC_Y_D_CSB> tempResult) {
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

    private PieOption getStatisticsOfRunningState(List<DMGC_Y_D_CSB> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("机组效率");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("低效区");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getBxlPj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("高效区");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "良好".equals(c.getBxlPj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("合理区");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getBxlPj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private PieOption getStatisticsOfThrottlingLoss(List<DMGC_Y_D_CSB> tempResult) {
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

    private PieOption getStaticOfHlRate(List<DMGC_Y_D_CSB> tempResult) {
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

    private PieOption getStaticOfFhl(List<DMGC_Y_D_CSB> tempResult) {
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
        List<DMGC_Y_D_CSB> dmgcSDZsbrsjList = new ArrayList<>();
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
        dmgcSDZsbrsjList = dmgcSDZsbrsjList.stream().sorted(Comparator.comparing(DMGC_Y_D_CSB::getRq).thenComparing(DMGC_Y_D_CSB::getJbEventId)).collect(Collectors.toList());
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
                List<DMGC_Y_D_CSB> temp = dmgcSDZsbrsjList.stream().filter(c -> c.getJbEventId().equals(jbId)).collect(Collectors.toList());
                for (String date : result.getXAxis()) {
                    Date d = DateUtils.parse(date, DateUtils.DATE_PATTERN);
                    Optional<DMGC_Y_D_CSB> optional = temp.stream().filter(c -> {
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
                        optional.ifPresent(c -> mySerie.getData().add(c.getBxl()));
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

    public List<DMGC_Y_D_CSB> getEffectiveDataOfDay(List<String> zids, String queryDate) {
        List<DMGC_Y_D_CSB> result = new ArrayList<>();
        try {
            Date date = DateUtils.parse(queryDate, DateUtils.DATE_PATTERN);
            LambdaQueryWrapper<DMGC_Y_D_CSB> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(DMGC_Y_D_CSB::getSszkEventId, zids);
            queryWrapper.eq(DMGC_Y_D_CSB::getYxzt, "01");
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getBxl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getJlssl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getHlRate);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getFhl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getBxl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getJlssl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getHlRate);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getFhl);
            queryWrapper.ne(DMGC_Y_D_CSB::getBxl, 0);
            queryWrapper.ne(DMGC_Y_D_CSB::getJlssl, 0);
            queryWrapper.ne(DMGC_Y_D_CSB::getHlRate, 0);
            queryWrapper.ne(DMGC_Y_D_CSB::getFhl, 0);
            queryWrapper.eq(DMGC_Y_D_CSB::getRq, date);
            result = mapper.selectList(queryWrapper);
            return result;
        } catch (Exception ex) {
            return result;
        }
    }

    public List<DMGC_Y_D_CSB> getEffectiveData(Date queryStartDate,Date queryEndDate) {
        List<DMGC_Y_D_CSB> result = new ArrayList<>();
        try {
            LambdaQueryWrapper<DMGC_Y_D_CSB> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DMGC_Y_D_CSB::getYxzt, "01");
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getBxl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getJlssl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getHlRate);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getFhl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getBxl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getJlssl);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getHlRate);
            queryWrapper.isNotNull(DMGC_Y_D_CSB::getFhl);
            queryWrapper.ne(DMGC_Y_D_CSB::getBxl, 0);
            queryWrapper.ne(DMGC_Y_D_CSB::getJlssl, 0);
            queryWrapper.ne(DMGC_Y_D_CSB::getHlRate, 0);
            queryWrapper.ne(DMGC_Y_D_CSB::getFhl, 0);
            queryWrapper.ge(DMGC_Y_D_CSB::getRq, queryStartDate);
            queryWrapper.le(DMGC_Y_D_CSB::getRq, queryEndDate);
            result = mapper.selectList(queryWrapper);
            return result;
        } catch (Exception ex) {
            return result;
        }
    }

    public void exportExamineData(String queryDate,
                                  HttpServletRequest request, HttpServletResponse response) {

        Workbook workbook = ExportUtils.createWorkbook();
        try {
            List<DMGC_Y_D_CSB> dataList = getAssessmentNoPage("日",queryDate).stream().filter(c->c.getJbScore() != null).collect(Collectors.toList());
            Map<String, List<String>> headerNamesAndFieldsMap = ExportUtils.getExamineHeaderNamesAndFields(DMGC_Y_D_CSB.class);
            List<String> headerNames = headerNamesAndFieldsMap.get("headerNames");
            List<String> fields = headerNamesAndFieldsMap.get("fields");
            ExportUtils.exportExcel("集输系统掺水泵日考核", headerNames, fields, 0, workbook, dataList);
            ExportUtils.responseWorkbook("集输系统掺水泵日考核", workbook, request, response);
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
}
