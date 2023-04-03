package com.jd.platform.jlog.dashboard.entity;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TracerListVO
 *
 * @author wuweifeng
 * @version 1.0
 * @date 2021-09-01
 */

public class TracerListVO {
    /**
     * 总记录数
     */
    private Long total;
    /**
     * 列表数据
     */
    private List<Map<String, Object>> rows = new ArrayList<>(16);

    /**
     * 消息状态码
     */
    private Integer code;

    /**
     * 消息内容
     */
    private Integer msg;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getMsg() {
        return msg;
    }

    public void setMsg(Integer msg) {
        this.msg = msg;
    }
}
