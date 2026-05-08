package com.xunjia.pes.bizData.waterInjection.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Station_pj {
    private String stationId;
    private String stationName;
    private String equipmentId;
    private Date rq;
    private String levelOne_name;
    private Double levelOne_weight;
    private Double levelOne_score;
    private String levelTwo_name;
    private Double levelTwo_weight;
    private Double levelTwo_score;

    private String levelThree_name;
    private Double levelThree_weight;
    private Double levelThree_score;

    private String levelFour_name;
    private Double levelFour_weight;
    private Double levelFour_score;

    private String levelFive_name;
    private Double levelFive_weight;
    private Double levelFive_score;
    private Double jx_score;
}
