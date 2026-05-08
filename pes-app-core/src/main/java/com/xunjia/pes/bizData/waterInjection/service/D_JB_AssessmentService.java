package com.xunjia.pes.bizData.waterInjection.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.PieOption;
import com.xunjia.pes.bizData.assessment.entity.Benchmark;
import com.xunjia.pes.bizData.assessment.entity.MonitoringIndicator;
import com.xunjia.pes.bizData.assessment.service.BenchmarkService;
import com.xunjia.pes.bizData.assessment.service.MonitoringIndicatorService;
import com.xunjia.pes.bizData.waterInjection.entity.*;
import com.xunjia.pes.bizData.waterInjection.mapper.D_JB_AssessmentMapper;
import com.xunjia.pes.score.Calculation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class D_JB_AssessmentService {
    @Autowired
    private D_JB_AssessmentMapper mapper;

    @Autowired
    private DMGC_S_JBService dmgc_s_jbService;

    @Autowired
    private DMGC_S_D_ZSBRSJService dmgc_s_d_zsbrsjService;

    @Autowired
    private MonitoringIndicatorService monitoringIndicatorService;

    @Autowired
    private BenchmarkService benchmarkService;

    @Autowired
    private DMGC_S_ZSZService zszService;

    public List<D_JB_Assessment> query(D_JB_Assessment example) {
        save(DateUtils.format(example.getRq(), DateUtils.DATE_PATTERN));
        LambdaQueryWrapper<D_JB_Assessment> wrapper = buildQueryWrapper(example);
        List<D_JB_Assessment> result = mapper.selectList(wrapper);
        return result;
    }

    private void save(String rq) {
        try {
            LambdaQueryWrapper<D_JB_Assessment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(D_JB_Assessment::getRq, DateUtils.parse(rq, DateUtils.DATE_TIME_PATTERN));
            List<D_JB_Assessment> result = mapper.selectList(wrapper);
            if (result.size() == 0) {
                List<MonitoringIndicator> monitoringIndicatorList = monitoringIndicatorService.finByTypeAndItemOneKey("注水泵机组监测项目与指标要求",null);
                List<DMGC_S_D_ZSBRSJ> dmgcSDZsbrsjList = dmgc_s_d_zsbrsjService.getDataOfDay(rq,rq);
                List<String> jbIds = dmgcSDZsbrsjList.stream().map(c -> c.getJbEventId()).distinct().collect(Collectors.toList());
                List<String> zszEventIds = dmgcSDZsbrsjList.stream().map(DMGC_S_D_ZSBRSJ::getSszkEventId).collect(Collectors.toList());
                List<DMGC_S_JB> dmgcSJbList = dmgc_s_jbService.getByEventIds(jbIds);
                List<DMGC_S_ZSZ> zszDataList = zszService.getByEventIds(zszEventIds);
                List<Benchmark> benchmarkList = benchmarkService.getBenchmarksByType("注水泵泵效");

                for (DMGC_S_D_ZSBRSJ param : dmgcSDZsbrsjList) {
                    D_JB_Assessment dJbAssessment = new D_JB_Assessment();
                    dJbAssessment.setJbId(param.getJbEventId());
                    Optional<DMGC_S_JB> jbOptional = dmgcSJbList.stream().filter(c -> c.getEventId().equals(param.getJbEventId())).findFirst();
                    jbOptional.ifPresent(c -> dJbAssessment.setJbName(c.getMc()));
                    dJbAssessment.setZszId(param.getSszkEventId());
                    Optional<DMGC_S_ZSZ> zszOptional = zszDataList.stream().filter(c -> c.getEventId().equals(param.getSszkEventId())).findFirst();
                    zszOptional.ifPresent(c -> dJbAssessment.setZszName(c.getMc()));
                    dJbAssessment.setRq(DateUtils.parse(rq, DateUtils.DATE_PATTERN));
                    jbOptional.ifPresent(c -> dJbAssessment.setJbType(c.getBlx()));
                    dJbAssessment.setBxl(param.getBsl());
                    dJbAssessment.setYdl(param.getRydl());
                    dJbAssessment.setBsdh(param.getBsdh());
                    dJbAssessment.setYxzt(param.getYxzt());
                    dJbAssessment.setLl(param.getLl());
                    jbOptional.ifPresent(c -> {
                        double pre = c.getEdll();
                        double behing = dJbAssessment.getLl();
                        dJbAssessment.setJlssl(Calculation.getDivisionResult((behing - pre), behing));
                    });
                    dJbAssessment.setBx(param.getBx());
                    if (dJbAssessment.getJbType().equals("2")) {
                        jbOptional.ifPresent(c -> {
                            MonitoringIndicator monitoringIndicator = filterByEdll(c.getEdll(),
                                    monitoringIndicatorList.stream().filter(d->d.getMonitoringItemOne().contains("离心泵")).collect(Collectors.toList()));
                            if(monitoringIndicator != null) {
                                setRunningState(dJbAssessment, monitoringIndicator);
                                setThrottlingLoss(dJbAssessment,monitoringIndicator);
                            }
                        });
                    }
                    if (dJbAssessment.getJbType().equals("3")) {
                        List<MonitoringIndicator> monitoringIndicators = monitoringIndicatorList.stream().filter(c->c.getMonitoringItemOne().contains("往复泵")).collect(Collectors.toList());
                        if(monitoringIndicators.size()>0){
                            setRunningState(dJbAssessment,monitoringIndicators.get(0));
                        }
                    }
                    dJbAssessment.setDybz("");
                    mapper.insert(dJbAssessment);
                }
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
    }

    private MonitoringIndicator filterByEdll(double edll,List<MonitoringIndicator> monitoringIndicatorList){
        for(MonitoringIndicator param:monitoringIndicatorList){
            if(param.getRatedDischargeMin() == null && param.getRatedDischargeMax() != null && edll < param.getRatedDischargeMax()){
                return param;
            }
            if(param.getRatedDischargeMin() != null && param.getRatedDischargeMax() != null && edll >= param.getRatedDischargeMin() && edll < param.getRatedDischargeMax())
            {
                return param;
            }
            if(param.getRatedDischargeMax() == null && param.getRatedDischargeMin() != null && edll >= param.getRatedDischargeMin()){
                return param;
            }
        }
        return null;
    }

    private void setRunningState(D_JB_Assessment param, MonitoringIndicator monitoringIndicator) {
        if (param.getBx() < monitoringIndicator.getMonitoringItemOneLimit()) {
            param.setRunningState("低效区");
            param.setBxpj(monitoringIndicator.getItemOneMinEvaluation());
            param.setDyfz(Calculation.getAssessmentResult(param.getBx(),monitoringIndicator.getMonitoringItemOneLimit(),0,60,0));
        }
        if (param.getBx() >= monitoringIndicator.getMonitoringItemOneLimit() && param.getBx() < monitoringIndicator.getMonitoringItemOneEnergy()) {
            param.setRunningState("合理区");
            param.setBxpj(monitoringIndicator.getItemOneMidEvaluation());
            param.setDyfz(Calculation.getAssessmentResult(param.getBx(),monitoringIndicator.getMonitoringItemOneEnergy(),monitoringIndicator.getMonitoringItemOneLimit(),80,60));
        }
        if (param.getBx() >= monitoringIndicator.getMonitoringItemOneEnergy()) {
            param.setRunningState("高效区");
            param.setBxpj(monitoringIndicator.getItemOneMaxEvaluation());
            param.setDyfz(Calculation.getAssessmentResult(param.getBx(),100,monitoringIndicator.getMonitoringItemOneEnergy(),100,80));
        }
    }

    private void setThrottlingLoss(D_JB_Assessment param, MonitoringIndicator monitoringIndicator) {
        if(param.getJlssl() <= monitoringIndicator.getMonitoringItemThreeLimit()){
            param.setSspj(monitoringIndicator.getItemThreeMaxEvaluation());
            param.setThrottlingLoss("节流损失正常");
            param.setSslfz(Calculation.getAssessmentResult(param.getJlssl(),monitoringIndicator.getMonitoringItemThreeLimit(),0,60,100));
        }else {
            param.setSspj(monitoringIndicator.getItemThreeMinEvaluation());
            param.setThrottlingLoss("节流损失偏大");
            param.setSslfz(Calculation.getAssessmentResult(param.getJlssl(),100,monitoringIndicator.getMonitoringItemThreeLimit(),0,60));
        }

    }

    private LambdaQueryWrapper<D_JB_Assessment> buildQueryWrapper(D_JB_Assessment example) {
        LambdaQueryWrapper<D_JB_Assessment> queryWrapper = new LambdaQueryWrapper<>();
        if (example != null) {
            if (StringUtils.isNotEmpty(example.getJbId())) {
                queryWrapper.eq(D_JB_Assessment::getJbId, example.getJbId());
            }
            if (StringUtils.isNotEmpty(example.getZszId())) {
                queryWrapper.eq(D_JB_Assessment::getZszId, example.getZszId());
            }
            if (example.getRq() != null) {
                queryWrapper.eq(D_JB_Assessment::getRunningState, example.getRq());
            }
        }
        queryWrapper.orderByAsc(D_JB_Assessment::getZszName).orderByAsc(D_JB_Assessment::getJbName);
        return queryWrapper;
    }


    public PieOption getStatisticsOfRunningState(String rq, String zszId) {
        PieOption result = new PieOption();
        try {
            D_JB_Assessment example = new D_JB_Assessment();
            example.setRq(DateUtils.parse(rq, DateUtils.DATE_PATTERN));
            example.setZszId(zszId);
            List<D_JB_Assessment> tempResult = query(example);

            result.setTitle("泵效运行状态");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("低效区");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "低效区".equals(c.getRunningState())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("高效区");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "高效区".equals(c.getRunningState())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("合理区");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合理区".equals(c.getRunningState())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    public PieOption getStatisticsOfThrottlingLoss(String rq, String zszId) {
        PieOption result = new PieOption();
        try {
            D_JB_Assessment example = new D_JB_Assessment();
            example.setRq(DateUtils.parse(rq, DateUtils.DATE_PATTERN));
            example.setZszId(zszId);
            List<D_JB_Assessment> tempResult = query(example);

            result.setTitle("泵效运行节流状态");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("节流损失偏大");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "节流损失偏大".equals(c.getRunningState())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("节流损失正常");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "节流损失正常".equals(c.getRunningState())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }
}
