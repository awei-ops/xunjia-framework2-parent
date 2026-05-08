package com.xunjia.pes.sync.oil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.BaseEntity;
import com.xunjia.pes.bizData.oil.entity.DMGC_JRL;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_D_JRL;
import com.xunjia.pes.bizData.oil.mapper.DMGC_JRLMapper;
import com.xunjia.pes.bizData.oil.mapper.DMGC_Y_D_JRLMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DMGC_Y_D_JRL_Sync {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DMGC_Y_D_JRLMapper mapper;

    @Autowired
    private DMGC_JRLMapper jrlMapper;

    public void sync(Date startTime, Date endTime) throws IllegalAccessException {
        for (Date start = startTime; start.getTime() <= endTime.getTime(); start = DateUtils.addDate(start, 1)) {
            List<DMGC_Y_D_JRL> records = getRecordsInDb(start);
            List<String> ids = records.stream().map(BaseEntity::getEventId).collect(Collectors.toList());
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < ids.size(); i++) {
                if (i != ids.size() - 1) {
                    builder.append("'").append(ids.get(i)).append("'").append(",");
                } else {
                    builder.append("'").append(ids.get(i)).append("'");
                }
            }
            String temp = builder.toString();
            String readDataSql;
            // 读取A5数据
            if (StringUtil.isNotBlank(temp)) {
                readDataSql = "select * from A5ADMIN.DMGC_Y_D_JRL where RQ=to_date('" + DateUtils.format(start, DateUtils.DATE_TIME_PATTERN) + "', 'yyyy-MM-dd HH24:mi:ss') AND EVENTID not in (" + temp + ")";
            } else {
                readDataSql = "select * from A5ADMIN.DMGC_Y_D_JRL where RQ=to_date('" + DateUtils.format(start, DateUtils.DATE_TIME_PATTERN) + "', 'yyyy-MM-dd HH24:mi:ss')";
            }
            List<Map<String, Object>> readDataList = jdbcTemplate.queryForList(readDataSql);
            // 读取readDataList，构造实体
            List<DMGC_Y_D_JRL> entities = this.convertToEntity(readDataList);
            // 实体批量写入
            if (!entities.isEmpty()) {
                setOther(entities);
                for (DMGC_Y_D_JRL entity : entities) {
                    mapper.insert(entity);
                }
            }
        }
    }

    private void setOther( List<DMGC_Y_D_JRL> entities){
        LambdaQueryWrapper<DMGC_JRL> queryWrapper = new LambdaQueryWrapper<>();
        List<DMGC_JRL> jrlList = jrlMapper.selectList(queryWrapper);
        for (DMGC_Y_D_JRL entity : entities) {
            Optional<DMGC_JRL> jrl = jrlList.stream().filter(c->c.getEventId().equals(entity.getJrlId())).findFirst();
            jrl.ifPresent(c->{
                entity.setZnbh(jrl.get().getZnbh());
            });
        }
    }

    private List<DMGC_Y_D_JRL> convertToEntity(List<Map<String, Object>> dataList) throws IllegalAccessException {
        List<DMGC_Y_D_JRL> entities = new ArrayList<>();
        Class clazz = DMGC_Y_D_JRL.class;
        List<Field> fields = new ArrayList<>(); // 保存属性对象数组到列表
        Field[] declaredFields = clazz.getDeclaredFields();
        Collections.addAll(fields, declaredFields);// 获取字节码对象的属性对象数组
        clazz = clazz.getSuperclass();  // 获得直接父类的字节码对象
        declaredFields = clazz.getDeclaredFields();
        Collections.addAll(fields, declaredFields);
        if (dataList.size() > 0) {
            for (Map<String, Object> data : dataList) {
                DMGC_Y_D_JRL newData = new DMGC_Y_D_JRL();
                for (Field field : fields) {
                    String entityKey = field.getName();
                    String key;
                    switch (entityKey) {
                        case "sszkEventId":
                            key = "SSZK_EVENTID";
                            break;
                        case "jrlId":
                            key = "JRLID";
                            break;
                        case "eventId":
                            key = "EVENTID";
                            break;
                        default:
                            key = StringUtils.humpToUnderline(field.getName()).toUpperCase();
                            break;
                    }
                    if (data.containsKey(key) && data.get(key) != null) {
                        field.setAccessible(true);
                        String type = field.getGenericType().toString();
                        switch (type) {
                            case "class java.lang.Long":
                                field.set(newData, Long.parseLong(data.get(key).toString()));
                                break;
                            case "class java.lang.Integer":
                                field.set(newData, Integer.parseInt(data.get(key).toString()));
                                break;
                            case "class java.lang.Double":
                                field.set(newData, Double.parseDouble(data.get(key).toString()));
                                break;
                            default:
                                field.set(newData, data.get(key));
                                break;
                        }
                    }
                }
                entities.add(newData);
            }
        }
        return entities;
    }

    private List<DMGC_Y_D_JRL> getRecordsInDb(Date rq) {
        LambdaQueryWrapper<DMGC_Y_D_JRL> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DMGC_Y_D_JRL::getRq, rq);
        List<DMGC_Y_D_JRL> result = mapper.selectList(wrapper);
        return result;
    }
}
