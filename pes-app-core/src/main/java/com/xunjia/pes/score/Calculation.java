package com.xunjia.pes.score;

import com.xunjia.pes.bizData.assessment.entity.MonitoringIndicatorNew;
import javassist.bytecode.stackmap.BasicBlock;

public class Calculation {
    public static String[] zyqNames = new String[]{"第一作业区", "第二作业区", "第三作业区", "第四作业区", "第五作业区", "第六作业区", "第七作业区", "第八作业区", "生产维修大队"};
    public static double getAssessmentResult(double param, double rightValue, double leftValue, double rightScore, double leftScore) {
        if (rightValue != leftValue) {
            double temp = leftScore + (rightScore - leftScore) * (param - leftValue) / (rightValue - leftValue);
            return (double) (Math.round(temp * 100) / 100);

        } else {
            return 0;
        }
    }

    public static double getDivisionResult(double leftParam, double rightParam) {
        if (rightParam != 0) {
            return (double) (Math.round(leftParam / rightParam * 100)) / 100;
        } else {
            return 0;
        }
    }

    public static double getMultiplicationResult(double leftParam, double rightParam) {
        return (double) (Math.round(leftParam * rightParam * 100)) / 100;
    }

    public static double getPlusResult(double leftParam, double rightParam) {
        return (double) (Math.round((leftParam + rightParam) * 100)) / 100;
    }

    public static double getReduceResult(double leftParam, double rightParam) {
        return (double) (Math.round((leftParam - rightParam) * 100)) / 100;
    }

    //单耗类计算（越小分越高那种）
    public static double calculationOfUnitConsumption(double param, MonitoringIndicatorNew monitoringIndicatorNew) {
        double result = 100;
        if (param < monitoringIndicatorNew.getMonitoringItemMin()) {
            result = monitoringIndicatorNew.getMonitoringItemMinScore();
        }
        if (param < monitoringIndicatorNew.getMonitoringItemEnergy() && param >= monitoringIndicatorNew.getMonitoringItemMin()) {
            result = getAssessmentResult(param, monitoringIndicatorNew.getMonitoringItemEnergy(), monitoringIndicatorNew.getMonitoringItemMin(),
                    monitoringIndicatorNew.getMonitoringItemEnergyScore(), monitoringIndicatorNew.getMonitoringItemMinScore());
        }
        if (param >= monitoringIndicatorNew.getMonitoringItemEnergy() && param <= monitoringIndicatorNew.getMonitoringItemLimit()) {
            result = getAssessmentResult(param, monitoringIndicatorNew.getMonitoringItemLimit(), monitoringIndicatorNew.getMonitoringItemEnergy(),
                    monitoringIndicatorNew.getMonitoringItemLimitScore(), monitoringIndicatorNew.getMonitoringItemEnergyScore());

        }
        if (param > monitoringIndicatorNew.getMonitoringItemLimit() && param <= monitoringIndicatorNew.getMonitoringItemMax()) {
            result = getAssessmentResult(param, monitoringIndicatorNew.getMonitoringItemMax(), monitoringIndicatorNew.getMonitoringItemLimit(),
                    monitoringIndicatorNew.getMonitoringItemMaxScore(), monitoringIndicatorNew.getMonitoringItemLimitScore());
        }
        if (param > monitoringIndicatorNew.getMonitoringItemMax()) {
            result = monitoringIndicatorNew.getMonitoringItemMaxScore();
        }
        return result;
    }

    //效率类计算（越大分越高那种）
    public static double efficiency(double param, MonitoringIndicatorNew monitoringIndicatorNew) {
        double result = 0;
        if (param < monitoringIndicatorNew.getMonitoringItemMin()) {
            result = monitoringIndicatorNew.getMonitoringItemMinScore();
        }
        if (param < monitoringIndicatorNew.getMonitoringItemLimit() && param >= monitoringIndicatorNew.getMonitoringItemMin()) {
            result = getAssessmentResult(param, monitoringIndicatorNew.getMonitoringItemLimit(), monitoringIndicatorNew.getMonitoringItemMin(),
                    monitoringIndicatorNew.getMonitoringItemLimitScore(), monitoringIndicatorNew.getMonitoringItemMinScore());
        }
        if (param >= monitoringIndicatorNew.getMonitoringItemLimit() && param <= monitoringIndicatorNew.getMonitoringItemEnergy()) {
            result = getAssessmentResult(param, monitoringIndicatorNew.getMonitoringItemEnergy(), monitoringIndicatorNew.getMonitoringItemLimit(),
                    monitoringIndicatorNew.getMonitoringItemEnergyScore(), monitoringIndicatorNew.getMonitoringItemLimitScore());
        }
        if (param > monitoringIndicatorNew.getMonitoringItemEnergy() && param <= monitoringIndicatorNew.getMonitoringItemMax()) {
            result = getAssessmentResult(param, monitoringIndicatorNew.getMonitoringItemMax(), monitoringIndicatorNew.getMonitoringItemEnergy(),
                    monitoringIndicatorNew.getMonitoringItemMaxScore(), monitoringIndicatorNew.getMonitoringItemEnergyScore());
        }
        if (param > monitoringIndicatorNew.getMonitoringItemMax()) {
            result = monitoringIndicatorNew.getMonitoringItemMaxScore();
        }
        return result;
    }

