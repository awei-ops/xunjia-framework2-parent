package com.xunjia.pes.basicDataManage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.basicDataManage.entity.RectificationMeasures;
import com.xunjia.pes.basicDataManage.mapper.RectificationMeasuresMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class RectificationMeasuresService {
    @Autowired
    private RectificationMeasuresMapper mapper;

    public ResponseData<Boolean> save(RectificationMeasures param) {
        ResponseData<Boolean> resp;
        try {
            if (param.getOriginalDataDate() != null) {
                param.setOriginalDataDate(DateUtils.parse(DateUtils.format(param.getOriginalDataDate(), DateUtils.DATE_PATTERN), DateUtils.DATE_PATTERN));
            }
            mapper.insert(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception e) {
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ResponseData<Boolean> update(RectificationMeasures param) {
        ResponseData<Boolean> resp;
        try {
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
            mapper.deleteBatchIds(ids);
            resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
        } catch (Exception e) {
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public RectificationMeasures getByParam(String queryDate, String equipmentId) {
        try {
            LambdaQueryWrapper<RectificationMeasures> wrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotEmpty(queryDate)) {
                Date date = DateUtils.parse(queryDate, DateUtils.DATE_PATTERN);
                wrapper.eq(RectificationMeasures::getOriginalDataDate, date);
            }
            if (StringUtils.isNotEmpty(equipmentId)) {
                wrapper.eq(RectificationMeasures::getEquipmentId, equipmentId);
            }
            return mapper.selectOne(wrapper);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return null;
    }

    public PageVO<RectificationMeasures> getRectificationMeasures(String startDate, String endDate, String measuresTypeCode, int page, int size) {
        PageVO<RectificationMeasures> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            LambdaQueryWrapper<RectificationMeasures> wrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotEmpty(startDate)) {
                Date date = DateUtils.parse(startDate, DateUtils.DATE_PATTERN);
                wrapper.ge(RectificationMeasures::getOriginalDataDate, date);
            }
            if (StringUtils.isNotEmpty(endDate)) {
                Date date = DateUtils.parse(endDate, DateUtils.DATE_PATTERN);
                wrapper.le(RectificationMeasures::getOriginalDataDate, date);
            }

            if (StringUtils.isNotEmpty(measuresTypeCode)) {
                wrapper.eq(RectificationMeasures::getMeasuresTypeCode, measuresTypeCode);
            }
            List<RectificationMeasures> dataList = mapper.selectList(wrapper).stream().sorted(Comparator.comparing(RectificationMeasures::getOriginalDataDate).reversed()).collect(Collectors.toList());
            PageInfo<RectificationMeasures> pageInfo = PageInfo.of(dataList);
            List<RectificationMeasures> pageList = dataList.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
            pageVO = new PageVO<>(pageInfo.getTotal(), pageList);
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return pageVO;
    }
}
