package com.xunjia.pes.basicDataManage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.basicDataManage.entity.EnergySavingDetail;
import com.xunjia.pes.basicDataManage.mapper.EnergySavingDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class EnergySavingDetailService {
    @Autowired
    private EnergySavingDetailMapper mapper;

    public List<EnergySavingDetail> getByClassificationCode(String classificationCode){
        LambdaQueryWrapper<EnergySavingDetail> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(classificationCode)){
            wrapper.eq(EnergySavingDetail::getClassificationCode,classificationCode);
        }
        List<EnergySavingDetail> result = mapper.selectList(wrapper).stream().sorted(Comparator.comparing(EnergySavingDetail::getOrderNo)).collect(Collectors.toList());
        return result;
    }

    public ResponseData<Boolean> save(EnergySavingDetail param) {
        ResponseData<Boolean> resp;
        try {
            mapper.insert(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception e) {
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ResponseData<Boolean> update(EnergySavingDetail param) {
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

    public EnergySavingDetail findById(String id) {
        return mapper.selectById(id);
    }

    public PageVO<EnergySavingDetail> getPageData(String measuresTypeCode,String classificationCode, int page, int rows) {
        PageVO<EnergySavingDetail> pageVO = null;
        try {
            PageMethod.startPage(page, rows);

            LambdaQueryWrapper<EnergySavingDetail> wrapper = new LambdaQueryWrapper<>();
            if(StringUtils.isNotEmpty(measuresTypeCode)) {
                wrapper.eq(EnergySavingDetail::getMeasuresTypeCode, measuresTypeCode);
            }
            if(StringUtils.isNotEmpty(classificationCode)) {
                wrapper.eq(EnergySavingDetail::getClassificationCode, classificationCode);
            }
            List<EnergySavingDetail> dataList = mapper.selectList(wrapper);
            PageInfo<EnergySavingDetail> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e) {
            pageVO = new PageVO<>();
        }
        return pageVO;
    }
}
