package com.xunjia.pes.sync.oil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.BaseEntity;
import com.xunjia.pes.bizData.oil.entity.DMGC_Y_LHZ;
import com.xunjia.pes.bizData.oil.mapper.DMGC_Y_LHZMapper;
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
public class DMGC_Y_LHZ_Sync {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DMGC_Y_LHZMapper mapper;

    public void sync(Date startTime, Date endTime) throws IllegalAccessException {
        List<DMGC_Y_LHZ> records = getRecordsInDb();
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
            readDataSql = "select * from A5ADMIN.dmgc_y_lhz where EVENTID not in (" + temp + ")";
        } else {
            readDataSql = "select * from A5ADMIN.dmgc_y_lhz";
        }
        List<Map<String, Object>> readDataList = jdbcTemplate.queryForList(readDataSql);

        // 读取readDataList，构造实体
        List<DMGC_Y_LHZ> entities = this.convertToEntity(readDataList);

        // 实体批量写入
        if (!entities.isEmpty()) {
            for (DMGC_Y_LHZ entity : entities) {
                mapper.insert(entity);
            }
        }
    }

    private List<DMGC_Y_LHZ> convertToEntity(List<Map<String, Object>> dataList) throws IllegalAccessException {
        List<DMGC_Y_LHZ> entities = new ArrayList<>();
        Class clazz = DMGC_Y_LHZ.class;
        List<Field> fields = new ArrayList<>(); // 保存属性对象数组到列表
        Field[] declaredFields = clazz.getDeclaredFields();
        Collections.addAll(fields,declaredFields);// 获取字节码对象的属性对象数组
        clazz = clazz.getSuperclass();  // 获得直接父类的字节码对象
        declaredFields = clazz.getDeclaredFields();
        Collections.addAll(fields,declaredFields);

        if (dataList.size() > 0) {
            for (Map<String, Object> data : dataList) {
                DMGC_Y_LHZ newData = new DMGC_Y_LHZ();
                for (Field field : fields) {
                    String entityKey = field.getName();
                    String key;
                    switch (entityKey) {
                        case "zkEventId":
                            key = "ZK_EVENTID";
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
                            case "int":
                                field.set(newData, Integer.parseInt(data.get(key).toString()));
                                break;
                            case "double":
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

    private List<DMGC_Y_LHZ> getRecordsInDb() {
        LambdaQueryWrapper<DMGC_Y_LHZ> wrapper = new LambdaQueryWrapper<>();
        List<DMGC_Y_LHZ> result = mapper.selectList(wrapper);
        return result;
    }
}
