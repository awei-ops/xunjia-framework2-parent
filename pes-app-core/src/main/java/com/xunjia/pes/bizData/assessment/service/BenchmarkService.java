package com.xunjia.pes.bizData.assessment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.assessment.entity.Benchmark;
import com.xunjia.pes.bizData.assessment.mapper.BenchmarkMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Transactional
@Slf4j
public class BenchmarkService {
    @Autowired
    private BenchmarkMapper mapper;

    public ResponseData<Boolean> save(Benchmark param) {
        ResponseData<Boolean> resp;
        try {
            if (StringUtils.isNotEmpty(param.getCode())) {
                LambdaQueryWrapper<Benchmark> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Benchmark::getCode, param.getCode());
                List<Benchmark> temp = mapper.selectList(wrapper);
                if (temp.size() > 0) {
                    resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_EXIST);
                    return resp;
                }
            } else {
                resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_NULL);
                return resp;
            }
            param.setDeleteFlag(0);
            mapper.insert(param);
            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception e) {
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ResponseData<Boolean> update(Benchmark param, String originalCode) {
        ResponseData<Boolean> resp;
        try {
            if (StringUtils.isNotEmpty(param.getCode())) {
                if (param.getCode().equals(originalCode)) {
                    mapper.updateById(param);
                    resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
                } else {
                    LambdaQueryWrapper<Benchmark> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Benchmark::getCode, param.getCode());
                    List<Benchmark> temp = mapper.selectList(wrapper);
                    if (temp.size() == 0) {
                        mapper.updateById(param);
                        resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
                    } else {
                        resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_EXIST);
                    }
                }
            } else {
                resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_NULL);
            }
        } catch (Exception e) {
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ResponseData<Boolean> deleteByIds(List<String> ids) {
        ResponseData<Boolean> resp;
        try {
            LambdaQueryWrapper<Benchmark> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Benchmark::getId, ids);
            List<Benchmark> deleteEntities = mapper.selectList(wrapper);
            for (Benchmark param : deleteEntities) {
                param.setDeleteFlag(1);
                mapper.updateById(param);
            }
            resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
        } catch (Exception e) {
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public Benchmark findById(String id) {
        return mapper.selectById(id);
    }

    public List<Benchmark> findAll() {
        LambdaQueryWrapper<Benchmark> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Benchmark::getCode).orderByAsc(Benchmark::getName);
        return mapper.selectList(wrapper);
    }

    public PageVO<Benchmark> getPageData(Benchmark example, int page, int size) {
        PageVO<Benchmark> pageVO = null;
        try {
            PageMethod.startPage(page, size);
            List<Benchmark> dataList = mapper.selectList(this.buildQueryWrapper(example));
            PageInfo<Benchmark> pageInfo = PageInfo.of(dataList);
            pageVO = new PageVO<>(pageInfo.getTotal(), dataList);
        } catch (Exception e) {
            pageVO = new PageVO<>();
        }
        return pageVO;
    }

    public Benchmark getByCode(String code){
        AtomicReference<Benchmark> result = new AtomicReference<>(new Benchmark());
        LambdaQueryWrapper<Benchmark> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Benchmark::getCode,code);
        Optional<Benchmark> optional = mapper.selectList(wrapper).stream().findFirst();
        optional.ifPresent(c-> result.set(c));
        return result.get();
    }

    public List<Benchmark> getBenchmarksByType(String type){
        Benchmark example = new Benchmark();
        example.setType(type);
        List<Benchmark> dataList = mapper.selectList(this.buildQueryWrapper(example));
        return dataList;
    }

    private LambdaQueryWrapper<Benchmark> buildQueryWrapper(Benchmark example) {
        LambdaQueryWrapper<Benchmark> queryWrapper = new LambdaQueryWrapper<>();
        if (example != null) {

            if (StringUtils.isNotEmpty(example.getName())) {
                queryWrapper.like(Benchmark::getName, example.getName());
            }
            if (StringUtils.isNotEmpty(example.getCode())) {
                queryWrapper.eq(Benchmark::getCode, example.getCode());
            }
            if (StringUtils.isNotEmpty(example.getType())) {
                queryWrapper.eq(Benchmark::getType, example.getType());
            }
            if (example.getDeleteFlag() != null) {
                queryWrapper.eq(Benchmark::getDeleteFlag, example.getDeleteFlag());
            }
        }
        queryWrapper.orderByAsc(Benchmark::getCode).orderByAsc(Benchmark::getName);
        return queryWrapper;
    }
}
