package com.xunjia.pes.bizData.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.report.entity.OilConsumePower;
import com.xunjia.pes.bizData.report.entity.WaterConsumePower;
import com.xunjia.pes.bizData.report.mapper.WaterConsumePowerMapper;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSBRSJ;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSZRSJ;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_JB;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_ZSZ;
import com.xunjia.pes.bizData.waterInjection.service.DMGC_S_D_ZSBRSJService;
import com.xunjia.pes.bizData.waterInjection.service.DMGC_S_D_ZSZRSJService;
import com.xunjia.pes.bizData.waterInjection.service.DMGC_S_JBService;
import com.xunjia.pes.bizData.waterInjection.service.DMGC_S_ZSZService;
import com.xunjia.pes.score.Calculation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class WaterConsumePowerService extends ServiceImpl<WaterConsumePowerMapper, WaterConsumePower> {
    @Autowired
    private DMGC_S_D_ZSZRSJService dmgcSDZszrsjService;

    @Autowired
    private DMGC_S_D_ZSBRSJService dmgcSDZsbrsjService;

    @Autowired
    private DMGC_S_ZSZService zszService;

    @Autowired
    private DMGC_S_JBService jbService;

    public ResponseData<Boolean> add(WaterConsumePower param) {
        ResponseData<Boolean> resp;
        try {
            this.save(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception ex) {
            resp = ResponseData.getError(ex);
        }
        return resp;
    }

    public ResponseData<Boolean> update(WaterConsumePower param) {
        ResponseData<Boolean> resp;
        try {
            this.updateById(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception ex) {
            resp = ResponseData.getError(ex);
        }
        return resp;
    }

    public List<WaterConsumePower> buildReportSchema(Integer year, Integer month, Boolean compute) {
        List<WaterConsumePower> result = queryWaterConsumePowerList(year, month);
        WaterConsumePower waterConsumePower;
        try {
            Date yearStart = DateUtils.parse(year + "-1-1", DateUtils.DATE_PATTERN);
            Date monthStart = DateUtils.parse(year + "-" + month + "-1", DateUtils.DATE_PATTERN);
            Date monthEnd = DateUtils.parse(DateUtils.getLastDay(monthStart), DateUtils.DATE_PATTERN);

            List<DMGC_S_D_ZSZRSJ> dmgcSDZszrsjList = dmgcSDZszrsjService.getEffectiveData(yearStart, monthEnd);
            List<DMGC_S_D_ZSBRSJ> dmgcSDZsbrsjList = dmgcSDZsbrsjService.getEffectiveData(yearStart, monthEnd);

            List<DMGC_S_ZSZ> zszList = zszService.getAll();
            List<DMGC_S_JB> jbList = jbService.getPageData(new DMGC_S_JB(), 1, 999).getRows();

            if (ListUtils.isListEmpty(result)) {
                for (String zyqName : Calculation.zyqNames) {
                    List<DMGC_S_ZSZ> tempZszs = zszList.stream().filter(c -> c.getZyqName() != null && c.getZyqName().equals(zyqName)).collect(Collectors.toList());
                    for (DMGC_S_ZSZ zsz : tempZszs) {
                        waterConsumePower = new WaterConsumePower();
                        waterConsumePower.setAreaName(zyqName);
                        waterConsumePower.setStationName(zsz.getMc());
                        waterConsumePower.setYear(year);
                        waterConsumePower.setMonth(month);
                        computeSingleWaterConsumePower(waterConsumePower, zsz, dmgcSDZszrsjList, dmgcSDZsbrsjList, monthStart, monthEnd, jbList, compute);
                        result.add(waterConsumePower);
                        add(waterConsumePower);
                    }
                }
            } else {
                for (String zyqName : Calculation.zyqNames) {
                    List<DMGC_S_ZSZ> tempZszs = zszList.stream().filter(c -> c.getZyqName() != null && c.getZyqName().equals(zyqName)).collect(Collectors.toList());
                    for (DMGC_S_ZSZ zsz : tempZszs) {
                        waterConsumePower = result.stream().filter(c -> c.getAreaName().equals(zyqName) && c.getStationName().equals(zsz.getMc())).findFirst().get();
                        computeSingleWaterConsumePower(waterConsumePower, zsz, dmgcSDZszrsjList, dmgcSDZsbrsjList, monthStart, monthEnd, jbList, compute);
                        if (compute) {
                            update(waterConsumePower);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private List<WaterConsumePower> queryWaterConsumePowerList(Integer year, Integer month) {
        LambdaQueryWrapper<WaterConsumePower> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WaterConsumePower::getYear, year);
        wrapper.eq(WaterConsumePower::getMonth, month);
        return this.list(wrapper);
    }

    public void computeSingleWaterConsumePower(WaterConsumePower waterConsumePower,
                                               DMGC_S_ZSZ zsz,
                                               List<DMGC_S_D_ZSZRSJ> dmgcSDZszrsjList,
                                               List<DMGC_S_D_ZSBRSJ> dmgcSDZsbrsjList,
                                               Date computeStart,
                                               Date computeEnd,
                                               List<DMGC_S_JB> jbList,
                                               Boolean compute) {
        if (waterConsumePower.getId() == null || compute) {
            //注水系统-月耗电量
            Double waterInjectPower = dmgcSDZszrsjList.stream().filter(c -> zsz.getEventId().equals(c.getZid())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getZhydl() != null).mapToDouble(DMGC_S_D_ZSZRSJ::getZhydl).sum();
            waterConsumePower.setWaterInjectPower(Calculation.getDivisionResult(waterInjectPower,10000));
        }
        //注水系统-月耗电量累计
        Double waterInjectPowerSum = dmgcSDZszrsjList.stream().filter(c -> zsz.getEventId().equals(c.getZid())
                && c.getZhydl() != null).mapToDouble(DMGC_S_D_ZSZRSJ::getZhydl).sum();
        waterConsumePower.setWaterInjectPowerSum(Calculation.getDivisionResult(waterInjectPowerSum,10000));

        if (waterConsumePower.getId() == null || compute) {
            //月泵水量
            Double pumpingWater = dmgcSDZsbrsjList.stream().filter(c -> zsz.getEventId().equals(c.getSszkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getBsl() != null).mapToDouble(DMGC_S_D_ZSBRSJ::getBsl).sum();
            waterConsumePower.setPumpingWater(Calculation.getDivisionResult(pumpingWater,10000));
        }
        //月泵水量累计
        Double pumpingWaterSum = dmgcSDZsbrsjList.stream().filter(c -> zsz.getEventId().equals(c.getSszkEventId())
                && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                && c.getBsl() != null).mapToDouble(DMGC_S_D_ZSBRSJ::getBsl).sum();
        waterConsumePower.setPumpingWaterSum(Calculation.getDivisionResult(pumpingWaterSum,10000));

        long count;
        if (waterConsumePower.getId() == null || compute) {
            //注水系统-泵水单耗-月均单耗
            Double waterInjectPumpingUnit = dmgcSDZsbrsjList.stream().filter(c -> zsz.getEventId().equals(c.getSszkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getBsdh() != null).mapToDouble(DMGC_S_D_ZSBRSJ::getBsdh).sum();
            count = dmgcSDZsbrsjList.stream().filter(c -> zsz.getEventId().equals(c.getSszkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getBsdh() != null).count();
            waterConsumePower.setWaterInjectPumpingUnit(Calculation.getDivisionResult(waterInjectPumpingUnit, count));
        }
        //注水系统-泵水单耗-月均单耗累计平均
        Double waterInjectPumpingUnitSumAve = dmgcSDZsbrsjList.stream().filter(c -> zsz.getEventId().equals(c.getSszkEventId())
                && c.getBsdh() != null).mapToDouble(DMGC_S_D_ZSBRSJ::getBsdh).sum();
        count = dmgcSDZsbrsjList.stream().filter(c -> zsz.getEventId().equals(c.getSszkEventId())
                && c.getBsdh() != null).count();
        waterConsumePower.setWaterInjectPumpingUnitSumAve(Calculation.getDivisionResult(waterInjectPumpingUnitSumAve,count));
        //注水系统-泵台数
        Integer pumpCount = Integer.parseInt(Long.toString(jbList.stream().filter(c -> c.getSszkid().equals(zsz.getEventId())).count()));
        waterConsumePower.setPumpCount(pumpCount);
        if (waterConsumePower.getId() == null || compute) {
            //注水系统-当月运行台数
            Integer pumpRunCount = Integer.parseInt(Long.toString(dmgcSDZsbrsjList.stream().filter(c -> zsz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()).map(DMGC_S_D_ZSBRSJ::getJbEventId).distinct().count()));
            waterConsumePower.setPumpRunCount(pumpRunCount);
        }
        //注水系统-累计平均运行台数
        Double pumpRunCountSumAve = Double.parseDouble(Long.toString(dmgcSDZsbrsjList.stream().filter(c -> zsz.getEventId().equals(c.getSszkEventId())).map(DMGC_S_D_ZSBRSJ::getJbEventId).distinct().count()));
        waterConsumePower.setPumpRunCountSumAve(Calculation.getDivisionResult(pumpRunCountSumAve, DateUtils.getMonth(computeStart)));

        if (waterConsumePower.getId() == null || compute) {
            //注水系统-月平均泵效
            Double pumpEfficiencyAve = dmgcSDZsbrsjList.stream().filter(c -> zsz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime() && c.getBx() != null).mapToDouble(DMGC_S_D_ZSBRSJ::getBx).sum();
            count = dmgcSDZsbrsjList.stream().filter(c -> zsz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime() && c.getBx() != null).mapToDouble(DMGC_S_D_ZSBRSJ::getBx).count();
            waterConsumePower.setPumpEfficiencyAve(Calculation.getDivisionResult(pumpEfficiencyAve, count));
        }
        //注水系统-月累计平均泵效
        Double pumpEfficiencySumAve = dmgcSDZsbrsjList.stream().filter(c -> zsz.getEventId().equals(c.getSszkEventId()) && c.getBx() != null).mapToDouble(DMGC_S_D_ZSBRSJ::getBx).sum();
        count = dmgcSDZsbrsjList.stream().filter(c -> zsz.getEventId().equals(c.getSszkEventId()) && c.getBx() != null).mapToDouble(DMGC_S_D_ZSBRSJ::getBx).count();
        waterConsumePower.setPumpEfficiencySumAve(Calculation.getDivisionResult(pumpEfficiencySumAve, count));
    }

    public ChartOption getStatistics(Integer year, Integer month) {
        ChartOption result = new ChartOption();
        String[] lengnds = new String[]{"耗电量"};
        result.setLegend(Arrays.asList(lengnds));
        result.setXAxis(Arrays.stream(Calculation.zyqNames).filter(c -> !c.equals("生产维修大队")).collect(Collectors.toList()));
        List<WaterConsumePower> waterConsumePowers = queryWaterConsumePowerList(year, month);
        for (String legend : result.getLegend()) {
            ChartOption.Serie mySerie = result.new Serie();
            mySerie.setName(legend);
            mySerie.setType("bar");
            for (String zyq : result.getXAxis()) {
                Double tempReult = (double) 0;
                switch (legend) {
                    case "耗电量":
                        Double waterInjectPower = waterConsumePowers.stream().filter(c -> c.getAreaName().equals(zyq) && c.getWaterInjectPower() != null).mapToDouble(WaterConsumePower::getWaterInjectPower).sum();
                        tempReult = Calculation.getPlusResult(tempReult, waterInjectPower);
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
        String[] lengnds = new String[]{ "泵水单耗"};
        result.setLegend(Arrays.asList(lengnds));
        result.setXAxis(Arrays.stream(Calculation.zyqNames).filter(c -> !c.equals("生产维修大队")).collect(Collectors.toList()));
        List<WaterConsumePower> waterConsumePowers = queryWaterConsumePowerList(year, month);
        for (String legend : result.getLegend()) {
            ChartOption.Serie mySerie = result.new Serie();
            mySerie.setName(legend);
            mySerie.setType("bar");
            for (String zyq : result.getXAxis()) {
                Double tempReult = (double) 0;
                switch (legend) {
                    case "泵水单耗":
                        Double waterInjectPumpingUnit = waterConsumePowers.stream().filter(c -> c.getAreaName().equals(zyq) && c.getWaterInjectPumpingUnit() != null).mapToDouble(WaterConsumePower::getWaterInjectPumpingUnit).sum();
                        long count = waterConsumePowers.stream().filter(c -> c.getAreaName().equals(zyq) && c.getWaterInjectPumpingUnit() != null).count();
                        tempReult = Calculation.getPlusResult(tempReult, Calculation.getDivisionResult(waterInjectPumpingUnit, count));
                        break;
                }
                mySerie.getData().add(tempReult);
            }
            result.getSeries().add(mySerie);
        }
        return result;
    }
}
