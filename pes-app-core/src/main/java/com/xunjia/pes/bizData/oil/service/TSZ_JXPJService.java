package com.xunjia.pes.bizData.oil.service;

import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.basicDataManage.entity.Indicators;
import com.xunjia.pes.basicDataManage.service.IndicatorsService;
import com.xunjia.pes.bizData.BaseEntity;
import com.xunjia.pes.bizData.DynamicWeightResult;
import com.xunjia.pes.bizData.DynamicWeights;
import com.xunjia.pes.bizData.assessment.entity.MonitoringIndicatorNew;
import com.xunjia.pes.bizData.assessment.service.MonitoringIndicatorNewService;
import com.xunjia.pes.bizData.oil.entity.*;
import com.xunjia.pes.bizData.waterInjection.entity.Station_pj;
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
public class TSZ_JXPJService {
    @Autowired
    private DMGC_Y_TSZ_NEWService yTszNewService;
    @Autowired
    private DMGC_Y_D_SYBService sybService;
    @Autowired
    private DMGC_Y_D_CSBService csbService;

    @Autowired
    private DMGC_Y_D_JRLService jrlService;
    @Autowired
    @Lazy(true)
    private DMGC_Y_D_TSZ_NEWService tszNewService;

    @Autowired
    private DMGC_Y_JBService jbService;
    @Autowired
    private DMGC_JRLService dmgcJrlService;

    @Autowired
    private IndicatorsService indicatorsService;

    @Autowired
    private MonitoringIndicatorNewService monitoringIndicatorNewService;

    @Autowired
    private DynamicWeights dynamicWeights;

