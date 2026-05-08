package com.xunjia.pes.bizData.waterTreatment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_SCLZ;
import com.xunjia.pes.bizData.waterTreatment.mapper.DMGC_S_SCLZMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DMGC_S_SCLZService {

    @Autowired
    private DMGC_S_SCLZMapper mapper;

    public PageVO<DMGC_S_SCLZ> getPageData(DMGC_S_SCLZ example, int page, int size){
        PageVO<DMGC_S_SCLZ> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_S_SCLZ> dataList = mapper.selectList(this.buildQueryWrapper(example));
            PageInfo<DMGC_S_SCLZ> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e){
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    private QueryWrapper<DMGC_S_SCLZ> buildQueryWrapper(DMGC_S_SCLZ example){
        QueryWrapper<DMGC_S_SCLZ> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(example.getMc())){
            queryWrapper.like("mc", "%" + example.getMc() + "%");
        }
        if (!StringUtils.isEmpty(example.getCode())){
            queryWrapper.like("code", "%" + example.getCode() + "%");
        }
        queryWrapper.orderBy(true, true, "convert( mc using gbk)");
        return queryWrapper;
    }

    List<DMGC_S_SCLZ> getByEventIds(List<String> eventIds){
        List<DMGC_S_SCLZ> dataList = mapper.selectList(new LambdaQueryWrapper<DMGC_S_SCLZ>()
                .in(DMGC_S_SCLZ::getEventId, eventIds));
        return dataList;
    }

    public List<DMGC_S_SCLZ> getAll(){
        DMGC_S_SCLZ example = new DMGC_S_SCLZ();
        List<DMGC_S_SCLZ> dataList = mapper.selectList(this.buildQueryWrapper(example));
        return dataList;
    }

    public List<String> getZYQNameList(List<String> zidList){
        LambdaQueryWrapper<DMGC_S_SCLZ> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DMGC_S_SCLZ::getEventId,zidList);
        List<DMGC_S_SCLZ> temp = mapper.selectList(queryWrapper);
        List<String> zyqNameList = temp.stream().filter(c->StringUtils.isNotEmpty(c.getZyqName())).map(c->c.getZyqName()).distinct().collect(Collectors.toList());
        return zyqNameList;
    }
}
