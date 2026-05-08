package com.xunjia.pes.bizData.oil.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_JB;
import com.xunjia.pes.bizData.oil.mapper.DMGC_Y_JBMapper;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_JB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class DMGC_Y_JBService {

    @Autowired
    private DMGC_Y_JBMapper mapper;

    public PageVO<DMGC_Y_JB> getPageData(DMGC_Y_JB example, int page, int size) {
        PageVO<DMGC_Y_JB> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_Y_JB> dataList = mapper.selectList(this.buildQueryWrapper(example));
            PageInfo<DMGC_Y_JB> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e) {
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    private LambdaQueryWrapper<DMGC_Y_JB> buildQueryWrapper(DMGC_Y_JB example) {
        LambdaQueryWrapper<DMGC_Y_JB> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(example.getEventId())) {
            queryWrapper.eq(DMGC_Y_JB::getEventId, example.getEventId());
        }
        if (!StringUtils.isEmpty(example.getZnbh())) {
            queryWrapper.like(DMGC_Y_JB::getZnbh, "%" + example.getZnbh() + "%");
        }
        if (!StringUtils.isEmpty(example.getZch())) {
            queryWrapper.eq(DMGC_Y_JB::getZch, example.getZch());
        }
        if (!StringUtils.isEmpty(example.getSszkName())) {
            queryWrapper.eq(DMGC_Y_JB::getSszkName, example.getSszkName());
        }
        queryWrapper.and(wrapper -> {
            wrapper.like(DMGC_Y_JB::getMc, "%外输泵%");
            wrapper.or().like(DMGC_Y_JB::getMc, "%掺水泵%");
        });
        return queryWrapper;
    }

    public List<DMGC_Y_JB> getAll() {
        LambdaQueryWrapper<DMGC_Y_JB> wrapper = new LambdaQueryWrapper<>();
        return mapper.selectList(wrapper);
    }

    public List<DMGC_Y_JB> getByEventIds(List<String> jbEventIds) {
        return mapper.selectList(new LambdaQueryWrapper<DMGC_Y_JB>()
                .in(DMGC_Y_JB::getEventId, jbEventIds));
    }

    public List<DMGC_Y_JB> getByName(String mc) {
        LambdaQueryWrapper<DMGC_Y_JB> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DMGC_Y_JB::getMc, mc);
        return mapper.selectList(wrapper);
    }

    public List<DMGC_Y_JB> getByZid(List<String> zids) {
        LambdaQueryWrapper<DMGC_Y_JB> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DMGC_Y_JB::getSszkEventId, zids);
        return mapper.selectList(wrapper);
    }
}
