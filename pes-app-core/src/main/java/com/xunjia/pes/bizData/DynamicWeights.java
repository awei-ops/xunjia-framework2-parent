package com.xunjia.pes.bizData;

import com.xunjia.framework.utils.ListUtils;
import com.xunjia.pes.bizData.oil.entity.*;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSBRSJ;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSZRSJ;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_JB;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_ZSZ;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_D_SCLZRSJ;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_SCLZ;
import com.xunjia.pes.score.Calculation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DynamicWeights {
    public List<DynamicWeightResult> getZyzWeightMap(List<DMGC_Y_ZYZ> zyzList, List<DMGC_JRL> jrlList, List<DMGC_Y_D_JRL> yDJrlList,
                                                     List<DMGC_Y_JB> yJbList, List<DMGC_Y_D_SYB> yDSybList, List<DMGC_Y_D_CSB> yDCsbList) {
        List<DynamicWeightResult> resultList = new ArrayList<>();
        for (DMGC_Y_ZYZ dmgcYZyz : zyzList) {
            //当前站下的有效加热炉数据
            List<DMGC_Y_D_JRL> yDJrls = yDJrlList.stream().filter(c -> c.getSszkEventId().equals(dmgcYZyz.getEventId())).collect(Collectors.toList());
            //当前站下的有效输油泵数据
            List<DMGC_Y_D_SYB> yDSybs = yDSybList.stream().filter(c -> c.getSszkEventId().equals(dmgcYZyz.getEventId())).collect(Collectors.toList());
            //当前站下的有效掺水泵数据
            List<DMGC_Y_D_CSB> yDCsbs = yDCsbList.stream().filter(c -> c.getSszkEventId().equals(dmgcYZyz.getEventId())).collect(Collectors.toList());
            //当前站下在运行的加热炉ID合集
            List<String> jrlIds = yDJrls.stream().map(DMGC_Y_D_JRL::getJrlId).collect(Collectors.toList());
            //额定热负荷和
            double allEdrfh = 0;
            if (!ListUtils.isListEmpty(jrlIds)) {
                allEdrfh = jrlList.stream().filter(c -> jrlIds.contains(c.getEventId())).mapToDouble(DMGC_JRL::getEdrfh).sum();
            }

            //当前站下在运行的机泵ID合集
            List<String> jbIds = new ArrayList<>();
            //当前站下在运行的输油泵ID合集
            List<String> sybIds = yDSybs.stream().map(DMGC_Y_D_SYB::getJbEventId).collect(Collectors.toList());
            //当前站下在运行的掺水泵ID合集
            List<String> csbIds = yDCsbs.stream().map(DMGC_Y_D_CSB::getJbEventId).collect(Collectors.toList());
            jbIds.addAll(sybIds);
            jbIds.addAll(csbIds);
            //电机功率和
            double allDjgl = 0;
            if (!ListUtils.isListEmpty(jbIds)) {
                allDjgl = yJbList.stream().filter(c -> jbIds.contains(c.getEventId())).mapToDouble(DMGC_Y_JB::getDjgl).sum();
            }
            //加热炉与机泵的功率和 = 加热炉总热负荷（MW） * 1000 + 17*总电机功率（KW）
            double sumJrlPower = Calculation.getMultiplicationResult(allEdrfh, 1000);
            double sumJbPower = Calculation.getMultiplicationResult(allDjgl,17);
            double sumPower = Calculation.getPlusResult(sumJrlPower, sumJbPower);
            double weightJrlLevel2 = Calculation.getDivisionResult(sumJrlPower, sumPower);
            double weightJbLevel2 = Calculation.getReduceResult(1, weightJrlLevel2);
            //轴油泵电机功率和
            double sybDjgl = 0;
            if (!ListUtils.isListEmpty(sybIds)) {
                sybDjgl = yJbList.stream().filter(c -> sybIds.contains(c.getEventId())).mapToDouble(DMGC_Y_JB::getDjgl).sum();
            }
            //掺水泵电机功率和
            double csbDjgl = 0;
            if (!ListUtils.isListEmpty(csbIds)) {
                csbDjgl = yJbList.stream().filter(c -> csbIds.contains(c.getEventId())).mapToDouble(DMGC_Y_JB::getDjgl).sum();
            }

            if (!ListUtils.isListEmpty(jrlIds)) {
                List<String> jrlTypes = yDJrls.stream().map(DMGC_Y_D_JRL::getMc).distinct().collect(Collectors.toList());
                Map<String, Double> jrlLevel3Weights = getJrlLevel3Weights(dmgcYZyz.getEventId(), jrlList, jrlTypes, allEdrfh);
                resultList.addAll(getJrlDynamicWeightResults(dmgcYZyz.getEventId(), jrlList, jrlTypes, yDJrls, weightJrlLevel2, jrlLevel3Weights));
            }

            if (!ListUtils.isListEmpty(jbIds)) {
                double weightSybLevel3 = Calculation.getDivisionResult(sybDjgl, allDjgl);
                double weightCsbLevel3 = Calculation.getReduceResult(1, weightSybLevel3);
                if (!ListUtils.isListEmpty(sybIds)) {
                    resultList.addAll(getSybDynamicWeightResults(dmgcYZyz.getEventId(), yJbList, yDSybs, sybDjgl, weightJbLevel2, weightSybLevel3));
                }
                if (!ListUtils.isListEmpty(csbIds)) {
                    resultList.addAll(getCsbDynamicWeightResults(dmgcYZyz.getEventId(), yJbList, yDCsbs, csbDjgl, weightJbLevel2, weightCsbLevel3));
                }
            }
        }
        return resultList;
    }

    public List<DynamicWeightResult> getTszWeightMap(List<DMGC_Y_TSZ_NEW> tszNewList, List<DMGC_JRL> jrlList, List<DMGC_Y_D_JRL> yDJrlList,
                                                     List<DMGC_Y_JB> yJbList, List<DMGC_Y_D_SYB> yDSybList, List<DMGC_Y_D_CSB> yDCsbList) {
        List<DynamicWeightResult> resultList = new ArrayList<>();
        for (DMGC_Y_TSZ_NEW dmgcYTsz : tszNewList) {
            //当前站下的有效加热炉数据
            List<DMGC_Y_D_JRL> yDJrls = yDJrlList.stream().filter(c -> c.getSszkEventId().equals(dmgcYTsz.getEventId())).collect(Collectors.toList());
            //当前站下的有效输油泵数据
            List<DMGC_Y_D_SYB> yDSybs = yDSybList.stream().filter(c -> c.getSszkEventId().equals(dmgcYTsz.getEventId())).collect(Collectors.toList());
            //当前站下的有效掺水泵数据
            List<DMGC_Y_D_CSB> yDCsbs = yDCsbList.stream().filter(c -> c.getSszkEventId().equals(dmgcYTsz.getEventId())).collect(Collectors.toList());
            //当前站下在运行的加热炉ID合集
            List<String> jrlIds = yDJrls.stream().map(DMGC_Y_D_JRL::getJrlId).collect(Collectors.toList());
            //额定热负荷和
            double allEdrfh = 0;
            if (!ListUtils.isListEmpty(jrlIds)) {
                allEdrfh = jrlList.stream().filter(c -> jrlIds.contains(c.getEventId())).mapToDouble(DMGC_JRL::getEdrfh).sum();
            }

            //当前站下在运行的机泵ID合集
            List<String> jbIds = new ArrayList<>();
            //当前站下在运行的输油泵ID合集
            List<String> sybIds = yDSybs.stream().map(DMGC_Y_D_SYB::getJbEventId).collect(Collectors.toList());
            //当前站下在运行的掺水泵ID合集
            List<String> csbIds = yDCsbs.stream().map(DMGC_Y_D_CSB::getJbEventId).collect(Collectors.toList());
            jbIds.addAll(sybIds);
            jbIds.addAll(csbIds);
            //电机功率和
            double allDjgl = 0;
            if (!ListUtils.isListEmpty(jbIds)) {
                allDjgl = yJbList.stream().filter(c -> jbIds.contains(c.getEventId())).mapToDouble(DMGC_Y_JB::getDjgl).sum();
            }
            //加热炉与机泵的功率和=（加热炉总热负荷（MW） * 1000 + 17*总电机功率（KW））
            double sumJrlPower = Calculation.getMultiplicationResult(allEdrfh, 1000);
            double sumJbPower = Calculation.getMultiplicationResult(allDjgl,17);
            double sumPower = Calculation.getPlusResult(sumJrlPower, sumJbPower);
            double weightJrlLevel2 = Calculation.getDivisionResult(sumJrlPower, sumPower);
            double weightJbLevel2 = Calculation.getReduceResult(1, weightJrlLevel2);
            //输油泵电机功率和
            double sybDjgl = 0;
            if (!ListUtils.isListEmpty(sybIds)) {
                sybDjgl = yJbList.stream().filter(c -> sybIds.contains(c.getEventId())).mapToDouble(DMGC_Y_JB::getDjgl).sum();
            }
            //掺水泵电机功率和
            double csbDjgl = 0;
            if (!ListUtils.isListEmpty(csbIds)) {
                csbDjgl = yJbList.stream().filter(c -> csbIds.contains(c.getEventId())).mapToDouble(DMGC_Y_JB::getDjgl).sum();
            }

            if (!ListUtils.isListEmpty(jrlIds)) {
                List<String> jrlTypes = yDJrls.stream().map(DMGC_Y_D_JRL::getMc).distinct().collect(Collectors.toList());
                Map<String, Double> jrlLevel3Weights = getJrlLevel3Weights(dmgcYTsz.getEventId(), jrlList, jrlTypes, allEdrfh);
                resultList.addAll(getJrlDynamicWeightResults(dmgcYTsz.getEventId(), jrlList, jrlTypes, yDJrls, weightJrlLevel2, jrlLevel3Weights));
            }

            if (!ListUtils.isListEmpty(jbIds)) {
                double weightSybLevel3 = Calculation.getDivisionResult(sybDjgl, allDjgl);
                double weightCsbLevel3 = Calculation.getReduceResult(1, weightSybLevel3);
                if (!ListUtils.isListEmpty(sybIds)) {
                    resultList.addAll(getSybDynamicWeightResults(dmgcYTsz.getEventId(), yJbList, yDSybs, sybDjgl, weightJbLevel2, weightSybLevel3));
                }
                if (!ListUtils.isListEmpty(csbIds)) {
                    resultList.addAll(getCsbDynamicWeightResults(dmgcYTsz.getEventId(), yJbList, yDCsbs, csbDjgl, weightJbLevel2, weightCsbLevel3));
                }
            }
        }
        return resultList;
    }

    public List<DynamicWeightResult> getZszWeightMap(List<DMGC_S_ZSZ> zszList, List<DMGC_S_JB> sJbList, List<DMGC_S_D_ZSBRSJ> sDZsbList) {
        List<DynamicWeightResult> resultList = new ArrayList<>();
        for (DMGC_S_ZSZ dmgcSZsz : zszList) {
            //当前站下的有效注水泵数据
            List<DMGC_S_D_ZSBRSJ> sDZsbs = sDZsbList.stream().filter(c -> c.getSszkEventId().equals(dmgcSZsz.getEventId())).collect(Collectors.toList());

            //当前站下在运行的机泵ID合集
            List<String> jbIds = sDZsbs.stream().map(DMGC_S_D_ZSBRSJ::getJbEventId).collect(Collectors.toList());
            //电机功率和
            double allDjgl = 0;
            if (!ListUtils.isListEmpty(jbIds)) {
                allDjgl = sJbList.stream().filter(c -> jbIds.contains(c.getEventId())).mapToDouble(DMGC_S_JB::getDjgl).sum();
                resultList.addAll(getZsbDynamicWeightResults(dmgcSZsz.getEventId(), sJbList, sDZsbs, allDjgl));
            }
        }
        return resultList;
    }

    private Map<String, Double> getJrlLevel3Weights(String zid, List<DMGC_JRL> jrlList, List<String> jrlTypes, double allEdrfh) {
        Map<String, Double> jrlLevel3Weights = new HashMap<>();
        double allJrlWeightLevel3 = 1;
        for (int i = 0; i < jrlTypes.size(); i++) {
            String jrlType = jrlTypes.get(i);
            //当前站下当前类型加热炉额定热负荷
            double sameTypeRfl = jrlList.stream().filter(c -> c.getSszkEventId().equals(zid)
                    && c.getMc().equals(jrlType)).mapToDouble(DMGC_JRL::getEdrfh).sum();
            //3权重=同类加热炉额定热负荷/额定热负荷和
            double weightLevel3 = Calculation.getDivisionResult(sameTypeRfl, allEdrfh);
            if (jrlTypes.size() != 1 && i == jrlTypes.size() - 1) {
                jrlLevel3Weights.put(jrlType, allJrlWeightLevel3);
            } else {
                allJrlWeightLevel3 = Calculation.getReduceResult(allJrlWeightLevel3, weightLevel3);
                jrlLevel3Weights.put(jrlType, weightLevel3);
            }
        }
        return jrlLevel3Weights;
    }

    private List<DynamicWeightResult> getJrlDynamicWeightResults(String zid, List<DMGC_JRL> jrlList, List<String> jrlTypes, List<DMGC_Y_D_JRL> yDJrls,
                                                                 double weightJrlLevel2, Map<String, Double> jrlLevel3Weights) {
        List<DynamicWeightResult> resultList = new ArrayList<>();
        for (String jrlType : jrlTypes) {
            //当前站下当前类型加热炉额定热负荷
            double sameTypeRfl = jrlList.stream().filter(c -> c.getSszkEventId().equals(zid)
                    && c.getMc().equals(jrlType)).mapToDouble(DMGC_JRL::getEdrfh).sum();
            //当前站下当前类型加热炉有效数据
            List<DMGC_Y_D_JRL> sameTypeJrls = yDJrls.stream().filter(c -> c.getMc().equals(jrlType)).collect(Collectors.toList());
            double allWeightLevel4 = 1;
            for (int i = 0; i < sameTypeJrls.size(); i++) {
                DMGC_Y_D_JRL yDJrl = sameTypeJrls.get(i);
                String jrlId = yDJrl.getJrlId();
                //额定热负荷
                double rfh = jrlList.stream().filter(c -> c.getEventId().equals(jrlId)).findFirst().get().getEdrfh();
                //4权重=额定热负荷/同类加热炉额定热负荷
                double weightLevel4 = Calculation.getDivisionResult(rfh, sameTypeRfl);

                DynamicWeightResult dynamicWeightResult;
                if (sameTypeJrls.size() != 1 && i == sameTypeJrls.size() - 1) {
                    dynamicWeightResult = getDynamicWeightResult(zid, jrlId, weightJrlLevel2, jrlLevel3Weights.get(yDJrl.getMc()), allWeightLevel4);
                } else {
                    allWeightLevel4 = Calculation.getReduceResult(allWeightLevel4, weightLevel4);
                    dynamicWeightResult = getDynamicWeightResult(zid, jrlId, weightJrlLevel2, jrlLevel3Weights.get(yDJrl.getMc()), weightLevel4);
                }
                resultList.add(dynamicWeightResult);
            }
        }
        return resultList;
    }

    private List<DynamicWeightResult> getSybDynamicWeightResults(String zid, List<DMGC_Y_JB> yJbList, List<DMGC_Y_D_SYB> yDSybList, double sybDjgl,
                                                                 double weightJbLevel2, double weightSybLevel3) {
        List<DynamicWeightResult> resultList = new ArrayList<>();
        double allWeightLevel4 = 1;
        for (int i = 0; i < yDSybList.size(); i++) {
            DMGC_Y_D_SYB yDSyb = yDSybList.get(i);
            String sybId = yDSyb.getJbEventId();
            //电机机率
            double djgl = yJbList.stream().filter(c -> c.getEventId().equals(sybId)).findFirst().get().getDjgl();
            //4权重=电机机率/输油泵电机功率和
            double weightLevel4 = Calculation.getDivisionResult(djgl, sybDjgl);

            DynamicWeightResult dynamicWeightResult;
            if (yDSybList.size() != 1 && i == yDSybList.size() - 1) {
                dynamicWeightResult = getDynamicWeightResult(zid, sybId, weightJbLevel2, weightSybLevel3, allWeightLevel4);
            } else {
                allWeightLevel4 = Calculation.getReduceResult(allWeightLevel4, weightLevel4);
                dynamicWeightResult = getDynamicWeightResult(zid, sybId, weightJbLevel2, weightSybLevel3, weightLevel4);
            }
            resultList.add(dynamicWeightResult);
        }
        return resultList;
    }

    private List<DynamicWeightResult> getCsbDynamicWeightResults(String zid, List<DMGC_Y_JB> yJbList, List<DMGC_Y_D_CSB> yDCsbList, double csbDjgl,
                                                                 double weightJbLevel2, double weightSybLevel3) {
        List<DynamicWeightResult> resultList = new ArrayList<>();
        double allWeightLevel4 = 1;
        for (int i = 0; i < yDCsbList.size(); i++) {
            DMGC_Y_D_CSB yDSyb = yDCsbList.get(i);
            String csbId = yDSyb.getJbEventId();
            //电机机率
            double djgl = yJbList.stream().filter(c -> c.getEventId().equals(csbId)).findFirst().get().getDjgl();
            //4权重=电机机率/掺水泵电机功率和
            double weightLevel4 = Calculation.getDivisionResult(djgl, csbDjgl);

            DynamicWeightResult dynamicWeightResult;
            if (yDCsbList.size() != 1 && i == yDCsbList.size() - 1) {
                dynamicWeightResult = getDynamicWeightResult(zid, csbId, weightJbLevel2, weightSybLevel3, allWeightLevel4);
            } else {
                allWeightLevel4 = Calculation.getReduceResult(allWeightLevel4, weightLevel4);
                dynamicWeightResult = getDynamicWeightResult(zid, csbId, weightJbLevel2, weightSybLevel3, weightLevel4);
            }
            resultList.add(dynamicWeightResult);
        }
        return resultList;
    }

    private List<DynamicWeightResult> getZsbDynamicWeightResults(String zid, List<DMGC_S_JB> sJbList, List<DMGC_S_D_ZSBRSJ> sDZsbList, double csbDjgl) {
        List<DynamicWeightResult> resultList = new ArrayList<>();
        double allWeightLevel4 = 1;
        for (int i = 0; i < sDZsbList.size(); i++) {
            DMGC_S_D_ZSBRSJ sDZsb = sDZsbList.get(i);
            String zsbId = sDZsb.getJbEventId();
            //电机机率
            double djgl = sJbList.stream().filter(c -> c.getEventId().equals(zsbId)).findFirst().get().getDjgl();
            //4权重=电机机率/掺水泵电机功率和
            double weightLevel4 = Calculation.getDivisionResult(djgl, csbDjgl);

            DynamicWeightResult dynamicWeightResult;
            if (sDZsbList.size() != 1 && i == sDZsbList.size() - 1) {
                dynamicWeightResult = getDynamicWeightResult(zid, zsbId, 1, 1, allWeightLevel4);
            } else {
                allWeightLevel4 = Calculation.getReduceResult(allWeightLevel4, weightLevel4);
                dynamicWeightResult = getDynamicWeightResult(zid, zsbId, 1, 1, weightLevel4);
            }
            resultList.add(dynamicWeightResult);
        }
        return resultList;
    }

    public List<DynamicWeightResult> getZyqWeightMap(List<DMGC_Y_ZYZ> yZyzList, List<DMGC_Y_D_ZYZ> yDZyzList,
                                                     List<DMGC_Y_TSZ_NEW> yTszNewList, List<DMGC_Y_D_TSZ_NEW> yDTszNewList,
                                                     List<DMGC_S_ZSZ> sZszList, List<DMGC_S_D_ZSZRSJ> sDZszrsjList,
                                                     List<DMGC_S_SCLZ> sSclzList, List<DMGC_S_D_SCLZRSJ> sDSclzrsjList) {
        String[] zyqNameArray = Calculation.zyqNames;
        List<DynamicWeightResult> resultList = new ArrayList<>();
        for (int i = 0; i < zyqNameArray.length; i++) {
            String zyqName = zyqNameArray[i];
            List<String> zyzIds = yZyzList.stream().filter(c -> c.getZyqName() != null && c.getZyqName().equals(zyqName)).map(BaseEntity::getEventId).collect(Collectors.toList());
            List<DMGC_Y_D_ZYZ> yDZyzs = yDZyzList.stream().filter(c -> zyzIds.contains(c.getZkEventId())).collect(Collectors.toList());
            double allZyzGasEnergy = 0;
            double allZyzElectricityEneryg = 0;
            double allZyzEnergy = 0;
            if (!ListUtils.isListEmpty(yDZyzs)) {
                allZyzGasEnergy = Calculation.getMultiplicationResult(yDZyzs.stream().filter(c -> zyzIds.contains(c.getZkEventId())).mapToDouble(DMGC_Y_D_ZYZ::getHql).sum(), 1.33);
                allZyzElectricityEneryg = Calculation.getMultiplicationResult(yDZyzs.stream().filter(c -> zyzIds.contains(c.getZkEventId())).mapToDouble(DMGC_Y_D_ZYZ::getZhhdl).sum(), 0.1229);
                allZyzEnergy = Calculation.getPlusResult(allZyzGasEnergy, allZyzElectricityEneryg);
            }

            List<String> tszIds = yTszNewList.stream().filter(c -> c.getZyqName() != null && c.getZyqName().equals(zyqName)).map(BaseEntity::getEventId).collect(Collectors.toList());
            List<DMGC_Y_D_TSZ_NEW> yDTszNews = yDTszNewList.stream().filter(c -> tszIds.contains(c.getZid())).collect(Collectors.toList());
            double allTszGasEnergy = 0;
            double allTszElectricityEneryg = 0;
            double allTszEnergy = 0;
            if (!ListUtils.isListEmpty(yDTszNews)) {
                allTszGasEnergy = Calculation.getMultiplicationResult(yDTszNews.stream().filter(c -> tszIds.contains(c.getZid())).mapToDouble(DMGC_Y_D_TSZ_NEW::getHql).sum(), 1.33);
                allTszElectricityEneryg = Calculation.getMultiplicationResult(yDTszNews.stream().filter(c -> tszIds.contains(c.getZid())).mapToDouble(DMGC_Y_D_TSZ_NEW::getZhhdl).sum(), 0.1229);
                allTszEnergy = Calculation.getPlusResult(allTszGasEnergy, allTszElectricityEneryg);
            }
            double allEnergy = Calculation.getPlusResult(allZyzEnergy, allTszEnergy);
            double zyzWeightLevel2 = 0;
            double tszWeightLevel2 = 0;
            if (allEnergy != 0) {
                zyzWeightLevel2 = Calculation.getDivisionResult(allZyzEnergy, allEnergy);
                tszWeightLevel2 = Calculation.getReduceResult(1, zyzWeightLevel2);
            }

            List<String> zszIds = sZszList.stream().filter(c -> c.getZyqName() != null && c.getZyqName().equals(zyqName)).map(BaseEntity::getEventId).collect(Collectors.toList());
            List<DMGC_S_D_ZSZRSJ> sDZszrsjs = sDZszrsjList.stream().filter(c -> zszIds.contains(c.getZid())).collect(Collectors.toList());
            double allZszElectricityEneryg = 0;
            if (!ListUtils.isListEmpty(sDZszrsjs)) {
                allZszElectricityEneryg = Calculation.getMultiplicationResult(sDZszrsjs.stream().filter(c -> zszIds.contains(c.getZid())).mapToDouble(DMGC_S_D_ZSZRSJ::getZhydl).sum(), 0.1229);
            }

            List<String> sclzIds = sSclzList.stream().filter(c -> c.getZyqName() != null && c.getZyqName().equals(zyqName)).map(BaseEntity::getEventId).collect(Collectors.toList());
            List<DMGC_S_D_SCLZRSJ> sDSclzrsjs = sDSclzrsjList.stream().filter(c -> sclzIds.contains(c.getZkEventId())).collect(Collectors.toList());
            double allSclzElectricityEneryg = 0;
            if (!ListUtils.isListEmpty(sDSclzrsjs)) {
                allSclzElectricityEneryg = Calculation.getMultiplicationResult(sDSclzrsjs.stream().filter(c -> sclzIds.contains(c.getZkEventId())).mapToDouble(DMGC_S_D_SCLZRSJ::getRhdl).sum(), 0.1229);
            }

            double allWeightLevel3 = 1;
            for (int j = 0; j < yDZyzs.size(); j++) {
                DynamicWeightResult dynamicWeightResult;
                DMGC_Y_D_ZYZ yDZyz = yDZyzs.get(j);
                double gasEnergy = Calculation.getMultiplicationResult(yDZyz.getHql(), 1.33);
                double electricityEneryg = Calculation.getMultiplicationResult(yDZyz.getZhhdl(), 0.1229);
                double energy = Calculation.getPlusResult(gasEnergy, electricityEneryg);
                double weightLevel3 = Calculation.getDivisionResult(energy, allZyzEnergy);
                if (yDZyzs.size() != 1 && j == yDZyzs.size() - 1) {
                    dynamicWeightResult = getDynamicWeightResult(zyqName, yDZyz.getZkEventId(), zyzWeightLevel2, allWeightLevel3, 0);
                } else {
                    allWeightLevel3 = Calculation.getReduceResult(allWeightLevel3, weightLevel3);
                    dynamicWeightResult = getDynamicWeightResult(zyqName, yDZyz.getZkEventId(), zyzWeightLevel2, weightLevel3, 0);
                }
                resultList.add(dynamicWeightResult);
            }

            allWeightLevel3 = 1;
            for (int j = 0; j < yDTszNews.size(); j++) {
                DynamicWeightResult dynamicWeightResult;
                DMGC_Y_D_TSZ_NEW yDTszNew = yDTszNews.get(j);
                double gasEnergy = Calculation.getMultiplicationResult(yDTszNew.getHql(), 1.33);
                double electricityEneryg = Calculation.getMultiplicationResult(yDTszNew.getZhhdl(), 0.1229);
                double energy = Calculation.getPlusResult(gasEnergy, electricityEneryg);
                double weightLevel3 = Calculation.getDivisionResult(energy, allTszEnergy);
                if (yDTszNews.size() != 1 && j == yDTszNews.size() - 1) {
                    dynamicWeightResult = getDynamicWeightResult(zyqName, yDTszNew.getZid(), tszWeightLevel2, allWeightLevel3, 0);
                } else {
                    allWeightLevel3 = Calculation.getReduceResult(allWeightLevel3, weightLevel3);
                    dynamicWeightResult = getDynamicWeightResult(zyqName, yDTszNew.getZid(), tszWeightLevel2, weightLevel3, 0);
                }
                resultList.add(dynamicWeightResult);
            }

            allWeightLevel3 = 1;
            for (int j = 0; j < sDZszrsjs.size(); j++) {
                DynamicWeightResult dynamicWeightResult;
                DMGC_S_D_ZSZRSJ yDZszRsj = sDZszrsjs.get(j);
                double electricityEneryg = Calculation.getMultiplicationResult(yDZszRsj.getZhydl(), 0.1229);
                double weightLevel3 = Calculation.getDivisionResult(electricityEneryg, allZszElectricityEneryg);
                if (sDZszrsjs.size() != 1 && j == sDZszrsjs.size() - 1) {
                    dynamicWeightResult = getDynamicWeightResult(zyqName, yDZszRsj.getZid(), 1, allWeightLevel3, 0);
                } else {
                    allWeightLevel3 = Calculation.getReduceResult(allWeightLevel3, weightLevel3);
                    dynamicWeightResult = getDynamicWeightResult(zyqName, yDZszRsj.getZid(), 1, weightLevel3, 0);
                }
                resultList.add(dynamicWeightResult);
            }

            allWeightLevel3 = 1;
            for (int j = 0; j < sDSclzrsjs.size(); j++) {
                DynamicWeightResult dynamicWeightResult;
                DMGC_S_D_SCLZRSJ yDZszRsj = sDSclzrsjs.get(j);
                double electricityEneryg = Calculation.getMultiplicationResult(yDZszRsj.getRhdl(), 0.1229);
                double weightLevel3 = Calculation.getDivisionResult(electricityEneryg, allSclzElectricityEneryg);
                if (sDSclzrsjs.size() != 1 && j == sDSclzrsjs.size() - 1) {
                    dynamicWeightResult = getDynamicWeightResult(zyqName, yDZszRsj.getZkEventId(), 1, allWeightLevel3, 0);
                } else {
                    allWeightLevel3 = Calculation.getReduceResult(allWeightLevel3, weightLevel3);
                    dynamicWeightResult = getDynamicWeightResult(zyqName, yDZszRsj.getZkEventId(), 1, weightLevel3, 0);
                }
                resultList.add(dynamicWeightResult);
            }
        }
        return resultList;
    }

    private DynamicWeightResult getDynamicWeightResult(String zid, String equipId, double weightLevel2, double weightLevel3, double weightLevel4) {
        DynamicWeightResult dynamicWeightResult = new DynamicWeightResult();
        dynamicWeightResult.setStationId(zid);
        dynamicWeightResult.setEquipId(equipId);
        dynamicWeightResult.setLevel2Weight(weightLevel2);
        dynamicWeightResult.setLevel3Weight(weightLevel3);
        dynamicWeightResult.setLevel4Weight(weightLevel4);
        return dynamicWeightResult;
    }
}
