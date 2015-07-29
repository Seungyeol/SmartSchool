package com.aura.smartschool.vo;

import java.io.Serializable;

/**
 * Created by eastflag on 2015-07-11.
 */
public class MeasureSummaryVO implements Serializable {
    public String measure_date;
    public float height;
    public String heightStatus;
    public String weight;
    public String weightStatus;
    public String fat;
    public String waist;
    public String skeletal;  //골격근량
    public int muscle_percent; //근육량 퍼센트
    public String weight_control; //체중 조절
    public String fat_control; //체지방 조절
    public String bmi;
    public String bmiStatus;
    public String bmiGradeId;
    public String ppm;
    public String cohd;
    public String smokeStatus;
    public float growthGrade;
}
