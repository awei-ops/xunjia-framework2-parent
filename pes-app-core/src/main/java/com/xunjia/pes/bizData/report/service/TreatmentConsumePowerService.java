package com.xunjia.pes.bizData.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.pes.bizData.BaseEntity;
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.report.entity.TreatmentConsumePower;
import com.xunjia.pes.bizData.report.mapper.TreatmentConsumePowerMapper;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_D_SBYX;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_D_SCLZRSJ;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_SCLJB;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_SCLZ;
import com.xunjia.pes.bizData.waterTreatment.service.DMGC_S_D_SBYXService;
import com.xunjia.pes.bizData.waterTreatment.service.DMGC_S_D_SCLZRSJService;
import com.xunjia.pes.bizData.waterTreatment.service.DMGC_S_SCLJBService;
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
public class TreatmentConsumePowerService extends ServiceImpl<TreatmentConsumePowerMapper, TreatmentConsumePower> {
    @Autowired
    private DMGC_S_D_SCLZRSJService dmgcSDSclzrsjService;

    @Autowired
    private DMGC_S_D_SBYXService dmgcSDSbyxService;

    @Autowired
    private DMGC_S_SCLZService sclzService;

    @Autowired
    private DMGC_S_SCLJBService jbService;

    public ResponseData<Boolean> add(TreatmentConsumePower param) {
        ResponseData<Boolean> resp;
        try {
            this.save(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception ex) {
            resp = ResponseData.getError(ex);
        }
        return resp;
    }

    public ResponseData<Boolean> update(TreatmentConsumePower param) {
        ResponseData<Boolean> resp;
        try {
            this.updateById(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception ex) {
            resp = ResponseData.getError(ex);
        }
        return resp;
    }

    public List<TreatmentConsumePower> buildReportSchema(Integer year, Integer month, Boolean compute) {
        List<TreatmentConsumePower> treatmentConsumePowersForSum = queryTreatmentConsumePowerListSum(year, month);
        List<TreatmentConsumePower> result = queryTreatmentConsumePowerList(year, month);
        TreatmentConsumePower waterConsumePower;
        try {
            Date yearStart = DateUtils.parse(year + "-1-1", DateUtils.DATE_PATTERN);
            Date monthStart = DateUtils.parse(year + "-" + month + "-1", DateUtils.DATE_PATTERN);
            Date monthEnd = DateUtils.parse(DateUtils.getLastDay(monthStart), DateUtils.DATE_PATTERN);

            List<DMGC_S_SCLZ> sclzList = sclzService.getAll();
            List<DMGC_S_SCLJB> jbList = jbService.getPageData(new DMGC_S_SCLJB(), 1, 999).getRows();

            List<String> jbIds = jbList.stream().map(BaseEntity::getEventId).collect(Collectors.toList());

            List<DMGC_S_D_SCLZRSJ> dmgcSDSclzrsjs = dmgcSDSclzrsjService.getEffectiveData(yearStart, monthEnd);
            List<DMGC_S_D_SBYX> dmgcSDSbyxList = dmgcSDSbyxService.getEffectiveData(jbIds, yearStart, monthEnd);

            if (ListUtils.isListEmpty(result)) {
                for (String zyqName : Arrays.stream(Calculation.zyqNames).filter(c -> !c.equals("生产维修大队")).collect(Collectors.toList())) {
                    List<DMGC_S_SCLZ> tempSclzs = sclzList.stream().filter(c -> c.getZyqName() != null && c.getZyqName().equals(zyqName)).collect(Collectors.toList());
                    for (DMGC_S_SCLZ sclz : tempSclzs) {
                        waterConsumePower = new TreatmentConsumePower();
                        waterConsumePower.setAreaName(zyqName);
                        waterConsumePower.setStationName(sclz.getMc());
                        waterConsumePower.setYear(year);
                        waterConsumePower.setMonth(month);
                        computeSingleTreatmentConsumePower(waterConsumePower, sclz, dmgcSDSclzrsjs, dmgcSDSbyxList, treatmentConsumePowersForSum, monthStart, monthEnd, jbList, compute);
                        result.add(waterConsumePower);
                        add(waterConsumePower);
                    }
                }
            } else {
                for (String zyqName : Arrays.stream(Calculation.zyqNames).filter(c -> !c.equals("生产维修大队")).collect(Collectors.toList())) {
                    List<DMGC_S_SCLZ> tempSclzs = sclzList.stream().filter(c -> c.getZyqName() != null && c.getZyqName().equals(zyqName)).collect(Collectors.toList());
                    for (DMGC_S_SCLZ sclz : tempSclzs) {
                        waterConsumePower = result.stream().filter(c -> c.getAreaName().equals(zyqName) && c.getStationName().equals(sclz.getMc())).findFirst().get();
                        computeSingleTreatmentConsumePower(waterConsumePower, sclz, dmgcSDSclzrsjs, dmgcSDSbyxList, treatmentConsumePowersForSum, monthStart, monthEnd, jbList, compute);
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

    private List<TreatmentConsumePower> queryTreatmentConsumePowerList(Integer year, Integer month) {
        LambdaQueryWrapper<TreatmentConsumePower> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TreatmentConsumePower::getYear, year);
        wrapper.eq(TreatmentConsumePower::getMonth, month);
        return this.list(wrapper);
    }

    private List<TreatmentConsumePower> queryTreatmentConsumePowerListSum(Integer year, Integer month) {
        LambdaQueryWrapper<TreatmentConsumePower> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TreatmentConsumePower::getYear, year);
        wrapper.ge(TreatmentConsumePower::getMonth, 1);
        wrapper.le(TreatmentConsumePower::getMonth, month);
        return this.list(wrapper);
    }

    public void computeSingleTreatmentConsumePower(TreatmentConsumePower treatmentConsumePower,
                                                   DMGC_S_SCLZ sclz,
                                                   List<DMGC_S_D_SCLZRSJ> dmgcSDSclzrsjs,
                                                   List<DMGC_S_D_SBYX> dmgcSDSbyxList,
                                                   List<TreatmentConsumePower> treatmentConsumePowersForSum,
                                                   Date computeStart,
                                                   Date computeEnd,
                                                   List<DMGC_S_SCLJB> jbList,
                                                   Boolean compute) {
        if (treatmentConsumePower.getId() == null || compute) {
            //水处理系统-月耗电量
            Double treatmentPower = dmgcSDSclzrsjs.stream().filter(c -> sclz.getEventId().equals(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getRhdl() != null).mapToDouble(DMGC_S_D_SCLZRSJ::getRhdl).sum();
            treatmentConsumePower.setTreatmentPower(Calculation.getDivisionResult(treatmentPower, 10000));
        }
        //水处理系统-月耗电量累计
        Double treatmentPowerSum = dmgcSDSclzrsjs.stream().filter(c -> sclz.getEventId().equals(c.getZkEventId())
                && c.getRhdl() != null).mapToDouble(DMGC_S_D_SCLZRSJ::getRhdl).sum();
        treatmentConsumePower.setTreatmentPowerSum(Calculation.getDivisionResult(treatmentPowerSum, 10000));

        if (treatmentConsumePower.getId() == null || compute) {
            //月泵水量
            Double pumpingWater = dmgcSDSclzrsjs.stream().filter(c -> sclz.getEventId().equals(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getRwssl() != null).mapToDouble(DMGC_S_D_SCLZRSJ::getRwssl).sum();
            treatmentConsumePower.setPumpingWater(Calculation.getDivisionResult(pumpingWater, 10000));
        }
        //月泵水量累计
        Double pumpingWaterSum = dmgcSDSclzrsjs.stream().filter(c -> sclz.getEventId().equals(c.getZkEventId())
                && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                && c.getRwssl() != null).mapToDouble(DMGC_S_D_SCLZRSJ::getRwssl).sum();
        treatmentConsumePower.setPumpingWaterSum(Calculation.getDivisionResult(pumpingWaterSum, 10000));

        long count;
        if (treatmentConsumePower.getId() == null || compute) {
            //水处理系统-泵水单耗-月均单耗
            Double treatmentPumpingUnit = dmgcSDSclzrsjs.stream().filter(c -> sclz.getEventId().equals(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getDh() != null).mapToDouble(DMGC_S_D_SCLZRSJ::getDh).sum();
            count = dmgcSDSclzrsjs.stream().filter(c -> sclz.getEventId().equals(c.getZkEventId())
                    && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()
                    && c.getDh() != null).count();
            treatmentConsumePower.setTreatmentPumpingUnit(Calculation.getDivisionResult(treatmentPumpingUnit, count));
        }
        //水处理系统-泵水单耗-月均单耗累计平均
        Double treatmentPumpingUnitSumAve = dmgcSDSclzrsjs.stream().filter(c -> sclz.getEventId().equals(c.getZkEventId())
                && c.getDh() != null).mapToDouble(DMGC_S_D_SCLZRSJ::getDh).sum();
        count = dmgcSDSclzrsjs.stream().filter(c -> sclz.getEventId().equals(c.getZkEventId())
                && c.getDh() != null).count();
        treatmentConsumePower.setTreatmentPumpingUnitSumAve(Calculation.getDivisionResult(treatmentPumpingUnitSumAve, count));
        //水处理系统-泵台数
        Integer pumpCount = Integer.parseInt(Long.toString(jbList.stream().filter(c -> c.getSszkid().equals(sclz.getEventId())).count()));
        treatmentConsumePower.setPumpCount(pumpCount);
        List<String> jbIds = jbList.stream().filter(c -> c.getSszkid().equals(sclz.getEventId())).map(BaseEntity::getEventId).collect(Collectors.toList());
        if (treatmentConsumePower.getId() == null || compute) {
            //水处理系统-当月运行台数
            Integer pumpRunCount = Integer.parseInt(Long.toString(dmgcSDSbyxList.stream().filter(c -> jbIds.contains(c.getSbid()) && c.getRq().getTime() >= computeStart.getTime() && c.getRq().getTime() <= computeEnd.getTime()).map(DMGC_S_D_SBYX::getSbid).distinct().count()));
            treatmentConsumePower.setPumpRunCount(pumpRunCount);
        }
        //水处理系统-累计平均运行台数
        Double pumpRunCountSumAve = Double.parseDouble(Long.toString(dmgcSDSbyxList.stream().filter(c -> jbIds.contains(c.getSbid())).map(DMGC_S_D_SBYX::getSbid).distinct().count()));
        treatmentConsumePower.setPumpRunCountSumAve(Calculation.getDivisionResult(pumpRunCountSumAve, DateUtils.getMonth(computeStart)));

        //水处理系统-月平均泵效（没有，依靠录入）
        //水处理系统-月累计平均泵效
        Double pumpEfficiencySumAve = treatmentConsumePowersForSum.stream().filter(c -> c.getAreaName().equals(treatmentConsumePower.getAreaName()) && c.getStationName().equals(treatmentConsumePower.getStationName()) && c.getPumpEfficiencyAve() != null).mapToDouble(TreatmentConsumePower::getPumpEfficiencyAve).sum();
        treatmentConsumePower.setPumpEfficiencySumAve(Calculation.getDivisionResult(pumpEfficiencySumAve, DateUtils.getMonth(computeStart)));
    }

    public ChartOption getStatistics(Integer year, Integer month) {
        ChartOption result = new ChartOption();
        String[] lengnds = new String[]{"耗电量"};
        result.setLegend(Arrays.asList(lengnds));
        result.setXAxis(Arrays.stream(Calculation.zyqNames).filter(c -> !c.equals("生产维修大队")).collect(Collectors.toList()));
        List<TreatmentConsumePower> treatmentConsumePowers = queryTreatmentConsumePowerList(year, month);
        for (String legend : result.getLegend()) {
            ChartOption.Serie mySerie = result.new Serie();
            mySerie.setName(legend);
            mySerie.setType("bar");
            for (String zyq : result.getXAxis()) {
                Double tempReult = (double) 0;
                switch (legend) {
                    case "耗电量":
                        Double treatmentPower = treatmentConsumePowers.stream().filter(c -> c.getAreaName().equals(zyq) && c.getTreatmentPower() != null).mapToDouble(TreatmentConsumePower::getTreatmentPower).sum();
                        tempReult = Calculation.getPlusResult(tempReult, treatmentPower);
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
        result.setXAxis(Arrays.stream(Calculation.zyqNames).filter(c -> !c.equals("生产维修大队")).collect(Collectors.toList()));
        List<TreatmentConsumePower> treatmentConsumePowers = queryTreatmentConsumePowerList(year, month);
        for (String legend : result.getLegend()) {
            ChartOption.Serie mySerie = result.new Serie();
            mySerie.setName(legend);
            mySerie.setType("bar");
            for (String zyq : result.getXAxis()) {
                Double tempReult = (double) 0;
                switch (legend) {
                    case "泵水单耗":
                        Double treatmentPumpingUnit = treatmentConsumePowers.stream().filter(c -> c.getAreaName().equals(zyq) && c.getTreatmentPumpingUnit() != null).mapToDouble(TreatmentConsumePower::getTreatmentPumpingUnit).sum();
                        long count = treatmentConsumePowers.stream().filter(c -> c.getAreaName().equals(zyq) && c.getTreatmentPumpingUnit() != null).count();
                        tempReult = Calculation.getPlusResult(tempReult, Calculation.getDivisionResult(treatmentPumpingUnit, count));
                        break;
                }
                mySerie.getData().add(tempReult);
            }
            result.getSeries().add(mySerie);
        }
        return result;
    }
}
