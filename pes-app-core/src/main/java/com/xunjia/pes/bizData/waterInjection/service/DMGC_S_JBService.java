package com.xunjia.pes.bizData.waterInjection.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSBRSJ;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_JB;
import com.xunjia.pes.bizData.waterInjection.mapper.DMGC_S_JBMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class DMGC_S_JBService {

    @Autowired
    private DMGC_S_JBMapper mapper;

    public List<DMGC_S_JB> getByEventIds(List<String> eventIds) {
        return mapper.selectList(new LambdaQueryWrapper<DMGC_S_JB>()
                .in(DMGC_S_JB::getEventId, eventIds));
    }

    public PageVO<DMGC_S_JB> getPageData(DMGC_S_JB example, int page, int size) {
        PageVO<DMGC_S_JB> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_S_JB> dataList = mapper.selectList(this.buildQueryWrapper(example));
            PageInfo<DMGC_S_JB> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e) {
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    private LambdaQueryWrapper<DMGC_S_JB> buildQueryWrapper(DMGC_S_JB example) {
        LambdaQueryWrapper<DMGC_S_JB> queryWrapper = new LambdaQueryWrapper<>();
//        if (!StringUtils.isEmpty(example.getMc())) {
//            queryWrapper.like(DMGC_S_JB::getMc, "%" + example.getMc() + "%");
//        }
        queryWrapper.like(DMGC_S_JB::getMc, "%注水泵%");
        if (!StringUtils.isEmpty(example.getBh())) {
            queryWrapper.like(DMGC_S_JB::getBh, "%" + example.getBh() + "%");
        }
        if (!StringUtils.isEmpty(example.getZch())) {
            queryWrapper.eq(DMGC_S_JB::getZch, example.getZch());
        }
        if (!StringUtils.isEmpty(example.getSszkmc())) {
            queryWrapper.like(DMGC_S_JB::getSszkmc, example.getSszkmc());
        }
        return queryWrapper;
    }

    public List<DMGC_S_JB> getByZszId(String zszId){
        LambdaQueryWrapper<DMGC_S_JB> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DMGC_S_JB::getSszkid,zszId);
        return mapper.selectList(wrapper);
    }
}
