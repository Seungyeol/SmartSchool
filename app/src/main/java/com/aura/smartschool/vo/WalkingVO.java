package com.aura.smartschool.vo;

/**
 * Created by Administrator on 2015-07-07.
 */
public class WalkingVO {
    public long date;
    public int count;
    public int time;

    public WalkingVO(){}

    public WalkingVO(long date, int count, int time) {
        this.date = date;
        this.count = count;
        this.time = time;
    }
}
