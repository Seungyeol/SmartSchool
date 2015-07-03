package com.aura.smartschool.vo;

import java.io.Serializable;

/**
 * Created by eastflag on 2015-06-21.
 */
public class SchoolVO  implements Serializable {
    public int school_id;
    public String school_name;
    public String school_grade;
    public String school_class;
    public String gubun1;
    public String gubun2; //초등학교,
    public String zipcode;
    public String address;
    public String new_address;
    public String lat;
    public String lng;
    public String homepage;
    public String fax;
    public String contact;
    public String sido; //시도: 경기, 서울, 인천
    public String gugun; //구군: 강남구, 파주시,
    public String support;
}