package com.columns;

import com.xunjia.pes.bizData.oil.entity.*;
import com.xunjia.pes.bizData.waterInjection.entity.*;
import com.xunjia.pes.bizData.waterTreatment.entity.*;
import io.swagger.annotations.ApiModelProperty;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson.JSONObject;

public class ColumnsApplication {
    public static void main(String[] args) {
        Class<DMGC_S_D_ZSBRSJ> test = DMGC_S_D_ZSBRSJ.class;
        Field[] fields = test.getDeclaredFields();
        List<ColumnInfo> columns = new ArrayList<>();
        ColumnInfo columnInfo;
        for (Field field : fields) {
            columnInfo = new ColumnInfo();
            columnInfo.setField(field.getName());
            if (field.getAnnotation(ApiModelProperty.class) != null) {
                columnInfo.setTitle(field.getAnnotation(ApiModelProperty.class).value());
            }
            columns.add(columnInfo);
        }
        String result = JSONObject.toJSONString(columns);
        String stop = result;
    }
}
