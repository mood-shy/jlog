package com.jd.platform.jlog.dashboard.utils;

import com.jd.platform.jlog.dashboard.db.DbOperator;
import com.jd.platform.jlog.dashboard.db.WhereCause;
import com.jd.platform.jlog.dashboard.model.QueryListModel;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 查询相关工具类
 *
 * @author shenkaiwen5
 * @version 1.0
 * @date 2021-08-30
 */
public class DbUtils {

    /**
     * 将VO转化为查询条件
     */
    public static List<WhereCause> convertTracerToCause(QueryListModel queryListModel) {
        List<WhereCause> res = new ArrayList<>(16);
        if (!StringUtils.isEmpty(queryListModel.getUid())) {
            addEqualWhereCause(res, "uid", queryListModel.getUid());
        }
        if (!StringUtils.isEmpty(queryListModel.getBeginTime())) {
            addGeWhereCause(res, "createTime", queryListModel.getBeginTime());
        } else {
            //pin和uuid都空，只查30秒
            if (StringUtils.isEmpty(queryListModel.getUid())) {
                //如果没传开始时间，则默认是30秒
                addGeWhereCause(res, "createTime",  formatDatetime(System.currentTimeMillis() - 30 * 1000L));
            } else {
                //查最近半小时
                addGeWhereCause(res, "createTime", formatDatetime(System.currentTimeMillis() - 30 * 60 * 1000L));
            }
        }
        if (!StringUtils.isEmpty(queryListModel.getEndTime())) {
            addLeWhereCause(res, "createTime", queryListModel.getEndTime());
        } else {
            //如果不传结束时间，则默认是现在
            addLeWhereCause(res, "createTime", formatDatetime(System.currentTimeMillis()));
        }

        if (!StringUtils.isEmpty(queryListModel.getTracerId())) {
            addEqualWhereCause(res, "tracerId", queryListModel.getTracerId());
        }
        if (!StringUtils.isEmpty(queryListModel.getUid())) {
            addEqualWhereCause(res, "uid", queryListModel.getUid());
        }
        if (!StringUtils.isEmpty(queryListModel.getUri())) {
            addEqualWhereCause(res, "uri", queryListModel.getUri());
        }
        if (!StringUtils.isEmpty(queryListModel.getErrno())) {
            addEqualWhereCause(res, "errno", queryListModel.getErrno());
        }
        if (!StringUtils.isEmpty(queryListModel.getErrmsg())) {
            addEqualWhereCause(res, "errmsg", queryListModel.getErrmsg());
        }
        return res;
    }

    /**
     * 时间格式化
     */
    private static String formatDatetime(long time) {
        Date date = new Date(time);
        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        return sdf.format(date);
    }


    /**
     * 添加字符串相等的查询条件
     */
    public static void addEqualWhereCause(List<WhereCause> res, String key, Object obj) {
        //过滤为null的
        if (obj == null) {
            return;
        }
        //转为字符串
        String val = String.valueOf(obj);
        //过滤为null的
        if (StringUtils.isEmpty(val) || "null".equals(val)) {
            return;
        }

        res.add(new WhereCause(key, val, DbOperator.EQUEL));
    }

    /**
     * 添加Long相等的查询条件
     */
    public static void addEqualWhereCause(List<WhereCause> res, String key, Long val) {
        if (val == null) {
            return;
        }

        res.add(new WhereCause(key, val, DbOperator.EQUEL));
    }

    /**
     * 添加大于的条件
     */
    public static void addGeWhereCause(List<WhereCause> res, String key, Object val) {
        if (val == null) {
            return;
        }

        res.add(new WhereCause(key, val, DbOperator.GE));
    }

    /**
     * 添加小于的条件
     */
    public static void addLeWhereCause(List<WhereCause> res, String key, Object val) {
        if (val == null) {
            return;
        }

        res.add(new WhereCause(key, val, DbOperator.LE));
    }
}
