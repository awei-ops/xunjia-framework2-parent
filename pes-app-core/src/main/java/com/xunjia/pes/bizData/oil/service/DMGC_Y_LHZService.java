package com.xunjia.pes.bizData.oil.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_LHZ;
import com.xunjia.pes.bizData.oil.mapper.DMGC_Y_LHZMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class DMGC_Y_LHZService {

    @Autowired
    private DMGC_Y_LHZMapper mapper;

    public PageVO<DMGC_Y_LHZ> getPageData(DMGC_Y_LHZ example, int page, int size){
        PageVO<DMGC_Y_LHZ> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_Y_LHZ> dataList = mapper.selectList(this.buildQueryWrapper(example));
            PageInfo<DMGC_Y_LHZ> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e){
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    private LambdaQueryWrapper<DMGC_Y_LHZ> buildQueryWrapper(DMGC_Y_LHZ example){
        LambdaQueryWrapper<DMGC_Y_LHZ> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(example.getMc())){
            queryWrapper.like(DMGC_Y_LHZ::getMc, "%" + example.getMc() + "%");
        }
        if (!StringUtils.isEmpty(example.getCode())){
            queryWrapper.like(DMGC_Y_LHZ::getCode, "%" + example.getCode() + "%");
        }
        return queryWrapper;
    }
}
