package com.xunjia.pes.bizData;

import lombok.Data;

@Data
public class DynamicWeightResult {
    private String stationId;
    private String equipId;
    private double level2Weight;
    private double level3Weight;
    private double level4Weight;
}
