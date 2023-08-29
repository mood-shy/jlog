package com.jd.platform.jlog.common.constant;

import java.io.Serializable;

//通讯方式实体类
public class SendMode implements Serializable {
    //true为单播，false为多播
    private Boolean unicast=true;
    public SendMode() {
    }
    public boolean getUnicast() {
        return unicast;
    }

    public void setUnicast(boolean unicast) {
        this.unicast = unicast;
    }

}
