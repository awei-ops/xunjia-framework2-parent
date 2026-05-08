package com.xunjia.pes.bizData.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.pes.bizData.BaseEntity;
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_ZYZ;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_ZYZ;
import com.xunjia.pes.bizData.oil.service.DMGC_Y_D_ZYZService;
import com.xunjia.pes.bizData.oil.service.DMGC_Y_ZYZService;
import com.xunjia.pes.bizData.report.entity.MonthlyEnergy;
import com.xunjia.pes.bizData.report.mapper.MonthlyEnergyMapper;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSBRSJ;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSZRSJ;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_ZSZ;
import com.xunjia.pes.bizData.waterInjection.service.DMGC_S_D_ZSBRSJService;
import com.xunjia.pes.bizData.waterInjection.service.DMGC_S_D_ZSZRSJService;
import com.xunjia.pes.bizData.waterInjection.service.DMGC_S_ZSZService;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_D_SCLZRSJ;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_SCLZ;
import com.xunjia.pes.bizData.waterTreatment.service.DMGC_S_D_SCLZRSJService;
import com.xunjia.pes.bizData.waterTreatment.service.DMGC_S_SCLZService;
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
public class MonthlyEnergyService extends ServiceImpl<MonthlyEnergyMapper, MonthlyEnergy> {

    @Autowired
    private DMGC_Y_D_ZYZService dmgcYDZyzService;

    @Autowired
    private DMGC_S_D_ZSZRSJService dmgcSDZszrsjService;

    @Autowired
    private DMGC_S_D_SCLZRSJService dmgcSDSclzrsjService;

    @Autowired
    private DMGC_S_D_ZSBRSJService dmgcSDZsbrsjService;

    @Autowired
    private DMGC_Y_ZYZService zyzService;

    @Autowired
    private DMGC_S_ZSZService zszService;

    @Autowired
    private DMGC_S_SCLZService sclzService;

    public ResponseData<Boolean> add(MonthlyEnergy param) {
        ResponseData<Boolean> resp;
        try {
            this.save(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception ex) {
            resp = ResponseData.getError(ex);
        }
        return resp;
    }

    public ResponseData<Boolean> update(MonthlyEnergy param) {
        ResponseData<Boolean> resp;
        try {
            this.updateById(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception ex) {
            resp = ResponseData.getError(ex);
        }
        return resp;
    }

    private List<MonthlyEnergy> queryMonthlyEnergyList(Integer year, Integer month) {
        LambdaQueryWrapper<MonthlyEnergy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonthlyEnergy::getYear, year);
        wrapper.eq(MonthlyEnergy::getMonth, month);
        return this.list(wrapper);
    }

    public List<MonthlyEnergy> queryMonthlyEnergyListSum(Integer year,Integer month) {
        LambdaQueryWrapper<MonthlyEnergy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonthlyEnergy::getYear, year);
        wrapper.ge(MonthlyEnergy::getMonth, 1);
        wrapper.le(MonthlyEnergy::getMonth,month);
        return this.list(wrapper);
    }

