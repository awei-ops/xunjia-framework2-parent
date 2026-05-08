package com.xunjia.pes.bizData;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PieOption {

    String title;
    List<PieData> series;

    public PieOption() {
        this.series = new ArrayList<>();
    }

    @Data
    public class PieData {
        String name;
        double value;
    }
}
