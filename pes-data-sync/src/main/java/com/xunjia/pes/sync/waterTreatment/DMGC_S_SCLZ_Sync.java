package com.xunjia.pes.sync.waterTreatment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xunjia.framework.utils.StringUtils;
import com.xunjia.pes.bizData.BaseEntity;
import com.xunjia.pes.bizData.waterTreatment.entity.DMGC_S_SCLZ;
import com.xunjia.pes.bizData.waterTreatment.mapper.DMGC_S_SCLZMapper;
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
public class DMGC_S_SCLZ_Sync {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DMGC_S_SCLZMapper mapper;

    public void sync(Date startTime, Date endTime) throws IllegalAccessException {
        List<DMGC_S_SCLZ> records = getRecordsInDb();
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
            readDataSql = "select * from A5ADMIN.dmgc_s_sclz where EVENTID not in (" + temp + ")";
        } else {
            readDataSql = "select * from A5ADMIN.dmgc_s_sclz";
        }
        List<Map<String, Object>> readDataList = jdbcTemplate.queryForList(readDataSql);

        // 读取readDataList，构造实体
        List<DMGC_S_SCLZ> entities = this.convertToEntity(readDataList);

        // 实体批量写入
        if (!entities.isEmpty()) {
            for (DMGC_S_SCLZ entity : entities) {
                mapper.insert(entity);
            }
        }
    }

    private List<DMGC_S_SCLZ> convertToEntity(List<Map<String, Object>> dataList) throws IllegalAccessException {
        List<DMGC_S_SCLZ> entities = new ArrayList<>();
        Class clazz = DMGC_S_SCLZ.class;
        List<Field> fields = new ArrayList<>(); // 保存属性对象数组到列表
        Field[] declaredFields = clazz.getDeclaredFields();
        Collections.addAll(fields,declaredFields);// 获取字节码对象的属性对象数组
        clazz = clazz.getSuperclass();  // 获得直接父类的字节码对象
        declaredFields = clazz.getDeclaredFields();
        Collections.addAll(fields,declaredFields);

        if (dataList.size() > 0) {
            for (Map<String, Object> data : dataList) {
                DMGC_S_SCLZ newData = new DMGC_S_SCLZ();
                for (Field field : fields) {
                    String entityKey = field.getName();
                    String key;
                    switch (entityKey) {
                        case "gsfxEventId":
                            key = "GSFX_EVENTID";
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

    private List<DMGC_S_SCLZ> getRecordsInDb() {
        LambdaQueryWrapper<DMGC_S_SCLZ> wrapper = new LambdaQueryWrapper<>();
        List<DMGC_S_SCLZ> result = mapper.selectList(wrapper);
        return result;
    }
}
