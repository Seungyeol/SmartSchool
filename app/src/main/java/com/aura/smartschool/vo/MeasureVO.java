package com.aura.smartschool.vo;

/**
 * Created by eastflag on 2015-07-10.
 */
public class MeasureVO {
    public float value;
    public float beforeValue;

    public String gradeId = "";
    public String gradeString = "";

    public String date = "";

    public String schoolGrade = "0";
    public String beforeSchoolGrade = "0";
    public String totalNumberOfStudent = "0";

    public float averageOfSchool;
    public float averageOfLocal;
    public float averageOfNation;
    public float averageOfStandard;

    public String percentageOfBodyFat; // DongQ 2014.03.12
    public String msGradeString;

    public String rank;
    public String beforeRank;
}
