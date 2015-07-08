package com.aura.smartschool.vo;

/**
 * Created by Administrator on 2015-07-07.
 */
public class WalkingVO {
    public long date;
    public int count;
    public int calories;
    public double distance;
    public int activeTime;

    public WalkingVO(){}

    public WalkingVO(long date, int count, int calories, double distance, int activeTime) {
        this.date = date;
        this.count = count;
        this.calories = calories;
        this.distance = distance;
        this.activeTime = activeTime;
    }
}
