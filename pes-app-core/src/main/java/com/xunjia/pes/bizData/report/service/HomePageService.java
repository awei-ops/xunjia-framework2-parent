package com.xunjia.pes.bizData.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.pes.bizData.PieOption;
import com.xunjia.pes.bizData.report.entity.ConsumeGasPower;
import com.xunjia.pes.bizData.report.entity.HomePage;
import com.xunjia.pes.bizData.report.entity.MonthlyEnergy;
import com.xunjia.pes.bizData.report.mapper.HomePageMapper;
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

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class HomePageService extends ServiceImpl<HomePageMapper, HomePage> {
    @Autowired
    private MonthlyEnergyService monthlyEnergyService;

    @Autowired
    private ConsumeGasPowerService consumeGasPowerService;

    @Value("${a5SyncUrl:127.0.0.1:8099}")
    private String a5SyncUrl;

    public ResponseData<Boolean> add(HomePage param) {
        ResponseData<Boolean> resp;
        try {
            this.save(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception ex) {
            resp = ResponseData.getError(ex);
        }
        return resp;
    }

    public ResponseData<Boolean> update(HomePage param) {
        ResponseData<Boolean> resp;
        try {
            this.updateById(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception ex) {
            resp = ResponseData.getError(ex);
        }
        return resp;
    }

    private HomePage queryHomePage(Integer year) {
        LambdaQueryWrapper<HomePage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HomePage::getYear, year);
        return this.getOne(queryWrapper, false);
    }

    public HomePage buildReportSchema(Integer year) {
        HomePage result = queryHomePage(year);
        try {
            List<MonthlyEnergy> monthlyEnergyList = monthlyEnergyService.queryMonthlyEnergyListSum(year, 12);
            List<ConsumeGasPower> consumeGasPowerList = consumeGasPowerService.queryConsumeGasListSum(year, 12);
            if (result == null) {
                result = new HomePage();
                result.setYear(year);
                creatHomePage(result, monthlyEnergyList, consumeGasPowerList);
                add(result);
            } else {
                creatHomePage(result, monthlyEnergyList, consumeGasPowerList);
                update(result);
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private void creatHomePage(HomePage homePage, List<MonthlyEnergy> monthlyEnergyList, List<ConsumeGasPower> consumeGasPowerList) {
        long count;
        // 油田耗气
        Double oilfieldGasConsumption = consumeGasPowerList.stream().filter(c -> c.getConsumeGas() != null).mapToDouble(ConsumeGasPower::getConsumeGas).sum();
        homePage.setOilfieldGasConsumption(oilfieldGasConsumption);

        // 气井产气(从A5采气井动态日数据取 DMGC_Q_D_CQJRSJ)
        Double gasWellProduction = (double)0;
        try {
            List<Map<String, Object>> mapList = getJqzSum(homePage.getYear());
            if(mapList.get(0).get("RCQL") != null){
                gasWellProduction = Double.parseDouble(mapList.get(0).get("RCQL").toString());
            }
        }catch (Exception ex){
            String err = ex.getMessage();
        }
        homePage.setGasWellProduction(Calculation.getDivisionResult(gasWellProduction, 10000));

        // 深层来气(没有，依靠录入)
        // 哈市供气 (没有，依靠录入)

        // 吨液耗气
        Double oilGasUnitSumAve = consumeGasPowerList.stream().filter(c -> c.getGasUnit() != null).mapToDouble(ConsumeGasPower::getGasUnit).sum();
        count = consumeGasPowerList.stream().filter(c -> c.getGasUnit() != null).count();
        homePage.setOilGasUnitSumAve(Calculation.getDivisionResult(oilGasUnitSumAve, count));

        //注水系统-泵水单耗
        Double waterInjectPumpingUnitSumAve = monthlyEnergyList.stream().filter(c -> c.getWaterInjectPumpingUnit() != null).mapToDouble(MonthlyEnergy::getWaterInjectPumpingUnit).sum();
        count = monthlyEnergyList.stream().filter(c -> c.getWaterInjectPumpingUnit() != null).count();
        homePage.setWaterInjectPumpingUnitSumAve(Calculation.getDivisionResult(waterInjectPumpingUnitSumAve,count));

        Double oilPowerUnitSumAve = monthlyEnergyList.stream().filter(c -> c.getOilPowerUnit() != null).mapToDouble(MonthlyEnergy::getOilPowerUnit).sum();
        count = monthlyEnergyList.stream().filter(c -> c.getOilPowerUnit() != null).count();
        homePage.setOilPowerUnitSumAve(Calculation.getDivisionResult(oilPowerUnitSumAve,count));

        //油田耗电
        Double oilfieldPowerConsumption = (double) 0;

        //集输系统-耗电量累计
        Double oilPowerSum = monthlyEnergyList.stream().filter(c -> c.getOilStationPower() != null).mapToDouble(MonthlyEnergy::getOilStationPower).sum();
        oilPowerSum = Calculation.getPlusResult(oilPowerSum, monthlyEnergyList.stream().filter(c -> c.getOilHeatingPower() != null).mapToDouble(MonthlyEnergy::getOilHeatingPower).sum());
        oilPowerSum = Calculation.getPlusResult(oilPowerSum, monthlyEnergyList.stream().filter(c -> c.getOilGasPower() != null).mapToDouble(MonthlyEnergy::getOilGasPower).sum());
        homePage.setOilPowerSum(oilPowerSum);

        oilfieldPowerConsumption = Calculation.getPlusResult(oilfieldPowerConsumption, oilPowerSum);

        //注水系统-耗电量累计
        Double waterInjectPowerSum = monthlyEnergyList.stream().filter(c -> c.getWaterInjectPower() != null).mapToDouble(MonthlyEnergy::getWaterInjectPower).sum();
        homePage.setWaterInjectPowerSum(waterInjectPowerSum);

        oilfieldPowerConsumption = Calculation.getPlusResult(oilfieldPowerConsumption, waterInjectPowerSum);

        //水处理系统-耗电量
        Double waterTreatmentPowerSum = monthlyEnergyList.stream().filter(c -> c.getWaterTreatmentPower() != null).mapToDouble(MonthlyEnergy::getWaterTreatmentPower).sum();
        homePage.setWaterTreatmentPowerSum(waterTreatmentPowerSum);

        oilfieldPowerConsumption = Calculation.getPlusResult(oilfieldPowerConsumption, waterTreatmentPowerSum);

        //供水系统-耗电量累计
        Double waterSupplyPowerSum = monthlyEnergyList.stream().filter(c -> c.getWaterSupplyPower() != null).mapToDouble(MonthlyEnergy::getWaterSupplyPower).sum();
        homePage.setWaterSupplyPowerSum(waterSupplyPowerSum);

        oilfieldPowerConsumption = Calculation.getPlusResult(oilfieldPowerConsumption, waterSupplyPowerSum);
        homePage.setOilfieldPowerConsumption(oilfieldPowerConsumption);
    }

    public PieOption getStatistics(HomePage homePage) {
        PieOption result = new PieOption();
        try {
            result.setTitle("采油十厂耗电");
            PieOption.PieData pieData = result.new PieData();
            pieData.setName("水处理系统");
            pieData.setValue(homePage.getWaterTreatmentPowerSum() == null ? 0 : homePage.getWaterTreatmentPowerSum());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("供水系统");
            pieData.setValue(homePage.getWaterSupplyPowerSum() == null ? 0 : homePage.getWaterSupplyPowerSum());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("集输系统");
            pieData.setValue(homePage.getOilPowerSum() == null ? 0 : homePage.getOilPowerSum());
            result.getSeries().add(pieData);
            pieData = result.new PieData();
            pieData.setName("注水系统");
            pieData.setValue(homePage.getWaterInjectPowerSum() == null ? 0 : homePage.getWaterInjectPowerSum());
            result.getSeries().add(pieData);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }

    private List<Map<String, Object>> getJqzSum(Integer year) {
        RestTemplate template = new RestTemplate();
        String url = "http://" + a5SyncUrl + "/a5_consume_gas/getQjcqSum?year=" +year;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<List<Map<String, Object>>> requestEntity = new HttpEntity<>(null, headers);
        List<Map<String, Object>> result = template.exchange(url, HttpMethod.GET, requestEntity, List.class).getBody();
        return result;
    }
}
