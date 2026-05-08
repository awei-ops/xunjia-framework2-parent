package com.xunjia.pes.bizData.waterTreatment.service;

import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.basicDataManage.entity.Indicators;
import com.xunjia.pes.basicDataManage.service.IndicatorsService;
import com.xunjia.pes.bizData.assessment.entity.MonitoringIndicatorNew;
import com.xunjia.pes.bizData.assessment.service.MonitoringIndicatorNewService;
import com.xunjia.pes.bizData.waterInjection.entity.Station_pj;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_D_SCLZRSJ;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_SCLZ;
import com.xunjia.pes.score.Calculation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class SCLZ_JXPJService {
    @Autowired
    @Lazy(true)
    private DMGC_S_D_SCLZRSJService dmgcSDSclzrsjService;

    @Autowired
    private DMGC_S_SCLZService sclzService;

    @Autowired
    private MonitoringIndicatorNewService monitoringIndicatorNewService;
    @Autowired
    private IndicatorsService indicatorsService;

    public List<Station_pj> getSCLZJXPJ(String cycle, String assessmentDate) {
        List<Station_pj> result = new ArrayList<>();
        List<Station_pj> myResult = new ArrayList<>();
        try {
//            List<DMGC_S_D_SCLZRSJ> dmgcSDSclzrsjs = dmgcSDSclzrsjService.getAssessmentNoPage(cycle, assessmentDate);
            List<DMGC_S_D_SCLZRSJ> dmgcSDSclzrsjs = dmgcSDSclzrsjService.getEffectiveDataOfDay(assessmentDate);
            List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("sclz")).collect(Collectors.toList());
            Date sumStart = DateUtils.parse(assessmentDate, DateUtils.DATE_PATTERN);
            Date sumEnd = DateUtils.parse(assessmentDate + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
            switch (cycle) {
                case "月":
                    sumStart = DateUtils.parse(DateUtils.getFirstDay(sumStart), DateUtils.DATE_PATTERN);
                    sumEnd = DateUtils.parse(DateUtils.getLastDay(sumStart) + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                    break;
                case "年":
                    sumStart = DateUtils.parse(DateUtils.getYear(sumStart) + "-01-01", DateUtils.DATE_PATTERN);
                    sumEnd = DateUtils.parse(DateUtils.getYear(sumStart) + "-12-31 23:59:59", DateUtils.DATE_TIME_PATTERN);
                    break;
            }
            for (DMGC_S_D_SCLZRSJ param : dmgcSDSclzrsjs) {
                List<Indicators> level2 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level2")).collect(Collectors.toList());
                for (Indicators indicators : level2) {
                    Station_pj zszJxpj = new Station_pj();
                    zszJxpj.setStationId(param.getZkEventId());
                    zszJxpj.setStationName(param.getZmc());
                    zszJxpj.setRq(sumEnd);

                    Indicators levll1 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level1") && c.getItemCode().equals("economics")).findFirst().get();
                    zszJxpj.setLevelOne_name(levll1.getItemName());
                    zszJxpj.setLevelOne_weight(levll1.getWeight());

                    zszJxpj.setLevelTwo_name(indicators.getItemName());
                    zszJxpj.setLevelTwo_weight(indicators.getWeight());
                    switch (indicators.getItemCode()) {
                        case "sclzdh":
                            zszJxpj.setLevelTwo_score(param.getDhScore());
                            break;
                        case "wswsdh":
                            zszJxpj.setLevelTwo_score(param.getWswsdhScore());
                            break;
                    }
                    result.add(zszJxpj);
                }
            }
            getComplete(result);
            result = result.stream().sorted(Comparator.comparing(Station_pj::getJx_score, Comparator.nullsFirst(Double::compareTo).reversed()).thenComparing(Station_pj::getStationName)).collect(Collectors.toList());
            List<String> stationNames = result.stream().map(Station_pj::getStationName).distinct().collect(Collectors.toList());
            List<Station_pj> finalResult = result;
            stationNames.forEach(stationName -> {
                List<Station_pj> temp = finalResult.stream().filter(c -> c.getStationName().equals(stationName)).collect(Collectors.toList());
                temp = temp.stream().sorted(Comparator.comparing(Station_pj::getLevelOne_name)
                        .thenComparing(Station_pj::getLevelTwo_name)
                        .thenComparing(Station_pj::getLevelThree_name)
                        .thenComparing(Station_pj::getEquipmentId, Comparator.nullsLast(String::compareTo))).collect(Collectors.toList());
                myResult.addAll(temp);
            });
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return myResult;
    }

    private void getComplete(List<Station_pj> partData) {
        for (Station_pj param : partData) {
            List<Station_pj> temp = partData.stream().filter(c -> c.getStationId().equals(param.getStationId())).collect(Collectors.toList());
            double level1 = 0;
            for (Station_pj stationPj : temp) {
                if (stationPj.getLevelTwo_score() != null) {
                    level1 = Calculation.getPlusResult(level1, Calculation.getMultiplicationResult(stationPj.getLevelTwo_score(), stationPj.getLevelTwo_weight()));
                }
            }
            param.setLevelOne_score(level1);
            if (param.getLevelOne_score() != null) {
                param.setJx_score(Calculation.getMultiplicationResult(param.getLevelOne_score(), param.getLevelOne_weight()));
            }
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
            //水处理站需要更新经济指标的分数
//            dmgcSDSclzrsjService.updateData(rq);
            List<Station_pj> temp = getSCLZJXPJ("日", nowDateString);
//            List<DMGC_S_D_SCLZRSJ> sclzList = dmgcSDSclzrsjService.getAssessmentNoPage("日", nowDateString);
            List<DMGC_S_D_SCLZRSJ> sclzList = dmgcSDSclzrsjService.getEffectiveDataOfDay(nowDateString);
            List<DMGC_S_D_SCLZRSJ> updateSclz = new ArrayList<>();
            List<String> sclzIds = temp.stream().map(c -> c.getStationId()).distinct().collect(Collectors.toList());
            for (String sclzId : sclzIds) {
                Station_pj myStation = temp.stream().filter(c -> c.getStationId().equals(sclzId)).collect(Collectors.toList()).get(0);
                Optional<DMGC_S_D_SCLZRSJ> optional = sclzList.stream().filter(c -> c.getZkEventId().equals(sclzId)).findFirst();
                optional.ifPresent(c -> {
                    c.setJxpjScore(myStation.getJx_score());
                    updateSclz.add(c);
                });
            }
            if (updateSclz.size() != 0) {
                dmgcSDSclzrsjService.updateData(updateSclz);
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
            result = false;
        }
        return result;
    }

    public List<Station_pj> getRelativeJXPJ(String cycle, String zid, String queryDateString) {
        List<Station_pj> result = new ArrayList<>();
        try {
            Date temp = DateUtils.parse(queryDateString, DateUtils.DATE_PATTERN);
            Date queryStart;
            Date nextStart;
            Date queryEnd;
            if (cycle.equals("月")) {
                queryStart = DateUtils.parse(DateUtils.getFirstDay(temp), DateUtils.DATE_PATTERN);
                nextStart = DateUtils.addMonth(queryStart, 1);
                queryEnd = DateUtils.parse(DateUtils.getLastDay(queryStart), DateUtils.DATE_PATTERN);
            } else {
                queryStart = DateUtils.parse(DateUtils.getYear(temp) + "-01-01", DateUtils.DATE_PATTERN);
                nextStart = DateUtils.addYear(queryStart, 1);
                queryEnd = DateUtils.parse(DateUtils.getYear(queryStart) + "-12-31", DateUtils.DATE_PATTERN);
            }
            List<DMGC_S_D_SCLZRSJ> myDatas = dmgcSDSclzrsjService.getDataForPortrait(zid, queryStart, queryEnd);
            List<String> stationIds = myDatas.stream().map(DMGC_S_D_SCLZRSJ::getZkEventId).distinct().collect(Collectors.toList());
            if (!myDatas.isEmpty()) {
                for (String stationId : stationIds) {
                    Station_pj stationPj = new Station_pj();
                    stationPj.setRq(queryStart);
                    stationPj.setStationId(stationId);
                    stationPj.setStationName(myDatas.stream().filter(c -> c.getZkEventId().equals(stationId)).collect(Collectors.toList()).get(0).getZmc());
                    long count = 1;
                    double curScore = 0;

                    count = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0 && c.getJxpjScore() != null).count();
                    curScore = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0 && c.getJxpjScore() != null)
                            .mapToDouble(DMGC_S_D_SCLZRSJ::getJxpjScore).sum();
                    curScore = Calculation.getDivisionResult(curScore, count);
                    stationPj.setJx_score(curScore);
                    result.add(stationPj);
                }
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        result = result.stream().sorted(Comparator.comparing(Station_pj::getJx_score, Comparator.nullsLast(Double::compareTo)).reversed()).collect(Collectors.toList());
        return result;
    }


    public List<Station_pj> getSinglePortraitJXPJ(String cycle, String zid, String queryDateString) {
        List<Station_pj> result = new ArrayList<>();
        try {
            Date temp = DateUtils.parse(queryDateString, DateUtils.DATE_PATTERN);
            Date queryStart;
            Date preStart;
            Date preEnd;
            Date nextStart;
            Date queryEnd;
            if (cycle.equals("月")) {
                queryStart = DateUtils.parse(DateUtils.getFirstDay(temp), DateUtils.DATE_PATTERN);
                //纵向绩效对比去年同期
                preStart = DateUtils.addYear(queryStart, -1);
                preEnd = DateUtils.addDate(DateUtils.addMonth(preStart, 1), -1);
                nextStart = DateUtils.addMonth(queryStart, 1);
                queryEnd = DateUtils.parse(DateUtils.getLastDay(queryStart), DateUtils.DATE_PATTERN);
            } else {
                queryStart = DateUtils.parse(DateUtils.getYear(temp) + "-01-01", DateUtils.DATE_PATTERN);
                preStart = DateUtils.addYear(queryStart, -1);
                preEnd = DateUtils.parse(DateUtils.getYear(preStart) + "-12-31", DateUtils.DATE_PATTERN);
                nextStart = DateUtils.addYear(queryStart, 1);
                queryEnd = DateUtils.parse(DateUtils.getYear(queryStart) + "-12-31", DateUtils.DATE_PATTERN);
            }
            List<DMGC_S_D_SCLZRSJ> myDatas = dmgcSDSclzrsjService.getDataForPortrait(zid, preStart, preEnd);
            myDatas.addAll(dmgcSDSclzrsjService.getDataForPortrait(zid, queryStart, queryEnd));
            List<String> stationIds = myDatas.stream().map(DMGC_S_D_SCLZRSJ::getZkEventId).distinct().collect(Collectors.toList());
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("纵向绩效指标要求", null);
            if (!monitoringIndicatorNewList.isEmpty() && !myDatas.isEmpty()) {
                for (String stationId : stationIds) {
                    Station_pj stationPj = new Station_pj();
                    stationPj.setRq(queryStart);
                    stationPj.setStationId(stationId);
                    stationPj.setStationName(myDatas.stream().filter(c -> c.getZkEventId().equals(stationId)).collect(Collectors.toList()).get(0).getZmc());
                    long count = 1;
                    double preScore = 0;
                    double curScore = 0;

                    count = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(preEnd) <= 0).count();
                    preScore = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(preEnd) <= 0)
                            .mapToDouble(DMGC_S_D_SCLZRSJ::getJxpjScore).sum();
                    preScore = Calculation.getDivisionResult(preScore, count);

                    count = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0).count();
                    curScore = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0)
                            .mapToDouble(DMGC_S_D_SCLZRSJ::getJxpjScore).sum();
                    curScore = Calculation.getDivisionResult(curScore, count);
                    stationPj.setJx_score(Calculation.getPortrait(preScore, curScore, monitoringIndicatorNewList.get(0)));
                    result.add(stationPj);
                }
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        result = result.stream().sorted(Comparator.comparing(Station_pj::getJx_score, Comparator.nullsLast(Double::compareTo)).reversed()).collect(Collectors.toList());
        return result;
    }

    public List<Station_pj> getSingleComprehensiveJXPJ(String cycle, String zid, String queryDateString) {
        List<Station_pj> result = new ArrayList<>();
        try {
            Date temp = DateUtils.parse(queryDateString, DateUtils.DATE_PATTERN);
            Date queryStart;
            Date preStart;
            Date preEnd;
            Date nextStart;
            Date queryEnd;
            if (cycle.equals("月")) {
                queryStart = DateUtils.parse(DateUtils.getFirstDay(temp), DateUtils.DATE_PATTERN);
                //纵向绩效对比去年同期
                preStart = DateUtils.addYear(queryStart, -1);
                preEnd = DateUtils.addDate(DateUtils.addMonth(preStart, 1), -1);
                nextStart = DateUtils.addMonth(queryStart, 1);
                queryEnd = DateUtils.parse(DateUtils.getLastDay(queryStart), DateUtils.DATE_PATTERN);
            } else {
                queryStart = DateUtils.parse(DateUtils.getYear(temp) + "-01-01", DateUtils.DATE_PATTERN);
                preStart = DateUtils.addYear(queryStart, -1);
                preEnd = DateUtils.parse(DateUtils.getYear(preStart) + "-12-31", DateUtils.DATE_PATTERN);
                nextStart = DateUtils.addYear(queryStart, 1);
                queryEnd = DateUtils.parse(DateUtils.getYear(queryStart) + "-12-31", DateUtils.DATE_PATTERN);
            }
            List<DMGC_S_D_SCLZRSJ> myDatas = dmgcSDSclzrsjService.getDataForPortrait(zid, preStart, preEnd);
            myDatas.addAll(dmgcSDSclzrsjService.getDataForPortrait(zid, queryStart, queryEnd));
            List<DMGC_S_SCLZ> sclzList = sclzService.getAll();
            if(StringUtils.isNotEmpty(zid)){
                sclzList = sclzList.stream().filter(c->c.getEventId().equals(zid)).collect(Collectors.toList());
            }
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("纵向绩效指标要求", null);
            List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("zhjx")).collect(Collectors.toList());
            if (!monitoringIndicatorNewList.isEmpty() && !myDatas.isEmpty()) {
                for (DMGC_S_SCLZ dmgcSSclz : sclzList) {
                    String stationId = dmgcSSclz.getEventId();
                    Station_pj stationPj = new Station_pj();
                    stationPj.setRq(queryStart);
                    stationPj.setStationId(stationId);
                    stationPj.setStationName(dmgcSSclz.getMc());
                    stationPj.setLevelOne_weight(indicatorsList.stream().filter(c -> c.getItemCode().equals("xdjxqz")).collect(Collectors.toList()).get(0).getWeight());//用来表示相对（横向）权重
                    stationPj.setLevelThree_weight(indicatorsList.stream().filter(c -> c.getItemCode().equals("zxjxqz")).collect(Collectors.toList()).get(0).getWeight());//用来表示纵向权重
                    long count = 1;
                    double preScore = 0;
                    double curScore = 0;

                    count = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(preEnd) <= 0 && c.getJxpjScore() != null).count();
                    preScore = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(preEnd) <= 0 && c.getJxpjScore() != null)
                            .mapToDouble(DMGC_S_D_SCLZRSJ::getJxpjScore).sum();
                    preScore = Calculation.getDivisionResult(preScore, count);

                    count = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0 && c.getJxpjScore() != null).count();
                    curScore = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0 && c.getJxpjScore() != null)
                            .mapToDouble(DMGC_S_D_SCLZRSJ::getJxpjScore).sum();
                    curScore = Calculation.getDivisionResult(curScore, count);
                    stationPj.setLevelOne_score(curScore);//用来表示相对（横向）得分
                    stationPj.setLevelThree_score(Calculation.getPortrait(preScore, curScore, monitoringIndicatorNewList.get(0)));//用来表示纵向得分
                    stationPj.setLevelTwo_score(Calculation.getMultiplicationResult(stationPj.getLevelOne_score(), stationPj.getLevelOne_weight()));//用来表示相对（横向）权重得分
                    stationPj.setLevelFour_score(Calculation.getMultiplicationResult(stationPj.getLevelThree_score(), stationPj.getLevelThree_weight()));//用来表示纵向权重得分
                    stationPj.setJx_score(Calculation.getPlusResult(stationPj.getLevelTwo_score(), stationPj.getLevelFour_score()));
                    result.add(stationPj);
                }
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        result = result.stream().sorted(Comparator.comparing(Station_pj::getJx_score, Comparator.nullsLast(Double::compareTo)).reversed()).collect(Collectors.toList());
        return result;
    }

    public List<Station_pj> getPortraitJXPJ(String zid, String queryStart, String queryEnd) {
        List<Station_pj> result = new ArrayList<>();
        try {
            Date sumStart = DateUtils.parse(queryStart, DateUtils.DATE_PATTERN);
            sumStart = DateUtils.parse(DateUtils.getFirstDay(sumStart), DateUtils.DATE_PATTERN);
            Date preSumStart = DateUtils.addMonth(sumStart, -1);
            Date sumEnd = DateUtils.parse(queryEnd, DateUtils.DATE_PATTERN);
            sumEnd = DateUtils.parse(DateUtils.getLastDay(sumEnd), DateUtils.DATE_PATTERN);
            List<DMGC_S_D_SCLZRSJ> myDatas = dmgcSDSclzrsjService.getDataForPortrait(zid, preSumStart, sumEnd);
            List<String> stationIds = myDatas.stream().map(c -> c.getZkEventId()).distinct().collect(Collectors.toList());
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("纵向绩效指标要求", null);
            if (monitoringIndicatorNewList.size() != 0 && myDatas.size() != 0) {
                for (String stationId : stationIds) {
                    for (Date i = sumStart; i.compareTo(sumEnd) <= 0; i = DateUtils.addMonth(i, 1)) {
                        Station_pj stationPj = new Station_pj();
                        stationPj.setRq(i);
                        stationPj.setStationId(stationId);
                        stationPj.setStationName(myDatas.stream().filter(c -> c.getZkEventId().equals(stationId)).collect(Collectors.toList()).get(0).getZmc());
                        double preScore = 0;
                        double curScore = 0;
                        long count = 1;
                        if (!i.equals(sumStart)) {
                            Date preStart = DateUtils.addMonth(i, -1);
                            Date finalI = i;
                            count = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(finalI) < 0).count();
                            preScore = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(finalI) < 0)
                                    .mapToDouble(DMGC_S_D_SCLZRSJ::getJxpjScore).sum();
                            preScore = Calculation.getDivisionResult(preScore, count);

                            count = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(finalI) >= 0 && c.getRq().compareTo(DateUtils.addMonth(finalI, 1)) < 0).count();
                            curScore = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(finalI) >= 0 && c.getRq().compareTo(DateUtils.addMonth(finalI, 1)) < 0)
                                    .mapToDouble(DMGC_S_D_SCLZRSJ::getJxpjScore).sum();
                            curScore = Calculation.getDivisionResult(curScore, count);
                            stationPj.setJx_score(Calculation.getPortrait(preScore, curScore, monitoringIndicatorNewList.get(0)));
                        } else {
                            double jxScore = 0;
                            Date start = i;
                            count = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(start) >= 0 && c.getRq().compareTo(DateUtils.addMonth(start, 1)) < 0).count();
                            jxScore = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(start) >= 0 && c.getRq().compareTo(DateUtils.addMonth(start, 1)) < 0)
                                    .mapToDouble(DMGC_S_D_SCLZRSJ::getJxpjScore).sum();
                            jxScore = Calculation.getDivisionResult(jxScore, count);
                            stationPj.setJx_score(jxScore);
                        }
                        result.add(stationPj);
                    }
                }
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        result = result.stream().sorted(Comparator.comparing(Station_pj::getJx_score, Comparator.nullsLast(Double::compareTo)).reversed()).collect(Collectors.toList());
        return result;
    }

    public List<Station_pj> getComprehensiveJXPJ(String zid, String queryStart, String queryEnd) {
        List<Station_pj> result = new ArrayList<>();
        try {
            Date sumStart = DateUtils.parse(queryStart, DateUtils.DATE_PATTERN);
            sumStart = DateUtils.parse(DateUtils.getFirstDay(sumStart), DateUtils.DATE_PATTERN);
            Date preSumStart = DateUtils.addMonth(sumStart, -1);
            Date sumEnd = DateUtils.parse(queryEnd, DateUtils.DATE_PATTERN);
            sumEnd = DateUtils.parse(DateUtils.getLastDay(sumEnd), DateUtils.DATE_PATTERN);
            List<DMGC_S_D_SCLZRSJ> myDatas = dmgcSDSclzrsjService.getDataForPortrait(zid, preSumStart, sumEnd);
            List<String> stationIds = myDatas.stream().map(c -> c.getZkEventId()).distinct().collect(Collectors.toList());
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("纵向绩效指标要求", null);
            List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("zhjx")).collect(Collectors.toList());
            if (monitoringIndicatorNewList.size() != 0 && myDatas.size() != 0 && indicatorsList.size() != 0) {
                for (String stationId : stationIds) {
                    for (Date i = sumStart; i.compareTo(sumEnd) <= 0; i = DateUtils.addMonth(i, 1)) {
                        Station_pj stationPj = new Station_pj();
                        stationPj.setRq(i);
                        stationPj.setStationId(stationId);
                        stationPj.setStationName(myDatas.stream().filter(c -> c.getZkEventId().equals(stationId)).collect(Collectors.toList()).get(0).getZmc());
                        stationPj.setLevelOne_weight(indicatorsList.stream().filter(c -> c.getItemCode().equals("xdjxqz")).collect(Collectors.toList()).get(0).getWeight());//用来表示相对（横向）权重
                        stationPj.setLevelThree_weight(indicatorsList.stream().filter(c -> c.getItemCode().equals("zxjxqz")).collect(Collectors.toList()).get(0).getWeight());//用来表示纵向权重
                        long count = 1;
                        if (!i.equals(sumStart)) {
                            double preScore = 0;
                            double curScore = 0;
                            Date preStart = DateUtils.addMonth(i, -1);
                            Date finalI = i;
                            count = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(finalI) < 0).count();
                            preScore = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(finalI) < 0)
                                    .mapToDouble(DMGC_S_D_SCLZRSJ::getJxpjScore).sum();
                            preScore = Calculation.getDivisionResult(preScore, count);

                            count = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(finalI) >= 0 && c.getRq().compareTo(DateUtils.addMonth(finalI, 1)) < 0).count();
                            curScore = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(finalI) >= 0 && c.getRq().compareTo(DateUtils.addMonth(finalI, 1)) < 0)
                                    .mapToDouble(DMGC_S_D_SCLZRSJ::getJxpjScore).sum();
                            curScore = Calculation.getDivisionResult(curScore, count);
                            stationPj.setLevelOne_score(curScore);//用来表示相对（横向）得分
                            stationPj.setLevelThree_score(Calculation.getPortrait(preScore, curScore, monitoringIndicatorNewList.get(0)));//用来表示纵向得分
                        } else {
                            double relativeScore = 0;
                            Date start = i;
                            count = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(start) >= 0 && c.getRq().compareTo(DateUtils.addMonth(start, 1)) < 0).count();
                            relativeScore = myDatas.stream().filter(c -> c.getZkEventId().equals(stationId) && c.getRq().compareTo(start) >= 0 && c.getRq().compareTo(DateUtils.addMonth(start, 1)) < 0)
                                    .mapToDouble(DMGC_S_D_SCLZRSJ::getJxpjScore).sum();
                            relativeScore = Calculation.getDivisionResult(relativeScore, count);
                            stationPj.setLevelOne_score(relativeScore);//用来表示相对（横向）得分
                            stationPj.setLevelThree_score(relativeScore);//用来表示纵向得分
                        }
                        stationPj.setLevelTwo_score(Calculation.getMultiplicationResult(stationPj.getLevelOne_score(), stationPj.getLevelOne_weight()));//用来表示相对（横向）权重得分
                        stationPj.setLevelFour_score(Calculation.getMultiplicationResult(stationPj.getLevelThree_score(), stationPj.getLevelThree_weight()));//用来表示纵向权重得分
                        stationPj.setJx_score(Calculation.getPlusResult(stationPj.getLevelTwo_score(), stationPj.getLevelFour_score()));
                        result.add(stationPj);
                    }
                }
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }
}
