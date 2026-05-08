package com.xunjia.pes.bizData.waterInjection.service;

import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.basicDataManage.entity.Indicators;
import com.xunjia.pes.basicDataManage.service.IndicatorsService;
import com.xunjia.pes.bizData.DynamicWeightResult;
import com.xunjia.pes.bizData.DynamicWeights;
import com.xunjia.pes.bizData.assessment.entity.MonitoringIndicatorNew;
import com.xunjia.pes.bizData.assessment.service.MonitoringIndicatorNewService;
import com.xunjia.pes.bizData.waterInjection.entity.*;
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
public class ZSZ_JXPJService {
    @Autowired
    @Lazy(true)
    private DMGC_S_ZSZService zszService;
    @Autowired
    private DMGC_S_D_ZSBRSJService zsbrsjService;
    @Autowired
    private DMGC_S_D_ZSZRSJService zszrsjService;
    @Autowired
    private IndicatorsService indicatorsService;
    @Autowired
    private DMGC_S_JBService jbService;

    @Autowired
    private MonitoringIndicatorNewService monitoringIndicatorNewService;

    @Autowired
    private DynamicWeights dynamicWeights;

    public List<Station_pj> getZSZJXPJ(String cycle, String assessmentDate) {
        List<Station_pj> result = new ArrayList<>();
        List<Station_pj> myResult = new ArrayList<>();
        try {
            List<DMGC_S_ZSZ> dmgcSZszs = zszService.getAll();
            List<String> zids = dmgcSZszs.stream().map(c -> c.getEventId()).collect(Collectors.toList());
//            List<DMGC_S_D_ZSBRSJ> dmgcSDZsbrsjList = zsbrsjService.getAssessmentNoPage(cycle, assessmentDate);
            List<DMGC_S_D_ZSBRSJ> dmgcSDZsbrsjList = zsbrsjService.getEffectiveDataOfDay(zids, assessmentDate);
//            List<DMGC_S_D_ZSZRSJ> dmgcSDZszrsjList = zszrsjService.getAssessmentNoPage(cycle, assessmentDate);
            List<DMGC_S_D_ZSZRSJ> dmgcSDZszrsjList = zszrsjService.getEffectiveDataOfDay(assessmentDate);
            List<String> jbIds = dmgcSDZsbrsjList.stream().map(DMGC_S_D_ZSBRSJ::getJbEventId).distinct().collect(Collectors.toList());
            List<DMGC_S_JB> jbList = jbService.getByEventIds(jbIds);
            List<DynamicWeightResult> dynamicWeightResultList = dynamicWeights.getZszWeightMap(dmgcSZszs, jbList, dmgcSDZsbrsjList);
            List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("zsz")).collect(Collectors.toList());
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
            DynamicWeightResult dynamicWeightResult;
            if (dmgcSDZsbrsjList.size() != 0) {
//                List<String> jbIds = dmgcSDZsbrsjList.stream().map(c -> c.getJbEventId()).distinct().collect(Collectors.toList());
//                List<DMGC_S_JB> jbList = jbService.getByEventIds(jbIds);
                for (DMGC_S_D_ZSBRSJ param : dmgcSDZsbrsjList) {
                    dynamicWeightResult = dynamicWeightResultList.stream().filter(c -> c.getStationId().equals(param.getSszkEventId()) && c.getEquipId().equals(param.getJbEventId())).findFirst().get();
                    List<Indicators> level5 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level5")).collect(Collectors.toList());
                    for (Indicators indicators : level5) {
                        Station_pj zszJxpj = new Station_pj();
                        zszJxpj.setStationId(param.getSszkEventId());
                        zszJxpj.setStationName(param.getZszName());
                        zszJxpj.setEquipmentId(param.getJbEventId());
                        zszJxpj.setRq(sumEnd);

                        Indicators levll1 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level1") && c.getItemCode().equals("technical")).findFirst().get();
                        zszJxpj.setLevelOne_name(levll1.getItemName());
                        zszJxpj.setLevelOne_weight(levll1.getWeight());

                        Indicators levll2 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level2") && c.getItemCode().equals("pump")).findFirst().get();
                        zszJxpj.setLevelTwo_name(levll2.getItemName());
//                        zszJxpj.setLevelTwo_weight(levll2.getWeight());
                        zszJxpj.setLevelTwo_weight(dynamicWeightResult.getLevel2Weight());

                        Indicators levll3 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level3") && c.getItemCode().equals("water")).findFirst().get();
                        zszJxpj.setLevelThree_name(levll3.getItemName());
//                        zszJxpj.setLevelThree_weight(levll3.getWeight());
                        zszJxpj.setLevelThree_weight(dynamicWeightResult.getLevel3Weight());

                        zszJxpj.setLevelFour_name(param.getJbName() + param.getBbh());
//                        double djgl_all = jbList.stream().filter(c -> c.getSszkid().equals(param.getSszkEventId())).mapToDouble(DMGC_S_JB::getDjgl).sum();
//                        double djgl_current = jbList.stream().filter(c -> c.getEventId().equals(param.getJbEventId())).findFirst().get().getDjgl();
//                        zszJxpj.setLevelFour_weight(Calculation.getDivisionResult(djgl_current, djgl_all));
                        zszJxpj.setLevelFour_weight(dynamicWeightResult.getLevel4Weight());
                        zszJxpj.setLevelFour_score(param.getJbScore());

                        zszJxpj.setLevelFive_name(indicators.getItemName());
//                        zszJxpj.setLevelFive_weight(indicators.getWeight());

                        switch (indicators.getItemCode()) {
                            case "efficiency":
                                zszJxpj.setLevelFive_weight(param.getWeightBx());
                                zszJxpj.setLevelFive_score(param.getBxScore());
                                break;
                            case "loss":
                                zszJxpj.setLevelFive_weight(param.getWeightJlssl());
                                zszJxpj.setLevelFive_score(param.getJlsslScore());
                                break;
                            case "hlssl":
                                zszJxpj.setLevelFive_weight(param.getWeightHlRate());
                                zszJxpj.setLevelFive_score(param.getHlRateScore());
                                break;
                            case "fhl":
                                zszJxpj.setLevelFive_weight(param.getWeightFhl());
                                zszJxpj.setLevelFive_score(param.getFhlScore());
                                break;
                        }
                        result.add(zszJxpj);
                    }
                }
            }
            for (DMGC_S_D_ZSZRSJ param : dmgcSDZszrsjList) {
                Indicators levll2 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level2") && c.getItemCode().equals("zszzhdh")).findFirst().get();
                Station_pj zszJxpj = new Station_pj();
                zszJxpj.setStationId(param.getZid());
                zszJxpj.setStationName(param.getZmc());
                zszJxpj.setRq(sumEnd);

                Indicators levll1 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level1") && c.getItemCode().equals("economics")).findFirst().get();
                zszJxpj.setLevelOne_name(levll1.getItemName());
                zszJxpj.setLevelOne_weight(levll1.getWeight());

                zszJxpj.setLevelTwo_name(levll2.getItemName());
                zszJxpj.setLevelFive_weight(levll2.getWeight());
                zszJxpj.setLevelFive_score(param.getZhdhScore());
                result.add(zszJxpj);
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
        } catch (Exception error) {
            String err = error.getMessage();
        }
        return myResult;
    }

