package com.xunjia.pes.bizData.oil.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_TSZ_NEW;
import com.xunjia.pes.bizData.oil.mapper.DMGC_Y_TSZ_NEWMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DMGC_Y_TSZ_NEWService {

    @Autowired
    private DMGC_Y_TSZ_NEWMapper mapper;

    public PageVO<DMGC_Y_TSZ_NEW> getPageData(DMGC_Y_TSZ_NEW example, int page, int size) {
        PageVO<DMGC_Y_TSZ_NEW> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<DMGC_Y_TSZ_NEW> dataList = mapper.selectList(this.buildQueryWrapper(example));
            PageInfo<DMGC_Y_TSZ_NEW> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e) {
            log.error(e.getMessage(), page, size);
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    private QueryWrapper<DMGC_Y_TSZ_NEW> buildQueryWrapper(DMGC_Y_TSZ_NEW example) {
        QueryWrapper<DMGC_Y_TSZ_NEW> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(example.getMc())) {
            queryWrapper.like("mc", "%" + example.getMc() + "%");
        }
        if (!StringUtils.isEmpty(example.getCode())) {
            queryWrapper.like("code", "%" + example.getCode() + "%");
        }
        queryWrapper.orderBy(true, true, "convert( mc using gbk)");
        return queryWrapper;
    }

    public List<DMGC_Y_TSZ_NEW> getByIds(List<String> ids) {
        LambdaQueryWrapper<DMGC_Y_TSZ_NEW> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DMGC_Y_TSZ_NEW::getEventId, ids);
        return mapper.selectList(wrapper);
    }

    public List<DMGC_Y_TSZ_NEW> getAll() {
        DMGC_Y_TSZ_NEW example = new DMGC_Y_TSZ_NEW();
        return mapper.selectList(this.buildQueryWrapper(example));
    }

    public List<String> getZYQNameList(List<String> zidList) {
        LambdaQueryWrapper<DMGC_Y_TSZ_NEW> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DMGC_Y_TSZ_NEW::getEventId, zidList);
        List<DMGC_Y_TSZ_NEW> temp = mapper.selectList(queryWrapper);
        List<String> zyqNameList = temp.stream().filter(c -> StringUtils.isNotEmpty(c.getZyqName())).map(c -> c.getZyqName()).distinct().collect(Collectors.toList());
        return zyqNameList;
    }
}
