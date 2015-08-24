package com.aura.smartschool.vo;

/**
 * Created by Administrator on 2015-08-25.
 */
public class MealVO {
    public int day;
    public String breakfast;
    public String lunch;
    public String dinner;

    public MealVO(int day, String breakfast, String lunch, String dinner) {
        this.day = day;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
    }
}
