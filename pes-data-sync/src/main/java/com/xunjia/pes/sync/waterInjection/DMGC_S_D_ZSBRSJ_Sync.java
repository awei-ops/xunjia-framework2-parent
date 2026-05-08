package com.xunjia.pes.sync.waterInjection;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.BaseEntity;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_D_ZSBRSJ;
import com.xunjia.pes.bizData.waterInjection.entity.DMGC_S_ZSZ;
import com.xunjia.pes.bizData.waterInjection.mapper.DMGC_S_D_ZSBRSJMapper;
import com.xunjia.pes.bizData.waterInjection.mapper.DMGC_S_ZSZMapper;
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
public class DMGC_S_D_ZSBRSJ_Sync {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DMGC_S_D_ZSBRSJMapper mapper;

    @Autowired
    private DMGC_S_ZSZMapper zszMapper;

    public void sync(Date startTime, Date endTime) throws IllegalAccessException {
        for (Date start = startTime; start.getTime() <= endTime.getTime(); start = DateUtils.addDate(start, 1)) {
            List<DMGC_S_D_ZSBRSJ> records = getRecordsInDb(start);
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
                readDataSql = "select * from A5ADMIN.DMGC_S_D_ZSBRSJ where RQ=to_date('" + DateUtils.format(start,DateUtils.DATE_TIME_PATTERN) + "', 'yyyy-MM-dd HH24:mi:ss') AND EVENTID not in (" + temp + ")";
            } else {
                readDataSql = "select * from A5ADMIN.DMGC_S_D_ZSBRSJ where RQ=to_date('" + DateUtils.format(start,DateUtils.DATE_TIME_PATTERN)  + "', 'yyyy-MM-dd HH24:mi:ss')";
            }
            List<Map<String, Object>> readDataList = jdbcTemplate.queryForList(readDataSql);
            // 读取readDataList，构造实体
            List<DMGC_S_D_ZSBRSJ> entities = this.convertToEntity(readDataList);
            // 实体批量写入
            if (!entities.isEmpty()) {
                for (DMGC_S_D_ZSBRSJ entity : entities) {
                    mapper.insert(entity);
                }
            }
        }
    }

    private List<DMGC_S_D_ZSBRSJ> convertToEntity(List<Map<String, Object>> dataList) throws IllegalAccessException {
        List<DMGC_S_D_ZSBRSJ> entities = new ArrayList<>();
        LambdaQueryWrapper<DMGC_S_ZSZ> wrapper = new LambdaQueryWrapper<>();
        List<DMGC_S_ZSZ> zszs = zszMapper.selectList(wrapper);
        Class clazz = DMGC_S_D_ZSBRSJ.class;
        List<Field> fields = new ArrayList<>(); // 保存属性对象数组到列表
        Field[] declaredFields = clazz.getDeclaredFields();
        Collections.addAll(fields,declaredFields);// 获取字节码对象的属性对象数组
        clazz = clazz.getSuperclass();  // 获得直接父类的字节码对象
        declaredFields = clazz.getDeclaredFields();
        Collections.addAll(fields,declaredFields);
        if (dataList.size() > 0) {
            for (Map<String, Object> data : dataList) {
                DMGC_S_D_ZSBRSJ newData = new DMGC_S_D_ZSBRSJ();
                for (Field field : fields) {
                    field.setAccessible(true);
                    String entityKey = field.getName();
                    String key;
                    switch (entityKey){
                        case "jbEventId":
                            key = "JB_EVENTID";
                            break;
                        case "eventId":
                            key = "EVENTID";
                            break;
                        case "sszkEventId":
                            key="SSZK_EVENTID";
                            break;
                        case "zszName":
                            key = StringUtils.humpToUnderline(field.getName()).toUpperCase();
                            List<DMGC_S_ZSZ> temp = zszs.stream().filter(c->c.getEventId().equals(data.get("SSZK_EVENTID").toString())).collect(Collectors.toList());
                            if(temp.size() != 0){
                                field.set(newData,temp.get(0).getMc());
                            }
                            break;
                        default:
                            key = StringUtils.humpToUnderline(field.getName()).toUpperCase();
                            break;
                    }
                    if (data.containsKey(key) && data.get(key) != null) {
                        String type = field.getGenericType().toString();
                        switch (type) {
                            case "class java.lang.Long":
                                field.set(newData, Long.parseLong(data.get(key).toString()));
                                break;
                            case "int":
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

    private List<DMGC_S_D_ZSBRSJ> getRecordsInDb(Date rq) {
        LambdaQueryWrapper<DMGC_S_D_ZSBRSJ> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DMGC_S_D_ZSBRSJ::getRq, rq);
        List<DMGC_S_D_ZSBRSJ> result = mapper.selectList(wrapper);
        return result;
    }
}
