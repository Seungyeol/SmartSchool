package com.aura.smartschool.vo;

/**
 * Created by Administrator on 2015-07-23.
 */
/*
category: 1:가정통신문, 2:공지사항, 3: 일정
"noti_seq":18,"school_id":18247,"category":1,"title":"가정통신문1","content":"aaabbbccc","noti_date":"2015-07-21"
 */
public class SchoolNotiVO {
    public int notiSeq;
    public int schoolId;
    public int category;
    public String title;
    public String content;
    public String notiDate;
    public int startIndex;
    public int pageSize;
    public int memberId;
}