    private void getComplete(List<Station_pj> partData) {
        List<String> stations = partData.stream().map(c -> c.getStationName()).distinct().collect(Collectors.toList());
        for (String station : stations) {
            List<Station_pj> currtStationPjs = partData.stream().filter(c -> c.getStationName().equals(station)).collect(Collectors.toList());
            for (Station_pj param : currtStationPjs) {
                if (param.getEquipmentId() != null) {
                    List<String> levelFourNames = partData.stream().filter(c -> c.getStationName().equals(station)
                                    && c.getLevelOne_name().equals(param.getLevelOne_name())
                                    && c.getLevelTwo_name().equals(param.getLevelTwo_name())
                                    && c.getLevelThree_name().equals(param.getLevelThree_name()))
                            .map(c -> c.getLevelFour_name()).distinct().collect(Collectors.toList());
                    double levelThreeScore = 0;
                    for (String levelFourName : levelFourNames) {
                        Station_pj temp = partData.stream().filter(c -> c.getStationName().equals(station)
                                && c.getLevelOne_name().equals(param.getLevelOne_name())
                                && c.getLevelTwo_name().equals(param.getLevelTwo_name())
                                && c.getLevelThree_name().equals(param.getLevelThree_name())
                                && c.getLevelFour_name().equals(levelFourName)).collect(Collectors.toList()).get(0);
                        if (temp.getLevelFour_score() != null) {
                            levelThreeScore = Calculation.getPlusResult(levelThreeScore, Calculation.getMultiplicationResult(temp.getLevelFour_score(), temp.getLevelFour_weight()));
                        }
                    }
                    param.setLevelThree_score(levelThreeScore);
                } else {
                    double levelOneScore = 0;
                    List<String> levelTwoNames = partData.stream().filter(c -> c.getStationName().equals(station)
                                    && c.getLevelOne_name().equals(param.getLevelOne_name()))
                            .map(c -> c.getLevelTwo_name()).distinct().collect(Collectors.toList());
                    for (String levelTwoName : levelTwoNames) {
                        Station_pj temp = partData.stream().filter(c -> c.getStationName().equals(station)
                                && c.getLevelOne_name().equals(param.getLevelOne_name())
                                && c.getLevelTwo_name().equals(levelTwoName)).collect(Collectors.toList()).get(0);
                        if (temp.getLevelFive_score() != null) {
                            levelOneScore = Calculation.getPlusResult(levelOneScore, Calculation.getMultiplicationResult(temp.getLevelFive_score(), temp.getLevelFive_weight()));
                        }
                    }
                    param.setLevelOne_score(levelOneScore);
                }
            }

            for (Station_pj param : currtStationPjs) {
                if (param.getEquipmentId() != null) {
                    List<String> levelThreeNames = partData.stream().filter(c -> c.getStationName().equals(station)
                                    && c.getLevelOne_name().equals(param.getLevelOne_name())
                                    && c.getLevelTwo_name().equals(param.getLevelTwo_name()))
                            .map(c -> c.getLevelThree_name()).distinct().collect(Collectors.toList());
                    double levelTwoScore = 0;
                    for (String levelThreeName : levelThreeNames) {
                        Station_pj temp = partData.stream().filter(c -> c.getStationName().equals(station)
                                && c.getLevelOne_name().equals(param.getLevelOne_name())
                                && c.getLevelTwo_name().equals(param.getLevelTwo_name())
                                && c.getLevelThree_name().equals(levelThreeName)).collect(Collectors.toList()).get(0);
                        if (temp.getLevelThree_score() != null) {
                            levelTwoScore = Calculation.getPlusResult(levelTwoScore, Calculation.getMultiplicationResult(temp.getLevelThree_score(), temp.getLevelThree_weight()));
                        }
                    }
                    param.setLevelTwo_score(levelTwoScore);
                }
            }

            for (Station_pj param : currtStationPjs) {
                if (param.getEquipmentId() != null) {
                    List<String> levelTwoNames = partData.stream().filter(c -> c.getStationName().equals(station)
                                    && c.getLevelOne_name().equals(param.getLevelOne_name()))
                            .map(c -> c.getLevelTwo_name()).distinct().collect(Collectors.toList());
                    double levelOneScore = 0;
                    for (String levelTwoName : levelTwoNames) {
                        Station_pj temp = partData.stream().filter(c -> c.getStationName().equals(station)
                                && c.getLevelOne_name().equals(param.getLevelOne_name())
                                && c.getLevelTwo_name().equals(levelTwoName)).collect(Collectors.toList()).get(0);
                        if (temp.getLevelTwo_score() != null) {
                            levelOneScore = Calculation.getPlusResult(levelOneScore, Calculation.getMultiplicationResult(temp.getLevelTwo_score(), temp.getLevelTwo_weight()));
                        }
                    }
                    param.setLevelOne_score(levelOneScore);
                }
            }

            for (Station_pj param : currtStationPjs) {
                List<String> levelOneNames = partData.stream().filter(c -> c.getStationName().equals(station))
                        .map(c -> c.getLevelOne_name()).distinct().collect(Collectors.toList());
                double jxScore = 0;
                for (String levelOneName : levelOneNames) {
                    Station_pj temp = partData.stream().filter(c -> c.getStationName().equals(station)
                            && c.getLevelOne_name().equals(levelOneName)).collect(Collectors.toList()).get(0);
                    if (temp.getLevelOne_score() != null) {
                        jxScore = Calculation.getPlusResult(jxScore, Calculation.getMultiplicationResult(temp.getLevelOne_score(), temp.getLevelOne_weight()));
                    }
                }
                param.setJx_score(jxScore);
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
            //注水泵靠审核来更新泵的分数，这里就不用调用更新了
            //注水站需要更新经济指标的分数
//            zszrsjService.updateData(rq);
            List<Station_pj> temp = getZSZJXPJ("日", nowDateString);
//            List<DMGC_S_D_ZSZRSJ> zszList = zszrsjService.getAssessmentNoPage("日", nowDateString);
            List<DMGC_S_D_ZSZRSJ> zszList = zszrsjService.getEffectiveDataOfDay(nowDateString);
            List<DMGC_S_D_ZSZRSJ> updateZsz = new ArrayList<>();
            List<String> zszIds = temp.stream().map(c -> c.getStationId()).distinct().collect(Collectors.toList());
            for (String zszId : zszIds) {
                Station_pj myStation = temp.stream().filter(c -> c.getStationId().equals(zszId)).collect(Collectors.toList()).get(0);
                Optional<DMGC_S_D_ZSZRSJ> optional = zszList.stream().filter(c -> c.getZid().equals(zszId)).findFirst();
                optional.ifPresent(c -> {
                    c.setJxpjScore(myStation.getJx_score());
                    updateZsz.add(c);
                });
            }
            if (updateZsz.size() != 0) {
                zszrsjService.updateData(updateZsz);
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
            List<DMGC_S_D_ZSZRSJ> myDatas = zszrsjService.getDataForPortrait(zid, queryStart, queryEnd);
            List<String> stationIds = myDatas.stream().map(DMGC_S_D_ZSZRSJ::getZid).distinct().collect(Collectors.toList());
            if (!myDatas.isEmpty()) {
                for (String stationId : stationIds) {
                    Station_pj stationPj = new Station_pj();
                    stationPj.setRq(queryStart);
                    stationPj.setStationId(stationId);
                    stationPj.setStationName(myDatas.stream().filter(c -> c.getZid().equals(stationId)).collect(Collectors.toList()).get(0).getZmc());
                    long count = 1;
                    double curScore = 0;

                    count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0 && c.getJxpjScore() != null).count();
                    curScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0 && c.getJxpjScore() != null)
                            .mapToDouble(DMGC_S_D_ZSZRSJ::getJxpjScore).sum();
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
            List<DMGC_S_D_ZSZRSJ> myDatas = zszrsjService.getDataForPortrait(zid, preStart, preEnd);
            myDatas.addAll(zszrsjService.getDataForPortrait(zid, queryStart, queryEnd));
            List<String> stationIds = myDatas.stream().map(DMGC_S_D_ZSZRSJ::getZid).distinct().collect(Collectors.toList());
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("纵向绩效指标要求", null);
            if (!monitoringIndicatorNewList.isEmpty() && !myDatas.isEmpty()) {
                for (String stationId : stationIds) {
                    Station_pj stationPj = new Station_pj();
                    stationPj.setRq(queryStart);
                    stationPj.setStationId(stationId);
                    stationPj.setStationName(myDatas.stream().filter(c -> c.getZid().equals(stationId)).collect(Collectors.toList()).get(0).getZmc());
                    long count = 1;
                    double preScore = 0;
                    double curScore = 0;

                    count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(preEnd) <= 0).count();
                    preScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(preEnd) <= 0)
                            .mapToDouble(DMGC_S_D_ZSZRSJ::getJxpjScore).sum();
                    preScore = Calculation.getDivisionResult(preScore, count);

                    count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0).count();
                    curScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0)
                            .mapToDouble(DMGC_S_D_ZSZRSJ::getJxpjScore).sum();
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
            List<DMGC_S_D_ZSZRSJ> myDatas = zszrsjService.getDataForPortrait(zid, preStart, preEnd);
            myDatas.addAll(zszrsjService.getDataForPortrait(zid, queryStart, queryEnd));
            List<DMGC_S_ZSZ> zszList = zszService.getAll();
            if(StringUtils.isNotEmpty(zid)){
                zszList = zszList.stream().filter(c->c.getEventId().equals(zid)).collect(Collectors.toList());
            }
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("纵向绩效指标要求", null);
            List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("zhjx")).collect(Collectors.toList());
            if (!monitoringIndicatorNewList.isEmpty() && !myDatas.isEmpty()) {
                for (DMGC_S_ZSZ dmgcSZsz : zszList) {
                    String stationId = dmgcSZsz.getEventId();
                    Station_pj stationPj = new Station_pj();
                    stationPj.setRq(queryStart);
                    stationPj.setStationId(stationId);
                    stationPj.setStationName(dmgcSZsz.getMc());
                    stationPj.setLevelOne_weight(indicatorsList.stream().filter(c -> c.getItemCode().equals("xdjxqz")).collect(Collectors.toList()).get(0).getWeight());//用来表示相对（横向）权重
                    stationPj.setLevelThree_weight(indicatorsList.stream().filter(c -> c.getItemCode().equals("zxjxqz")).collect(Collectors.toList()).get(0).getWeight());//用来表示纵向权重
                    long count = 1;
                    double preScore = 0;
                    double curScore = 0;

                    count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(preEnd) <= 0 && c.getJxpjScore() != null).count();
                    preScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(preEnd) <= 0 && c.getJxpjScore() != null)
                            .mapToDouble(DMGC_S_D_ZSZRSJ::getJxpjScore).sum();
                    preScore = Calculation.getDivisionResult(preScore, count);

                    count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0 && c.getJxpjScore() != null).count();
                    curScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0 && c.getJxpjScore() != null)
                            .mapToDouble(DMGC_S_D_ZSZRSJ::getJxpjScore).sum();
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
            List<DMGC_S_D_ZSZRSJ> myDatas = zszrsjService.getDataForPortrait(zid, preSumStart, sumEnd);
            List<String> stationIds = myDatas.stream().map(c -> c.getZid()).distinct().collect(Collectors.toList());
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("纵向绩效指标要求", null);
            if (monitoringIndicatorNewList.size() != 0 && myDatas.size() != 0) {
                for (String stationId : stationIds) {
                    for (Date i = sumStart; i.compareTo(sumEnd) <= 0; i = DateUtils.addMonth(i, 1)) {
                        Station_pj stationPj = new Station_pj();
                        stationPj.setRq(i);
                        stationPj.setStationId(stationId);
                        stationPj.setStationName(myDatas.stream().filter(c -> c.getZid().equals(stationId)).collect(Collectors.toList()).get(0).getZmc());
                        double preScore = 0;
                        double curScore = 0;
                        long count = 1;
                        if (!i.equals(sumStart)) {
                            Date preStart = DateUtils.addMonth(i, -1);
                            Date finalI = i;
                            count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(finalI) < 0).count();
                            preScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(finalI) < 0)
                                    .mapToDouble(DMGC_S_D_ZSZRSJ::getJxpjScore).sum();
                            preScore = Calculation.getDivisionResult(preScore, count);

                            count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(finalI) >= 0 && c.getRq().compareTo(DateUtils.addMonth(finalI, 1)) < 0).count();
                            curScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(finalI) >= 0 && c.getRq().compareTo(DateUtils.addMonth(finalI, 1)) < 0)
                                    .mapToDouble(DMGC_S_D_ZSZRSJ::getJxpjScore).sum();
                            curScore = Calculation.getDivisionResult(curScore, count);
                            stationPj.setJx_score(Calculation.getPortrait(preScore, curScore, monitoringIndicatorNewList.get(0)));
                        } else {
                            double jxScore = 0;
                            Date start = i;
                            count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(start) >= 0 && c.getRq().compareTo(DateUtils.addMonth(start, 1)) < 0).count();
                            jxScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(start) >= 0 && c.getRq().compareTo(DateUtils.addMonth(start, 1)) < 0)
                                    .mapToDouble(DMGC_S_D_ZSZRSJ::getJxpjScore).sum();
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
            List<DMGC_S_D_ZSZRSJ> myDatas = zszrsjService.getDataForPortrait(zid, preSumStart, sumEnd);
            List<String> stationIds = myDatas.stream().map(c -> c.getZid()).distinct().collect(Collectors.toList());
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("纵向绩效指标要求", null);
            List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("zhjx")).collect(Collectors.toList());
            if (monitoringIndicatorNewList.size() != 0 && myDatas.size() != 0 && indicatorsList.size() != 0) {
                for (String stationId : stationIds) {
                    for (Date i = sumStart; i.compareTo(sumEnd) <= 0; i = DateUtils.addMonth(i, 1)) {
                        Station_pj stationPj = new Station_pj();
                        stationPj.setRq(i);
                        stationPj.setStationId(stationId);
                        stationPj.setStationName(myDatas.stream().filter(c -> c.getZid().equals(stationId)).collect(Collectors.toList()).get(0).getZmc());
                        stationPj.setLevelOne_weight(indicatorsList.stream().filter(c -> c.getItemCode().equals("xdjxqz")).collect(Collectors.toList()).get(0).getWeight());//用来表示相对（横向）权重
                        stationPj.setLevelThree_weight(indicatorsList.stream().filter(c -> c.getItemCode().equals("zxjxqz")).collect(Collectors.toList()).get(0).getWeight());//用来表示纵向权重
                        long count = 1;
                        if (!i.equals(sumStart)) {
                            double preScore = 0;
                            double curScore = 0;
                            Date preStart = DateUtils.addMonth(i, -1);
                            Date finalI = i;
                            count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(finalI) < 0).count();
                            preScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(finalI) < 0)
                                    .mapToDouble(DMGC_S_D_ZSZRSJ::getJxpjScore).sum();
                            preScore = Calculation.getDivisionResult(preScore, count);

                            count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(finalI) >= 0 && c.getRq().compareTo(DateUtils.addMonth(finalI, 1)) < 0).count();
                            curScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(finalI) >= 0 && c.getRq().compareTo(DateUtils.addMonth(finalI, 1)) < 0)
                                    .mapToDouble(DMGC_S_D_ZSZRSJ::getJxpjScore).sum();
                            curScore = Calculation.getDivisionResult(curScore, count);
                            stationPj.setLevelOne_score(curScore);//用来表示相对（横向）得分
                            stationPj.setLevelThree_score(Calculation.getPortrait(preScore, curScore, monitoringIndicatorNewList.get(0)));//用来表示纵向得分
                        } else {
                            double relativeScore = 0;
                            Date start = i;
                            count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(start) >= 0 && c.getRq().compareTo(DateUtils.addMonth(start, 1)) < 0).count();
                            relativeScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(start) >= 0 && c.getRq().compareTo(DateUtils.addMonth(start, 1)) < 0)
                                    .mapToDouble(DMGC_S_D_ZSZRSJ::getJxpjScore).sum();
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
