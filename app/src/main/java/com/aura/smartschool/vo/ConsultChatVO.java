package com.aura.smartschool.vo;

import java.util.Date;

/**
 * Created by Administrator on 2015-07-12.
 */
public class ConsultChatVO {

    public int msgFrom;
    public String msg;
    public Date time;
    public int sendResult;

    public ConsultChatVO (int msgFrom, String msg, Date time, int sendResult) {
        this.msgFrom = msgFrom;
        this.msg = msg;
        this.time = time;
        this.sendResult = sendResult;
    }
}