    public static double specialFhl(double param, MonitoringIndicatorNew monitoringIndicatorNew) {
        double result = 0;
        if (param <= 100) {
            result = getAssessmentResult(param, 100, 0, 100, 0);
        }
        if (param > 100 && param <= 200) {
            result = getAssessmentResult(param, 200, 100, 0, 100);
        }
        return result;
    }

    public static String specialFhlComment(double param, MonitoringIndicatorNew monitoringIndicatorNew) {
        if (param < monitoringIndicatorNew.getMonitoringItemLimitScore()) {
            return monitoringIndicatorNew.getItemMinEvaluation();
        } else {
            return monitoringIndicatorNew.getItemMidEvaluation();
        }
    }

    //获取单耗类评价（越小分越高那种）
    public static String getUnitConsumptionComment(double param, MonitoringIndicatorNew monitoringIndicatorNew) {
        if (param < monitoringIndicatorNew.getMonitoringItemLimitScore()) {
            return monitoringIndicatorNew.getItemMinEvaluation();
        } else {
            return monitoringIndicatorNew.getItemMidEvaluation();
        }
    }

    //获取效率类评价（越大分越高那种）
    public static String getEfficiencyComment(double param, MonitoringIndicatorNew monitoringIndicatorNew) {
        String result = "";
        if (param < monitoringIndicatorNew.getMonitoringItemLimitScore()) {
            result = monitoringIndicatorNew.getItemMinEvaluation();
        }
        if (param >= monitoringIndicatorNew.getMonitoringItemLimitScore() && param <= monitoringIndicatorNew.getMonitoringItemEnergyScore()) {
            result = monitoringIndicatorNew.getItemMidEvaluation();
        }
        if (param > monitoringIndicatorNew.getMonitoringItemEnergyScore()) {
            result = monitoringIndicatorNew.getItemMaxEvaluation();
        }
        return result;
    }

    /**
     * 纵向绩效计算
     *
     * @param preScore               前一周期得分
     * @param curScore               本同期得分
     * @param monitoringIndicatorNew 指标对比明细
     * @return 计算得分
     */
    public static double getPortrait(double preScore, double curScore, MonitoringIndicatorNew monitoringIndicatorNew) {
        double result = 0;
        try {
            double divResult = getReduceResult(curScore, preScore);
            double temp = getMultiplicationResult(getDivisionResult(divResult, preScore), 100);
            if (temp >= monitoringIndicatorNew.getMonitoringItemEnergy()) {
                result = 100;
            }
            if (temp < monitoringIndicatorNew.getMonitoringItemMin()) {
                result = monitoringIndicatorNew.getMonitoringItemMinScore();
            }
            if (temp >= monitoringIndicatorNew.getMonitoringItemLimit() && temp < 0) {
                result = getAssessmentResult(temp, 0, monitoringIndicatorNew.getMonitoringItemLimit(),
                        preScore, monitoringIndicatorNew.getMonitoringItemLimitScore());
            }
            if (temp < monitoringIndicatorNew.getMonitoringItemLimit() && temp >= monitoringIndicatorNew.getMonitoringItemMin()) {
                result = getAssessmentResult(temp, monitoringIndicatorNew.getMonitoringItemLimit(), monitoringIndicatorNew.getMonitoringItemMin(),
                        monitoringIndicatorNew.getMonitoringItemLimitScore(), monitoringIndicatorNew.getMonitoringItemMinScore());
            }

            if (temp >= 0 && temp < monitoringIndicatorNew.getMonitoringItemEnergy()) {
                result = getAssessmentResult(temp, monitoringIndicatorNew.getMonitoringItemEnergy(), 0,
                        monitoringIndicatorNew.getMonitoringItemEnergyScore(), preScore);
            }
        } catch (Exception ex) {
            String err = ex.getMessage();
        }
        return result;
    }
}
