package com.xunjia.pes.bizData.waterTreatment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_SCLJB;
import com.xunjia.pes.bizData.waterTreatment.mapper.DMGC_S_SCLJBMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class DMGC_S_SCLJBService {

    @Autowired
    private DMGC_S_SCLJBMapper mapper;

    public PageVO<DMGC_S_SCLJB> getPageData(DMGC_S_SCLJB example, int page, int size){
        PageVO<DMGC_S_SCLJB> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_S_SCLJB> dataList = mapper.selectList(this.buildQueryWrapper(example));
            PageInfo<DMGC_S_SCLJB> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e){
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    private LambdaQueryWrapper<DMGC_S_SCLJB> buildQueryWrapper(DMGC_S_SCLJB example){
        LambdaQueryWrapper<DMGC_S_SCLJB> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(example.getMc())){
            queryWrapper.like(DMGC_S_SCLJB::getMc, "%" + example.getMc() + "%");
        }
        if (!StringUtils.isEmpty(example.getZnbh())){
            queryWrapper.like(DMGC_S_SCLJB::getZnbh, "%" + example.getZnbh() + "%");
        }
        if (!StringUtils.isEmpty(example.getZch())){
            queryWrapper.eq(DMGC_S_SCLJB::getZch, example.getZch());
        }
        if(StringUtils.isNotEmpty(example.getSszkmc())){
            queryWrapper.like(DMGC_S_SCLJB::getSszkmc,example.getSszkmc());
        }
        return queryWrapper;
    }
}
