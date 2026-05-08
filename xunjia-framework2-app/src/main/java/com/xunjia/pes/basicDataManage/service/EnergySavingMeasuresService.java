package com.xunjia.pes.basicDataManage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.basicDataManage.entity.EnergySavingMeasures;
import com.xunjia.pes.basicDataManage.mapper.EnergySavingMeasuresMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class EnergySavingMeasuresService {
    @Autowired
    private EnergySavingMeasuresMapper mapper;

    public List<EnergySavingMeasures> getEnergySavingMeasuresByTypeCode(String typeCode){
        LambdaQueryWrapper<EnergySavingMeasures> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(typeCode)){
            wrapper.eq(EnergySavingMeasures::getMeasuresTypeCode,typeCode);
        }
        List<EnergySavingMeasures> result = mapper.selectList(wrapper).stream().sorted(Comparator.comparing(EnergySavingMeasures::getOrderNo)).collect(Collectors.toList());
        return result;
    }
}
