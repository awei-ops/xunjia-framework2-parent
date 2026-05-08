package com.xunjia.pes.bizData.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.pes.bizData.BaseEntity;
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.oil.entity.*;
import com.xunjia.pes.bizData.oil.service.*;
import com.xunjia.pes.bizData.report.entity.MonthlyEnergy;
import com.xunjia.pes.bizData.report.entity.OilConsumePower;
import com.xunjia.pes.bizData.report.mapper.OilConsumePowerMapper;
import com.xunjia.pes.score.Calculation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class OilConsumePowerService extends ServiceImpl<OilConsumePowerMapper, OilConsumePower> {

    @Autowired
    private DMGC_Y_D_ZYZService dmgcYDZyzService;

    @Autowired
    private DMGC_Y_JBService dmgcYJbService;

    @Autowired
    private DMGC_Y_D_CSBService dmgcYDCsbService;

    @Autowired
    private DMGC_Y_D_SYBService dmgcYDSybService;

    @Autowired
    private DMGC_Y_D_JRLService dmgcYDJrlService;

    @Autowired
    private DMGC_JRLService dmgcJrlService;

    @Autowired
    private DMGC_Y_ZYZService zyzService;

    public ResponseData<Boolean> add(OilConsumePower param) {
        ResponseData<Boolean> resp;
        try {
            this.save(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception ex) {
            resp = ResponseData.getError(ex);
        }
        return resp;
    }

    public ResponseData<Boolean> update(OilConsumePower param) {
        ResponseData<Boolean> resp;
        try {
            this.updateById(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception ex) {
            resp = ResponseData.getError(ex);
        }
        return resp;
    }

    public List<OilConsumePower> buildReportSchema(Integer year, Integer month, Boolean compute) {
        List<OilConsumePower> oilConsumePowersForSum = queryOilConsumePowerListSum(year,month);
        List<OilConsumePower> result = queryOilConsumePowerList(year, month);
        OilConsumePower oilConsumePower;
        try {
            Date yearStart = DateUtils.parse(year + "-1-1", DateUtils.DATE_PATTERN);
            Date monthStart = DateUtils.parse(year + "-" + month + "-1", DateUtils.DATE_PATTERN);
            Date monthEnd = DateUtils.parse(DateUtils.getLastDay(monthStart), DateUtils.DATE_PATTERN);

            List<DMGC_Y_D_ZYZ> dmgcYDZyzList = dmgcYDZyzService.getEffectiveData(yearStart, monthEnd);
            List<DMGC_Y_D_CSB> dmgcYDCsbList = dmgcYDCsbService.getEffectiveData(yearStart, monthEnd);
            List<DMGC_Y_D_SYB> dmgcYDSybList = dmgcYDSybService.getEffectiveData(yearStart, monthEnd);
            List<DMGC_Y_D_JRL> dmgcYDJrlList = dmgcYDJrlService.getEffectiveData(yearStart, monthEnd);

            List<DMGC_Y_ZYZ> zyzList = zyzService.getAll();
            List<DMGC_Y_JB> jbList = new ArrayList<>();
            jbList.addAll(dmgcYJbService.getByName("掺水泵"));
            jbList.addAll(dmgcYJbService.getByName("外输泵"));
            List<DMGC_JRL> jrlList = dmgcJrlService.getAll();

            if (ListUtils.isListEmpty(result)) {
                for (String zyqName : Calculation.zyqNames) {
                    List<DMGC_Y_ZYZ> tempZyzs = zyzList.stream().filter(c -> c.getZyqName() != null && c.getZyqName().equals(zyqName)).collect(Collectors.toList());
                    for (DMGC_Y_ZYZ zyz : tempZyzs) {
                        oilConsumePower = new OilConsumePower();
                        oilConsumePower.setAreaName(zyqName);
                        oilConsumePower.setStationName(zyz.getMc());
                        oilConsumePower.setYear(year);
                        oilConsumePower.setMonth(month);
                        computeSingleOilConsumePower(oilConsumePower, zyz, dmgcYDZyzList, dmgcYDCsbList, dmgcYDSybList, monthStart, monthEnd, jbList, oilConsumePowersForSum, jrlList, dmgcYDJrlList, compute);
                        result.add(oilConsumePower);
                        add(oilConsumePower);
                    }
                }
            } else {
                for (String zyqName : Calculation.zyqNames) {
                    List<DMGC_Y_ZYZ> tempZyzs = zyzList.stream().filter(c -> c.getZyqName() != null && c.getZyqName().equals(zyqName)).collect(Collectors.toList());
                    for (DMGC_Y_ZYZ zyz : tempZyzs) {
                        oilConsumePower = result.stream().filter(c -> c.getAreaName().equals(zyqName) && c.getStationName().equals(zyz.getMc())).findFirst().get();
                        computeSingleOilConsumePower(oilConsumePower, zyz, dmgcYDZyzList, dmgcYDCsbList, dmgcYDSybList, monthStart, monthEnd, jbList, oilConsumePowersForSum, jrlList, dmgcYDJrlList, compute);
                        if (compute) {
                            update(oilConsumePower);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private List<OilConsumePower> queryOilConsumePowerList(Integer year, Integer month) {
        LambdaQueryWrapper<OilConsumePower> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OilConsumePower::getYear, year);
        wrapper.eq(OilConsumePower::getMonth, month);
        return this.list(wrapper);
    }

    private List<OilConsumePower> queryOilConsumePowerListSum(Integer year,Integer month) {
        LambdaQueryWrapper<OilConsumePower> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OilConsumePower::getYear, year);
        wrapper.ge(OilConsumePower::getMonth, 1);
        wrapper.le(OilConsumePower::getMonth,month);
        return this.list(wrapper);
    }

    private void computeSingleOilConsumePower(OilConsumePower oilConsumePower, DMGC_Y_ZYZ zyz, List<DMGC_Y_D_ZYZ> dmgcYDZyzList, List<DMGC_Y_D_CSB> dmgcYDCsbList, List<DMGC_Y_D_SYB> dmgcYDSybList, Date computeStart, Date computeEnd, List<DMGC_Y_JB> jbList, List<OilConsumePower> oilConsumePowersForSum, List<DMGC_JRL> jrlList, List<DMGC_Y_D_JRL> dmgcYDJrlList, Boolean compute) {
        if (oilConsumePower.getId() == null || compute) {
            //集输系统-站场-月耗电量
            Double oilStationPower = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime() && c.getZhhdl() != null).mapToDouble(DMGC_Y_D_ZYZ::getZhhdl).sum();
            oilConsumePower.setOilStationPower(Calculation.getDivisionResult(oilStationPower, 10000));
        }
        //集输系统-站场-月耗电量累计
        Double oilStationPowerSum = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId()) && c.getZhhdl() != null).mapToDouble(DMGC_Y_D_ZYZ::getZhhdl).sum();
        oilConsumePower.setOilStationPowerSum(Calculation.getDivisionResult(oilStationPowerSum, 10000));
        //集输系统-电加热-月耗电量（没有，依靠录入）
        //集输系统-电加热-月耗电量累计（累加电加热各月的录入数据）
        Double oilHeatingPowerSum = oilConsumePowersForSum.stream().filter(c -> c.getAreaName().equals(oilConsumePower.getAreaName()) && c.getStationName().equals(oilConsumePower.getStationName()) && c.getOilHeatingPower() != null).mapToDouble(OilConsumePower::getOilHeatingPower).sum();
        oilConsumePower.setOilHeatingPowerSum(oilHeatingPowerSum);
        //集输系统-气系统-月耗电量（没有，依靠录入）
        //集输系统-气系统-月耗电量累计（累加气系统各月的录入数据）
        Double oilGasPowerSum = oilConsumePowersForSum.stream().filter(c -> c.getAreaName().equals(oilConsumePower.getAreaName()) && c.getStationName().equals(oilConsumePower.getStationName()) && c.getOilGasPower() != null).mapToDouble(OilConsumePower::getOilGasPower).sum();
        oilConsumePower.setOilGasPowerSum(oilGasPowerSum);
        //集输系统-月耗电量合计
        Double powerMonthSum = Calculation.getPlusResult(oilConsumePower.getOilStationPower() == null ? 0 : oilConsumePower.getOilStationPower(),
                Calculation.getPlusResult(oilConsumePower.getOilHeatingPower() == null ? 0 : oilConsumePower.getOilHeatingPower(),
                        oilConsumePower.getOilGasPower() == null ? 0 : oilConsumePower.getOilGasPower()));
        oilConsumePower.setPowerMonthSum(powerMonthSum);
        //集输系统-月耗电量累计
        Double powerSum = Calculation.getPlusResult(oilConsumePower.getOilStationPowerSum(), Calculation.getPlusResult(oilConsumePower.getOilHeatingPowerSum(), oilConsumePower.getOilGasPowerSum()));
        oilConsumePower.setPowerSum(powerSum);
        if (oilConsumePower.getId() == null || compute) {
            //月产液量
            Double liquidProduction = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime() && c.getCll() != null).mapToDouble(DMGC_Y_D_ZYZ::getCll).sum();
            oilConsumePower.setLiquidProduction(Calculation.getDivisionResult(liquidProduction, 10000));
        }
        //月产液量累计
        Double liquidProductionSum = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId()) && c.getCll() != null).mapToDouble(DMGC_Y_D_ZYZ::getCll).sum();
        oilConsumePower.setLiquidProductionSum(Calculation.getDivisionResult(liquidProductionSum, 10000));
        long count;
        if (oilConsumePower.getId() == null || compute) {
            //集输系统-吨液耗电-月均单耗
            Double oilPowerUnit = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime() && c.getDyhd() != null).mapToDouble(DMGC_Y_D_ZYZ::getDyhd).sum();
            count = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime() && c.getDyhd() != null).count();
            oilConsumePower.setOilPowerUnit(Calculation.getDivisionResult(oilPowerUnit, count));
        }
        //集输系统-吨液耗电-月均单耗累计平均
        Double oilPowerUnitSumAve = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId()) && c.getDyhd() != null).mapToDouble(DMGC_Y_D_ZYZ::getDyhd).sum();
        count = dmgcYDZyzList.stream().filter(c -> zyz.getEventId().equals(c.getZkEventId()) && c.getDyhd() != null).count();
        oilConsumePower.setOilPowerUnitSumAve(Calculation.getDivisionResult(oilPowerUnitSumAve, count));

        //集输系统-掺水泵台数
        Integer pumpCsbCount = Integer.parseInt(Long.toString(jbList.stream().filter(c -> c.getMc().equals("掺水泵") && c.getSszkEventId().equals(zyz.getEventId())).count()));
        oilConsumePower.setPumpCsbCount(pumpCsbCount);
        if (oilConsumePower.getId() == null || compute) {
            //集输系统-掺水泵当月运行台数
            Integer pumpCsbRunCount = Integer.parseInt(Long.toString(dmgcYDCsbList.stream().filter(c -> zyz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()).map(DMGC_Y_D_CSB::getJbEventId).distinct().count()));
            oilConsumePower.setPumpCsbRunCount(pumpCsbRunCount);
        }
        //集输系统-掺水泵累计平均运行台数
        Double pumpCsbRunCountSumAve = Double.parseDouble(Long.toString(dmgcYDCsbList.stream().filter(c -> zyz.getEventId().equals(c.getSszkEventId())).map(DMGC_Y_D_CSB::getJbEventId).distinct().count()));
        oilConsumePower.setPumpCsbRunCountSumAve(Calculation.getDivisionResult(pumpCsbRunCountSumAve, DateUtils.getMonth(computeStart)));

        if (oilConsumePower.getId() == null || compute) {
            //集输系统-掺水泵月平均泵效
            Double pumpCsbEfficiencyAve = dmgcYDCsbList.stream().filter(c -> zyz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime() && c.getBxl() != null).mapToDouble(DMGC_Y_D_CSB::getBxl).sum();
            count = dmgcYDCsbList.stream().filter(c -> zyz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime() && c.getBxl() != null).mapToDouble(DMGC_Y_D_CSB::getBxl).count();
            oilConsumePower.setPumpCsbEfficiencyAve(Calculation.getDivisionResult(pumpCsbEfficiencyAve, count));
        }
        //集输系统-掺水泵月累计平均泵效
        Double pumpCsbEfficiencySumAve = dmgcYDCsbList.stream().filter(c -> zyz.getEventId().equals(c.getSszkEventId()) && c.getBxl() != null).mapToDouble(DMGC_Y_D_CSB::getBxl).sum();
        count = dmgcYDCsbList.stream().filter(c -> zyz.getEventId().equals(c.getSszkEventId()) && c.getBxl() != null).count();
        oilConsumePower.setPumpCsbEfficiencySumAve(Calculation.getDivisionResult(pumpCsbEfficiencySumAve, count));

        //集输系统-外输泵台数
        Integer pumpWsbCount = Integer.parseInt(Long.toString(jbList.stream().filter(c -> c.getMc().equals("外输泵") && c.getSszkEventId().equals(zyz.getEventId())).count()));
        oilConsumePower.setPumpWsbCount(pumpWsbCount);
        if (oilConsumePower.getId() == null || compute) {
            //集输系统-外输泵当月运行台数
            Integer pumpWsbRunCount = Integer.parseInt(Long.toString(dmgcYDSybList.stream().filter(c -> zyz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()).map(DMGC_Y_D_SYB::getJbEventId).distinct().count()));
            oilConsumePower.setPumpWsbRunCount(pumpWsbRunCount);
        }
        //集输系统-外输泵累计平均运行台数
        Double pumpWsbRunCountSumAve = Double.parseDouble(Long.toString(dmgcYDSybList.stream().filter(c -> zyz.getEventId().equals(c.getSszkEventId())).map(DMGC_Y_D_SYB::getJbEventId).distinct().count()));
        oilConsumePower.setPumpWsbRunCountSumAve(Calculation.getDivisionResult(pumpWsbRunCountSumAve, DateUtils.getMonth(computeStart)));
        if (oilConsumePower.getId() == null || compute) {
            //集输系统-外输泵月平均泵效
            Double pumpWsbEfficiencyAve = dmgcYDSybList.stream().filter(c -> zyz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime() && c.getSybxl() != null).mapToDouble(DMGC_Y_D_SYB::getSybxl).sum();
            count = dmgcYDSybList.stream().filter(c -> zyz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime() && c.getSybxl() != null).mapToDouble(DMGC_Y_D_SYB::getSybxl).count();
            oilConsumePower.setPumpWsbEfficiencyAve(Calculation.getDivisionResult(pumpWsbEfficiencyAve, count));
        }
        //集输系统-外输泵月累计平均泵效
        Double pumpWsbEfficiencySumAve = dmgcYDSybList.stream().filter(c -> zyz.getEventId().equals(c.getSszkEventId()) && c.getSybxl() != null).mapToDouble(DMGC_Y_D_SYB::getSybxl).sum();
        count = dmgcYDSybList.stream().filter(c -> zyz.getEventId().equals(c.getSszkEventId()) && c.getSybxl() != null).count();
        oilConsumePower.setPumpWsbEfficiencySumAve(Calculation.getDivisionResult(pumpWsbEfficiencySumAve, count));

        //集输系统-掺水炉台数
        List<DMGC_JRL> cslList = jrlList.stream().filter(c -> c.getMc().equals("掺水炉") && c.getSszkEventId().equals(zyz.getEventId())).collect(Collectors.toList());
        List<String> cslIds = cslList.stream().map(BaseEntity::getEventId).collect(Collectors.toList());
        Integer furnaceCslCount = cslList.size();
        oilConsumePower.setFurnaceCslCount(furnaceCslCount);
        if (oilConsumePower.getId() == null || compute) {
            //集输系统-掺水炉当月运行台数
            Integer furnaceCslRunCount = Integer.parseInt(Long.toString(dmgcYDJrlList.stream().filter(c -> cslIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()).map(DMGC_Y_D_JRL::getJrlId).distinct().count()));
            oilConsumePower.setFurnaceCslRunCount(furnaceCslRunCount);
        }
        //集输系统-掺水炉累计平均运行台数
        Double furnaceCslRunCountSumAve = Double.parseDouble(Long.toString(dmgcYDJrlList.stream().filter(c -> cslIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId())).map(DMGC_Y_D_JRL::getJrlId).distinct().count()));
        oilConsumePower.setFurnaceCslRunCountSumAve(Calculation.getDivisionResult(furnaceCslRunCountSumAve, DateUtils.getMonth(computeStart)));
        if (oilConsumePower.getId() == null || compute) {
            //集输系统-掺水炉月平均炉效
            Double furnaceCslEfficiencyAve = dmgcYDJrlList.stream().filter(c -> cslIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime() && c.getLx() != null).mapToDouble(DMGC_Y_D_JRL::getLx).sum();
            count = dmgcYDJrlList.stream().filter(c -> cslIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime() && c.getLx() != null).count();
            oilConsumePower.setFurnaceCslEfficiencyAve(Calculation.getDivisionResult(furnaceCslEfficiencyAve, count));
        }
        //集输系统-掺水炉月累计平均炉效
        Double furnaceCslEfficiencySumAve = dmgcYDJrlList.stream().filter(c -> cslIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId()) && c.getLx() != null).mapToDouble(DMGC_Y_D_JRL::getLx).sum();
        count = dmgcYDJrlList.stream().filter(c -> cslIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId()) && c.getLx() != null).count();
        oilConsumePower.setFurnaceCslEfficiencySumAve(Calculation.getDivisionResult(furnaceCslEfficiencySumAve, count));

        //集输系统-外输炉台数
        List<DMGC_JRL> wslList = jrlList.stream().filter(c -> c.getMc().equals("外输炉") && c.getSszkEventId().equals(zyz.getEventId())).collect(Collectors.toList());
        List<String> wslIds = wslList.stream().map(BaseEntity::getEventId).collect(Collectors.toList());
        Integer furnaceWslCount = wslList.size();
        oilConsumePower.setFurnaceWslCount(furnaceWslCount);
        if (oilConsumePower.getId() == null || compute) {
            //集输系统-外输炉当月运行台数
            Integer furnaceWslRunCount = Integer.parseInt(Long.toString(dmgcYDJrlList.stream().filter(c -> wslIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()).map(DMGC_Y_D_JRL::getJrlId).distinct().count()));
            oilConsumePower.setFurnaceWslRunCount(furnaceWslRunCount);
        }
        //集输系统-外输炉累计平均运行台数
        Double furnaceWslRunCountSumAve = Double.parseDouble(Long.toString(dmgcYDJrlList.stream().filter(c -> wslIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId())).map(DMGC_Y_D_JRL::getJrlId).distinct().count()));
        oilConsumePower.setFurnaceWslRunCountSumAve(Calculation.getDivisionResult(furnaceWslRunCountSumAve, DateUtils.getMonth(computeStart)));
        if (oilConsumePower.getId() == null || compute) {
            //集输系统-外输炉月平均炉效
            Double furnaceWslEfficiencyAve = dmgcYDJrlList.stream().filter(c -> wslIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime() && c.getLx() != null).mapToDouble(DMGC_Y_D_JRL::getLx).sum();
            count = dmgcYDJrlList.stream().filter(c -> wslIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime() && c.getLx() != null).count();
            oilConsumePower.setFurnaceWslEfficiencyAve(Calculation.getDivisionResult(furnaceWslEfficiencyAve, count));
        }
        //集输系统-外输炉月累计平均炉效
        Double furnaceWslEfficiencySumAve = dmgcYDJrlList.stream().filter(c -> wslIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId()) && c.getLx() != null).mapToDouble(DMGC_Y_D_JRL::getLx).sum();
        count = dmgcYDJrlList.stream().filter(c -> wslIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId()) && c.getLx() != null).count();
        oilConsumePower.setFurnaceWslEfficiencySumAve(Calculation.getDivisionResult(furnaceWslEfficiencySumAve, count));

        //集输系统-采暖炉台数
        List<DMGC_JRL> cnlList = jrlList.stream().filter(c -> c.getMc().equals("采暖炉") && c.getSszkEventId().equals(zyz.getEventId())).collect(Collectors.toList());
        List<String> cnlIds = cnlList.stream().map(BaseEntity::getEventId).collect(Collectors.toList());
        Integer furnaceCnlCount = cnlList.size();
        oilConsumePower.setFurnaceCnlCount(furnaceCnlCount);
        if (oilConsumePower.getId() == null || compute) {
            //集输系统-采暖炉当月运行台数
            Integer furnaceCnlRunCount = Integer.parseInt(Long.toString(dmgcYDJrlList.stream().filter(c -> cnlIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()).map(DMGC_Y_D_JRL::getJrlId).distinct().count()));
            oilConsumePower.setFurnaceCnlRunCount(furnaceCnlRunCount);
        }
        //集输系统-采暖炉累计平均运行台数
        Double furnaceCnlRunCountSumAve = Double.parseDouble(Long.toString(dmgcYDJrlList.stream().filter(c -> cnlIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId())).map(DMGC_Y_D_JRL::getJrlId).distinct().count()));
        oilConsumePower.setFurnaceCnlRunCountSumAve(Calculation.getDivisionResult(furnaceCnlRunCountSumAve, DateUtils.getMonth(computeStart)));
        if (oilConsumePower.getId() == null || compute) {
            //集输系统-采暖炉月平均炉效
            Double furnaceCnlEfficiencyAve = dmgcYDJrlList.stream().filter(c -> cnlIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime() && c.getLx() != null).mapToDouble(DMGC_Y_D_JRL::getLx).sum();
            count = dmgcYDJrlList.stream().filter(c -> cnlIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime() && c.getLx() != null).count();
            oilConsumePower.setFurnaceCnlEfficiencyAve(Calculation.getDivisionResult(furnaceCnlEfficiencyAve, count));
        }
        //集输系统-采暖炉月累计平均炉效
        Double furnaceCnlEfficiencySumAve = dmgcYDJrlList.stream().filter(c -> cnlIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId()) && c.getLx() != null).mapToDouble(DMGC_Y_D_JRL::getLx).sum();
        count = dmgcYDJrlList.stream().filter(c -> cnlIds.contains(c.getJrlId()) && zyz.getEventId().equals(c.getSszkEventId()) && c.getLx() != null).count();
        oilConsumePower.setFurnaceCnlEfficiencySumAve(Calculation.getDivisionResult(furnaceCnlEfficiencySumAve, count));
    }

    public ChartOption getStatistics(Integer year, Integer month) {
        ChartOption result = new ChartOption();
        String[] lengnds = new String[]{"耗电量"};
        result.setLegend(Arrays.asList(lengnds));
        result.setXAxis(Arrays.stream(Calculation.zyqNames).filter(c -> !c.equals("生产维修大队")).collect(Collectors.toList()));
        List<OilConsumePower> oilConsumePowerList = queryOilConsumePowerList(year, month);
        for (String legend : result.getLegend()) {
            ChartOption.Serie mySerie = result.new Serie();
            mySerie.setName(legend);
            mySerie.setType("bar");
            for (String zyq : result.getXAxis()) {
                Double tempReult = (double) 0;
                switch (legend) {
                    case "耗电量":
                        Double oilStationPower = oilConsumePowerList.stream().filter(c -> c.getAreaName().equals(zyq) && c.getOilStationPower() != null).mapToDouble(OilConsumePower::getOilStationPower).sum();
                        tempReult = Calculation.getPlusResult(tempReult, oilStationPower);
                        Double oilHeatingPower = oilConsumePowerList.stream().filter(c -> c.getAreaName().equals(zyq) && c.getOilHeatingPower() != null).mapToDouble(OilConsumePower::getOilHeatingPower).sum();
                        tempReult = Calculation.getPlusResult(tempReult, oilHeatingPower);
                        Double oilGasPower = oilConsumePowerList.stream().filter(c -> c.getAreaName().equals(zyq) && c.getOilGasPower() != null).mapToDouble(OilConsumePower::getOilGasPower).sum();
                        tempReult = Calculation.getPlusResult(tempReult, oilGasPower);
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
        String[] lengnds = new String[]{"吨液耗电"};
        result.setLegend(Arrays.asList(lengnds));
        result.setXAxis(Arrays.stream(Calculation.zyqNames).filter(c -> !c.equals("生产维修大队")).collect(Collectors.toList()));
        List<OilConsumePower> oilConsumePowerList = queryOilConsumePowerList(year, month);
        for (String legend : result.getLegend()) {
            ChartOption.Serie mySerie = result.new Serie();
            mySerie.setName(legend);
            mySerie.setType("bar");
            for (String zyq : result.getXAxis()) {
                Double tempReult = (double) 0;
                switch (legend) {
                    case "吨液耗电":
                        Double oilPowerUnit = oilConsumePowerList.stream().filter(c -> c.getAreaName().equals(zyq) && c.getOilPowerUnit() != null).mapToDouble(OilConsumePower::getOilPowerUnit).sum();
                        long count = oilConsumePowerList.stream().filter(c -> c.getAreaName().equals(zyq) && c.getOilPowerUnit() != null).count();
                        tempReult = Calculation.getPlusResult(tempReult, Calculation.getDivisionResult(oilPowerUnit, count));
                        break;
                }
                mySerie.getData().add(tempReult);
            }
            result.getSeries().add(mySerie);
        }
        return result;
    }
}
