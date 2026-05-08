package com.xunjia.pes.bizData.waterInjection.service;

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
import com.xunjia.pes.bizData.assessment.service.MonitoringIndicatorNewService;
import com.xunjia.pes.bizData.waterInjection.entity.*;
import com.xunjia.pes.bizData.waterInjection.mapper.DMGC_S_D_ZSBRSJMapper;
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
public class DMGC_S_D_ZSBRSJService extends ServiceImpl<DMGC_S_D_ZSBRSJMapper, DMGC_S_D_ZSBRSJ> {

    @Autowired
    private DMGC_S_D_ZSBRSJMapper mapper;

    @Autowired
    private DMGC_S_JBService jbService;

    @Autowired
    private DMGC_S_ZSZService zszService;

    @Autowired
    private MonitoringIndicatorNewService monitoringIndicatorNewService;

    @Autowired
    private IndicatorsService indicatorsService;

    public Boolean saveHll(String id, String yxzt, Double bx, Double bckyl, Double bsshgyl, Double ll, Double hll) {
        try {
            DMGC_S_D_ZSBRSJ dmgcSDZsbrsj = mapper.selectById(id);
            if (Integer.parseInt(yxzt) == 1) {
                yxzt = "01";
            }
            dmgcSDZsbrsj.setYxzt(yxzt);
            dmgcSDZsbrsj.setBx(bx);
            dmgcSDZsbrsj.setBckyl(bckyl);
            dmgcSDZsbrsj.setBsshgyl(bsshgyl);
            dmgcSDZsbrsj.setLl(ll);
            dmgcSDZsbrsj.setHll(hll);
            mapper.updateById(dmgcSDZsbrsj);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Boolean auditData(String rq) {
        try {
            LambdaQueryWrapper<DMGC_S_D_ZSBRSJ> wrapper = new LambdaQueryWrapper<>();
            Date startDate = DateUtils.parse(rq, DateUtils.DATE_PATTERN);
            wrapper.ge(DMGC_S_D_ZSBRSJ::getRq, startDate);
            Date endDate = DateUtils.parse(rq + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
            wrapper.le(DMGC_S_D_ZSBRSJ::getRq, endDate);
            wrapper.eq(DMGC_S_D_ZSBRSJ::getYxzt, "01");
            wrapper.isNotNull(DMGC_S_D_ZSBRSJ::getBx);
            wrapper.isNotNull(DMGC_S_D_ZSBRSJ::getBckyl);
            wrapper.isNotNull(DMGC_S_D_ZSBRSJ::getBsshgyl);
            wrapper.isNotNull(DMGC_S_D_ZSBRSJ::getLl);
            wrapper.isNotNull(DMGC_S_D_ZSBRSJ::getHll);
            wrapper.ne(DMGC_S_D_ZSBRSJ::getBx, 0);
            wrapper.ne(DMGC_S_D_ZSBRSJ::getBckyl, 0);
            wrapper.ne(DMGC_S_D_ZSBRSJ::getBsshgyl, 0);
            wrapper.ne(DMGC_S_D_ZSBRSJ::getLl, 0);
            wrapper.ne(DMGC_S_D_ZSBRSJ::getHll, 0);
            List<DMGC_S_D_ZSBRSJ> dmgcSDZsbrsjList = mapper.selectList(wrapper);
//            for (DMGC_S_D_ZSBRSJ param : dmgcSDZsbrsjList) {
//                if (param.getHll() != null && (param.getDataAlreadyAudited() == null || param.getDataAlreadyAudited().equals(false))) {
//                    param.setDataAlreadyAudited(true);
//                }
//            }
            if (dmgcSDZsbrsjList.size() != 0) {
                getCompleteData(dmgcSDZsbrsjList);
                this.updateBatchById(dmgcSDZsbrsjList);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Boolean getIfSomeDataNotInput(String rq) {
        try {
            LambdaQueryWrapper<DMGC_S_D_ZSBRSJ> wrapper = new LambdaQueryWrapper<>();
            Date queryDate = DateUtils.parse(rq, DateUtils.DATE_PATTERN);
            wrapper.eq(DMGC_S_D_ZSBRSJ::getRq, queryDate);
            wrapper.eq(DMGC_S_D_ZSBRSJ::getYxzt, "01");
            wrapper.and(item -> item.isNull(DMGC_S_D_ZSBRSJ::getBx)
                    .or().isNull(DMGC_S_D_ZSBRSJ::getBckyl)
                    .or().isNull(DMGC_S_D_ZSBRSJ::getBsshgyl)
                    .or().isNull(DMGC_S_D_ZSBRSJ::getLl)
                    .or().isNull(DMGC_S_D_ZSBRSJ::getHll)
                    .or().eq(DMGC_S_D_ZSBRSJ::getBx, 0)
                    .or().eq(DMGC_S_D_ZSBRSJ::getBckyl, 0)
                    .or().eq(DMGC_S_D_ZSBRSJ::getBsshgyl, 0)
                    .or().eq(DMGC_S_D_ZSBRSJ::getLl, 0)
                    .or().eq(DMGC_S_D_ZSBRSJ::getHll, 0));
            return (long) mapper.selectList(wrapper).size() > 0;
        } catch (Exception ex) {
            return false;
        }
    }

    public Boolean updateData() {
        boolean result = true;
        try {
            Date now = new Date();
            String nowDateString = DateUtils.format(now, DateUtils.DATE_PATTERN);
//            String nowDateString = "2023-01-01";
            Date updateDate = DateUtils.parse(nowDateString, DateUtils.DATE_PATTERN);
            LambdaQueryWrapper<DMGC_S_D_ZSBRSJ> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DMGC_S_D_ZSBRSJ::getRq, updateDate);
            List<DMGC_S_D_ZSBRSJ> dataList = mapper.selectList(wrapper);
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

    public void exportData(DMGC_S_D_ZSBRSJ example, String startDate, String endDate,
                           HttpServletRequest request, HttpServletResponse response) {

        Workbook workbook = ExportUtils.createWorkbook();
        try {
            List<DMGC_S_D_ZSBRSJ> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            if (!ListUtils.isListEmpty(dataList)) {
                List<String> zszEventIds = dataList.stream().map(DMGC_S_D_ZSBRSJ::getSszkEventId).collect(Collectors.toList());
                List<String> jbEventIds = dataList.stream().map(DMGC_S_D_ZSBRSJ::getJbEventId).collect(Collectors.toList());
                List<DMGC_S_ZSZ> zszDataList = zszService.getByEventIds(zszEventIds);
                List<DMGC_S_JB> jbDataList = jbService.getByEventIds(jbEventIds);
                for (DMGC_S_D_ZSBRSJ rsj : dataList) {
                    Optional<DMGC_S_ZSZ> zszOptional = zszDataList.stream().filter(c -> c.getEventId().equals(rsj.getSszkEventId())).findFirst();
                    Optional<DMGC_S_JB> jbOptional = jbDataList.stream().filter(c -> c.getEventId().equals(rsj.getJbEventId())).findFirst();
                    zszOptional.ifPresent(c -> rsj.setZszName(c.getMc()));
                    jbOptional.ifPresent(c -> rsj.setJbName(c.getMc()));
                }
            }

            Map<String, List<String>> headerNamesAndFieldsMap = ExportUtils.getHeaderNamesAndFields(DMGC_S_D_ZSBRSJ.class);
            List<String> headerNames = headerNamesAndFieldsMap.get("headerNames");
            List<String> fields = headerNamesAndFieldsMap.get("fields");
            ExportUtils.exportExcel("注水机泵生产日运行数据", headerNames, fields, 0, workbook, dataList);
            ExportUtils.responseWorkbook("注水机泵生产日运行数据", workbook, request, response);
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
            List<DMGC_S_D_ZSBRSJ> dataList = getAssessmentNoPage("日",queryDate).stream().filter(c->c.getJbScore() != null).collect(Collectors.toList());
            Map<String, List<String>> headerNamesAndFieldsMap = ExportUtils.getExamineHeaderNamesAndFields(DMGC_S_D_ZSBRSJ.class);
            List<String> headerNames = headerNamesAndFieldsMap.get("headerNames");
            List<String> fields = headerNamesAndFieldsMap.get("fields");
            ExportUtils.exportExcel("注水系统机泵日考核", headerNames, fields, 0, workbook, dataList);
            ExportUtils.responseWorkbook("注水系统机泵日考核", workbook, request, response);
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

    public PageVO<DMGC_S_D_ZSBRSJ> getPageData(DMGC_S_D_ZSBRSJ example, String startDate, String endDate, int page, int size) {
        PageVO<DMGC_S_D_ZSBRSJ> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_S_D_ZSBRSJ> dataList = mapper.selectList(this.buildQueryWrapper(example, startDate, endDate));
            if (!ListUtils.isListEmpty(dataList)) {
                List<String> zszEventIds = dataList.stream().map(DMGC_S_D_ZSBRSJ::getSszkEventId).collect(Collectors.toList());
                List<String> jbEventIds = dataList.stream().map(DMGC_S_D_ZSBRSJ::getJbEventId).collect(Collectors.toList());
                List<DMGC_S_ZSZ> zszDataList = zszService.getByEventIds(zszEventIds);
                List<DMGC_S_JB> jbDataList = jbService.getByEventIds(jbEventIds);
                for (DMGC_S_D_ZSBRSJ rsj : dataList) {
                    Optional<DMGC_S_ZSZ> zszOptional = zszDataList.stream().filter(c -> c.getEventId().equals(rsj.getSszkEventId())).findFirst();
                    Optional<DMGC_S_JB> jbOptional = jbDataList.stream().filter(c -> c.getEventId().equals(rsj.getJbEventId())).findFirst();
                    zszOptional.ifPresent(c -> rsj.setZszName(c.getMc()));
                    jbOptional.ifPresent(c -> rsj.setJbName(c.getMc()));
                }
            }
            PageInfo<DMGC_S_D_ZSBRSJ> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e) {
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    private LambdaQueryWrapper<DMGC_S_D_ZSBRSJ> buildQueryWrapper(DMGC_S_D_ZSBRSJ example, String startDate, String endDate) {
        LambdaQueryWrapper<DMGC_S_D_ZSBRSJ> queryWrapper = new LambdaQueryWrapper<>();
        if (example != null) {
            if (StringUtils.isNotEmpty(example.getJbEventId())) {
                queryWrapper.eq(DMGC_S_D_ZSBRSJ::getJbEventId, example.getJbEventId());
            }
            if (StringUtils.isNotEmpty(example.getSszkEventId())) {
                queryWrapper.eq(DMGC_S_D_ZSBRSJ::getSszkEventId, example.getSszkEventId());
            }
            if (StringUtils.isNotEmpty(example.getZszName())) {
                queryWrapper.like(DMGC_S_D_ZSBRSJ::getZszName, example.getZszName());
            }
        }
        if (!StringUtils.isEmpty(startDate)) {
            try {
                Date date = DateUtils.parse(startDate, DateUtils.DATE_PATTERN);
                queryWrapper.ge(DMGC_S_D_ZSBRSJ::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!StringUtils.isEmpty(endDate)) {
            try {
                Date date = DateUtils.parse(endDate + " 23:59:59", DateUtils.DATE_TIME_PATTERN);
                queryWrapper.le(DMGC_S_D_ZSBRSJ::getRq, date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        queryWrapper.orderByDesc(DMGC_S_D_ZSBRSJ::getRq);
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

    public List<DMGC_S_D_ZSBRSJ> getDataOfDay(String startDate, String endDate) {
        List<DMGC_S_D_ZSBRSJ> dataList = mapper.selectList(this.buildQueryWrapper(null, startDate, endDate));
        return dataList;
    }

    public List<DMGC_S_D_ZSBRSJ> getDataOfMonth(String startDate, String endDate) {
        List<DMGC_S_D_ZSBRSJ> dataList = new ArrayList<>();
        String queryStart;
        String queryEnd;
        try {
            queryStart = DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getMonth(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-01";
            queryEnd = DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getMonth(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-" +
                    DateUtils.getDay(DateUtils.parse(DateUtils.getLastDay(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)), DateUtils.DATE_PATTERN));
            LambdaQueryWrapper<DMGC_S_D_ZSBRSJ> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.ge(DMGC_S_D_ZSBRSJ::getRq, queryStart);
            queryWrapper.le(DMGC_S_D_ZSBRSJ::getRq, queryEnd);
            queryWrapper.eq(DMGC_S_D_ZSBRSJ::getYxzt, "01");
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getBx);
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getBckyl);
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getBsshgyl);
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getLl);
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getHll);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getBx, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getBckyl, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getBsshgyl, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getLl, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getHll, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getJbScore, 0);
            dataList = mapper.selectList(queryWrapper);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return dataList;
    }

    public List<DMGC_S_D_ZSBRSJ> getDataOfYear(String startDate, String endDate) {
        List<DMGC_S_D_ZSBRSJ> dataList = new ArrayList<>();
        String queryStart;
        String queryEnd;
        try {
            queryStart = DateUtils.getYear(DateUtils.parse(startDate, DateUtils.DATE_PATTERN)) + "-01-01";
            queryEnd = DateUtils.getYear(DateUtils.parse(endDate, DateUtils.DATE_PATTERN)) + "-12-31";
            LambdaQueryWrapper<DMGC_S_D_ZSBRSJ> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.ge(DMGC_S_D_ZSBRSJ::getRq, queryStart);
            queryWrapper.le(DMGC_S_D_ZSBRSJ::getRq, queryEnd);
            queryWrapper.eq(DMGC_S_D_ZSBRSJ::getYxzt, "01");
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getBx);
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getBckyl);
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getBsshgyl);
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getLl);
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getHll);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getBx, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getBckyl, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getBsshgyl, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getLl, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getHll, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getJbScore, 0);
            dataList = mapper.selectList(queryWrapper);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return dataList;
    }

    private void getCompleteData(List<DMGC_S_D_ZSBRSJ> partData) {
        if (partData.size() == 0) {
            return;
        }
        List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("注水泵机组监测项目与指标要求", null);
        List<String> jbIds = partData.stream().map(DMGC_S_D_ZSBRSJ::getJbEventId).distinct().collect(Collectors.toList());
        List<String> zszEventIds = partData.stream().map(DMGC_S_D_ZSBRSJ::getSszkEventId).collect(Collectors.toList());
        List<DMGC_S_JB> dmgcSJbList = jbService.getByEventIds(jbIds);
        List<DMGC_S_ZSZ> zszDataList = zszService.getByEventIds(zszEventIds);
        List<Indicators> indicatorsList = indicatorsService.findAll().stream().filter(c -> c.getTypeCode().equals("zsz")).collect(Collectors.toList());
        for (DMGC_S_D_ZSBRSJ param : partData) {
            Optional<DMGC_S_ZSZ> zszOptional = zszDataList.stream().filter(c -> c.getEventId().equals(param.getSszkEventId())).findFirst();
            zszOptional.ifPresent(zsz -> {
                param.setZszName(zsz.getMc());
            });
            Optional<DMGC_S_JB> jbOptional = dmgcSJbList.stream().filter(c -> c.getEventId().equals(param.getJbEventId())).findFirst();
            jbOptional.ifPresent(jb -> {
                param.setJbType(jb.getBlx());
                param.setJbName(jb.getMc());
                MonitoringIndicatorNew monitoringIndicatorNew;
                //离心泵有节流损失率，往复泵没有
                //当前泵是否是离心泵
                boolean isCentrifugalPump = jb.getBlx().equals("3");
                // 往复泵没有节流损失率，他的权重分给其他三个指标
                // 获取正常的节流损失率权重
                double jlsslWeight = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level5") && c.getItemCode().equals("loss")).collect(Collectors.toList()).get(0).getWeight();
                // 计算额外的泵效权重 = 节流损失率权重/3
                double additionalBxWeight = Calculation.getDivisionResult(jlsslWeight, 3);
                //计算额外的回流损失率权重 =节流损失率权重/3
                double additionalHllWeight = Calculation.getDivisionResult(jlsslWeight, 3);
                //计算额外的负荷率权重 = 节流损失率权重 - 额外的泵效权重 -额外的回流损失率权重
                double additionalFhlWeight = Calculation.getReduceResult(Calculation.getReduceResult(jlsslWeight, additionalBxWeight), additionalHllWeight);

                //往复泵:往复泵节流损失率得分设置为0,权重设置为0
                if (!isCentrifugalPump) {
                    param.setJlsslScore((double) 0);
                    param.setWeightJlssl((double) 0);
                    param.setWeightJlsslScore((double) 0);
                }

                //离心泵:节流损失率
                if (isCentrifugalPump) {
                    monitoringIndicatorNew = filterByQueryData(jb.getEdll(), monitoringIndicatorNewList, "节流损失率");
                    param.setWeightJlssl(jlsslWeight);
                    if (monitoringIndicatorNew != null && param.getBckyl() != null && param.getBsshgyl() != null) {
                        //节流损失率=（泵出口压力-泵所属汇管压力）/泵出口压力*100
                        double tempParam1 = Calculation.getReduceResult(param.getBckyl(), param.getBsshgyl());
                        double tempParam2 = Calculation.getDivisionResult(tempParam1, param.getBckyl());
                        param.setJlssl(Calculation.getMultiplicationResult(tempParam2, 100));
                        param.setJlsslScore(Calculation.calculationOfUnitConsumption(param.getJlssl(), monitoringIndicatorNew));
                        param.setWeightJlsslScore(Calculation.getMultiplicationResult(param.getJlsslScore(), param.getWeightJlssl()));
                        param.setJlsslpj(Calculation.getUnitConsumptionComment(param.getJlsslScore(), monitoringIndicatorNew));
                    }
                }

                //离心泵
                if (isCentrifugalPump) {
                    monitoringIndicatorNew = filterByQueryData(jb.getEdll(), monitoringIndicatorNewList, "离心泵机组效率");
                } else {
                    monitoringIndicatorNew = filterByQueryData(jb.getEdll(), monitoringIndicatorNewList, "往复泵机组效率");
                }
                double bxWeight = indicatorsList.stream().filter(c -> c.getLevelCode().equals("level5") && c.getItemCode().equals("efficiency")).collect(Collectors.toList()).get(0).getWeight();
                if (isCentrifugalPump) {
                    param.setWeightBx(bxWeight);
                } else {
                    param.setWeightBx(Calculation.getPlusResult(bxWeight, additionalBxWeight));
                }
                //两种泵泵效的算法一致，只是限定值不同
                if (monitoringIndicatorNew != null && param.getBx() != null) {
                    param.setBxScore(Calculation.efficiency(param.getBx(), monitoringIndicatorNew));
                    param.setWeightBxScore(Calculation.getMultiplicationResult(param.getBxScore(), param.getWeightBx()));
                    param.setBxpj(Calculation.getEfficiencyComment(param.getBxScore(), monitoringIndicatorNew));
                }

                monitoringIndicatorNew = filterByQueryData(jb.getEdll(), monitoringIndicatorNewList, "回流损失率");
                double hlRateWeight = indicatorsList.stream().filter(d -> d.getLevelCode().equals("level5") && d.getItemCode().equals("hlssl")).collect(Collectors.toList()).get(0).getWeight();
                if (isCentrifugalPump) {
                    param.setWeightHlRate(hlRateWeight);
                } else {
                    param.setWeightHlRate(Calculation.getPlusResult(hlRateWeight, additionalHllWeight));
                }
                if (monitoringIndicatorNew != null && param.getHll() != null && param.getLl() != null) {
                    //回流率=回流量/流量*100
                    param.setHlRate(Calculation.getMultiplicationResult(Calculation.getDivisionResult(param.getHll(), param.getLl()), 100));
                    param.setHlRateScore(Calculation.calculationOfUnitConsumption(param.getHlRate(), monitoringIndicatorNew));
                    param.setWeightHlRateScore(Calculation.getMultiplicationResult(param.getHlRateScore(), param.getWeightHlRate()));
                    param.setHlRatepj(Calculation.getUnitConsumptionComment(param.getHlRateScore(), monitoringIndicatorNew));
                }

                monitoringIndicatorNew = filterByQueryData(jb.getEdll(), monitoringIndicatorNewList, "负荷率");
                double fhlWeight = indicatorsList.stream().filter(d -> d.getLevelCode().equals("level5") && d.getItemCode().equals("fhl")).collect(Collectors.toList()).get(0).getWeight();
                if (isCentrifugalPump) {
                    param.setWeightFhl(fhlWeight);
                } else {
                    param.setWeightFhl(Calculation.getPlusResult(fhlWeight, additionalFhlWeight));
                }
                if (monitoringIndicatorNew != null && param.getLl() != null) {
                    //负荷率=流量/额定流量*100
                    param.setFhl(Calculation.getMultiplicationResult(Calculation.getDivisionResult(param.getLl(), jb.getEdll()), 100));
                    param.setFhlScore(Calculation.specialFhl(param.getFhl(), monitoringIndicatorNew));
                    param.setWeightFhlScore(Calculation.getMultiplicationResult(param.getFhlScore(), param.getWeightFhl()));
                    param.setFhlpj(Calculation.specialFhlComment(param.getFhlScore(), monitoringIndicatorNew));
                }

                monitoringIndicatorNew = filterByQueryData(jb.getEdll(), monitoringIndicatorNewList, "绩效评价");
                if (monitoringIndicatorNew != null && param.getWeightBxScore() != null && param.getWeightJlsslScore() != null
                        && param.getWeightHlRateScore() != null && param.getWeightFhlScore() != null) {
                    param.setJbScore(Calculation.getPlusResult((param.getWeightBxScore() + param.getWeightJlsslScore() + param.getWeightHlRateScore() + param.getWeightFhlScore()), 0));
                    param.setJbpj(Calculation.getEfficiencyComment(param.getJbScore(), monitoringIndicatorNew));
                }
            });
        }
    }

    public PageVO<DMGC_S_D_ZSBRSJ> getAssessment(String cycle, String assessmentDate, int page, int size,DMGC_S_D_ZSBRSJ example) {
        PageVO<DMGC_S_D_ZSBRSJ> pageVO = null;
        List<DMGC_S_D_ZSBRSJ> tempResult = new ArrayList<>();
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
            List<DMGC_S_D_ZSBRSJ> dataList = tempResult;
            if (cycle.equals("月") || cycle.equals("年")) {
                dataList = buildAssessment(tempResult, sumStart, sumEnd);
            }
            if(example != null){
                if(StringUtils.isNotEmpty(example.getZszName())){
                    dataList = dataList.stream().filter(c->c.getZszName() != null && c.getZszName().contains(example.getZszName())).collect(Collectors.toList());
                }

                if(StringUtils.isNotEmpty(example.getJbName())){
                    dataList = dataList.stream().filter(c->c.getJbName() != null && c.getJbName().contains(example.getJbName())).collect(Collectors.toList());
                }

                if(StringUtils.isNotEmpty(example.getBbh())){
                    dataList = dataList.stream().filter(c->c.getBbh() != null && c.getBbh().contains(example.getBbh())).collect(Collectors.toList());
                }

                if (StringUtils.isNotEmpty(example.getJbpj())) {
                    if ("不合格".equals(example.getJbpj())) {
                        dataList = dataList.stream().filter(c -> (StringUtils.isEmpty(c.getBxpj()) || "不合格".equals(c.getBxpj()))
                                || (StringUtils.isEmpty(c.getJlsslpj()) || "不合格".equals(c.getJlsslpj()))
                                || (StringUtils.isEmpty(c.getHlRatepj()) || "不合格".equals(c.getHlRatepj()))
                                || (StringUtils.isEmpty(c.getFhlpj()) || "不合格".equals(c.getFhlpj()))
                                || (StringUtils.isEmpty(c.getJbpj()) || "不合格".equals(c.getJbpj()))).collect(Collectors.toList());
                    } else {
                        dataList = dataList.stream().filter(c -> (StringUtils.isNotEmpty(c.getBxpj()) && !"不合格".equals(c.getBxpj()))
                                && (StringUtils.isNotEmpty(c.getJlsslpj()) && !"不合格".equals(c.getJlsslpj()))
                                && (StringUtils.isNotEmpty(c.getHlRatepj()) && !"不合格".equals(c.getHlRatepj()))
                                && (StringUtils.isNotEmpty(c.getFhlpj()) && !"不合格".equals(c.getFhlpj()))
                                && (StringUtils.isNotEmpty(c.getJbpj()) && !"不合格".equals(c.getJbpj()))).collect(Collectors.toList());
                    }
                }
            }
            dataList = dataList.stream().sorted(Comparator.comparing(DMGC_S_D_ZSBRSJ::getZszName, Comparator.nullsLast(String::compareTo)).thenComparing(DMGC_S_D_ZSBRSJ::getJbName, Comparator.nullsLast(String::compareTo))).collect(Collectors.toList());
//            PageMethod.startPage(page, size);
            PageInfo<DMGC_S_D_ZSBRSJ> pageInfo = PageInfo.of(dataList);
            List<DMGC_S_D_ZSBRSJ> pageList = dataList.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
            pageVO = new PageVO<>(pageInfo.getTotal(), pageList);
            return pageVO;
        } catch (Exception ex) {
            String err = ex.getMessage();
            return new PageVO<>();
        }
    }

    public List<DMGC_S_D_ZSBRSJ> getAssessmentNoPage(String cycle, String assessmentDate) {
        List<DMGC_S_D_ZSBRSJ> result = new ArrayList<>();
        List<DMGC_S_D_ZSBRSJ> tempResult = new ArrayList<>();
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
                return result;
            }
            result = tempResult;
            if (cycle.equals("月") || cycle.equals("年")) {
                result = buildAssessment(tempResult, sumStart, sumEnd);
            }
            result = result.stream().sorted(Comparator.comparing(DMGC_S_D_ZSBRSJ::getZszName).thenComparing(DMGC_S_D_ZSBRSJ::getJbName)).collect(Collectors.toList());
            return result;
        } catch (Exception ex) {
            String err = ex.getMessage();
            return result;
        }
    }

    private List<DMGC_S_D_ZSBRSJ> buildAssessment(List<DMGC_S_D_ZSBRSJ> dmgcSDZsbrsjList, Date sumStart, Date sumEnd) {
        if (dmgcSDZsbrsjList.size() == 0) {
            return new ArrayList<>();
        }
        List<MonitoringIndicatorNew> monitoringIndicatorNewList = monitoringIndicatorNewService.findByParams("注水泵机组监测项目与指标要求", null);
        List<String> jbIds = dmgcSDZsbrsjList.stream().map(c -> c.getJbEventId()).distinct().collect(Collectors.toList());
        List<DMGC_S_JB> dmgcSJbList = jbService.getByEventIds(jbIds);
        List<DMGC_S_D_ZSBRSJ> resultList = new ArrayList<>();
        for (String jbId : jbIds) {
            if (resultList.stream().filter(c -> c.getJbEventId().equals(jbId)).count() != 0) {
                continue;
            }
            Date finalSumStart = sumStart;
            Date finalSumEnd = sumEnd;

            DMGC_S_D_ZSBRSJ result = new DMGC_S_D_ZSBRSJ();
            DMGC_S_D_ZSBRSJ temp = dmgcSDZsbrsjList.stream().filter(c -> c.getJbEventId().equals(jbId)).findFirst().get();
            result.setJbEventId(jbId);
            result.setJbName(temp.getJbName());
            result.setEventId(temp.getEventId());
            result.setZszName(temp.getZszName());
            result.setRq(sumEnd);
            result.setBbh(temp.getBbh());
            result.setJbType(temp.getJbType());

            long jbIdCount = dmgcSDZsbrsjList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime()
                    && (c.getBx() != null || c.getFhl() != null || c.getJlssl() != null || c.getHlRate() != null)).count();
            Optional<DMGC_S_JB> jbOptional = dmgcSJbList.stream().filter(c -> c.getEventId().equals(jbId)).findFirst();
            jbOptional.ifPresent(jb -> {
                MonitoringIndicatorNew monitoringIndicatorNew = null;
                //离心泵
                if (jb.getBlx().equals("3")) {
                    monitoringIndicatorNew = filterByQueryData(jb.getEdll(), monitoringIndicatorNewList, "离心泵机组效率");
                }
                //往复泵
                if (jb.getBlx().equals("2")) {
                    monitoringIndicatorNew = filterByQueryData(jb.getEdll(), monitoringIndicatorNewList, "往复泵机组效率");
                    //往复泵节流损失率得分设置为0
                    result.setJlsslScore((double) 0);
                    result.setWeightJlsslScore((double) 0);
                }
                if (monitoringIndicatorNew != null) {
//                    result.setWeightBx(temp.getWeightBx());
                    result.setBxScore(Calculation.getDivisionResult(dmgcSDZsbrsjList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getBxScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_S_D_ZSBRSJ::getBxScore).sum(), jbIdCount));
//                    result.setWeightBxScore(Calculation.getMultiplicationResult(result.getBxScore(), result.getWeightBx()));
                    result.setWeightBxScore(Calculation.getDivisionResult(dmgcSDZsbrsjList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getBxScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_S_D_ZSBRSJ::getWeightBxScore).sum(), jbIdCount));
                    result.setBxpj(Calculation.getEfficiencyComment(result.getBxScore(), monitoringIndicatorNew));
                }
                //离心泵有节流损失率，往复泵没有
                if (jb.getBlx().equals("3")) {
                    monitoringIndicatorNew = filterByQueryData(jb.getEdll(), monitoringIndicatorNewList, "节流损失率");
                    if (monitoringIndicatorNew != null) {
//                        result.setWeightJlssl(temp.getWeightJlssl());
                        result.setJlsslScore(Calculation.getDivisionResult(dmgcSDZsbrsjList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getJlsslScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                                .mapToDouble(DMGC_S_D_ZSBRSJ::getJlsslScore).sum(), jbIdCount));
//                        result.setWeightJlsslScore(Calculation.getMultiplicationResult(result.getJlsslScore(), result.getWeightJlssl()));
                        result.setWeightJlsslScore(Calculation.getDivisionResult(dmgcSDZsbrsjList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getJlsslScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                                .mapToDouble(DMGC_S_D_ZSBRSJ::getWeightJlsslScore).sum(), jbIdCount));
                        result.setJlsslpj(Calculation.getUnitConsumptionComment(result.getJlsslScore(), monitoringIndicatorNew));
                    }
                }

                monitoringIndicatorNew = filterByQueryData(jb.getEdll(), monitoringIndicatorNewList, "回流损失率");
                if (monitoringIndicatorNew != null) {
//                    result.setWeightHlRate(temp.getWeightHlRate());
                    result.setHlRateScore(Calculation.getDivisionResult(dmgcSDZsbrsjList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getHlRateScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_S_D_ZSBRSJ::getHlRateScore).sum(), jbIdCount));
//                    result.setWeightHlRateScore(Calculation.getMultiplicationResult(result.getHlRateScore(), result.getWeightHlRate()));
                    result.setWeightHlRateScore(Calculation.getDivisionResult(dmgcSDZsbrsjList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getHlRateScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_S_D_ZSBRSJ::getWeightHlRateScore).sum(), jbIdCount));
                    result.setHlRatepj(Calculation.getUnitConsumptionComment(result.getHlRateScore(), monitoringIndicatorNew));
                }

                monitoringIndicatorNew = filterByQueryData(jb.getEdll(), monitoringIndicatorNewList, "负荷率");
                if (monitoringIndicatorNew != null) {
//                    result.setWeightFhl(temp.getWeightFhl());
                    result.setFhlScore(Calculation.getDivisionResult(dmgcSDZsbrsjList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getFhlScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_S_D_ZSBRSJ::getFhlScore).sum(), jbIdCount));
//                    result.setWeightFhlScore(Calculation.getMultiplicationResult(result.getFhlScore(), result.getWeightFhl()));
                    result.setWeightFhlScore(Calculation.getDivisionResult(dmgcSDZsbrsjList.stream().filter(c -> c.getJbEventId().equals(jbId) && c.getFhlScore() != null && c.getRq().getTime() >= finalSumStart.getTime() && c.getRq().getTime() <= finalSumEnd.getTime())
                            .mapToDouble(DMGC_S_D_ZSBRSJ::getWeightFhlScore).sum(), jbIdCount));
                    result.setFhlpj(Calculation.specialFhlComment(result.getFhlScore(), monitoringIndicatorNew));
                }

                monitoringIndicatorNew = filterByQueryData(jb.getEdll(), monitoringIndicatorNewList, "绩效评价");
                if (monitoringIndicatorNew != null && result.getWeightBxScore() != null && result.getWeightJlsslScore() != null
                        && result.getWeightHlRateScore() != null && result.getWeightFhlScore() != null) {
                    result.setJbScore(Calculation.getPlusResult((result.getWeightBxScore() + result.getWeightJlsslScore() + result.getWeightHlRateScore() + result.getWeightFhlScore()), 0));
                    result.setJbpj(Calculation.getEfficiencyComment(result.getJbScore(), monitoringIndicatorNew));
                }
            });
            resultList.add(result);
        }
        return resultList;
    }

    public List<PieOption> getAllStaticsOfPipe(String rq) {
        List<DMGC_S_D_ZSBRSJ> tempResult = getDataOfDay(rq, rq).stream().filter(c->"01".equals(c.getYxzt())).collect(Collectors.toList());
        List<PieOption> result = new ArrayList<>();
        result.add(getStaticOfRunStatic(tempResult));
        result.add(getStaticOfJxpj(tempResult));
        result.add(getStatisticsOfRunningState(tempResult));
        result.add(getStatisticsOfThrottlingLoss(tempResult));
        result.add(getStaticOfHlRate(tempResult));
        result.add(getStaticOfFhl(tempResult));
        return result;
    }

    private PieOption getStaticOfRunStatic(List<DMGC_S_D_ZSBRSJ> tempResult) {
        PieOption result = new PieOption();
        try {
            long unqualified = tempResult.stream()
                    .filter(c -> "不合格".equals(c.getBxpj()) || StringUtils.isEmpty(c.getBxpj())
                            || "不合格".equals(c.getJlsslpj()) || StringUtils.isEmpty(c.getJlsslpj())
                            || "不合格".equals(c.getHlRatepj()) || StringUtils.isEmpty(c.getHlRatepj())
                            || "不合格".equals(c.getFhlpj()) || StringUtils.isEmpty(c.getFhlpj())
                            || "不合格".equals(c.getJbpj()) || StringUtils.isEmpty(c.getJbpj())).count();
            result.setTitle("注水泵综合运行评价");
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

    private PieOption getStaticOfJxpj(List<DMGC_S_D_ZSBRSJ> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("综合绩效评价");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("不合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getJbpj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getJbpj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("良好");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "良好".equals(c.getJbpj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private PieOption getStatisticsOfRunningState(List<DMGC_S_D_ZSBRSJ> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("机组效率");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("低效区");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getBxpj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("高效区");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "良好".equals(c.getBxpj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("合理区");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getBxpj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private PieOption getStatisticsOfThrottlingLoss(List<DMGC_S_D_ZSBRSJ> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("节流损失率");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("节流损失偏大");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getJlsslpj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("节流损失正常");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getJlsslpj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private PieOption getStaticOfHlRate(List<DMGC_S_D_ZSBRSJ> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("回流损失率");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("不合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getHlRatepj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getHlRatepj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("良好");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "良好".equals(c.getHlRatepj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private PieOption getStaticOfFhl(List<DMGC_S_D_ZSBRSJ> tempResult) {
        PieOption result = new PieOption();
        try {
            result.setTitle("负荷率");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("不合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "不合格".equals(c.getFhlpj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("合格");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "合格".equals(c.getFhlpj())).count());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("良好");
            pieData.setValue(tempResult.stream()
                    .filter(c -> "良好".equals(c.getFhlpj())).count());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    public ChartOption getStatistics(String startDate, String endDate, String cycle, String type, String jbId) {
        List<DMGC_S_D_ZSBRSJ> dmgcSDZsbrsjList = new ArrayList<>();
        switch (cycle) {
            case "日":
                dmgcSDZsbrsjList = getDataOfDay(startDate, endDate);
                break;
            case "月":
                dmgcSDZsbrsjList = getDataOfMonth(startDate, endDate);
                break;
            case "年":
                dmgcSDZsbrsjList = getDataOfYear(startDate, endDate);
                break;
        }
        dmgcSDZsbrsjList = dmgcSDZsbrsjList.stream().filter(c -> c.getJbEventId().equals(jbId)).collect(Collectors.toList());
        dmgcSDZsbrsjList = dmgcSDZsbrsjList.stream().sorted(Comparator.comparing(DMGC_S_D_ZSBRSJ::getRq).thenComparing(DMGC_S_D_ZSBRSJ::getJbEventId)).collect(Collectors.toList());
        ChartOption result = new ChartOption();
        result.setLegend(dmgcSDZsbrsjList.stream().map(c -> c.getJbName()).distinct().collect(Collectors.toList()));
        Collections.sort(result.getLegend());
        result.setXAxis(dmgcSDZsbrsjList.stream().map(c -> DateUtils.format(c.getRq(), DateUtils.DATE_PATTERN)).distinct().collect(Collectors.toList()));
        Collections.sort(result.getXAxis());
        if (type.equals("绩效")) {
            result.setTitle("绩效曲线");
        }
        if (type.equals("泵效")) {
            result.setTitle("泵效曲线");
        }
        try {
            for (String legend : result.getLegend()) {
                ChartOption.Serie mySerie = result.new Serie();
                mySerie.setName(legend);
                mySerie.setType("line");
                mySerie.setStack("总量");
                List<DMGC_S_D_ZSBRSJ> temp = dmgcSDZsbrsjList.stream().filter(c -> c.getJbEventId().equals(jbId)).collect(Collectors.toList());
                for (String date : result.getXAxis()) {
                    Date d = DateUtils.parse(date, DateUtils.DATE_PATTERN);
                    Optional<DMGC_S_D_ZSBRSJ> optional = temp.stream().filter(c -> {
                        try {
                            return DateUtils.parse(DateUtils.format(c.getRq(), DateUtils.DATE_PATTERN), DateUtils.DATE_PATTERN).getTime() == d.getTime();
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }).findFirst();
                    if (type.equals("绩效")) {
                        optional.ifPresent(c -> mySerie.getData().add(c.getJbScore()));
                    }
                    if (type.equals("泵效")) {
                        optional.ifPresent(c -> mySerie.getData().add(c.getBx()));
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

    public List<DMGC_S_D_ZSBRSJ> getEffectiveDataOfDay(List<String> zids, String queryDate) {
        List<DMGC_S_D_ZSBRSJ> result = new ArrayList<>();
        try {
            Date date = DateUtils.parse(queryDate, DateUtils.DATE_PATTERN);
            LambdaQueryWrapper<DMGC_S_D_ZSBRSJ> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(DMGC_S_D_ZSBRSJ::getSszkEventId, zids);
            queryWrapper.eq(DMGC_S_D_ZSBRSJ::getYxzt, "01");
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getBx);
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getBckyl);
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getBsshgyl);
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getLl);
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getHll);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getBx, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getBckyl, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getBsshgyl, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getLl, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getHll, 0);
            queryWrapper.eq(DMGC_S_D_ZSBRSJ::getRq, date);
            result = mapper.selectList(queryWrapper);
            return result;
        } catch (Exception ex) {
            return result;
        }
    }

    public List<DMGC_S_D_ZSBRSJ> getEffectiveData(Date queryStartDate,Date queryEndDate) {
        List<DMGC_S_D_ZSBRSJ> result = new ArrayList<>();
        try {
            LambdaQueryWrapper<DMGC_S_D_ZSBRSJ> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DMGC_S_D_ZSBRSJ::getYxzt, "01");
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getBx);
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getBckyl);
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getBsshgyl);
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getLl);
            queryWrapper.isNotNull(DMGC_S_D_ZSBRSJ::getHll);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getBx, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getBckyl, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getBsshgyl, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getLl, 0);
            queryWrapper.ne(DMGC_S_D_ZSBRSJ::getHll, 0);
            queryWrapper.ge(DMGC_S_D_ZSBRSJ::getRq, queryStartDate);
            queryWrapper.le(DMGC_S_D_ZSBRSJ::getRq, queryEndDate);
            result = mapper.selectList(queryWrapper);
            return result;
        } catch (Exception ex) {
            return result;
        }
    }
}