    public List<Station_pj> getJXPJ(String cycle, String assessmentDate) {
        List<Station_pj> result = new ArrayList<>();
        List<Station_pj> myResult = new ArrayList<>();
        try {
            List<DMGC_Y_TSZ_NEW> yTszNewList = yTszNewService.getAll();
            List<String> tszIds = yTszNewList.stream().map(BaseEntity::getEventId).distinct().collect(Collectors.toList());
//            List<DMGC_Y_D_SYB> yDSybList = sybService.getAssessmentNoPage(cycle, assessmentDate);
//            yDSybList = yDSybList.stream().filter(c -> tszIds.contains(c.getSszkEventId())).collect(Collectors.toList());
            List<DMGC_Y_D_SYB> yDSybList = sybService.getEffectiveDataOfDay(tszIds, assessmentDate);
//            List<DMGC_Y_D_CSB> yDCsbList = csbService.getAssessmentNoPage(cycle, assessmentDate);
//            yDCsbList = yDCsbList.stream().filter(c -> tszIds.contains(c.getSszkEventId())).collect(Collectors.toList());
            List<DMGC_Y_D_CSB> yDCsbList = csbService.getEffectiveDataOfDay(tszIds, assessmentDate);
//            List<DMGC_Y_D_JRL> yDJrlList = jrlService.getAssessmentNoPage(cycle, assessmentDate);
//            yDJrlList = yDJrlList.stream().filter(c -> tszIds.contains(c.getSszkEventId())).collect(Collectors.toList());
            List<DMGC_Y_D_JRL> yDJrlList = jrlService.getEffectiveDataOfDay(tszIds, assessmentDate);
//            List<DMGC_Y_D_TSZ_NEW> yDTszList = tszNewService.getAssessmentNoPage(cycle, assessmentDate);
            List<DMGC_Y_D_TSZ_NEW> yDTszList = tszNewService.getEffectiveDataOfDay(assessmentDate);
            List<String> jbIds = new ArrayList<>();
            jbIds.addAll(yDSybList.stream().map(DMGC_Y_D_SYB::getJbEventId).distinct().collect(Collectors.toList()));
            jbIds.addAll(yDCsbList.stream().map(DMGC_Y_D_CSB::getJbEventId).distinct().collect(Collectors.toList()));
            List<DMGC_Y_JB> jbList = jbService.getByEventIds(jbIds);
            List<String> jrlIds = yDJrlList.stream().map(DMGC_Y_D_JRL::getJrlId).distinct().collect(Collectors.toList());
            List<DMGC_JRL> dmgc_jrlList = dmgcJrlService.getByIds(jrlIds);
            List<DynamicWeightResult> dynamicWeightResultList = dynamicWeights.getTszWeightMap(yTszNewList, dmgc_jrlList, yDJrlList,
                    jbList, yDSybList, yDCsbList);
            List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("tsz")).collect(Collectors.toList());
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
            List<Indicators> level5 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level5")).collect(Collectors.toList());
            String jbLevel5 = "efficiency,loss,hlssl,fhl";
            String jrlLevel5 = "jrlxl,pywd,kqxs,rfh,ltbmwd";
            List<String> jbLevel5s = Arrays.asList(jbLevel5.split(","));
            List<String> jrlLevel5s = Arrays.asList(jrlLevel5.split(","));
            List<Indicators> jbIndicators = level5.stream().filter(c -> jbLevel5s.contains(c.getItemCode())).collect(Collectors.toList());
            List<Indicators> jrlIndicators = level5.stream().filter(c -> jrlLevel5s.contains(c.getItemCode())).collect(Collectors.toList());
            DynamicWeightResult dynamicWeightResult;
            if (yDSybList.size() != 0) {
                for (DMGC_Y_D_SYB param : yDSybList) {
                    dynamicWeightResult = dynamicWeightResultList.stream().filter(c -> c.getStationId().equals(param.getSszkEventId()) && c.getEquipId().equals(param.getJbEventId())).findFirst().get();
                    for (Indicators indicators : jbIndicators) {
                        Station_pj tszJxpj = new Station_pj();
                        tszJxpj.setStationId(param.getSszkEventId());
                        tszJxpj.setStationName(param.getZm());
                        tszJxpj.setEquipmentId(param.getJbEventId());
                        tszJxpj.setRq(sumEnd);

                        Indicators levll1 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level1") && c.getItemCode().equals("technical")).findFirst().get();
                        tszJxpj.setLevelOne_name(levll1.getItemName());
                        tszJxpj.setLevelOne_weight(levll1.getWeight());

                        Indicators levll2 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level2") && c.getItemCode().equals("pump")).findFirst().get();
                        tszJxpj.setLevelTwo_name(levll2.getItemName());
//                        zyzJxpj.setLevelTwo_weight(levll2.getWeight());
                        tszJxpj.setLevelTwo_weight(dynamicWeightResult.getLevel2Weight());

//                        Indicators levll3 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level3") && c.getItemCode().equals("sybjszb")).findFirst().get();
//                        tszJxpj.setLevelThree_name(levll3.getItemName());
//                        tszJxpj.setLevelThree_weight(levll3.getWeight());
                        tszJxpj.setLevelThree_name(param.getEquipName());
                        tszJxpj.setLevelThree_weight(dynamicWeightResult.getLevel3Weight());

                        tszJxpj.setLevelFour_name(param.getEquipName() + param.getSybbh());
//                        double djgl_all = jbList.stream().filter(c -> c.getSszkEventId().equals(param.getSszkEventId())).mapToDouble(DMGC_Y_JB::getDjgl).sum();
//                        double djgl_current = jbList.stream().filter(c -> c.getEventId().equals(param.getJbEventId())).findFirst().get().getDjgl();
//                        tszJxpj.setLevelFour_weight(Calculation.getDivisionResult(djgl_current, djgl_all));
                        tszJxpj.setLevelFour_weight(dynamicWeightResult.getLevel4Weight());
                        tszJxpj.setLevelFour_score(param.getJbScore());

                        tszJxpj.setLevelFive_name(indicators.getItemName());
                        tszJxpj.setLevelFive_weight(indicators.getWeight());
                        switch (indicators.getItemCode()) {
                            case "efficiency":
                                tszJxpj.setLevelFive_score(param.getSybxlScore());
                                break;
                            case "loss":
                                tszJxpj.setLevelFive_score(param.getJlsslScore());
                                break;
                            case "hlssl":
                                tszJxpj.setLevelFive_score(param.getHlRateScore());
                                break;
                            case "fhl":
                                tszJxpj.setLevelFive_score(param.getFhlScore());
                                break;
                        }
                        result.add(tszJxpj);
                    }
                }
            }

            if (yDCsbList.size() != 0) {
                for (DMGC_Y_D_CSB param : yDCsbList) {
                    dynamicWeightResult = dynamicWeightResultList.stream().filter(c -> c.getStationId().equals(param.getSszkEventId()) && c.getEquipId().equals(param.getJbEventId())).findFirst().get();
                    for (Indicators indicators : jbIndicators) {
                        Station_pj tszJxpj = new Station_pj();
                        tszJxpj.setStationId(param.getSszkEventId());
                        tszJxpj.setStationName(param.getZm());
                        tszJxpj.setEquipmentId(param.getJbEventId());
                        tszJxpj.setRq(sumEnd);

                        Indicators levll1 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level1") && c.getItemCode().equals("technical")).findFirst().get();
                        tszJxpj.setLevelOne_name(levll1.getItemName());
                        tszJxpj.setLevelOne_weight(levll1.getWeight());

                        Indicators levll2 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level2") && c.getItemCode().equals("pump")).findFirst().get();
                        tszJxpj.setLevelTwo_name(levll2.getItemName());
//                        tszJxpj.setLevelTwo_weight(levll2.getWeight());
                        tszJxpj.setLevelTwo_weight(dynamicWeightResult.getLevel2Weight());

//                        Indicators levll3 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level3") && c.getItemCode().equals("csbjszb")).findFirst().get();
//                        tszJxpj.setLevelThree_name(levll3.getItemName());
//                        tszJxpj.setLevelThree_weight(levll3.getWeight());
                        tszJxpj.setLevelThree_name(param.getEquipName());
                        tszJxpj.setLevelThree_weight(dynamicWeightResult.getLevel3Weight());

                        tszJxpj.setLevelFour_name(param.getEquipName() + param.getZnbh());
//                        double djgl_all = jbList.stream().filter(c -> c.getSszkEventId().equals(param.getSszkEventId())).mapToDouble(DMGC_Y_JB::getDjgl).sum();
//                        double djgl_current = jbList.stream().filter(c -> c.getEventId().equals(param.getJbEventId())).findFirst().get().getDjgl();
//                        tszJxpj.setLevelFour_weight(Calculation.getDivisionResult(djgl_current, djgl_all));
                        tszJxpj.setLevelFour_weight(dynamicWeightResult.getLevel4Weight());
                        tszJxpj.setLevelFour_score(param.getJbScore());

                        tszJxpj.setLevelFive_name(indicators.getItemName());
                        tszJxpj.setLevelFive_weight(indicators.getWeight());
                        switch (indicators.getItemCode()) {
                            case "efficiency":
                                tszJxpj.setLevelFive_score(param.getBxlScore());
                                break;
                            case "loss":
                                tszJxpj.setLevelFive_score(param.getJlsslScore());
                                break;
                            case "hlssl":
                                tszJxpj.setLevelFive_score(param.getHlRateScore());
                                break;
                            case "fhl":
                                tszJxpj.setLevelFive_score(param.getFhlScore());
                                break;
                        }
                        result.add(tszJxpj);
                    }
                }
            }

            if (yDJrlList.size() != 0) {
                for (DMGC_Y_D_JRL param : yDJrlList) {
                    dynamicWeightResult = dynamicWeightResultList.stream().filter(c -> c.getStationId().equals(param.getSszkEventId()) && c.getEquipId().equals(param.getJrlId())).findFirst().get();
                    for (Indicators indicators : jrlIndicators) {
                        Station_pj tszJxpj = new Station_pj();
                        tszJxpj.setStationId(param.getSszkEventId());
                        tszJxpj.setStationName(param.getSszkName());
                        tszJxpj.setEquipmentId(param.getJrlId());
                        tszJxpj.setRq(sumEnd);

                        Indicators levll1 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level1") && c.getItemCode().equals("technical")).findFirst().get();
                        tszJxpj.setLevelOne_name(levll1.getItemName());
                        tszJxpj.setLevelOne_weight(levll1.getWeight());

                        Indicators levll2 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level2") && c.getItemCode().equals("heating")).findFirst().get();
                        tszJxpj.setLevelTwo_name(levll2.getItemName());
//                        tszJxpj.setLevelTwo_weight(levll2.getWeight());
                        tszJxpj.setLevelTwo_weight(dynamicWeightResult.getLevel2Weight());

//                        Indicators levll3 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level3") && c.getItemCode().equals("cnljszb")).findFirst().get();
//                        tszJxpj.setLevelThree_name(levll3.getItemName());
//                        tszJxpj.setLevelThree_weight(levll3.getWeight());
                        tszJxpj.setLevelThree_name(param.getMc());
                        tszJxpj.setLevelThree_weight(dynamicWeightResult.getLevel3Weight());

                        tszJxpj.setLevelFour_name(param.getMc() + param.getZnbh());
//                        double djgl_all = dmgc_jrlList.stream().filter(c -> c.getSszm().equals(param.getSszkName())).mapToDouble(DMGC_JRL::getEdrfh).sum();
//                        double djgl_current = dmgc_jrlList.stream().filter(c -> c.getEventId().equals(param.getJrlId())).findFirst().get().getEdrfh();
//                        tszJxpj.setLevelFour_weight(Calculation.getDivisionResult(djgl_current, djgl_all));
                        tszJxpj.setLevelFour_weight(dynamicWeightResult.getLevel4Weight());
                        tszJxpj.setLevelFour_score(param.getJxScore());

                        tszJxpj.setLevelFive_name(indicators.getItemName());
                        tszJxpj.setLevelFive_weight(indicators.getWeight());
                        switch (indicators.getItemCode()) {
                            case "jrlxl":
                                tszJxpj.setLevelFive_score(param.getLxScore());
                                break;
                            case "pywd":
                                tszJxpj.setLevelFive_score(param.getYqwdScore());
                                break;
                            case "kqxs":
                                tszJxpj.setLevelFive_score(param.getKqxsScore());
                                break;
                            case "rfh":
                                tszJxpj.setLevelFive_score(param.getRfhScore());
                                break;
                            case "ltbmwd":
                                tszJxpj.setLevelFive_score(param.getLtwbmwdScore());
                                break;
                        }
                        result.add(tszJxpj);
                    }
                }
            }

            for (DMGC_Y_D_TSZ_NEW param : yDTszList) {
                Indicators levll2 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level2") && c.getItemCode().equals("tszzhdh")).findFirst().get();
                Station_pj zyzJxpj = new Station_pj();
                zyzJxpj.setStationId(param.getZid());
                zyzJxpj.setStationName(param.getStationName());
                zyzJxpj.setRq(sumEnd);

                Indicators levll1 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level1") && c.getItemCode().equals("economics")).findFirst().get();
                zyzJxpj.setLevelOne_name(levll1.getItemName());
                zyzJxpj.setLevelOne_weight(levll1.getWeight());

                zyzJxpj.setLevelTwo_name(levll2.getItemName());
                zyzJxpj.setLevelFive_weight(levll2.getWeight());
                zyzJxpj.setLevelFive_score(param.getZhdhScore());
                result.add(zyzJxpj);

                levll2 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level2") && c.getItemCode().equals("tszdyohd")).findFirst().get();
                zyzJxpj = new Station_pj();
                zyzJxpj.setStationId(param.getZid());
                zyzJxpj.setStationName(param.getStationName());
                zyzJxpj.setRq(sumEnd);

                levll1 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level1") && c.getItemCode().equals("economics")).findFirst().get();
                zyzJxpj.setLevelOne_name(levll1.getItemName());
                zyzJxpj.setLevelOne_weight(levll1.getWeight());

                zyzJxpj.setLevelTwo_name(levll2.getItemName());
                zyzJxpj.setLevelFive_weight(levll2.getWeight());
                zyzJxpj.setLevelFive_score(param.getDyohdScore());
                result.add(zyzJxpj);

                levll2 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level2") && c.getItemCode().equals("tszdyohq")).findFirst().get();
                zyzJxpj = new Station_pj();
                zyzJxpj.setStationId(param.getZid());
                zyzJxpj.setStationName(param.getStationName());
                zyzJxpj.setRq(sumEnd);

                levll1 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level1") && c.getItemCode().equals("economics")).findFirst().get();
                zyzJxpj.setLevelOne_name(levll1.getItemName());
                zyzJxpj.setLevelOne_weight(levll1.getWeight());

                zyzJxpj.setLevelTwo_name(levll2.getItemName());
                zyzJxpj.setLevelFive_weight(levll2.getWeight());
                zyzJxpj.setLevelFive_score(param.getDyohqScore());
                result.add(zyzJxpj);

                levll2 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level2") && c.getItemCode().equals("tszdyehd")).findFirst().get();
                zyzJxpj = new Station_pj();
                zyzJxpj.setStationId(param.getZid());
                zyzJxpj.setStationName(param.getStationName());
                zyzJxpj.setRq(sumEnd);

                levll1 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level1") && c.getItemCode().equals("economics")).findFirst().get();
                zyzJxpj.setLevelOne_name(levll1.getItemName());
                zyzJxpj.setLevelOne_weight(levll1.getWeight());

                zyzJxpj.setLevelTwo_name(levll2.getItemName());
                zyzJxpj.setLevelFive_weight(levll2.getWeight());
                zyzJxpj.setLevelFive_score(param.getDyehdScore());
                result.add(zyzJxpj);

                levll2 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level2") && c.getItemCode().equals("tszdyehq")).findFirst().get();
                zyzJxpj = new Station_pj();
                zyzJxpj.setStationId(param.getZid());
                zyzJxpj.setStationName(param.getStationName());
                zyzJxpj.setRq(sumEnd);

                levll1 = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level1") && c.getItemCode().equals("economics")).findFirst().get();
                zyzJxpj.setLevelOne_name(levll1.getItemName());
                zyzJxpj.setLevelOne_weight(levll1.getWeight());

                zyzJxpj.setLevelTwo_name(levll2.getItemName());
                zyzJxpj.setLevelFive_weight(levll2.getWeight());
                zyzJxpj.setLevelFive_score(param.getDyehqScore());
                result.add(zyzJxpj);
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
            //输油泵靠审核来更新泵的分数，这里就不用调用更新了
            //掺水泵靠审核来更新泵的分数，这里就不用调用更新了,但是掺水泵数据表是自建表，要创建当日的基本数据，再由操作人员录入
            //加热炉靠审核来更新泵的分数，这里就不用调用更新了
            //脱水站需要更新经济指标的分数
//            tszNewService.updateData(rq);
            List<Station_pj> temp = getJXPJ("日", nowDateString);
//            List<DMGC_Y_D_TSZ_NEW> tszList = tszNewService.getAssessmentNoPage("日", nowDateString);
            List<DMGC_Y_D_TSZ_NEW> tszList = tszNewService.getEffectiveDataOfDay(nowDateString);
            List<DMGC_Y_D_TSZ_NEW> updateTsz = new ArrayList<>();
            List<String> tszIds = temp.stream().map(c -> c.getStationId()).distinct().collect(Collectors.toList());
            for (String tszId : tszIds) {
                Station_pj myStation = temp.stream().filter(c -> c.getStationId().equals(tszId)).collect(Collectors.toList()).get(0);
                Optional<DMGC_Y_D_TSZ_NEW> optional = tszList.stream().filter(c -> c.getZid().equals(tszId)).findFirst();
                optional.ifPresent(c -> {
                    c.setJxpjScore(myStation.getJx_score());
                    updateTsz.add(c);
                });
            }
            if (updateTsz.size() != 0) {
                tszNewService.updateData(updateTsz);
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
            List<DMGC_Y_D_TSZ_NEW> myDatas = tszNewService.getDataForPortrait(zid, queryStart, queryEnd);
            List<String> stationIds = myDatas.stream().map(DMGC_Y_D_TSZ_NEW::getZid).distinct().collect(Collectors.toList());
            if (!myDatas.isEmpty()) {
                for (String stationId : stationIds) {
                    Station_pj stationPj = new Station_pj();
                    stationPj.setRq(queryStart);
                    stationPj.setStationId(stationId);
                    stationPj.setStationName(myDatas.stream().filter(c -> c.getZid().equals(stationId)).collect(Collectors.toList()).get(0).getStationName());
                    long count = 1;
                    double curScore = 0;

                    count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0 && c.getJxpjScore() != null).count();
                    curScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0 && c.getJxpjScore() != null)
                            .mapToDouble(DMGC_Y_D_TSZ_NEW::getJxpjScore).sum();
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
            List<DMGC_Y_D_TSZ_NEW> myDatas = tszNewService.getDataForPortrait(zid, preStart, preEnd);
            myDatas.addAll(tszNewService.getDataForPortrait(zid, queryStart, queryEnd));
            List<String> stationIds = myDatas.stream().map(DMGC_Y_D_TSZ_NEW::getZid).distinct().collect(Collectors.toList());
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("纵向绩效指标要求", null);
            if (!monitoringIndicatorNewList.isEmpty() && !myDatas.isEmpty()) {
                for (String stationId : stationIds) {
                    Station_pj stationPj = new Station_pj();
                    stationPj.setRq(queryStart);
                    stationPj.setStationId(stationId);
                    stationPj.setStationName(myDatas.stream().filter(c -> c.getZid().equals(stationId)).collect(Collectors.toList()).get(0).getStationName());
                    long count = 1;
                    double preScore = 0;
                    double curScore = 0;

                    count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(preEnd) <= 0).count();
                    preScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(preEnd) <= 0)
                            .mapToDouble(DMGC_Y_D_TSZ_NEW::getJxpjScore).sum();
                    preScore = Calculation.getDivisionResult(preScore, count);

                    count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0).count();
                    curScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0)
                            .mapToDouble(DMGC_Y_D_TSZ_NEW::getJxpjScore).sum();
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
            List<DMGC_Y_D_TSZ_NEW> myDatas = tszNewService.getDataForPortrait(zid, preStart, preEnd);
            myDatas.addAll(tszNewService.getDataForPortrait(zid, queryStart, queryEnd));
            List<DMGC_Y_TSZ_NEW> tszNewList = yTszNewService.getAll();
            if(StringUtils.isNotEmpty(zid)){
                tszNewList = tszNewList.stream().filter(c->c.getEventId().equals(zid)).collect(Collectors.toList());
            }
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("纵向绩效指标要求", null);
            List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("zhjx")).collect(Collectors.toList());
            if (!monitoringIndicatorNewList.isEmpty() && !myDatas.isEmpty()) {
                for (DMGC_Y_TSZ_NEW dmgcYTszNew : tszNewList) {
                    String stationId = dmgcYTszNew.getEventId();
                    Station_pj stationPj = new Station_pj();
                    stationPj.setRq(queryStart);
                    stationPj.setStationId(stationId);
                    stationPj.setStationName(dmgcYTszNew.getMc());
                    stationPj.setLevelOne_weight(indicatorsList.stream().filter(c -> c.getItemCode().equals("xdjxqz")).collect(Collectors.toList()).get(0).getWeight());//用来表示相对（横向）权重
                    stationPj.setLevelThree_weight(indicatorsList.stream().filter(c -> c.getItemCode().equals("zxjxqz")).collect(Collectors.toList()).get(0).getWeight());//用来表示纵向权重
                    long count = 1;
                    double preScore = 0;
                    double curScore = 0;

                    count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(preEnd) <= 0 && c.getJxpjScore() != null).count();
                    preScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(preEnd) <= 0 && c.getJxpjScore() != null)
                            .mapToDouble(DMGC_Y_D_TSZ_NEW::getJxpjScore).sum();
                    preScore = Calculation.getDivisionResult(preScore, count);


