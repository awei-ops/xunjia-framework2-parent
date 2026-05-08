package com.xunjia.pes.bizData;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChartOption {
    String title;
    List<String> legend;
    List<String> xAxis;
    List<Serie> series;

    public ChartOption() {
        this.legend = new ArrayList<>();
        this.xAxis = new ArrayList<>();
        this.series = new ArrayList<>();
    }

    @Data
    public class Serie {
        String name;
        String type;
        String stack;
        List<Double> data;

        public Serie() {
            this.data = new ArrayList<>();
        }
    }
}