    public List<MonthlyEnergy> buildReportSchema(Integer year, Integer month, Boolean compute) {
        List<MonthlyEnergy> monthlyEnergiesForSum = queryMonthlyEnergyListSum(year,month);
        List<MonthlyEnergy> result = queryMonthlyEnergyList(year, month);
        MonthlyEnergy monthlyEnergy;
        try {
            Date yearStart = DateUtils.parse(year + "-1-1", DateUtils.DATE_PATTERN);
            Date monthStart = DateUtils.parse(year + "-" + month + "-1", DateUtils.DATE_PATTERN);
            Date monthEnd = DateUtils.parse(DateUtils.getLastDay(monthStart), DateUtils.DATE_PATTERN);

            List<DMGC_Y_D_ZYZ> dmgcYDZyzList = dmgcYDZyzService.getEffectiveData(yearStart, monthEnd);
            List<DMGC_S_D_ZSZRSJ> dmgcSDZszrsjList = dmgcSDZszrsjService.getEffectiveData(yearStart, monthEnd);
            List<DMGC_S_D_SCLZRSJ> dmgcSDSclzrsjList = dmgcSDSclzrsjService.getEffectiveData(yearStart, monthEnd);
            List<DMGC_S_D_ZSBRSJ> dmgcSDZsbrsjList = dmgcSDZsbrsjService.getEffectiveData(yearStart, monthEnd);

            List<DMGC_Y_ZYZ> zyzList = zyzService.getAll();
            List<DMGC_S_ZSZ> zszList = zszService.getAll();
            List<DMGC_S_SCLZ> sclzList = sclzService.getAll();

            if (ListUtils.isListEmpty(result)) {
                for (String zyqName : Calculation.zyqNames) {
                    monthlyEnergy = new MonthlyEnergy();
                    monthlyEnergy.setAreaName(zyqName);
                    monthlyEnergy.setYear(year);
                    monthlyEnergy.setMonth(month);
                    computeSingleMonthlyEnergy(monthlyEnergy, dmgcYDZyzList, dmgcSDZszrsjList, dmgcSDSclzrsjList, dmgcSDZsbrsjList, monthStart, monthEnd,
                            zyzList, zszList, sclzList, monthlyEnergiesForSum, compute);
                    result.add(monthlyEnergy);
                    add(monthlyEnergy);
                }
            } else {
                for (String zyqName : Calculation.zyqNames) {
                    monthlyEnergy = result.stream().filter(c -> c.getAreaName().equals(zyqName)).findFirst().get();
                    computeSingleMonthlyEnergy(monthlyEnergy, dmgcYDZyzList, dmgcSDZszrsjList, dmgcSDSclzrsjList, dmgcSDZsbrsjList, monthStart, monthEnd,
                            zyzList, zszList, sclzList, monthlyEnergiesForSum, compute);
                    if (compute) {
                        update(monthlyEnergy);
                    }
                }
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private void computeSingleMonthlyEnergy(MonthlyEnergy monthlyEnergy,
                                            List<DMGC_Y_D_ZYZ> dmgcYDZyzList,
                                            List<DMGC_S_D_ZSZRSJ> dmgcSDZszrsjList,
                                            List<DMGC_S_D_SCLZRSJ> dmgcSDSclzrsjList,
                                            List<DMGC_S_D_ZSBRSJ> dmgcSDZsbrsjList,
                                            Date computeStart,
                                            Date computeEnd,
                                            List<DMGC_Y_ZYZ> zyzList,
                                            List<DMGC_S_ZSZ> zszList,
                                            List<DMGC_S_SCLZ> sclzList,
                                            List<MonthlyEnergy> monthlyEnergiesForSum,
                                            Boolean compute) {
        List<String> zyzIds = zyzList.stream().filter(c -> c.getZyqName() != null && c.getZyqName().equals(monthlyEnergy.getAreaName())).map(BaseEntity::getEventId).collect(Collectors.toList());
        // id为null表明是新加的行，要计算，否则直接取保存后的值即可
        if (monthlyEnergy.getId() == null || compute) {
            //集输系统-站场-月耗电量（选择月份）
            Double oilStationPower = dmgcYDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getZhhdl() != null).mapToDouble(DMGC_Y_D_ZYZ::getZhhdl).sum();
            monthlyEnergy.setOilStationPower(Calculation.getDivisionResult(oilStationPower, 10000));
        }
        //集输系统-站场-月耗电量累计（每年1月1日到当前时间）
        Double oilStationPowerSum = dmgcYDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())
                && c.getZhhdl() != null).mapToDouble(DMGC_Y_D_ZYZ::getZhhdl).sum();
        monthlyEnergy.setOilStationPowerSum(Calculation.getDivisionResult(oilStationPowerSum, 10000));
        //集输系统-电加热-月耗电量（没有，依靠录入）
        //集输系统-电加热-月耗电量累计（累加电加热各月的录入数据）
        Double oilHeatingPowerSum = monthlyEnergiesForSum.stream().filter(c -> c.getAreaName().equals(monthlyEnergy.getAreaName()) && c.getOilHeatingPower() != null)
                .mapToDouble(MonthlyEnergy::getOilHeatingPower).sum();
        monthlyEnergy.setOilHeatingPowerSum(oilHeatingPowerSum);
        //集输系统-气系统-月耗电量（没有，依靠录入）
        //集输系统-气系统-月耗电量累计（累加气系统各月的录入数据）
        Double oilGasPowerSum = monthlyEnergiesForSum.stream().filter(c -> c.getAreaName().equals(monthlyEnergy.getAreaName()) && c.getOilGasPower() != null)
                .mapToDouble(MonthlyEnergy::getOilGasPower).sum();
        monthlyEnergy.setOilGasPowerSum(oilGasPowerSum);

        List<String> zszIds = zszList.stream().filter(c -> c.getZyqName() != null && c.getZyqName().equals(monthlyEnergy.getAreaName())).map(BaseEntity::getEventId).collect(Collectors.toList());
        if (monthlyEnergy.getId() == null || compute) {
            //注水系统-月耗电量
            Double waterInjectPower = dmgcSDZszrsjList.stream().filter(c -> zszIds.contains(c.getZid())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getZhydl() != null).mapToDouble(DMGC_S_D_ZSZRSJ::getZhydl).sum();
            monthlyEnergy.setWaterInjectPower(Calculation.getDivisionResult(waterInjectPower, 10000));
        }
        //注水系统-月耗电量累计
        Double waterInjectPowerSum = dmgcSDZszrsjList.stream().filter(c -> zszIds.contains(c.getZid())
                && c.getZhydl() != null).mapToDouble(DMGC_S_D_ZSZRSJ::getZhydl).sum();
        monthlyEnergy.setWaterInjectPowerSum(Calculation.getDivisionResult(waterInjectPowerSum, 10000));

        List<String> sclzIds = sclzList.stream().filter(c -> c.getZyqName() != null && c.getZyqName().equals(monthlyEnergy.getAreaName())).map(BaseEntity::getEventId).collect(Collectors.toList());
        if (monthlyEnergy.getId() == null || compute) {
            //水处理系统-月耗电量
            Double waterTreatmentPower = dmgcSDSclzrsjList.stream().filter(c -> sclzIds.contains(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getRhdl() != null).mapToDouble(DMGC_S_D_SCLZRSJ::getRhdl).sum();
            monthlyEnergy.setWaterTreatmentPower(Calculation.getDivisionResult(waterTreatmentPower, 10000));
        }
        //水处理系统-月耗电量累计
        Double waterTreatmentPowerSum = dmgcSDSclzrsjList.stream().filter(c -> sclzIds.contains(c.getZkEventId())
                && c.getRhdl() != null).mapToDouble(DMGC_S_D_SCLZRSJ::getRhdl).sum();
        monthlyEnergy.setWaterTreatmentPowerSum(Calculation.getDivisionResult(waterTreatmentPowerSum, 10000));

        //供水系统-月耗电量（没有，依靠录入）
        //供水系统-月耗电量累计（累加供水系统各月的录入数据）
        Double waterSupplyPowerSum = monthlyEnergiesForSum.stream().filter(c -> c.getAreaName().equals(monthlyEnergy.getAreaName()) && c.getWaterSupplyPower() != null)
                .mapToDouble(MonthlyEnergy::getWaterSupplyPower).sum();
        monthlyEnergy.setWaterSupplyPowerSum(waterSupplyPowerSum);

        if (monthlyEnergy.getId() == null || compute) {
            //月耗气量
            Double consumeGas = dmgcYDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getHql() != null).mapToDouble(DMGC_Y_D_ZYZ::getHql).sum();
            monthlyEnergy.setConsumeGas(Calculation.getDivisionResult(consumeGas, 10000));
        }
        //月耗气量累计
        Double consumeGasSum = dmgcYDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())
                && c.getHql() != null).mapToDouble(DMGC_Y_D_ZYZ::getHql).sum();
        monthlyEnergy.setConsumeGasSum(Calculation.getDivisionResult(consumeGasSum, 10000));

        //月能耗量=（各系统总耗电量和*1.229）+（总耗气量*13.3）
        Double energyConsumption = (double) 0;
        double oilAllPower = 0;
        oilAllPower = Calculation.getPlusResult(oilAllPower, monthlyEnergy.getOilStationPower() == null ? 0 : monthlyEnergy.getOilStationPower());
        oilAllPower = Calculation.getPlusResult(oilAllPower, monthlyEnergy.getOilHeatingPower() == null ? 0 : monthlyEnergy.getOilHeatingPower());
        oilAllPower = Calculation.getPlusResult(oilAllPower, monthlyEnergy.getOilGasPower() == null ? 0 : monthlyEnergy.getOilGasPower());
        oilAllPower = Calculation.getPlusResult(oilAllPower, monthlyEnergy.getWaterInjectPower() == null ? 0 : monthlyEnergy.getWaterInjectPower());
        oilAllPower = Calculation.getPlusResult(oilAllPower, monthlyEnergy.getWaterTreatmentPower() == null ? 0 : monthlyEnergy.getWaterTreatmentPower());
        oilAllPower = Calculation.getPlusResult(oilAllPower, monthlyEnergy.getWaterSupplyPower() == null ? 0 : monthlyEnergy.getWaterSupplyPower());
        energyConsumption = Calculation.getMultiplicationResult(oilAllPower, 1.229);
        energyConsumption = Calculation.getPlusResult(energyConsumption, Calculation.getMultiplicationResult(13.3, monthlyEnergy.getConsumeGas() == null ? 0 : monthlyEnergy.getConsumeGas()));
        monthlyEnergy.setEnergyConsumption(energyConsumption);

        //月能耗量累计=（各系统总耗电量累计和*1.229）+（总耗气量累计*13.3）
        Double energyConsumptionSum = (double)0;
        double oilAllPowerSum = 0;
        oilAllPowerSum = Calculation.getPlusResult(oilAllPowerSum, monthlyEnergy.getOilStationPowerSum() == null ? 0 : monthlyEnergy.getOilStationPowerSum());
        oilAllPowerSum = Calculation.getPlusResult(oilAllPowerSum, monthlyEnergy.getOilHeatingPowerSum() == null ? 0 : monthlyEnergy.getOilHeatingPowerSum());
        oilAllPowerSum = Calculation.getPlusResult(oilAllPowerSum, monthlyEnergy.getOilGasPowerSum() == null ? 0 : monthlyEnergy.getOilGasPowerSum());
        oilAllPowerSum = Calculation.getPlusResult(oilAllPowerSum, monthlyEnergy.getWaterInjectPowerSum() == null ? 0 : monthlyEnergy.getWaterInjectPowerSum());
        oilAllPowerSum = Calculation.getPlusResult(oilAllPowerSum, monthlyEnergy.getWaterTreatmentPowerSum() == null ? 0 : monthlyEnergy.getWaterTreatmentPowerSum());
        oilAllPowerSum = Calculation.getPlusResult(oilAllPowerSum, monthlyEnergy.getWaterSupplyPowerSum() == null ? 0 : monthlyEnergy.getWaterSupplyPowerSum());
        energyConsumptionSum = Calculation.getMultiplicationResult(oilAllPowerSum, 1.229);
        energyConsumptionSum = Calculation.getPlusResult(energyConsumptionSum, Calculation.getMultiplicationResult(13.3, monthlyEnergy.getConsumeGasSum() == null ? 0 : monthlyEnergy.getConsumeGasSum()));
        monthlyEnergy.setEnergyConsumptionSum(energyConsumptionSum);

        if (monthlyEnergy.getId() == null || compute) {
            //月产液量
            Double liquidProduction = dmgcYDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getCll() != null).mapToDouble(DMGC_Y_D_ZYZ::getCll).sum();
            monthlyEnergy.setLiquidProduction(Calculation.getDivisionResult(liquidProduction, 10000));
        }
        //月产液量累计
        Double liquidProductionSum = dmgcYDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())
                && c.getCll() != null).mapToDouble(DMGC_Y_D_ZYZ::getCll).sum();
        monthlyEnergy.setLiquidProductionSum(Calculation.getDivisionResult(liquidProductionSum, 10000));

        if (monthlyEnergy.getId() == null || compute) {
            //月产油量(外输油量)
            Double oilProduction = dmgcYDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getWsyoul() != null).mapToDouble(DMGC_Y_D_ZYZ::getWsyoul).sum();
            monthlyEnergy.setOilProduction(Calculation.getDivisionResult(oilProduction, 10000));
        }
        //月产油量累计
        Double oilProductionSum = dmgcYDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())
                && c.getWsyoul() != null).mapToDouble(DMGC_Y_D_ZYZ::getWsyoul).sum();
        monthlyEnergy.setOilProductionSum(Calculation.getDivisionResult(oilProductionSum, 10000));

        if (monthlyEnergy.getId() == null || compute) {
            //月泵水量
            Double pumpingWater = dmgcSDZsbrsjList.stream().filter(c -> zszIds.contains(c.getSszkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getBsl() != null).mapToDouble(DMGC_S_D_ZSBRSJ::getBsl).sum();
            monthlyEnergy.setPumpingWater(Calculation.getDivisionResult(pumpingWater,10000));
        }
        //月泵水量累计
        Double pumpingWaterSum = dmgcSDZsbrsjList.stream().filter(c -> zszIds.contains(c.getSszkEventId())
                && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                && c.getBsl() != null).mapToDouble(DMGC_S_D_ZSBRSJ::getBsl).sum();
        monthlyEnergy.setPumpingWaterSum(Calculation.getDivisionResult(pumpingWaterSum,10000));

        if (monthlyEnergy.getId() == null || compute) {
            //月水处理量
            Double waterTreatment = dmgcSDSclzrsjList.stream().filter(c -> sclzIds.contains(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getRclsl() != null).mapToDouble(DMGC_S_D_SCLZRSJ::getRclsl).sum();
            monthlyEnergy.setWaterTreatment(Calculation.getDivisionResult(waterTreatment,10000));
        }
        //月水处理量累计
        Double waterTreatmentSum = dmgcSDSclzrsjList.stream().filter(c -> sclzIds.contains(c.getZkEventId())
                && c.getRclsl() != null).mapToDouble(DMGC_S_D_SCLZRSJ::getRclsl).sum();
        monthlyEnergy.setWaterTreatmentSum(Calculation.getDivisionResult(waterTreatmentSum,10000));

        long count;
        if (monthlyEnergy.getId() == null || compute) {
            //集输系统-吨液耗电-月均单耗
            Double oilPowerUnit = dmgcYDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getDyhd() != null).mapToDouble(DMGC_Y_D_ZYZ::getDyhd).sum();
            count = dmgcYDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getDyhd() != null).count();
            monthlyEnergy.setOilPowerUnit(Calculation.getDivisionResult(oilPowerUnit, count));
        }
        //集输系统-吨液耗电-月均单耗累计平均
        Double oilPowerUnitSumAve = dmgcYDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())
                && c.getDyhd() != null).mapToDouble(DMGC_Y_D_ZYZ::getDyhd).sum();
        count = dmgcYDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())
                && c.getDyhd() != null).count();
        monthlyEnergy.setOilPowerUnitSumAve(Calculation.getDivisionResult(oilPowerUnitSumAve, count));

        if (monthlyEnergy.getId() == null || compute) {
            //集输系统-吨液耗气-月均单耗
            Double oilGasUnit = dmgcYDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getDyhq() != null).mapToDouble(DMGC_Y_D_ZYZ::getDyhq).sum();
            count = dmgcYDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getDyhq() != null).count();
            monthlyEnergy.setOilGasUnit(Calculation.getDivisionResult(oilGasUnit, count));
        }
        //集输系统-吨液耗气-月均单耗累计平均
        Double oilGasUnitSumAve = dmgcYDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())
                && c.getDyhq() != null).mapToDouble(DMGC_Y_D_ZYZ::getDyhq).sum();
        count = dmgcYDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())
                && c.getDyhq() != null).count();
        monthlyEnergy.setOilGasUnitSumAve(Calculation.getDivisionResult(oilGasUnitSumAve, count));

        // 吨油耗电=集输系统总耗电/产油量
        Double petroleumPowerUnit = Calculation.getDivisionResult(energyConsumption,monthlyEnergy.getOilProduction());
        monthlyEnergy.setPetroleumPowerUnit(petroleumPowerUnit);
        Double petroleumPowerUnitAve = Calculation.getDivisionResult(energyConsumptionSum,monthlyEnergy.getOilProductionSum());
        monthlyEnergy.setPetroleumPowerUnitSumAve(petroleumPowerUnitAve);

        //吨油耗气=耗气量/产油量
        Double petroleumGasUnit = Calculation.getDivisionResult(monthlyEnergy.getConsumeGas(),monthlyEnergy.getOilProduction());
        monthlyEnergy.setPetroleumGasUnit(petroleumGasUnit);
        Double petroleumGasUnitSumAve = Calculation.getDivisionResult(monthlyEnergy.getConsumeGasSum(),monthlyEnergy.getOilProductionSum());
        monthlyEnergy.setPetroleumGasUnitSumAve(petroleumGasUnitSumAve);

        if (monthlyEnergy.getId() == null || compute) {
            //注水系统-泵水单耗-月均单耗
            Double waterInjectPumpingUnit = dmgcSDZsbrsjList.stream().filter(c -> zszIds.contains(c.getSszkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getBsdh() != null).mapToDouble(DMGC_S_D_ZSBRSJ::getBsdh).sum();
            count = dmgcSDZsbrsjList.stream().filter(c -> zszIds.contains(c.getSszkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getBsdh() != null).count();
            monthlyEnergy.setWaterInjectPumpingUnit(Calculation.getDivisionResult(waterInjectPumpingUnit, count));
        }
        //注水系统-泵水单耗-月均单耗累计平均
        Double waterInjectPumpingUnitSumAve = dmgcSDZsbrsjList.stream().filter(c -> zszIds.contains(c.getSszkEventId())
                && c.getBsdh() != null).mapToDouble(DMGC_S_D_ZSBRSJ::getBsdh).sum();
        count = dmgcSDZsbrsjList.stream().filter(c -> zszIds.contains(c.getSszkEventId())
                && c.getBsdh() != null).count();
        monthlyEnergy.setWaterInjectPumpingUnitSumAve(Calculation.getDivisionResult(waterInjectPumpingUnitSumAve, count));
    }

    public ChartOption getStatistics(Integer year, Integer month) {
        ChartOption result = new ChartOption();
        String[] lengnds = new String[]{"总耗电量", "耗气量"};
        result.setLegend(Arrays.asList(lengnds));
        result.setXAxis(Arrays.asList(Calculation.zyqNames));
        List<MonthlyEnergy> monthlyEnergyList = queryMonthlyEnergyList(year, month);
        for (String legend : result.getLegend()) {
            ChartOption.Serie mySerie = result.new Serie();
            mySerie.setName(legend);
            mySerie.setType("bar");
            for (String zyq : result.getXAxis()) {
                Double tempReult = (double) 0;
                switch (legend) {
                    case "总耗电量":
                        Double oilStationPower = monthlyEnergyList.stream().filter(c -> c.getAreaName().equals(zyq)).findFirst().get().getOilStationPower();
                        tempReult = Calculation.getPlusResult(tempReult, oilStationPower == null ? 0 : oilStationPower);
                        Double oilHeatingPower = monthlyEnergyList.stream().filter(c -> c.getAreaName().equals(zyq)).findFirst().get().getOilHeatingPower();
                        tempReult = Calculation.getPlusResult(tempReult, oilHeatingPower == null ? 0 : oilHeatingPower);
                        Double oilGasPower = monthlyEnergyList.stream().filter(c -> c.getAreaName().equals(zyq)).findFirst().get().getOilGasPower();
                        tempReult = Calculation.getPlusResult(tempReult, oilGasPower == null ? 0 : oilGasPower);
                        Double waterInjectPower = monthlyEnergyList.stream().filter(c -> c.getAreaName().equals(zyq)).findFirst().get().getWaterInjectPower();
                        tempReult = Calculation.getPlusResult(tempReult, waterInjectPower == null ? 0 : waterInjectPower);
                        Double waterTreatmentPower = monthlyEnergyList.stream().filter(c -> c.getAreaName().equals(zyq)).findFirst().get().getWaterTreatmentPower();
                        tempReult = Calculation.getPlusResult(tempReult, waterTreatmentPower == null ? 0 : waterTreatmentPower);
                        Double waterSupplyPower = monthlyEnergyList.stream().filter(c -> c.getAreaName().equals(zyq)).findFirst().get().getWaterSupplyPower();
                        tempReult = Calculation.getPlusResult(tempReult, waterSupplyPower == null ? 0 : waterSupplyPower);
                        break;
                    case "耗气量":
                        Double consumeGas = monthlyEnergyList.stream().filter(c -> c.getAreaName().equals(zyq)).findFirst().get().getConsumeGas();
                        tempReult = Calculation.getPlusResult(tempReult, consumeGas == null ? 0 : consumeGas);
                        break;
                    case "吨液耗电":
                        Double oilPowerUnit = monthlyEnergyList.stream().filter(c -> c.getAreaName().equals(zyq)).findFirst().get().getOilPowerUnit();
                        tempReult = Calculation.getPlusResult(tempReult, oilPowerUnit == null ? 0 : oilPowerUnit);
                        break;
                    case "吨液耗气":
                        Double oilGasUnit = monthlyEnergyList.stream().filter(c -> c.getAreaName().equals(zyq)).findFirst().get().getOilGasUnit();
                        tempReult = Calculation.getPlusResult(tempReult, oilGasUnit == null ? 0 : oilGasUnit);
                        break;
                    case "泵水单耗":
                        Double waterInjectPumpingUnit = monthlyEnergyList.stream().filter(c -> c.getAreaName().equals(zyq)).findFirst().get().getWaterInjectPumpingUnit();
                        tempReult = Calculation.getPlusResult(tempReult, waterInjectPumpingUnit == null ? 0 : waterInjectPumpingUnit);
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
        String[] lengnds = new String[]{"吨液耗电", "吨液耗气", "泵水单耗"};
        result.setLegend(Arrays.asList(lengnds));
        result.setXAxis(Arrays.asList(Calculation.zyqNames));
        List<MonthlyEnergy> monthlyEnergyList = queryMonthlyEnergyList(year, month);
        for (String legend : result.getLegend()) {
            ChartOption.Serie mySerie = result.new Serie();
            mySerie.setName(legend);
            mySerie.setType("bar");
            for (String zyq : result.getXAxis()) {
                Double tempReult = (double) 0;
                switch (legend) {
                    case "吨液耗电":
                        Double oilPowerUnit = monthlyEnergyList.stream().filter(c -> c.getAreaName().equals(zyq)).findFirst().get().getOilPowerUnit();
                        tempReult = Calculation.getPlusResult(tempReult, oilPowerUnit == null ? 0 : oilPowerUnit);
                        break;
                    case "吨液耗气":
                        Double oilGasUnit = monthlyEnergyList.stream().filter(c -> c.getAreaName().equals(zyq)).findFirst().get().getOilGasUnit();
                        tempReult = Calculation.getPlusResult(tempReult, oilGasUnit == null ? 0 : oilGasUnit);
                        break;
                    case "泵水单耗":
                        Double waterInjectPumpingUnit = monthlyEnergyList.stream().filter(c -> c.getAreaName().equals(zyq)).findFirst().get().getWaterInjectPumpingUnit();
                        tempReult = Calculation.getPlusResult(tempReult, waterInjectPumpingUnit == null ? 0 : waterInjectPumpingUnit);
                        break;
                }
                mySerie.getData().add(tempReult);
            }
            result.getSeries().add(mySerie);
        }
        return result;
    }
}
