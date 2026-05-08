package com.xunjia.pes.bizData.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.pes.bizData.BaseEntity;
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.report.entity.WaterSupplyConsumePower;
import com.xunjia.pes.bizData.report.entity.WaterSupplyConsumePower;
import com.xunjia.pes.bizData.report.mapper.WaterSupplyConsumePowerMapper;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_D_SBYX;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_D_SCLZRSJ;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_SCLJB;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_SCLZ;
import com.xunjia.pes.score.Calculation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class WaterSupplyConsumePowerService extends ServiceImpl<WaterSupplyConsumePowerMapper, WaterSupplyConsumePower> {
    static String[] stations = new String[]{"转水站", "提升站", "净水厂", "厂区饮用水处理站", "厂生活污水处理站"};

    public ResponseData<Boolean> add(WaterSupplyConsumePower param) {
        ResponseData<Boolean> resp;
        try {
            this.save(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception ex) {
            resp = ResponseData.getError(ex);
        }
        return resp;
    }

    public ResponseData<Boolean> update(WaterSupplyConsumePower param) {
        ResponseData<Boolean> resp;
        try {
            this.updateById(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception ex) {
            resp = ResponseData.getError(ex);
        }
        return resp;
    }

    private List<WaterSupplyConsumePower> queryWaterSupplyConsumePowerList(Integer year, Integer month) {
        LambdaQueryWrapper<WaterSupplyConsumePower> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WaterSupplyConsumePower::getYear, year);
        wrapper.eq(WaterSupplyConsumePower::getMonth, month);
        return this.list(wrapper);
    }

    private List<WaterSupplyConsumePower> queryWaterSupplyConsumePowerListSum(Integer year, Integer month) {
        LambdaQueryWrapper<WaterSupplyConsumePower> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WaterSupplyConsumePower::getYear, year);
        wrapper.ge(WaterSupplyConsumePower::getMonth, 1);
        wrapper.le(WaterSupplyConsumePower::getMonth, month);
        return this.list(wrapper);
    }

    public List<WaterSupplyConsumePower> buildReportSchema(Integer year, Integer month, Boolean compute) {
        List<WaterSupplyConsumePower> waterSupplyConsumePowersForSum = queryWaterSupplyConsumePowerListSum(year, month);
        List<WaterSupplyConsumePower> result = queryWaterSupplyConsumePowerList(year, month);
        WaterSupplyConsumePower watersupplyConsumePower;
        try {
            Date monthStart = DateUtils.parse(year + "-" + month + "-1", DateUtils.DATE_PATTERN);
            Date monthEnd = DateUtils.parse(DateUtils.getLastDay(monthStart), DateUtils.DATE_PATTERN);

            if (ListUtils.isListEmpty(result)) {
                for (String station : stations) {
                    watersupplyConsumePower = new WaterSupplyConsumePower();
                    watersupplyConsumePower.setAreaName("生产维修大队");
                    watersupplyConsumePower.setStationName(station);
                    watersupplyConsumePower.setYear(year);
                    watersupplyConsumePower.setMonth(month);
                    computeSingleWaterSupplyConsumePower(watersupplyConsumePower, waterSupplyConsumePowersForSum, monthStart, monthEnd, compute);
                    result.add(watersupplyConsumePower);
                    add(watersupplyConsumePower);
                }
            } else {
                for (String station : stations) {
                    watersupplyConsumePower = result.stream().filter(c -> c.getAreaName().equals("生产维修大队") && c.getStationName().equals(station)).findFirst().get();
                    computeSingleWaterSupplyConsumePower(watersupplyConsumePower, waterSupplyConsumePowersForSum, monthStart, monthEnd, compute);
                    if (compute) {
                        update(watersupplyConsumePower);
                    }
                }
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    public void computeSingleWaterSupplyConsumePower(WaterSupplyConsumePower watersupplyConsumePower,
                                                     List<WaterSupplyConsumePower> waterSupplyConsumePowersForSum,
                                                     Date computeStart,
                                                     Date computeEnd,
                                                     Boolean compute) {

        //供水系统-月耗电量（没有，依靠录入）
        //供水系统-月耗电量累计
        Double waterSupplyPowerSum = waterSupplyConsumePowersForSum.stream().filter(c -> c.getAreaName().equals(watersupplyConsumePower.getAreaName()) && c.getStationName().equals(watersupplyConsumePower.getStationName()) && c.getWaterSupplyPower() != null).mapToDouble(WaterSupplyConsumePower::getWaterSupplyPower).sum();
        watersupplyConsumePower.setWaterSupplyPowerSum(waterSupplyPowerSum);

        //月泵水量（没有，依靠录入）
        //月泵水量累计
        Double pumpingWaterSum = waterSupplyConsumePowersForSum.stream().filter(c -> c.getAreaName().equals(watersupplyConsumePower.getAreaName()) && c.getStationName().equals(watersupplyConsumePower.getStationName()) && c.getPumpingWater() != null).mapToDouble(WaterSupplyConsumePower::getPumpingWater).sum();
        watersupplyConsumePower.setPumpingWaterSum(pumpingWaterSum);

        //供水系统-泵水单耗-月均单耗（没有，依靠录入）
        //供水系统-泵水单耗-月均单耗累计平均
        Double waterSupplyPumpingUnitSumAve = waterSupplyConsumePowersForSum.stream().filter(c -> c.getAreaName().equals(watersupplyConsumePower.getAreaName()) && c.getStationName().equals(watersupplyConsumePower.getStationName()) && c.getWaterSupplyPumpingUnit() != null).mapToDouble(WaterSupplyConsumePower::getWaterSupplyPumpingUnit).sum();
        watersupplyConsumePower.setWaterSupplyPumpingUnitSumAve(Calculation.getDivisionResult(waterSupplyPumpingUnitSumAve, DateUtils.getMonth(computeStart)));

        //供水系统-泵台数（没有，依靠录入）

        //供水系统-当月运行台数（没有，依靠录入）
        //供水系统-累计平均运行台数
        Double pumpRunCountSumAve = waterSupplyConsumePowersForSum.stream().filter(c -> c.getAreaName().equals(watersupplyConsumePower.getAreaName()) && c.getStationName().equals(watersupplyConsumePower.getStationName()) && c.getPumpRunCount() != null).mapToDouble(WaterSupplyConsumePower::getPumpRunCount).sum();
        watersupplyConsumePower.setPumpRunCountSumAve(Calculation.getDivisionResult(pumpRunCountSumAve, DateUtils.getMonth(computeStart)));

        //供水系统-月平均泵效（没有，依靠录入）
        //供水系统-月累计平均泵效
        Double pumpEfficiencySumAve = waterSupplyConsumePowersForSum.stream().filter(c -> c.getAreaName().equals(watersupplyConsumePower.getAreaName()) && c.getStationName().equals(watersupplyConsumePower.getStationName()) && c.getPumpEfficiencyAve() != null).mapToDouble(WaterSupplyConsumePower::getPumpEfficiencyAve).sum();
        watersupplyConsumePower.setPumpEfficiencySumAve(Calculation.getDivisionResult(pumpEfficiencySumAve, DateUtils.getMonth(computeStart)));
    }

    public ChartOption getStatistics(Integer year, Integer month) {
        ChartOption result = new ChartOption();
        String[] lengnds = new String[]{"耗电量"};
        result.setLegend(Arrays.asList(lengnds));
        result.setXAxis(Arrays.stream(stations).collect(Collectors.toList()));
        List<WaterSupplyConsumePower> treatmentConsumePowers = queryWaterSupplyConsumePowerList(year, month);
        for (String legend : result.getLegend()) {
            ChartOption.Serie mySerie = result.new Serie();
            mySerie.setName(legend);
            mySerie.setType("bar");
            for (String station : result.getXAxis()) {
                Double tempReult = (double) 0;
                switch (legend) {
                    case "耗电量":
                        Double waterSupplyPower = treatmentConsumePowers.stream().filter(c -> c.getStationName().equals(station) && c.getWaterSupplyPower() != null).mapToDouble(WaterSupplyConsumePower::getWaterSupplyPower).sum();
                        tempReult = Calculation.getPlusResult(tempReult, waterSupplyPower);
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
        String[] lengnds = new String[]{"泵水单耗"};
        result.setLegend(Arrays.asList(lengnds));
        result.setXAxis(Arrays.stream(stations).collect(Collectors.toList()));
        List<WaterSupplyConsumePower> treatmentConsumePowers = queryWaterSupplyConsumePowerList(year, month);
        for (String legend : result.getLegend()) {
            ChartOption.Serie mySerie = result.new Serie();
            mySerie.setName(legend);
            mySerie.setType("bar");
            for (String station : result.getXAxis()) {
                Double tempReult = (double) 0;
                switch (legend) {
                    case "泵水单耗":
                        Double waterSupplyPumpingUnit = treatmentConsumePowers.stream().filter(c -> c.getStationName().equals(station) && c.getWaterSupplyPumpingUnit() != null).mapToDouble(WaterSupplyConsumePower::getWaterSupplyPumpingUnit).sum();
                        long count = treatmentConsumePowers.stream().filter(c -> c.getStationName().equals(station) && c.getWaterSupplyPumpingUnit() != null).count();
                        tempReult = Calculation.getPlusResult(tempReult, Calculation.getDivisionResult(waterSupplyPumpingUnit, count));
                        break;
                }
                mySerie.getData().add(tempReult);
            }
            result.getSeries().add(mySerie);
        }
        return result;
    }
}
