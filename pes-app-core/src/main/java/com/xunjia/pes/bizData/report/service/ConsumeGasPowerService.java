package com.xunjia.pes.bizData.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.oil.entity.*;
import com.xunjia.pes.bizData.oil.service.DMGC_Y_D_ZYZService;
import com.xunjia.pes.bizData.oil.service.DMGC_Y_ZYZService;
import com.xunjia.pes.bizData.report.entity.ConsumeGasPower;
import com.xunjia.pes.bizData.report.mapper.ConsumeGasPowerMapper;
import com.xunjia.pes.score.Calculation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ConsumeGasPowerService extends ServiceImpl<ConsumeGasPowerMapper, ConsumeGasPower> {

    @Autowired
    private DMGC_Y_D_ZYZService dmgcYDZyzService;

    @Autowired
    private DMGC_Y_ZYZService zyzService;

    @Value("${a5SyncUrl:127.0.0.1:8099}")
    private String a5SyncUrl;

    //第六作业区气站（报表中第六作业区增加以下几个气站的耗气量）
    static String[] sixthAreaStations = new String[]{"三-1集气站", "三-2集气站", "五站集气站", "涝洲集气站", "庄深1集气站"};
    //生产维修大队（报表中生产维修大队增加净水厂的耗气量）
    static String[] maintenanceBrigade = new String[]{"净水厂"};

    public ResponseData<Boolean> add(ConsumeGasPower param) {
        ResponseData<Boolean> resp;
        try {
            this.save(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception ex) {
            resp = ResponseData.getError(ex);
        }
        return resp;
    }

    public ResponseData<Boolean> update(ConsumeGasPower param) {
        ResponseData<Boolean> resp;
        try {
            this.updateById(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception ex) {
            resp = ResponseData.getError(ex);
        }
        return resp;
    }

    private List<ConsumeGasPower> queryConsumeGasList(Integer year, Integer month) {
        LambdaQueryWrapper<ConsumeGasPower> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConsumeGasPower::getYear, year);
        wrapper.eq(ConsumeGasPower::getMonth, month);
        return this.list(wrapper);
    }

    public List<ConsumeGasPower> queryConsumeGasListSum(Integer year, Integer month) {
        LambdaQueryWrapper<ConsumeGasPower> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConsumeGasPower::getYear, year);
        wrapper.ge(ConsumeGasPower::getMonth, 1);
        wrapper.le(ConsumeGasPower::getMonth, month);
        return this.list(wrapper);
    }


    public List<ConsumeGasPower> buildReportSchema(Integer year, Integer month, Boolean compute) {
        List<ConsumeGasPower> consumeGasPowerForSum = queryConsumeGasListSum(year, month);
        List<ConsumeGasPower> result = queryConsumeGasList(year, month);
        ConsumeGasPower consumeGasPower;
        try {
            Date yearStart = DateUtils.parse(year + "-1-1", DateUtils.DATE_PATTERN);
            Date monthStart = DateUtils.parse(year + "-" + month + "-1", DateUtils.DATE_PATTERN);
            Date monthEnd = DateUtils.parse(DateUtils.getLastDay(monthStart), DateUtils.DATE_PATTERN);
            //从A5查询集所站月累计数据
            List<Map<String, Object>> jqzYsj = new ArrayList<>();
            if (ListUtils.isListEmpty(result) || compute) {
                try {
                    jqzYsj = getJqzSum(DateUtils.format(monthStart, DateUtils.DATE_PATTERN), DateUtils.format(monthEnd, DateUtils.DATE_PATTERN));
                } catch (Exception ex) {
                    String err = ex.getMessage();
                }
            }
            List<DMGC_Y_D_ZYZ> dmgcYDZyzList = dmgcYDZyzService.getEffectiveData(yearStart, monthEnd);
            List<DMGC_Y_ZYZ> zyzList = zyzService.getAll();

            if (ListUtils.isListEmpty(result)) {
                for (String zyqName : Calculation.zyqNames) {
                    List<DMGC_Y_ZYZ> tempZyzs = zyzList.stream().filter(c -> c.getZyqName() != null && c.getZyqName().equals(zyqName)).collect(Collectors.toList());
                    for (DMGC_Y_ZYZ zyz : tempZyzs) {
                        consumeGasPower = new ConsumeGasPower();
                        consumeGasPower.setAreaName(zyqName);
                        consumeGasPower.setStationName(zyz.getMc());
                        consumeGasPower.setYear(year);
                        consumeGasPower.setMonth(month);
                        computeSingleConsumeGas(consumeGasPower, zyz, dmgcYDZyzList, monthStart, monthEnd, compute);
                        result.add(consumeGasPower);
                        add(consumeGasPower);
                    }
                    if (zyqName.equals("第六作业区")) {
                        for (String stationName : sixthAreaStations) {
                            consumeGasPower = new ConsumeGasPower();
                            consumeGasPower.setAreaName(zyqName);
                            consumeGasPower.setStationName(stationName);
                            consumeGasPower.setYear(year);
                            consumeGasPower.setMonth(month);
                            computeSpecial(consumeGasPower, monthStart, monthEnd, consumeGasPowerForSum, compute, jqzYsj);
                            result.add(consumeGasPower);
                            add(consumeGasPower);
                        }
                    }
                    if (zyqName.equals("生产维修大队")) {
                        for (String stationName : maintenanceBrigade) {
                            consumeGasPower = new ConsumeGasPower();
                            consumeGasPower.setAreaName(zyqName);
                            consumeGasPower.setStationName(stationName);
                            consumeGasPower.setYear(year);
                            consumeGasPower.setMonth(month);
                            computeSpecial(consumeGasPower, monthStart, monthEnd, consumeGasPowerForSum, compute, jqzYsj);
                            result.add(consumeGasPower);
                            add(consumeGasPower);
                        }
                    }
                }

            } else {
                for (String zyqName : Calculation.zyqNames) {
                    List<DMGC_Y_ZYZ> tempZyzs = zyzList.stream().filter(c -> c.getZyqName() != null && c.getZyqName().equals(zyqName)).collect(Collectors.toList());
                    for (DMGC_Y_ZYZ zyz : tempZyzs) {
                        consumeGasPower = result.stream().filter(c -> c.getAreaName().equals(zyqName) && c.getStationName().equals(zyz.getMc())).findFirst().get();
                        computeSingleConsumeGas(consumeGasPower, zyz, dmgcYDZyzList, monthStart, monthEnd, compute);
                        if (compute) {
                            update(consumeGasPower);
                        }
                        if (zyqName.equals("第六作业区")) {
                            for (String stationName : sixthAreaStations) {
                                consumeGasPower = result.stream().filter(c -> c.getAreaName().equals(zyqName) && c.getStationName().equals(stationName)).findFirst().get();
                                computeSpecial(consumeGasPower, monthStart, monthEnd, consumeGasPowerForSum, compute, jqzYsj);
                                if (compute) {
                                    update(consumeGasPower);
                                }
                            }
                        }

                        if (zyqName.equals("生产维修大队")) {
                            for (String stationName : maintenanceBrigade) {
                                consumeGasPower = result.stream().filter(c -> c.getAreaName().equals(zyqName) && c.getStationName().equals(stationName)).findFirst().get();
                                computeSpecial(consumeGasPower, monthStart, monthEnd, consumeGasPowerForSum, compute, jqzYsj);
                                if (compute) {
                                    update(consumeGasPower);
                                }
                            }
                        }
                    }
                }

            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    public void computeSingleConsumeGas(ConsumeGasPower consumeGasPower, DMGC_Y_ZYZ zyz, List<DMGC_Y_D_ZYZ> dmgcYDZyzList, Date computeStart, Date computeEnd, Boolean compute) {
        //月耗气量
        Double consumeGas = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId())
                && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                && c.getHql() != null).mapToDouble(DMGC_Y_D_ZYZ::getHql).sum();
        if (consumeGasPower.getId() == null || compute) {
            consumeGasPower.setConsumeGas(Calculation.getDivisionResult(consumeGas, 10000));
        }
        //月耗气量累计
        Double consumeGasSum = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId())
                && c.getHql() != null).mapToDouble(DMGC_Y_D_ZYZ::getHql).sum();
        consumeGasPower.setConsumeGasSum(Calculation.getDivisionResult(consumeGasSum, 10000));
        if (consumeGasPower.getId() == null || compute) {
            //月产液量
            Double liquidProduction = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getCll() != null).mapToDouble(DMGC_Y_D_ZYZ::getCll).sum();
            consumeGasPower.setLiquidProduction(Calculation.getDivisionResult(liquidProduction, 10000));
        }
        //月产液量累计
        Double liquidProductionSum = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId())
                && c.getCll() != null).mapToDouble(DMGC_Y_D_ZYZ::getCll).sum();
        consumeGasPower.setLiquidProductionSum(Calculation.getDivisionResult(liquidProductionSum, 10000));
        long count;
        if (consumeGasPower.getId() == null || compute) {
            //吨液耗气-月均单耗
            Double gasUnit = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getDyhq() != null).mapToDouble(DMGC_Y_D_ZYZ::getDyhq).sum();
            count = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getDyhq() != null).count();
            consumeGasPower.setGasUnit(Calculation.getDivisionResult(gasUnit, count));
        }
        //吨液耗气-月均单耗累计平均
        Double gasUnitSumAve = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId())
                && c.getDyhq() != null).mapToDouble(DMGC_Y_D_ZYZ::getDyhq).sum();
        count = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId())
                && c.getDyhq() != null).count();
        consumeGasPower.setGasUnitSumAve(Calculation.getDivisionResult(gasUnitSumAve, count));

        //月产油量
        Double oilProduction = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId())
                && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                && c.getWsyoul() != null).mapToDouble(DMGC_Y_D_ZYZ::getWsyoul).sum();
        if (consumeGasPower.getId() == null || compute) {
            consumeGasPower.setOilProduction(Calculation.getDivisionResult(oilProduction, 10000));
        }

        //月产油量累计
        Double oilProductionSum = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId())
                && c.getWsyoul() != null).mapToDouble(DMGC_Y_D_ZYZ::getWsyoul).sum();
        consumeGasPower.setOilProductionSum(Calculation.getDivisionResult(oilProductionSum, 10000));

        //吨油耗气-月均单耗
        Double petroleumGasUnit = Calculation.getDivisionResult(consumeGas, oilProductionSum);
        consumeGasPower.setPetroleumGasUnit(petroleumGasUnit);

        //吨油耗气-月均单耗累计平均
        Double petroleumGasUnitSumAve = Calculation.getDivisionResult(consumeGasSum, oilProductionSum);
        consumeGasPower.setPetroleumGasUnitSumAve(petroleumGasUnitSumAve);
    }

    private void computeSpecial(ConsumeGasPower consumeGasPower, Date computeStart, Date computeEnd, List<ConsumeGasPower> consumeGasPowerForSum, Boolean compute, List<Map<String, Object>> jqzYsj) {
        //月耗气量(生产维修大队的耗气量依靠录入，第六作业区集气站的耗气量从A5的气井集气站生产动态日数据取 DMGC_Q_D_QJJQZRSJ)
        if (!consumeGasPower.getAreaName().equals("生产维修大队")) {
            if (consumeGasPower.getId() == null || compute) {
                for (Map<String, Object> stringObjectMap : jqzYsj) {
                    if (stringObjectMap.get("JQZMC").equals(consumeGasPower.getStationName())) {
                        Double consumeGas = Double.parseDouble(stringObjectMap.get("YHQL").toString());//查询A5
                        consumeGasPower.setConsumeGas(Calculation.getDivisionResult(consumeGas, 10000));
                        break;
                    }
                }
            }
        }
        //集输系统-电加热-月耗电量累计（累加电加热各月的录入数据）
        Double consumeGasSum = consumeGasPowerForSum.stream().filter(c -> c.getAreaName().equals(consumeGasPower.getAreaName()) && c.getStationName().equals(consumeGasPower.getStationName()) && c.getConsumeGas() != null)
                .mapToDouble(ConsumeGasPower::getConsumeGas).sum();
        consumeGasPower.setConsumeGasSum(consumeGasSum);
    }

    public ChartOption getStatistics(Integer year, Integer month) {
        ChartOption result = new ChartOption();
        String[] lengnds = new String[]{"耗气量"};
        result.setLegend(Arrays.asList(lengnds));
        result.setXAxis(Arrays.stream(Calculation.zyqNames).filter(c -> !c.equals("生产维修大队") && !c.equals("第八作业区")).collect(Collectors.toList()));
        List<ConsumeGasPower> consumeGasPowers = queryConsumeGasList(year, month);
        for (String legend : result.getLegend()) {
            ChartOption.Serie mySerie = result.new Serie();
            mySerie.setName(legend);
            mySerie.setType("bar");
            for (String zyq : result.getXAxis()) {
                Double tempReult = (double) 0;
                switch (legend) {
                    case "耗气量":
                        Double consumeGas = consumeGasPowers.stream().filter(c -> c.getAreaName().equals(zyq) && c.getConsumeGas() != null).mapToDouble(ConsumeGasPower::getConsumeGas).sum();
                        tempReult = Calculation.getPlusResult(tempReult, consumeGas);
                        break;
                }
                mySerie.getData().add(tempReult);
            }
            result.getSeries().add(mySerie);
        }
        return result;
    }

    public ChartOption getStatisticsUnit(Integer year, Integer month) {
        ChartOption result = new ChartOption();
        String[] lengnds = new String[]{"吨液耗气"};
        result.setLegend(Arrays.asList(lengnds));
        result.setXAxis(Arrays.stream(Calculation.zyqNames).filter(c -> !c.equals("生产维修大队") && !c.equals("第八作业区")).collect(Collectors.toList()));
        List<ConsumeGasPower> consumeGasPowers = queryConsumeGasList(year, month);
        for (String legend : result.getLegend()) {
            ChartOption.Serie mySerie = result.new Serie();
            mySerie.setName(legend);
            mySerie.setType("bar");
            for (String zyq : result.getXAxis()) {
                Double tempReult = (double) 0;
                switch (legend) {
                    case "吨液耗气":
                        Double gasUnit = consumeGasPowers.stream().filter(c -> c.getAreaName().equals(zyq) && c.getGasUnit() != null).mapToDouble(ConsumeGasPower::getGasUnit).sum();
                        long count = consumeGasPowers.stream().filter(c -> c.getAreaName().equals(zyq) && c.getGasUnit() != null).count();
                        tempReult = Calculation.getPlusResult(tempReult, Calculation.getDivisionResult(gasUnit, count));
                        break;
                }
                mySerie.getData().add(tempReult);
            }
            result.getSeries().add(mySerie);
        }
        return result;
    }

    private List<Map<String, Object>> getJqzSum(String startDate, String endDate) {
        RestTemplate template = new RestTemplate();
        String url = "http://" + a5SyncUrl + "/a5_consume_gas/getJqzSum?startDate=" + startDate + "&endDate=" + endDate;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<List<Map<String, Object>>> requestEntity = new HttpEntity<>(null, headers);
        List<Map<String, Object>> result = template.exchange(url, HttpMethod.GET, requestEntity, List.class).getBody();
        return result;
    }
}
