package com.columns;

import lombok.Data;

@Data
public class ColumnInfo {
    private String field;
    private String title;
    private int width =80;
}
