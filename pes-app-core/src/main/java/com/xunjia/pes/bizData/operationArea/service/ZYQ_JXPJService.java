package com.xunjia.pes.bizData.operationArea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.basicDataManage.entity.Indicators;
import com.xunjia.pes.basicDataManage.service.IndicatorsService;
import com.xunjia.pes.bizData.DynamicWeightResult;
import com.xunjia.pes.bizData.DynamicWeights;
import com.xunjia.pes.bizData.assessment.entity.MonitoringIndicatorNew;
import com.xunjia.pes.bizData.assessment.service.MonitoringIndicatorNewService;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_TSZ_NEW;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_ZYZ;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_TSZ_NEW;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_ZYZ;
import com.xunjia.pes.bizData.oil.service.DMGC_Y_D_TSZ_NEWService;
import com.xunjia.pes.bizData.oil.service.DMGC_Y_D_ZYZService;
import com.xunjia.pes.bizData.oil.service.DMGC_Y_TSZ_NEWService;
import com.xunjia.pes.bizData.oil.service.DMGC_Y_ZYZService;
import com.xunjia.pes.bizData.operationArea.entiey.ZYQ_D_RSJ;
import com.xunjia.pes.bizData.operationArea.mapper.ZYQ_D_RSJMapper;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSZRSJ;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_ZSZ;
import com.xunjia.pes.bizData.waterInjection.entity.Station_pj;
import com.xunjia.pes.bizData.waterInjection.service.DMGC_S_D_ZSZRSJService;
import com.xunjia.pes.bizData.waterInjection.service.DMGC_S_ZSZService;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_D_SCLZRSJ;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_SCLZ;
import com.xunjia.pes.bizData.waterTreatment.service.DMGC_S_D_SCLZRSJService;
import com.xunjia.pes.bizData.waterTreatment.service.DMGC_S_SCLZService;
import com.xunjia.pes.score.Calculation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ZYQ_JXPJService extends ServiceImpl<ZYQ_D_RSJMapper, ZYQ_D_RSJ> {
    @Autowired
    private DMGC_Y_D_ZYZService dmgcYDZyzService;

    @Autowired
    private DMGC_Y_D_TSZ_NEWService dmgcYDTszNewService;

    @Autowired
    private DMGC_S_D_ZSZRSJService dmgcSDZszrsjService;

    @Autowired
    private DMGC_S_D_SCLZRSJService dmgcSDSclzrsjService;

    @Autowired
    private DMGC_Y_ZYZService zyzService;

    @Autowired
    private DMGC_Y_TSZ_NEWService tszNewService;

    @Autowired
    private DMGC_S_ZSZService zszService;

    @Autowired
    private DMGC_S_SCLZService sclzService;

    @Autowired
    private IndicatorsService indicatorsService;

    @Autowired
    private DynamicWeights dynamicWeights;

    @Autowired
    private MonitoringIndicatorNewService monitoringIndicatorNewService;

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
            Date dateRq = DateUtils.parse(nowDateString, DateUtils.DATE_PATTERN);
            List<ZYQ_D_RSJ> datas = new ArrayList<>();
            List<Station_pj> temp = getZYQJXPJ("日", nowDateString);
            List<String> zyqNames = temp.stream().map(Station_pj::getStationName).distinct().collect(Collectors.toList());
            LambdaQueryWrapper<ZYQ_D_RSJ> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ZYQ_D_RSJ::getRq, dateRq);
            List<ZYQ_D_RSJ> dataInDb = this.list(wrapper);
            ZYQ_D_RSJ zyqDRsj;
            for (String zyqName : zyqNames) {
                Optional<ZYQ_D_RSJ> optional = dataInDb.stream().filter(c -> c.getZyqName().equals(zyqName)).findFirst();
                if (optional.isPresent()) {
                    zyqDRsj = optional.get();
                    zyqDRsj.setZyqScore(temp.stream().filter(c -> c.getStationName().equals(zyqName)).collect(Collectors.toList()).get(0).getJx_score());
                } else {
                    zyqDRsj = new ZYQ_D_RSJ();
                    zyqDRsj.setZyqName(zyqName);
                    zyqDRsj.setRq(dateRq);
                    zyqDRsj.setZyqScore(temp.stream().filter(c -> c.getStationName().equals(zyqName)).collect(Collectors.toList()).get(0).getJx_score());
                    datas.add(zyqDRsj);
                }
            }
            if (datas.size() > 0) {
                this.saveBatch(datas);
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
            result = false;
        }
        return result;
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
            Date dateRq = DateUtils.parse(nowDateString, DateUtils.DATE_PATTERN);
            List<Station_pj> temp = getZYQJXPJ("日", nowDateString);
            LambdaQueryWrapper<ZYQ_D_RSJ> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ZYQ_D_RSJ::getRq, dateRq);
            List<ZYQ_D_RSJ> dataInDb = this.list(wrapper);
            if (dataInDb.size() != 0) {
                for (ZYQ_D_RSJ param : dataInDb) {
                    Optional<Station_pj> optional = temp.stream().filter(c -> c.getStationName().equals(param.getZyqName())
                            && c.getRq().equals(dateRq)).findFirst();
                    optional.ifPresent(c -> {
                        param.setZyqScore(c.getJx_score());
                    });
                }
            }
            if (dataInDb.size() != 0) {
                this.updateBatchById(dataInDb);
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
            result = false;
        }
        return result;
    }

    public List<ZYQ_D_RSJ> getDataForPortrait(Date queryStart, Date queryEnd) {
        LambdaQueryWrapper<ZYQ_D_RSJ> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(ZYQ_D_RSJ::getZyqScore);
        wrapper.ge(ZYQ_D_RSJ::getRq, queryStart);
        wrapper.le(ZYQ_D_RSJ::getRq, queryEnd);
        return this.list(wrapper);
    }

    public List<Station_pj> getZYQJXPJ(String cycle, String assessmentDate) {
        List<Station_pj> result = new ArrayList<>();
        List<Station_pj> myResult = new ArrayList<>();
        try {
            Date sumStart = DateUtils.parse(assessmentDate, DateUtils.DATE_PATTERN);
            List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("zyq")).collect(Collectors.toList());
            List<DMGC_Y_D_ZYZ> dmgcYDZyzs = dmgcYDZyzService.getDataForZYQ(cycle, sumStart);
            List<DMGC_Y_ZYZ> zyzs = zyzService.getAll();
            List<DMGC_Y_D_TSZ_NEW> dmgcYDTszNews = dmgcYDTszNewService.getDataForZYQ(cycle, sumStart);
            List<DMGC_Y_TSZ_NEW> tszNews = tszNewService.getAll();
            List<DMGC_S_D_ZSZRSJ> dmgcSDZszrsjList = dmgcSDZszrsjService.getDataForZYQ(cycle, sumStart);
            List<DMGC_S_ZSZ> zszs = zszService.getAll();
            List<DMGC_S_D_SCLZRSJ> dmgcSDSclzrsjs = dmgcSDSclzrsjService.getDataForZYQ(cycle, sumStart);
            List<DMGC_S_SCLZ> sclzs = sclzService.getAll();
            List<DynamicWeightResult> dynamicWeightResultList = dynamicWeights.getZyqWeightMap(zyzs, dmgcYDZyzs, tszNews, dmgcYDTszNews, zszs, dmgcSDZszrsjList, sclzs, dmgcSDSclzrsjs);
            for (DMGC_Y_D_ZYZ param : dmgcYDZyzs) {
//                Optional<Indicators> optional = indicatorsList.stream().filter(c -> c.getItemCode().equals(param.getZkEventId())).findFirst();
                Optional<DynamicWeightResult> optional = dynamicWeightResultList.stream().filter(c -> c.getEquipId().equals(param.getZkEventId())).findFirst();
                optional.ifPresent(c -> {
                    Indicators levelOne = indicatorsList.stream().filter(i -> i.getItemCode().equals("yyjsxtjx")).findFirst().get();
                    Indicators levelTwo = indicatorsList.stream().filter(i -> i.getItemCode().equals("zyfszjx")).findFirst().get();
                    Station_pj stationPj = new Station_pj();
                    stationPj.setStationName(zyzs.stream().filter(d -> d.getEventId().equals(param.getZkEventId())).collect(Collectors.toList()).get(0).getZyqName());
                    stationPj.setRq(sumStart);
                    stationPj.setLevelOne_name(levelOne.getItemName());
                    stationPj.setLevelOne_weight(levelOne.getWeight());
                    stationPj.setLevelTwo_name(levelTwo.getItemName());
//                    stationPj.setLevelTwo_weight(levelTwo.getWeight());
                    stationPj.setLevelTwo_weight(c.getLevel2Weight());
//                    stationPj.setLevelThree_name(c.getItemName());
//                    stationPj.setLevelThree_weight(c.getWeight());
                    stationPj.setLevelThree_name(param.getStationName() + "权重");
                    stationPj.setLevelThree_weight(c.getLevel3Weight());
                    stationPj.setLevelThree_score(param.getJxpjScore());
                    result.add(stationPj);
                });
            }

            for (DMGC_Y_D_TSZ_NEW param : dmgcYDTszNews) {
//                Optional<Indicators> optional = indicatorsList.stream().filter(c -> c.getItemCode().equals(param.getZid())).findFirst();
                Optional<DynamicWeightResult> optional = dynamicWeightResultList.stream().filter(c -> c.getEquipId().equals(param.getZid())).findFirst();
                optional.ifPresent(c -> {
                    Indicators levelOne = indicatorsList.stream().filter(i -> i.getItemCode().equals("yyjsxtjx")).findFirst().get();
                    Indicators levelTwo = indicatorsList.stream().filter(i -> i.getItemCode().equals("tszjx")).findFirst().get();
                    Station_pj stationPj = new Station_pj();
                    stationPj.setStationName(tszNews.stream().filter(d -> d.getEventId().equals(param.getZid())).collect(Collectors.toList()).get(0).getZyqName());
                    stationPj.setRq(sumStart);
                    stationPj.setLevelOne_name(levelOne.getItemName());
                    stationPj.setLevelOne_weight(levelOne.getWeight());
                    stationPj.setLevelTwo_name(levelTwo.getItemName());
//                    stationPj.setLevelTwo_weight(levelTwo.getWeight());
                    stationPj.setLevelTwo_weight(c.getLevel2Weight());
//                    stationPj.setLevelThree_name(c.getItemName());
//                    stationPj.setLevelThree_weight(c.getWeight());
                    stationPj.setLevelThree_name(param.getStationName() + "权重");
                    stationPj.setLevelThree_weight(c.getLevel3Weight());
                    stationPj.setLevelThree_score(param.getJxpjScore());
                    result.add(stationPj);
                });
            }

            for (DMGC_S_D_ZSZRSJ param : dmgcSDZszrsjList) {
//                Optional<Indicators> optional = indicatorsList.stream().filter(c -> c.getItemCode().equals(param.getZid())).findFirst();
                Optional<DynamicWeightResult> optional = dynamicWeightResultList.stream().filter(c -> c.getEquipId().equals(param.getZid())).findFirst();
                optional.ifPresent(c -> {
                    Indicators levelOne = indicatorsList.stream().filter(i -> i.getItemCode().equals("zrxtjx")).findFirst().get();
                    Indicators levelTwo = indicatorsList.stream().filter(i -> i.getItemCode().equals("zszjx")).findFirst().get();
                    Station_pj stationPj = new Station_pj();
                    stationPj.setStationName(zszs.stream().filter(d -> d.getEventId().equals(param.getZid())).collect(Collectors.toList()).get(0).getZyqName());
                    stationPj.setRq(sumStart);
                    stationPj.setLevelOne_name(levelOne.getItemName());
                    stationPj.setLevelOne_weight(levelOne.getWeight());
                    stationPj.setLevelTwo_name(levelTwo.getItemName());
//                    stationPj.setLevelTwo_weight(levelTwo.getWeight());
                    stationPj.setLevelTwo_weight(c.getLevel2Weight());
//                    stationPj.setLevelThree_name(c.getItemName());
//                    stationPj.setLevelThree_weight(c.getWeight());
                    stationPj.setLevelThree_name(param.getZmc() + "权重");
                    stationPj.setLevelThree_weight(c.getLevel3Weight());
                    stationPj.setLevelThree_score(param.getJxpjScore());
                    result.add(stationPj);
                });
            }

            for (DMGC_S_D_SCLZRSJ param : dmgcSDSclzrsjs) {
//                Optional<Indicators> optional = indicatorsList.stream().filter(c -> c.getItemCode().equals(param.getZkEventId())).findFirst();
                Optional<DynamicWeightResult> optional = dynamicWeightResultList.stream().filter(c -> c.getEquipId().equals(param.getZkEventId())).findFirst();
                optional.ifPresent(c -> {
                    Indicators levelOne = indicatorsList.stream().filter(i -> i.getItemCode().equals("xclxtjx")).findFirst().get();
                    Indicators levelTwo = indicatorsList.stream().filter(i -> i.getItemCode().equals("wsclzjx")).findFirst().get();
                    Station_pj stationPj = new Station_pj();
                    stationPj.setStationName(sclzs.stream().filter(d -> d.getEventId().equals(param.getZkEventId())).collect(Collectors.toList()).get(0).getZyqName());
                    stationPj.setRq(sumStart);
                    stationPj.setLevelOne_name(levelOne.getItemName());
                    stationPj.setLevelOne_weight(levelOne.getWeight());
                    stationPj.setLevelTwo_name(levelTwo.getItemName());
//                    stationPj.setLevelTwo_weight(levelTwo.getWeight());
                    stationPj.setLevelTwo_weight(c.getLevel2Weight());
//                    stationPj.setLevelThree_name(c.getItemName());
//                    stationPj.setLevelThree_weight(c.getWeight());
                    stationPj.setLevelThree_name(param.getZmc() + "权重");
                    stationPj.setLevelThree_weight(c.getLevel3Weight());
                    stationPj.setLevelThree_score(param.getJxpjScore());
                    result.add(stationPj);
                });
            }
            getComplete(result);
            myResult = result.stream().sorted(Comparator.comparing(Station_pj::getJx_score, Comparator.nullsFirst(Double::compareTo).reversed()).thenComparing(Station_pj::getStationName).thenComparing(Station_pj::getLevelOne_name)).collect(Collectors.toList());
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
                    levelTwoScore = Calculation.getPlusResult(levelTwoScore, Calculation.getMultiplicationResult(temp.getLevelThree_score(), temp.getLevelThree_weight()));
                }
                param.setLevelTwo_score(levelTwoScore);
            }

            for (Station_pj param : currtStationPjs) {
                List<String> levelTwoNames = partData.stream().filter(c -> c.getStationName().equals(station)
                                && c.getLevelOne_name().equals(param.getLevelOne_name()))
                        .map(c -> c.getLevelTwo_name()).distinct().collect(Collectors.toList());
                double levelOneScore = 0;
                for (String levelTwoName : levelTwoNames) {
                    Station_pj temp = partData.stream().filter(c -> c.getStationName().equals(station)
                            && c.getLevelOne_name().equals(param.getLevelOne_name())
                            && c.getLevelTwo_name().equals(levelTwoName)).collect(Collectors.toList()).get(0);
                    levelOneScore = Calculation.getPlusResult(levelOneScore, Calculation.getMultiplicationResult(temp.getLevelTwo_score(), temp.getLevelTwo_weight()));
                }
                param.setLevelOne_score(levelOneScore);
            }

            for (Station_pj param : currtStationPjs) {
                List<String> levelOneNames = partData.stream().filter(c -> c.getStationName().equals(station))
                        .map(c -> c.getLevelOne_name()).distinct().collect(Collectors.toList());
                double jxScore = 0;
                for (String levelOneName : levelOneNames) {
                    Station_pj temp = partData.stream().filter(c -> c.getStationName().equals(station)
                            && c.getLevelOne_name().equals(levelOneName)).collect(Collectors.toList()).get(0);
                    jxScore = Calculation.getPlusResult(jxScore, Calculation.getMultiplicationResult(temp.getLevelOne_score(), temp.getLevelOne_weight()));
                }
                param.setJx_score(jxScore);
            }
        }
    }

    public List<Station_pj> getRelativeJXPJ(String cycle, String queryDateString) {
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
            List<ZYQ_D_RSJ> myDatas = getDataForPortrait(queryStart, queryEnd);
            List<String> zyqNames = myDatas.stream().map(ZYQ_D_RSJ::getZyqName).distinct().collect(Collectors.toList());
            if (!myDatas.isEmpty()) {
                for (String zyqName : zyqNames) {
                    Station_pj stationPj = new Station_pj();
                    stationPj.setRq(queryStart);
                    stationPj.setStationName(zyqName);
                    long count = 1;
                    double curScore = 0;

                    count = myDatas.stream().filter(c -> c.getZyqName().equals(zyqName) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0 && c.getZyqScore() != null).count();
                    curScore = myDatas.stream().filter(c -> c.getZyqName().equals(zyqName) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0 && c.getZyqScore() != null)
                            .mapToDouble(ZYQ_D_RSJ::getZyqScore).sum();
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

    public List<Station_pj> getSinglePortraitJXPJ(String cycle, String queryDateString) {
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
            List<ZYQ_D_RSJ> myDatas = getDataForPortrait(preStart, preEnd);
            myDatas.addAll(getDataForPortrait(queryStart, queryEnd));
            List<String> zyqNames = myDatas.stream().map(ZYQ_D_RSJ::getZyqName).distinct().collect(Collectors.toList());
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("纵向绩效指标要求", null);
            if (!monitoringIndicatorNewList.isEmpty() && !myDatas.isEmpty()) {
                for (String zyqName : zyqNames) {
                    Station_pj stationPj = new Station_pj();
                    stationPj.setRq(queryStart);
                    stationPj.setStationName(zyqName);
                    long count = 1;
                    double preScore = 0;
                    double curScore = 0;

                    count = myDatas.stream().filter(c -> c.getZyqName().equals(zyqName) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(preEnd) <= 0).count();
                    preScore = myDatas.stream().filter(c -> c.getZyqName().equals(zyqName) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(preEnd) <= 0)
                            .mapToDouble(ZYQ_D_RSJ::getZyqScore).sum();
                    preScore = Calculation.getDivisionResult(preScore, count);

                    count = myDatas.stream().filter(c -> c.getZyqName().equals(zyqName) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0).count();
                    curScore = myDatas.stream().filter(c -> c.getZyqName().equals(zyqName) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0)
                            .mapToDouble(ZYQ_D_RSJ::getZyqScore).sum();
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

    public List<Station_pj> getSingleComprehensiveJXPJ(String cycle, String queryDateString) {
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
            List<ZYQ_D_RSJ> myDatas = getDataForPortrait(preStart, preEnd);
            myDatas.addAll(getDataForPortrait(queryStart, queryEnd));
            String[] zyqNames = Calculation.zyqNames;
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("纵向绩效指标要求", null);
            List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("zhjx")).collect(Collectors.toList());
            if (!monitoringIndicatorNewList.isEmpty() && !myDatas.isEmpty()) {
                for (String zyqName : zyqNames) {
                    Station_pj stationPj = new Station_pj();
                    stationPj.setRq(queryStart);
                    stationPj.setStationName(zyqName);
                    stationPj.setLevelOne_weight(indicatorsList.stream().filter(c -> c.getItemCode().equals("xdjxqz")).collect(Collectors.toList()).get(0).getWeight());//用来表示相对（横向）权重
                    stationPj.setLevelThree_weight(indicatorsList.stream().filter(c -> c.getItemCode().equals("zxjxqz")).collect(Collectors.toList()).get(0).getWeight());//用来表示纵向权重
                    long count = 1;
                    double preScore = 0;
                    double curScore = 0;

                    count = myDatas.stream().filter(c -> c.getZyqName().equals(zyqName) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(preEnd) <= 0 && c.getZyqScore() != null).count();
                    preScore = myDatas.stream().filter(c -> c.getZyqName().equals(zyqName) && c.getRq().compareTo(preStart) >= 0 && c.getRq().compareTo(preEnd) <= 0 && c.getZyqScore() != null)
                            .mapToDouble(ZYQ_D_RSJ::getZyqScore).sum();
                    preScore = Calculation.getDivisionResult(preScore, count);

                    count = myDatas.stream().filter(c -> c.getZyqName().equals(zyqName) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0 && c.getZyqScore() != null).count();
                    curScore = myDatas.stream().filter(c -> c.getZyqName().equals(zyqName) && c.getRq().compareTo(queryStart) >= 0 && c.getRq().compareTo(nextStart) < 0 && c.getZyqScore() != null)
                            .mapToDouble(ZYQ_D_RSJ::getZyqScore).sum();
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
}