                    count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0 && c.getJxpjScore() != null).count();
                    curScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0 && c.getJxpjScore() != null)
                            .mapToDouble(DMGC_Y_D_TSZ_NEW::getJxpjScore).sum();
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
            List<DMGC_Y_D_TSZ_NEW> myDatas = tszNewService.getDataForPortrait(zid, preSumStart, sumEnd);
            List<String> stationIds = myDatas.stream().map(c -> c.getZid()).distinct().collect(Collectors.toList());
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("纵向绩效指标要求", null);
            if (monitoringIndicatorNewList.size() != 0 && myDatas.size() != 0) {
                for (String stationId : stationIds) {
                    for (Date i = sumStart; i.compareTo(sumEnd) <= 0; i = DateUtils.addMonth(i, 1)) {
                        Station_pj stationPj = new Station_pj();
                        stationPj.setRq(i);
                        stationPj.setStationId(stationId);
                        stationPj.setStationName(myDatas.stream().filter(c -> c.getZid().equals(stationId)).collect(Collectors.toList()).get(0).getStationName());
                        double preScore = 0;
                        double curScore = 0;
                        long count = 1;
                        if (!i.equals(sumStart)) {
                            Date preStart = DateUtils.addMonth(i, -1);
                            Date finalI = i;
                            count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(finalI) < 0).count();
                            preScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(finalI) < 0)
                                    .mapToDouble(DMGC_Y_D_TSZ_NEW::getJxpjScore).sum();
                            preScore = Calculation.getDivisionResult(preScore, count);

                            count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(finalI) >= 0 && c.getRq().compareTo(DateUtils.addMonth(finalI, 1)) < 0).count();
                            curScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(finalI) >= 0 && c.getRq().compareTo(DateUtils.addMonth(finalI, 1)) < 0)
                                    .mapToDouble(DMGC_Y_D_TSZ_NEW::getJxpjScore).sum();
                            curScore = Calculation.getDivisionResult(curScore, count);
                            stationPj.setJx_score(Calculation.getPortrait(preScore, curScore, monitoringIndicatorNewList.get(0)));
                        } else {
                            double jxScore = 0;
                            Date start = i;
                            count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(start) >= 0 && c.getRq().compareTo(DateUtils.addMonth(start, 1)) < 0).count();
                            jxScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(start) >= 0 && c.getRq().compareTo(DateUtils.addMonth(start, 1)) < 0)
                                    .mapToDouble(DMGC_Y_D_TSZ_NEW::getJxpjScore).sum();
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
            List<DMGC_Y_D_TSZ_NEW> myDatas = tszNewService.getDataForPortrait(zid, preSumStart, sumEnd);
            List<String> stationIds = myDatas.stream().map(c -> c.getZid()).distinct().collect(Collectors.toList());
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("纵向绩效指标要求", null);
            List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("zhjx")).collect(Collectors.toList());
            if (monitoringIndicatorNewList.size() != 0 && myDatas.size() != 0 && indicatorsList.size() != 0) {
                for (String stationId : stationIds) {
                    for (Date i = sumStart; i.compareTo(sumEnd) <= 0; i = DateUtils.addMonth(i, 1)) {
                        Station_pj stationPj = new Station_pj();
                        stationPj.setRq(i);
                        stationPj.setStationId(stationId);
                        stationPj.setStationName(myDatas.stream().filter(c -> c.getZid().equals(stationId)).collect(Collectors.toList()).get(0).getStationName());
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
                                    .mapToDouble(DMGC_Y_D_TSZ_NEW::getJxpjScore).sum();
                            preScore = Calculation.getDivisionResult(preScore, count);

                            count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(finalI) >= 0 && c.getRq().compareTo(DateUtils.addMonth(finalI, 1)) < 0).count();
                            curScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(finalI) >= 0 && c.getRq().compareTo(DateUtils.addMonth(finalI, 1)) < 0)
                                    .mapToDouble(DMGC_Y_D_TSZ_NEW::getJxpjScore).sum();
                            curScore = Calculation.getDivisionResult(curScore, count);
                            stationPj.setLevelOne_score(curScore);//用来表示相对（横向）得分
                            stationPj.setLevelThree_score(Calculation.getPortrait(preScore, curScore, monitoringIndicatorNewList.get(0)));//用来表示纵向得分
                        } else {
                            double relativeScore = 0;
                            Date start = i;
                            count = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(start) >= 0 && c.getRq().compareTo(DateUtils.addMonth(start, 1)) < 0).count();
                            relativeScore = myDatas.stream().filter(c -> c.getZid().equals(stationId) && c.getRq().compareTo(start) >= 0 && c.getRq().compareTo(DateUtils.addMonth(start, 1)) < 0)
                                    .mapToDouble(DMGC_Y_D_TSZ_NEW::getJxpjScore).sum();
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
