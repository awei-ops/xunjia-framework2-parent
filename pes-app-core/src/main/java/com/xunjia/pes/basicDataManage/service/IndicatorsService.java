package com.xunjia.pes.basicDataManage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.basicDataManage.entity.IndicatorItem;
import com.xunjia.pes.basicDataManage.entity.IndicatorRelation;
import com.xunjia.pes.basicDataManage.entity.IndicatorTypeInfo;
import com.xunjia.pes.basicDataManage.entity.Indicators;
import com.xunjia.pes.basicDataManage.mapper.IndicatorItemMapper;
import com.xunjia.pes.basicDataManage.mapper.IndicatorRelationMapper;
import com.xunjia.pes.basicDataManage.mapper.IndicatorTypeInfoMapper;
import com.xunjia.pes.basicDataManage.mapper.IndicatorsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class IndicatorsService {
    @Autowired
    private IndicatorsMapper mapper;
    @Autowired
    private IndicatorTypeInfoMapper indicatorTypeInfoMapper;
    @Autowired
    private IndicatorItemMapper indicatorItemMapper;
    @Autowired
    private IndicatorRelationMapper indicatorRelationMapper;

    public ResponseData<Boolean> save(Indicators param) {
        ResponseData<Boolean> resp;
        try {
            LambdaQueryWrapper<IndicatorTypeInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(IndicatorTypeInfo::getTypeCode,param.getTypeCode());
            wrapper.eq(IndicatorTypeInfo::getLevelCode,param.getLevelCode());
            IndicatorTypeInfo indicatorTypeInfo = indicatorTypeInfoMapper.selectList(wrapper).get(0);
            param.setTypeName(indicatorTypeInfo.getTypeName());
            param.setLevelName(indicatorTypeInfo.getLevelName());
            param.setDeleteFlag(0);
            mapper.insert(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception e) {
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ResponseData<Boolean> update(Indicators param) {
        ResponseData<Boolean> resp;
        try {
            LambdaQueryWrapper<IndicatorTypeInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(IndicatorTypeInfo::getTypeCode,param.getTypeCode());
            wrapper.eq(IndicatorTypeInfo::getLevelCode,param.getLevelCode());
            IndicatorTypeInfo indicatorTypeInfo = indicatorTypeInfoMapper.selectList(wrapper).get(0);
            param.setTypeName(indicatorTypeInfo.getTypeName());
            param.setLevelName(indicatorTypeInfo.getLevelName());
            mapper.updateById(param);
            resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);

        } catch (Exception e) {
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ResponseData<Boolean> deleteByIds(List<String> ids) {
        ResponseData<Boolean> resp;
        try {
            LambdaQueryWrapper<Indicators> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Indicators::getId, ids);
            List<Indicators> deleteEntities = mapper.selectList(wrapper);
            for (Indicators param : deleteEntities) {
                param.setDeleteFlag(1);
                mapper.updateById(param);
            }
            resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
        } catch (Exception e) {
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public Indicators findById(String id) {
        return mapper.selectById(id);
    }

    public List<Indicators> findAll() {
        LambdaQueryWrapper<Indicators> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Indicators::getTypeName).orderByAsc(Indicators::getLevelCode);
        return mapper.selectList(wrapper);
    }

    public PageVO<Indicators> getPageData(String typeCode, String levelCode, String itemCode, int page, int size) {
        PageVO<Indicators> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            LambdaQueryWrapper<Indicators> wrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotEmpty(typeCode)) {
                wrapper.eq(Indicators::getTypeCode, typeCode);
            }
            if (StringUtils.isNotEmpty(levelCode)) {
                wrapper.eq(Indicators::getLevelCode, levelCode);
            }
            if (StringUtils.isNotEmpty(itemCode)) {
                wrapper.eq(Indicators::getItemCode, itemCode);
            }
            wrapper.eq(Indicators::getDeleteFlag, 0);
            wrapper.orderByAsc(Indicators::getLevelCode);
            List<Indicators> dataList = mapper.selectList(wrapper);
            PageInfo<Indicators> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e) {
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    private LambdaQueryWrapper<Indicators> buildQueryWrapper(Indicators example) {
        LambdaQueryWrapper<Indicators> queryWrapper = new LambdaQueryWrapper<>();
        if (example != null) {
            if (StringUtils.isNotEmpty(example.getLevelCode())) {
                queryWrapper.eq(Indicators::getLevelCode, example.getLevelCode());
            }
            if (StringUtils.isNotEmpty(example.getTypeCode())) {
                queryWrapper.eq(Indicators::getTypeCode, example.getTypeCode());
            }
            if (StringUtils.isNotEmpty(example.getItemCode())) {
                queryWrapper.eq(Indicators::getItemCode, example.getItemCode());
            }
            if (example.getDeleteFlag() != null) {
                queryWrapper.eq(Indicators::getDeleteFlag, example.getDeleteFlag());
            }
        }
        queryWrapper.orderByAsc(Indicators::getLevelName).orderByAsc(Indicators::getItemName);
        return queryWrapper;
    }

    private List<IndicatorRelation> getRelationByTypeCode(String typeCode) {
        LambdaQueryWrapper<IndicatorTypeInfo> infoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        infoLambdaQueryWrapper.eq(IndicatorTypeInfo::getTypeCode, typeCode);
        List<IndicatorTypeInfo> indicatorTypeInfoList = indicatorTypeInfoMapper.selectList(infoLambdaQueryWrapper);

        List<Integer> typeIds = indicatorTypeInfoList.stream().map(c -> c.getId()).collect(Collectors.toList());
        LambdaQueryWrapper<IndicatorRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(IndicatorRelation::getIndicatorTypeInfoId, typeIds);
        List<IndicatorRelation> result = indicatorRelationMapper.selectList(wrapper);

        List<Integer> itemIds = result.stream().map(c -> c.getIndicatorItemId()).distinct().collect(Collectors.toList());
        LambdaQueryWrapper<IndicatorItem> itemLambdaQueryWrapper = new LambdaQueryWrapper<>();
        itemLambdaQueryWrapper.in(IndicatorItem::getId, itemIds);
        List<IndicatorItem> indicatorItemList = indicatorItemMapper.selectList(itemLambdaQueryWrapper);

        for (IndicatorRelation param : result) {
            param.setIndicatorTypeInfo(indicatorTypeInfoList.stream().filter(c -> c.getId().equals(param.getIndicatorTypeInfoId())).findFirst().get());
            param.setIndicatorItem(indicatorItemList.stream().filter(c -> c.getId().equals(param.getIndicatorItemId())).findFirst().get());
        }
        return result;
    }

    public List<IndicatorItem> getByTypeCodeAndLevelCode(String typeCode, String levelCode) {
        List<IndicatorRelation> relationList = getRelationByTypeCode(typeCode);
        List<IndicatorItem> result = relationList.stream().filter(c -> c.getIndicatorTypeInfo().getLevelCode().equals(levelCode))
                .map(c -> c.getIndicatorItem()).collect(Collectors.toList());
        return result;
    }
}
