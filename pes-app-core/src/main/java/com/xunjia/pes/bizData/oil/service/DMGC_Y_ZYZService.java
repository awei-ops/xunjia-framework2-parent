package com.xunjia.pes.bizData.oil.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_ZYZ;
import com.xunjia.pes.bizData.oil.mapper.DMGC_Y_ZYZMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DMGC_Y_ZYZService {

    @Autowired
    private DMGC_Y_ZYZMapper mapper;

    public PageVO<DMGC_Y_ZYZ> getPageData(DMGC_Y_ZYZ example, int page, int size) {
        PageVO<DMGC_Y_ZYZ> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_Y_ZYZ> dataList = mapper.selectList(this.buildQueryWrapper(example));
            PageInfo<DMGC_Y_ZYZ> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e) {
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    private QueryWrapper<DMGC_Y_ZYZ> buildQueryWrapper(DMGC_Y_ZYZ example) {
        QueryWrapper<DMGC_Y_ZYZ> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(example.getMc())) {
            queryWrapper.like("mc", "%" + example.getMc() + "%");
        }
        if (!StringUtils.isEmpty(example.getCode())) {
            queryWrapper.like("code", "%" + example.getCode() + "%");
        }
        queryWrapper.orderBy(true, true, "convert( mc using gbk)");
        return queryWrapper;
    }

    public List<DMGC_Y_ZYZ> getAll() {
        DMGC_Y_ZYZ example = new DMGC_Y_ZYZ();
        return mapper.selectList(this.buildQueryWrapper(example));
    }

    public List<String> getZYQNameList(List<String> zidList) {
        LambdaQueryWrapper<DMGC_Y_ZYZ> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DMGC_Y_ZYZ::getEventId, zidList);
        List<DMGC_Y_ZYZ> temp = mapper.selectList(queryWrapper);
        List<String> zyqNameList = temp.stream().filter(c -> StringUtils.isNotEmpty(c.getZyqName())).map(c -> c.getZyqName()).distinct().collect(Collectors.toList());
        return zyqNameList;
    }
}
