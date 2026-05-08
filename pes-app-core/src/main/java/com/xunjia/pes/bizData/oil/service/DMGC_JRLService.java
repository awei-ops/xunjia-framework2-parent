package com.xunjia.pes.bizData.oil.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.oil.entity.DMGC_JRL;
import com.xunjia.pes.bizData.oil.mapper.DMGC_JRLMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class DMGC_JRLService {

    @Autowired
    private DMGC_JRLMapper mapper;

    public PageVO<DMGC_JRL> getPageData(DMGC_JRL example, int page, int size){
        PageVO<DMGC_JRL> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_JRL> dataList = mapper.selectList(this.buildQueryWrapper(example));
            PageInfo<DMGC_JRL> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e){
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    private LambdaQueryWrapper<DMGC_JRL> buildQueryWrapper(DMGC_JRL example){
        LambdaQueryWrapper<DMGC_JRL> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(example.getMc())){
            queryWrapper.like(DMGC_JRL::getMc, "%" + example.getMc() + "%");
        }
        if (!StringUtils.isEmpty(example.getZnbh())){
            queryWrapper.like(DMGC_JRL::getZnbh, "%" + example.getZnbh() + "%");
        }
        if (!StringUtils.isEmpty(example.getGgxh())){
            queryWrapper.like(DMGC_JRL::getGgxh, "%" + example.getGgxh() + "%");
        }
        if (!StringUtils.isEmpty(example.getZch())){
            queryWrapper.eq(DMGC_JRL::getZch, example.getZch());
        }
        if(StringUtils.isNotEmpty(example.getSszm())){
            queryWrapper.like(DMGC_JRL::getSszm,example.getSszm());
        }
        return queryWrapper;
    }

    public List<DMGC_JRL> getByIds(List<String> jrlIds){
        if(ListUtils.isListEmpty(jrlIds)){
            return new ArrayList<>();
        }
        LambdaQueryWrapper<DMGC_JRL> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DMGC_JRL::getEventId,jrlIds);
        return mapper.selectList(wrapper);
    }

    public List<DMGC_JRL> getByZid(List<String> zids){
        LambdaQueryWrapper<DMGC_JRL> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DMGC_JRL::getSszkEventId,zids);
        return mapper.selectList(wrapper);
    }

    public List<DMGC_JRL> getAll(){
        LambdaQueryWrapper<DMGC_JRL> wrapper = new LambdaQueryWrapper<>();
        return mapper.selectList(wrapper);
    }
}
