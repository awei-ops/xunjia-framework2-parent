package com.xunjia.pes.bizData.oil.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.framework.utils.excel.ExportUtils;
import com.xunjia.pes.basicDataManage.entity.Indicators;
import com.xunjia.pes.basicDataManage.service.IndicatorsService;
import com.xunjia.pes.bizData.ChartOption;
import com.xunjia.pes.bizData.PieOption;
import com.xunjia.pes.bizData.assessment.entity.MonitoringIndicatorNew;
import com.xunjia.pes.bizData.assessment.service.BenchmarkService;
import com.xunjia.pes.bizData.assessment.service.MonitoringIndicatorNewService;
import com.xunjia.pes.bizData.oil.entity.DMGC_JRL;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_JRL;
import com.xunjia.pes.bizData.oil.mapper.DMGC_Y_D_JRLMapper;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSBRSJ;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSZRSJ;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_ZSZ;
import com.xunjia.pes.score.Calculation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DMGC_Y_D_JRLService extends ServiceImpl<DMGC_Y_D_JRLMapper, DMGC_Y_D_JRL> {

    @Autowired
    private DMGC_Y_D_JRLMapper mapper;

    @Autowired
    private DMGC_JRLService jrlService;

    @Autowired
    private MonitoringIndicatorNewService monitoringIndicatorNewService;

    @Autowired
    private BenchmarkService benchmarkService;

    @Autowired
    private IndicatorsService indicatorsService;

    public Boolean saveLtbmwd(String id, String jrlyxzk, Double lx, Integer yqwd, Double yqhyl, Integer jrl, Double ltwbmwd) {
        try {
            DMGC_Y_D_JRL dmgcYDJrl = mapper.selectById(id);
            if (Integer.parseInt(jrlyxzk) == 1) {
                jrlyxzk = "01";
            }
            dmgcYDJrl.setJrlyxzk(jrlyxzk);
            dmgcYDJrl.setLx(lx);
            dmgcYDJrl.setYqwd(yqwd);
            dmgcYDJrl.setYqhyl(yqhyl);
            dmgcYDJrl.setJrl(jrl);
            dmgcYDJrl.setLtwbmwd(ltwbmwd);
            mapper.updateById(dmgcYDJrl);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Boolean auditData(String rq) {
        try {
            LambdaQueryWrapper<DMGC_Y_D_JRL> wrapper = new LambdaQueryWrapper<>();
            Date startDate = DateUtils.parse(rq, DateUtils.DATE_PATTERN);
            wrapper.ge(DMGC_Y_D_JRL::getRq, startDate);
            Date endDate = DateUtils.parse(rq + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
            wrapper.le(DMGC_Y_D_JRL::getRq, endDate);
            wrapper.eq(DMGC_Y_D_JRL::getJrlyxzk, "01");
            wrapper.isNotNull(DMGC_Y_D_JRL::getLx);
            wrapper.isNotNull(DMGC_Y_D_JRL::getYqwd);
            wrapper.isNotNull(DMGC_Y_D_JRL::getYqhyl);
            wrapper.isNotNull(DMGC_Y_D_JRL::getJrl);
            wrapper.isNotNull(DMGC_Y_D_JRL::getLtwbmwd);
            wrapper.ne(DMGC_Y_D_JRL::getLx, 0);
            wrapper.ne(DMGC_Y_D_JRL::getYqwd, 0);
            wrapper.ne(DMGC_Y_D_JRL::getYqhyl, 0);
            wrapper.ne(DMGC_Y_D_JRL::getJrl, 0);
            wrapper.ne(DMGC_Y_D_JRL::getLtwbmwd, 0);
            List<DMGC_Y_D_JRL> dmgcYDJrlList = mapper.selectList(wrapper);
//            for (DMGC_Y_D_JRL dmgcYDJrl : dmgcYDJrlList) {
//                if (dmgcYDJrl.getLtwbmwd() != null && (dmgcYDJrl.getDataAlreadyAudited() == null || dmgcYDJrl.getDataAlreadyAudited().equals(false))) {
//                    dmgcYDJrl.setDataAlreadyAudited(true);
//                }
//            }
            if (dmgcYDJrlList.size() != 0) {
                getCompleteData(dmgcYDJrlList);
                this.updateBatchById(dmgcYDJrlList);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Boolean getIfSomeDataNotInput(String rq) {
        try {
            LambdaQueryWrapper<DMGC_Y_D_JRL> wrapper = new LambdaQueryWrapper<>();
            Date queryDate = DateUtils.parse(rq, DateUtils.DATE_PATTERN);
            wrapper.eq(DMGC_Y_D_JRL::getRq, queryDate);
            wrapper.eq(DMGC_Y_D_JRL::getJrlyxzk, "01");
            wrapper.and(item -> item.isNull(DMGC_Y_D_JRL::getLx)
                    .or().isNull(DMGC_Y_D_JRL::getYqwd)
                    .or().isNull(DMGC_Y_D_JRL::getYqhyl)
                    .or().isNull(DMGC_Y_D_JRL::getJrl)
                    .or().isNull(DMGC_Y_D_JRL::getLtwbmwd)
                    .or().eq(DMGC_Y_D_JRL::getLx, 0)
                    .or().eq(DMGC_Y_D_JRL::getYqwd, 0)
                    .or().eq(DMGC_Y_D_JRL::getYqhyl, 0)
                    .or().eq(DMGC_Y_D_JRL::getJrl, 0)
                    .or().eq(DMGC_Y_D_JRL::getLtwbmwd, 0));
            return (long) mapper.selectList(wrapper).size() > 0;
        } catch (Exception ex) {
            return false;
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
            Date updateDate = DateUtils.parse(nowDateString, DateUtils.DATE_PATTERN);
            LambdaQueryWrapper<DMGC_Y_D_JRL> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DMGC_Y_D_JRL::getRq, updateDate);
            List<DMGC_Y_D_JRL> dataList = mapper.selectList(wrapper);
            if (dataList.size() != 0) {
                getCompleteData(dataList);
                this.updateBatchById(dataList);
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
            result = false;
        }
        return result;
    }

    public PageVO<DMGC_Y_D_JRL> getPageData(DMGC_Y_D_JRL example, String startDate, String endDate, int page, int size) {
        PageVO<DMGC_Y_D_JRL> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_Y_D_JRL> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            PageInfo<DMGC_Y_D_JRL> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e) {
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    public void exportData(DMGC_Y_D_JRL example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response) {

        Workbook workbook = ExportUtils.createWorkbook();
        try {
            List<DMGC_Y_D_JRL> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            Map<String, List<String>> headerNamesAndFieldsMap = ExportUtils.getHeaderNamesAndFields(DMGC_Y_D_JRL.class);
            List<String> headerNames = headerNamesAndFieldsMap.get("headerNames");
            List<String> fields = headerNamesAndFieldsMap.get("fields");
            ExportUtils.exportExcel("加热炉运行动态日数据", headerNames, fields, 0, workbook, dataList);
            ExportUtils.responseWorkbook("加热炉运行动态日数据", workbook, request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exportExamineData(String queryDate,
                           HttpServletRequest request, HttpServletResponse response) {

        Workbook workbook = ExportUtils.createWorkbook();
        try {
            List<DMGC_Y_D_JRL> dataList = getAssessmentNoPage("日",queryDate).stream().filter(c->c.getJxScore() != null).collect(Collectors.toList());
            Map<String, List<String>> headerNamesAndFieldsMap = ExportUtils.getExamineHeaderNamesAndFields(DMGC_Y_D_JRL.class);
            List<String> headerNames = headerNamesAndFieldsMap.get("headerNames");
            List<String> fields = headerNamesAndFieldsMap.get("fields");
            ExportUtils.exportExcel("集输系统加热炉日考核", headerNames, fields, 0, workbook, dataList);
            ExportUtils.responseWorkbook("集输系统加热炉日考核", workbook, request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private LambdaQueryWrapper<DMGC_Y_D_JRL> buildQueryWrapper(DMGC_Y_D_JRL example, String startDate, String endDate) {
        LambdaQueryWrapper<DMGC_Y_D_JRL> queryWrapper = new LambdaQueryWrapper<>();
        if (example != null) {
            if (!StringUtils.isEmpty(example.getMc())) {
                queryWrapper.like(DMGC_Y_D_JRL::getMc, "%" + example.getMc() + "%");
            }
            if (!StringUtils.isEmpty(example.getSszkName())) {
                queryWrapper.like(DMGC_Y_D_JRL::getSszkName, example.getSszkName());
            }
            if(!StringUtils.isEmpty(example.getZnbh())){
                queryWrapper.like(DMGC_Y_D_JRL::getZnbh,example.getZnbh());
            }
        }
        if (!StringUtils.isEmpty(startDate)) {
            try {
                Date date = DateUtils.parse(startDate, DateUtils.DATE_PATTERN);
                queryWrapper.ge(DMGC_Y_D_JRL::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!StringUtils.isEmpty(endDate)) {
            try {
                Date date = DateUtils.parse(endDate + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                queryWrapper.le(DMGC_Y_D_JRL::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        queryWrapper.orderByDesc(DMGC_Y_D_JRL::getRq);
        return queryWrapper;
    }

    private MonitoringIndicatorNew filterByQueryData(double queryData, List<MonitoringIndicatorNew> monitoringIndicatorNewList, String monitoringItem) {
        List<MonitoringIndicatorNew> temp = monitoringIndicatorNewList.stream().filter(c -> c.getMonitoringItem().equals(monitoringItem)).collect(Collectors.toList());
        if (temp.size() == 1) {
            return temp.get(0);
        }
        for (MonitoringIndicatorNew param : temp) {
            if (param.getValueMin() == null && param.getValueMax() != null && queryData <= param.getValueMax()) {
                return param;
            }
            if (param.getValueMin() != null && param.getValueMax() != null && queryData > param.getValueMin() && queryData <= param.getValueMax()) {
                return param;
            }
            if (param.getValueMax() == null && param.getValueMin() != null && queryData > param.getValueMin()) {
                return param;
            }
        }
        return null;
    }

    private List<DMGC_Y_D_JRL> getDataOfDay(String startDate, String endDate) {
        List<DMGC_Y_D_JRL> dataList = mapper.selectList(this.buildQueryWrapper(null, startDate, endDate));
        return dataList;
    }

    private List<DMGC_Y_D_JRL> getDataOfMonth(String startDate, String endDate) {
        List<DMGC_Y_D_JRL> dataList = new ArrayList<>();
        String queryStart = startDate;
        String queryEnd = endDate;
        try {
            queryStart = DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getMonth(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-01";
            queryEnd = DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getMonth(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getDay(DateUtils.parse(DateUtils.getLastDay(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)), DateUtils.DATE_PATTERN));
            LambdaQueryWrapper<DMGC_Y_D_JRL> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DMGC_Y_D_JRL::getJrlyxzk, "01");
            queryWrapper.ge(DMGC_Y_D_JRL::getRq, queryStart);
            queryWrapper.le(DMGC_Y_D_JRL::getRq, queryEnd);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getLx);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getYqwd);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getYqhyl);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getJrl);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getLtwbmwd);
            queryWrapper.ne(DMGC_Y_D_JRL::getLx, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getYqwd, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getYqhyl, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getJrl, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getLtwbmwd, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getJxScore, 0);
            dataList = mapper.selectList(queryWrapper);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return dataList;
    }

    private List<DMGC_Y_D_JRL> getDataOfYear(String startDate, String endDate) {
        List<DMGC_Y_D_JRL> dataList = new ArrayList<>();
        String queryStart = startDate;
        String queryEnd = endDate;
        try {
            queryStart = DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-01-01";
            queryEnd = DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-12-31";
            LambdaQueryWrapper<DMGC_Y_D_JRL> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DMGC_Y_D_JRL::getJrlyxzk, "01");
            queryWrapper.ge(DMGC_Y_D_JRL::getRq, queryStart);
            queryWrapper.le(DMGC_Y_D_JRL::getRq, queryEnd);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getLx);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getYqwd);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getYqhyl);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getJrl);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getLtwbmwd);
            queryWrapper.ne(DMGC_Y_D_JRL::getLx, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getYqwd, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getYqhyl, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getJrl, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getLtwbmwd, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getJxScore, 0);
            dataList = mapper.selectList(queryWrapper);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return dataList;
    }

    private void getCompleteData(List<DMGC_Y_D_JRL> partData) {
        if (partData.size() == 0) {
            return;
        }
        try {
            List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("燃气加热炉监测项目与指标要求", null);
            List<String> jrlIds = partData.stream().map(c -> c.getJrlId()).distinct().collect(Collectors.toList());
            List<DMGC_JRL> jrlList = jrlService.getByIds(jrlIds);
            //转油放水站权重
            List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("zyfsz")).collect(Collectors.toList());
            for (DMGC_Y_D_JRL param : partData) {
                Optional<DMGC_JRL> optional = jrlList.stream().filter(c -> c.getEventId().equals(param.getJrlId())).findFirst();
                optional.ifPresent(c -> {
                    param.setZnbh(c.getZnbh());
                    MonitoringIndicatorNew monitoringIndicatorNew = filterByQueryData(c.getEdrfh(), monitoringIndicatorNewList, "热效率");
                    param.setWeightLx(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level5") && d.getItemCode().equals("jrlxl")).collect(Collectors.toList()).get(0).getWeight());
                    if (monitoringIndicatorNew != null && param.getLx() != null) {
                        param.setLxScore(Calculation.efficiency(param.getLx(), monitoringIndicatorNew));
                        param.setWeightLxScore(Calculation.getMultiplicationResult(param.getLxScore(), param.getWeightLx()));
                        param.setLxpj(Calculation.getEfficiencyComment(param.getLxScore(), monitoringIndicatorNew));
                    }

                    monitoringIndicatorNew = filterByQueryData(c.getEdrfh(), monitoringIndicatorNewList, "排烟温度");
                    param.setWeightYqwd(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level5") && d.getItemCode().equals("pywd")).collect(Collectors.toList()).get(0).getWeight());
                    if (monitoringIndicatorNew != null && param.getYqwd() != null) {
                        param.setYqwdScore(Calculation.calculationOfUnitConsumption(param.getYqwd(), monitoringIndicatorNew));
                        param.setWeightYqwdScore(Calculation.getMultiplicationResult(param.getYqwdScore(), param.getWeightYqwd()));
                        param.setYqwdpj(Calculation.getEfficiencyComment(param.getYqwdScore(), monitoringIndicatorNew));
                    }
                    monitoringIndicatorNew = filterByQueryData(c.getEdrfh(), monitoringIndicatorNewList, "空气系数");
                    param.setWeightKqxs(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level5") && d.getItemCode().equals("kqxs")).collect(Collectors.toList()).get(0).getWeight());
                    if (monitoringIndicatorNew != null && param.getYqhyl() != null) {
                        //空气系数=21/(21-79*烟气含氧量/(100-烟气含氧量))
                        double tempParam1 = Calculation.getMultiplicationResult(79, param.getYqhyl());
                        double tempParams2 = Calculation.getReduceResult(100, param.getYqhyl());
                        double tempParams3 = Calculation.getDivisionResult(tempParam1, tempParams2);
                        double tempParams4 = Calculation.getReduceResult(21, tempParams3);
                        double kqxs = Calculation.getDivisionResult(21, tempParams4);
                        param.setKqxs(kqxs);
                        param.setKqxsScore(Calculation.calculationOfUnitConsumption(kqxs, monitoringIndicatorNew));
                        param.setWeightKqxsScore(Calculation.getMultiplicationResult(param.getKqxsScore(), param.getWeightKqxs()));
                        param.setKqxspj(Calculation.getUnitConsumptionComment(param.getKqxsScore(), monitoringIndicatorNew));
                    }
                    //热负荷=加热量/(额定热负荷*1000)*100
                    monitoringIndicatorNew = filterByQueryData(c.getEdrfh(), monitoringIndicatorNewList, "热负荷");
                    param.setWeightRfh(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level5") && d.getItemCode().equals("rfh")).collect(Collectors.toList()).get(0).getWeight());
                    if (monitoringIndicatorNew != null && param.getJrl() != null) {
                        param.setRfh(Calculation.getMultiplicationResult(Calculation.getDivisionResult(param.getJrl(), Calculation.getMultiplicationResult(c.getEdrfh(), 1000)), 100));
                        param.setRfhScore(Calculation.efficiency(param.getRfh(), monitoringIndicatorNew));
                        param.setWeightRfhScore(Calculation.getMultiplicationResult(param.getRfhScore(), param.getWeightRfh()));
                        param.setRfhpj(Calculation.getEfficiencyComment(param.getRfhScore(), monitoringIndicatorNew));
                    }
                    monitoringIndicatorNew = filterByQueryData(c.getEdrfh(), monitoringIndicatorNewList, "炉体外表面温度");
                    param.setWeightLtwbmwd(indicatorsList.stream().filter(d -> d.getLevelCode().equals("level5") && d.getItemCode().equals("ltbmwd")).collect(Collectors.toList()).get(0).getWeight());
                    if (monitoringIndicatorNew != null && param.getLtwbmwd() != null) {
                        param.setLtwbmwdScore(Calculation.calculationOfUnitConsumption(param.getLtwbmwd(), monitoringIndicatorNew));
                        param.setWeightLtwbmwdScore(Calculation.getMultiplicationResult(param.getLtwbmwdScore(), param.getWeightLtwbmwd()));
                        param.setLtwbmwdpj(Calculation.getUnitConsumptionComment(param.getLtwbmwdScore(), monitoringIndicatorNew));
                    }

                    monitoringIndicatorNew = filterByQueryData(c.getEdrfh(), monitoringIndicatorNewList, "绩效评价");
                    if (monitoringIndicatorNew != null && param.getWeightLxScore() != null && param.getWeightYqwdScore() != null
                            && param.getWeightKqxsScore() != null && param.getWeightRfhScore() != null && param.getWeightLtwbmwdScore() != null) {
                        param.setJxScore(Calculation.getPlusResult((param.getWeightLxScore() + param.getWeightYqwdScore() + param.getWeightKqxsScore() +
                                param.getWeightRfhScore() + param.getWeightLtwbmwdScore()), 0));
                        param.setJxpj(Calculation.getEfficiencyComment(param.getJxScore(), monitoringIndicatorNew));
                    }
                });
            }
        } catch (Exception ex) {
            String error = ex.getMessage();
        }
    }

    public PageVO<DMGC_Y_D_JRL> getAssessment(String cycle, String assessmentDate, int page, int size,DMGC_Y_D_JRL example) {
        PageVO<DMGC_Y_D_JRL> pageVO = null;
        List<DMGC_Y_D_JRL> tempResult = new ArrayList<>();
        try {
            Date sumStart = DateUtils.parse(assessmentDate, DateUtils.DATE_PATTERN);
            Date sumEnd = DateUtils.parse(assessmentDate + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
            switch (cycle) {
                case "日":
                    tempResult = getDataOfDay(assessmentDate, assessmentDate);
                    break;
                case "月":
                    sumStart = DateUtils.parse(DateUtils.getFirstDay(sumStart), DateUtils.DATE_PATTERN);
                    sumEnd = DateUtils.parse(DateUtils.getLastDay(sumStart) + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                    tempResult = getDataOfMonth(assessmentDate, assessmentDate);
                    break;
                case "年":
                    sumStart = DateUtils.parse(DateUtils.getYear(sumStart) + "-01-01", DateUtils.DATE_PATTERN);
                    sumEnd = DateUtils.parse(DateUtils.getYear(sumStart) + "-12-31 23:59:59", DateUtils.DATE_TIME_PATTERN);
                    tempResult = getDataOfYear(assessmentDate, assessmentDate);
                    break;
            }
            if (tempResult.size() == 0) {
                return new PageVO<>();
            }
            List<DMGC_Y_D_JRL> dataList = tempResult;
            if (cycle.equals("月") || cycle.equals("年")) {
                dataList = buildAssessment(tempResult, sumStart, sumEnd);
            }

            if(example != null) {
                if(StringUtils.isNotEmpty(example.getSszkName())) {
                    dataList = dataList.stream().filter(c -> c.getSszkName() != null && c.getSszkName().contains(example.getSszkName())).collect(Collectors.toList());
                }
                if(StringUtils.isNotEmpty(example.getMc())) {
                    dataList = dataList.stream().filter(c -> c.getMc() != null && c.getMc().contains(example.getMc())).collect(Collectors.toList());
                }
                if(StringUtils.isNotEmpty(example.getZnbh())) {
                    dataList = dataList.stream().filter(c -> c.getZnbh() != null && c.getZnbh().contains(example.getZnbh())).collect(Collectors.toList());
                }

                if (StringUtils.isNotEmpty(example.getJxpj())) {
                    if ("不合格".equals(example.getJxpj())) {
                        dataList = dataList.stream().filter(c -> (StringUtils.isEmpty(c.getLxpj()) || "不合格".equals(c.getLxpj()))
                                || (StringUtils.isEmpty(c.getLtwbmwdpj()) || "不合格".equals(c.getLtwbmwdpj()))
                                || (StringUtils.isEmpty(c.getKqxspj()) || "不合格".equals(c.getKqxspj()))
                                || (StringUtils.isEmpty(c.getYqwdpj()) || "不合格".equals(c.getYqwdpj()))
                                || (StringUtils.isEmpty(c.getRfhpj()) || "不合格".equals(c.getRfhpj()))
                                || (StringUtils.isEmpty(c.getJxpj()) || "不合格".equals(c.getJxpj()))).collect(Collectors.toList());
                    } else {
                        dataList = dataList.stream().filter(c -> (StringUtils.isNotEmpty(c.getLxpj()) && !"不合格".equals(c.getLxpj()))
                                && (StringUtils.isNotEmpty(c.getLtwbmwdpj()) && !"不合格".equals(c.getLtwbmwdpj()))
                                && (StringUtils.isNotEmpty(c.getKqxspj()) && !"不合格".equals(c.getKqxspj()))
                                && (StringUtils.isNotEmpty(c.getYqwdpj()) && !"不合格".equals(c.getYqwdpj()))
                                && (StringUtils.isNotEmpty(c.getRfhpj()) && !"不合格".equals(c.getRfhpj()))
                                && (StringUtils.isNotEmpty(c.getJxpj()) && !"不合格".equals(c.getJxpj()))).collect(Collectors.toList());
                    }
                }
            }
            dataList = dataList.stream().sorted(Comparator.comparing(DMGC_Y_D_JRL::getSszkName).thenComparing(DMGC_Y_D_JRL::getMc)).collect(Collectors.toList());

//            PageMethod.startPage(page, size);
            PageInfo<DMGC_Y_D_JRL> pageInfo = PageInfo.of(dataList);
            List<DMGC_Y_D_JRL> pageList = dataList.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
            pageVO = new PageVO<>(pageInfo.getTotal(), pageList);
            return pageVO;
        } catch (Exception ex) {
            String err = ex.getMessage();
            return new PageVO<>();
        }
    }

    public List<DMGC_Y_D_JRL> getAssessmentNoPage(String cycle, String assessmentDate) {
        List<DMGC_Y_D_JRL> result = new ArrayList<>();
        List<DMGC_Y_D_JRL> tempResult = new ArrayList<>();
        try {
            Date sumStart = DateUtils.parse(assessmentDate, DateUtils.DATE_PATTERN);
            Date sumEnd = DateUtils.parse(assessmentDate + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
            switch (cycle) {
                case "日":
                    tempResult = getDataOfDay(assessmentDate, assessmentDate);
                    break;
                case "月":
                    sumStart = DateUtils.parse(DateUtils.getFirstDay(sumStart), DateUtils.DATE_PATTERN);
                    sumEnd = DateUtils.parse(DateUtils.getLastDay(sumStart) + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                    tempResult = getDataOfMonth(assessmentDate, assessmentDate);
                    break;
                case "年":
                    sumStart = DateUtils.parse(DateUtils.getYear(sumStart) + "-01-01", DateUtils.DATE_PATTERN);
                    sumEnd = DateUtils.parse(DateUtils.getYear(sumStart) + "-12-31 23:59:59", DateUtils.DATE_TIME_PATTERN);
                    tempResult = getDataOfYear(assessmentDate, assessmentDate);
                    break;
            }

            List<DMGC_Y_D_JRL> dataList = tempResult;
            if (cycle.equals("月") || cycle.equals("年")) {
                dataList = buildAssessment(tempResult, sumStart, sumEnd);
            }
            dataList = dataList.stream().sorted(Comparator.comparing(DMGC_Y_D_JRL::getSszkName).thenComparing(DMGC_Y_D_JRL::getMc)).collect(Collectors.toList());
            result = dataList;
            return result;
        } catch (Exception ex) {
            String err = ex.getMessage();
            return result;
        }
    }

    private List<DMGC_Y_D_JRL> buildAssessment(List<DMGC_Y_D_JRL> dmgcYDJrls, Date sumStart, Date sumEnd) {
        List<String> jrlIds = dmgcYDJrls.stream().map(c -> c.getJrlId()).distinct().collect(Collectors.toList());
        List<DMGC_Y_D_JRL> dmgcYDJrlList = new ArrayList<>();
        List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("燃气加热炉监测项目与指标要求", null);
        List<DMGC_JRL> jrlList = jrlService.getByIds(jrlIds);
        for (String jrlId : jrlIds) {
            if (dmgcYDJrlList.stream().filter(c -> c.getJrlId().equals(jrlId)).count() != 0) {
                continue;
            }
            Date finalSumStart = sumStart;
            Date finalSumEnd = sumEnd;
            DMGC_Y_D_JRL result = new DMGC_Y_D_JRL();
            result.setJrlId(jrlId);
            DMGC_Y_D_JRL temp = dmgcYDJrls.stream().filter(c -> c.getJrlId().equals(jrlId)).collect(Collectors.toList()).get(0);
            result.setEventId(temp.getEventId());
            result.setZnbh(temp.getZnbh());
            long jrlIdCount = dmgcYDJrls.stream()
                    .filter(c -> c.getJrlId().equals(jrlId) && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime()
                            && (c.getLx() != null || c.getYqwd() != null || c.getYqhyl() != null || c.getJrl() != null || c.getLtwbmwd() != null)).count();
            result.setMc(temp.getMc());
            result.setSszkName(temp.getSszkName());
            result.setSsdwdm(temp.getSsdwdm());
            result.setRq(sumEnd);
            Optional<DMGC_JRL> optional = jrlList.stream().filter(c -> c.getEventId().equals(jrlId)).findFirst();
            optional.ifPresent(c -> {
                MonitoringIndicatorNew monitoringIndicatorNew = filterByQueryData(c.getEdrfh(), monitoringIndicatorNewList, "热效率");
                if (monitoringIndicatorNew != null) {
//                    result.setWeightLx(temp.getWeightLx());
                    result.setLxScore(Calculation.getDivisionResult(dmgcYDJrls.stream().filter(d -> d.getJrlId().equals(jrlId) && d.getLxScore() != null && d.getRq().getTime() >= finalSumStart.getTime() && d.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_JRL::getLxScore).sum(), jrlIdCount));
//                    result.setWeightLxScore(Calculation.getMultiplicationResult(result.getLxScore(), result.getWeightLx()));
                    result.setWeightLxScore(Calculation.getDivisionResult(dmgcYDJrls.stream().filter(d -> d.getJrlId().equals(jrlId) && d.getLxScore() != null && d.getRq().getTime() >= finalSumStart.getTime() && d.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_JRL::getWeightLxScore).sum(), jrlIdCount));
                    result.setLxpj(Calculation.getEfficiencyComment(result.getLxScore(), monitoringIndicatorNew));

                }
                monitoringIndicatorNew = filterByQueryData(c.getEdrfh(), monitoringIndicatorNewList, "排烟温度");
                if (monitoringIndicatorNew != null) {
//                    result.setWeightYqwd(temp.getWeightYqwd());
                    result.setYqwdScore(Calculation.getDivisionResult(dmgcYDJrls.stream().filter(d -> d.getJrlId().equals(jrlId) && d.getYqwdScore() != null && d.getRq().getTime() >= finalSumStart.getTime() && d.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_JRL::getYqwdScore).sum(), jrlIdCount));
//                    result.setWeightYqwdScore(Calculation.getMultiplicationResult(result.getYqwdScore(), result.getWeightYqwd()));
                    result.setWeightYqwdScore(Calculation.getDivisionResult(dmgcYDJrls.stream().filter(d -> d.getJrlId().equals(jrlId) && d.getYqwdScore() != null && d.getRq().getTime() >= finalSumStart.getTime() && d.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_JRL::getWeightYqwdScore).sum(), jrlIdCount));
                    result.setYqwdpj(Calculation.getEfficiencyComment(result.getYqwdScore(), monitoringIndicatorNew));
                }
                monitoringIndicatorNew = filterByQueryData(c.getEdrfh(), monitoringIndicatorNewList, "空气系数");
                if (monitoringIndicatorNew != null) {
//                    result.setWeightKqxs(temp.getWeightKqxs());
                    result.setKqxsScore(Calculation.getDivisionResult(dmgcYDJrls.stream().filter(d -> d.getJrlId().equals(jrlId) && d.getKqxsScore() != null && d.getRq().getTime() >= finalSumStart.getTime() && d.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_JRL::getKqxsScore).sum(), jrlIdCount));
//                    result.setWeightKqxsScore(Calculation.getMultiplicationResult(result.getKqxsScore(), result.getWeightKqxs()));
                    result.setWeightKqxsScore(Calculation.getDivisionResult(dmgcYDJrls.stream().filter(d -> d.getJrlId().equals(jrlId) && d.getKqxsScore() != null && d.getRq().getTime() >= finalSumStart.getTime() && d.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_JRL::getWeightKqxsScore).sum(), jrlIdCount));
                    result.setKqxspj(Calculation.getUnitConsumptionComment(result.getKqxsScore(), monitoringIndicatorNew));
                }
                //热负荷=加热量/(额定热负荷*1000)*100
                monitoringIndicatorNew = filterByQueryData(c.getEdrfh(), monitoringIndicatorNewList, "热负荷");
                if (monitoringIndicatorNew != null) {
//                    result.setWeightRfh(temp.getWeightRfh());
                    result.setRfhScore(Calculation.getDivisionResult(dmgcYDJrls.stream().filter(d -> d.getJrlId().equals(jrlId) && d.getRfhScore() != null && d.getRq().getTime() >= finalSumStart.getTime() && d.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_JRL::getRfhScore).sum(), jrlIdCount));
//                    result.setWeightRfhScore(Calculation.getMultiplicationResult(result.getRfhScore(), result.getWeightRfh()));
                    result.setWeightRfhScore(Calculation.getDivisionResult(dmgcYDJrls.stream().filter(d -> d.getJrlId().equals(jrlId) && d.getRfhScore() != null && d.getRq().getTime() >= finalSumStart.getTime() && d.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_JRL::getWeightRfhScore).sum(), jrlIdCount));
                    result.setRfhpj(Calculation.getEfficiencyComment(result.getRfhScore(), monitoringIndicatorNew));
                }
                monitoringIndicatorNew = filterByQueryData(c.getEdrfh(), monitoringIndicatorNewList, "炉体外表面温度");
                if (monitoringIndicatorNew != null) {
//                    result.setWeightLtwbmwd(temp.getWeightLtwbmwd());
                    result.setLtwbmwdScore(Calculation.getDivisionResult(dmgcYDJrls.stream().filter(d -> d.getJrlId().equals(jrlId) && d.getLtwbmwdScore() != null && d.getRq().getTime() >= finalSumStart.getTime() && d.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_JRL::getLtwbmwdScore).sum(), jrlIdCount));
//                    result.setWeightLtwbmwdScore(Calculation.getMultiplicationResult(result.getLtwbmwdScore(), result.getWeightLtwbmwd()));
                    result.setWeightLtwbmwdScore(Calculation.getDivisionResult(dmgcYDJrls.stream().filter(d -> d.getJrlId().equals(jrlId) && d.getLtwbmwdScore() != null && d.getRq().getTime() >= finalSumStart.getTime() && d.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_Y_D_JRL::getWeightLtwbmwdScore).sum(), jrlIdCount));
                    result.setLtwbmwdpj(Calculation.getUnitConsumptionComment(result.getLtwbmwdScore(), monitoringIndicatorNew));
                }

                monitoringIndicatorNew = filterByQueryData(c.getEdrfh(), monitoringIndicatorNewList, "绩效评价");
                if (monitoringIndicatorNew != null) {
                    result.setJxScore(Calculation.getPlusResult((result.getWeightLxScore() + result.getWeightYqwdScore() + result.getWeightKqxsScore() +
                            result.getWeightRfhScore() + result.getWeightLtwbmwdScore()), 0));
                    result.setJxpj(Calculation.getEfficiencyComment(result.getJxScore(), monitoringIndicatorNew));
                }
            });

            dmgcYDJrlList.add(result);
        }
        return dmgcYDJrlList;
    }

    public List<PieOption> getAllStaticsOfPipe(String rq) {
        List<DMGC_Y_D_JRL> tempResult = getDataOfDay(rq, rq).stream().filter(c->"01".equals(c.getJrlyxzk())).collect(Collectors.toList());
        List<PieOption> result = new ArrayList<>();
        result.add(getStaticOfRunStatic(tempResult));
        result.add(getStaticOfJxpj(tempResult));
        result.add(getStaticOfLxpj(tempResult));
        result.add(getStaticOfPywd(tempResult));
        result.add(getStaticOfKqxs(tempResult));
        result.add(getStaticOfLtwbmwd(tempResult));
        result.add(getStaticOfRfh(tempResult));
        return result;
    }

    private PieOption getStaticOfRunStatic(List<DMGC_Y_D_JRL> tempResult) {
        PieOption result = new PieOption();
        try {
            long unqualified = tempResult.stream()
                    .filter(c -> "不合格".equals(c.getLxpj()) || StringUtils.isEmpty(c.getLxpj())
                            || "不合格".equals(c.getYqwdpj()) || StringUtils.isEmpty(c.getYqwdpj())
                            || "不合格".equals(c.getKqxspj()) || StringUtils.isEmpty(c.getKqxspj())
                            || "不合格".equals(c.getRfhpj()) || StringUtils.isEmpty(c.getRfhpj())
                            || "不合格".equals(c.getLtwbmwdpj()) || StringUtils.isEmpty(c.getLtwbmwdpj())
                            || "不合格".equals(c.getJxpj()) || StringUtils.isEmpty(c.getJxpj())).count();
            result.setTitle("加热炉综合运行评价");
            PieOption.PieData pieData = result.new PieData();

            pieData.setName("合格");
            pieData.setValue(tempResult.size() - unqualified);
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("不合格");
            pieData.setValue(unqualified);
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private PieOption getStaticOfJxpj(List<DMGC_Y_D_JRL> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("综合绩效评价");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("不合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getJxpj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getJxpj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("良好");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "良好".equals(c.getJxpj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private PieOption getStaticOfLxpj(List<DMGC_Y_D_JRL> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("热效率评价");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("不合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getLxpj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getLxpj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("良好");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "良好".equals(c.getLxpj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private PieOption getStaticOfPywd(List<DMGC_Y_D_JRL> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("排烟温度评价");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("不合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getYqwdpj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getYqwdpj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private PieOption getStaticOfKqxs(List<DMGC_Y_D_JRL> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("空气系数评价");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("不合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getKqxspj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getKqxspj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private PieOption getStaticOfLtwbmwd(List<DMGC_Y_D_JRL> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("炉体外表面温度评价");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("不合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getLtwbmwdpj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getLtwbmwdpj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private PieOption getStaticOfRfh(List<DMGC_Y_D_JRL> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("热负荷评价");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("不合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getRfhpj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getRfhpj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("良好");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "良好".equals(c.getRfhpj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    public ChartOption getStatistics(String startDate, String endDate, String cycle, String type, String jrlId) {
        List<DMGC_Y_D_JRL> dmgcYDJrlList = new ArrayList<>();
        switch (cycle) {
            case "日":
                dmgcYDJrlList = getDataOfDay(startDate, endDate);
                break;
            case "年":
                dmgcYDJrlList = getDataOfYear(startDate, endDate);
                break;
            case "月":
                dmgcYDJrlList = getDataOfMonth(startDate, endDate);
                break;
        }
        dmgcYDJrlList = dmgcYDJrlList.stream().filter(c -> c.getJrlId().equals(jrlId)).collect(Collectors.toList());
        dmgcYDJrlList = dmgcYDJrlList.stream().sorted(Comparator.comparing(DMGC_Y_D_JRL::getRq).thenComparing(DMGC_Y_D_JRL::getJrlId)).collect(Collectors.toList());
        ChartOption result = new ChartOption();
        result.setLegend(dmgcYDJrlList.stream().map(c -> c.getMc()).distinct().collect(Collectors.toList()));
        Collections.sort(result.getLegend());
        result.setXAxis(dmgcYDJrlList.stream().map(c -> DateUtils.format(c.getRq(), DateUtils.DATE_PATTERN)).distinct().collect(Collectors.toList()));
        Collections.sort(result.getXAxis());
        if (type.equals("绩效")) {
            result.setTitle("绩效曲线");
        }
        if (type.equals("炉效")) {
            result.setTitle("炉效曲线");
        }

        try {
            for (String legend : result.getLegend()) {
                ChartOption.Serie mySerie = result.new Serie();
                mySerie.setName(legend);
                mySerie.setType("line");
                mySerie.setStack("总量");
                List<DMGC_Y_D_JRL> temp = dmgcYDJrlList.stream().filter(c -> c.getJrlId().equals(jrlId)).collect(Collectors.toList());
                for (String date : result.getXAxis()) {
                    Date d = DateUtils.parse(date, DateUtils.DATE_PATTERN);
                    Optional<DMGC_Y_D_JRL> optional = temp.stream().filter(c -> {
                        try {
                            return DateUtils.parse(DateUtils.format(c.getRq(), DateUtils.DATE_PATTERN), DateUtils.DATE_PATTERN).getTime() == d.getTime();
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }).findFirst();
                    if (type.equals("绩效")) {
                        optional.ifPresent(c -> mySerie.getData().add(c.getJxScore()));
                    }
                    if (type.equals("炉效")) {
                        optional.ifPresent(c -> mySerie.getData().add(c.getLx()));
                    }
                    if (!optional.isPresent()) {
                        mySerie.getData().add((double) 0);
                    }
                }
                result.getSeries().add(mySerie);
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    public List<DMGC_Y_D_JRL> getEffectiveDataOfDay(List<String> zids, String queryDate) {
        List<DMGC_Y_D_JRL> result = new ArrayList<>();
        try {
            Date date = DateUtils.parse(queryDate, DateUtils.DATE_PATTERN);
            LambdaQueryWrapper<DMGC_Y_D_JRL> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(DMGC_Y_D_JRL::getSszkEventId, zids);
            queryWrapper.eq(DMGC_Y_D_JRL::getJrlyxzk, "01");
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getLx);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getYqwd);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getYqhyl);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getJrl);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getLtwbmwd);
            queryWrapper.ne(DMGC_Y_D_JRL::getLx, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getYqwd, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getYqhyl, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getJrl, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getLtwbmwd, 0);
            queryWrapper.eq(DMGC_Y_D_JRL::getRq, date);
            result = mapper.selectList(queryWrapper);
            return result;
        } catch (Exception ex) {
            return result;
        }
    }

    public List<DMGC_Y_D_JRL> getEffectiveData(Date queryStartDate,Date queryEndDate) {
        List<DMGC_Y_D_JRL> result = new ArrayList<>();
        try {
            LambdaQueryWrapper<DMGC_Y_D_JRL> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DMGC_Y_D_JRL::getJrlyxzk, "01");
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getLx);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getYqwd);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getYqhyl);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getJrl);
            queryWrapper.isNotNull(DMGC_Y_D_JRL::getLtwbmwd);
            queryWrapper.ne(DMGC_Y_D_JRL::getLx, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getYqwd, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getYqhyl, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getJrl, 0);
            queryWrapper.ne(DMGC_Y_D_JRL::getLtwbmwd, 0);
            queryWrapper.ge(DMGC_Y_D_JRL::getRq, queryStartDate);
            queryWrapper.le(DMGC_Y_D_JRL::getRq, queryEndDate);
            result = mapper.selectList(queryWrapper);
            return result;
        } catch (Exception ex) {
            return result;
        }
    }
}

