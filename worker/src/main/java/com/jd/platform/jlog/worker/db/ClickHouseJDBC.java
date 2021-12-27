package com.jd.platform.jlog.worker.db;//package com.usertracer.worker.db;
//
//import cn.hutool.core.date.DateUtil;
//import com.usertracer.common.db.Db;
//import com.usertracer.common.db.DbOperator;
//import com.usertracer.common.db.WhereCause;
//import com.usertracer.common.utils.FastJsonUtils;
//import com.usertracer.common.utils.ProtostuffUtils;
//import com.usertracer.common.utils.SnappyUtils;
//import com.usertracer.common.utils.ZstdUtils;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.logging.log4j.util.Base64Util;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.annotation.Nullable;
//import java.io.UnsupportedEncodingException;
//import java.nio.charset.StandardCharsets;
//import java.sql.*;
//import java.util.*;
//import java.util.Date;
//import java.util.regex.Pattern;
//
///**
// * @author wuweifeng
// * @version 1.0
// * @date 2021-08-23
// */
//
//public class ClickHouseJDBC {
//
//    private static Logger logger = LoggerFactory.getLogger("ClickHouseJDBC");
//
//    public static void main(String[] args) throws Exception {
//        String sqlDB = "show databases";//查询数据库
//        String sqlTab = "show tables";//查看表
////        String sqlCount = "select count(*) count from ontime";//查询ontime数据量
//        String sqlCount = "insert into tracer_model";//查询ontime数据量
////        exeSql(sqlDB);
////        exeSql(sqlTab);
////        exeSql(sqlCount);
//
//        //插入一条
//        List<Map<String, Object>> datas = new ArrayList<>();
//        datas.add(build());
////        System.out.println("success " + insertAll("tracer_model", datas));
//
//        Map<String, Object> whereMap = new HashMap<>();
//        whereMap.put("tracerId", 777);
//
//        List<WhereCause> whereCauseList = new ArrayList<>();
//        WhereCause whereCause = new WhereCause();
//        whereCause.setKey("createTime");
//        whereCause.setOperator(DbOperator.GREATER);
//        whereCause.setValue("2021-01-01 10:00:00");
//        whereCauseList.add(whereCause);
//
//        List<Map<String, Object>> list = new Db().count("tracer_model", whereCauseList);
//
//
////        List<Map<String, Object>> list = query("tracer_model", whereMap);
//
//
//        for (Map<String, Object> map : list) {
//            Object object = map.get("responseContent");
//            String res = (String)object;
//
//            byte[] responseBytes = res.getBytes(StandardCharsets.UTF_8);
//
////            byte[] responseBytes = Base64.decodeBase64(res.getBytes(StandardCharsets.UTF_8));
//
//            String userIp = (String) map.get("userIp");
//            System.out.println(userIp);
//            String response = new String(ZstdUtils.decompressBytes(responseBytes));
//            System.out.println(response);
//        }
//    }
//
//    private static Map<String, Object> build() throws UnsupportedEncodingException {
//        Map<String, Object> map = new HashMap<>();
//
//        Map<String, Object> requestMap = new HashMap<>();
//        requestMap.put("abc", "wfefw");
//
//        String response = "System.out.println(啊 DateUtil.formatDateTime(new Date()))";
//        byte[] responseBytes = ZstdUtils.compress(response.getBytes(StandardCharsets.UTF_8));
//        map.put("responseContent", new String(responseBytes));
////        map.put("responseContent", Base64.encodeBase64(responseBytes));
//
//
//
//        map.put("requestContent", FastJsonUtils.collectToString(requestMap));
//
//
//        map.put("createTime", DateUtil.formatDateTime(new Date()));
//
//        map.put("tracerId", 888);
//
//        map.put("pin", "awefw");
//
//        map.put("uuid", "345634");
//
//        int clientType = 0;
//        clientType = 1;
//        map.put("clientType", clientType);
//        map.put("clientVersion", "123.23");
//
//        map.put("userIp", "1234.2.2.4");
//        map.put("serverIp", "3.4.45.6");
//
//        map.put("intoDbTime", DateUtil.formatDateTime(new Date()));
//        return map;
//    }
//
//
//    /**
//     * 执行数据库插入操作
//     *
//     * @param datas     插入数据表中key为列名和value为列对应的值的Map对象的List集合
//     * @param tableName 要插入的数据库的表名
//     * @return 影响的行数
//     * @throws SQLException SQL异常
//     */
//    public static int insertAll(String tableName, List<Map<String, Object>> datas) throws SQLException {
//        //影响的行数
//        int affectRowCount = 0;
//        PreparedStatement preparedStatement = null;
//        try {
//            Map<String, Object> valueMap = datas.get(0);
//            //获取数据库插入的Map的键值对的值
//            Set<String> keySet = valueMap.keySet();
//            Iterator<String> iterator = keySet.iterator();
//            //要插入的字段sql，其实就是用key拼起来的
//            StringBuilder columnSql = new StringBuilder();
//            //要插入的字段值，其实就是？
//            StringBuilder unknownMarkSql = new StringBuilder();
//            Object[] keys = new Object[valueMap.size()];
//            int i = 0;
//            while (iterator.hasNext()) {
//                String key = iterator.next();
//                keys[i] = key;
//                columnSql.append(i == 0 ? "" : ",");
//                columnSql.append(key);
//
//                unknownMarkSql.append(i == 0 ? "" : ",");
//                unknownMarkSql.append("?");
//                i++;
//            }
//            //开始拼插入的sql语句
//            StringBuilder sql = new StringBuilder();
//            sql.append("INSERT INTO ");
//            sql.append(tableName);
//            sql.append(" (");
//            sql.append(columnSql);
//            sql.append(" )  VALUES (");
//            sql.append(unknownMarkSql);
//            sql.append(" )");
//
//            String address = "jdbc:clickhouse://127.0.0.1:8123/default";
//            Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
//            Connection connection = DriverManager.getConnection(address);
//
//            //执行SQL预编译
//            preparedStatement = connection.prepareStatement(sql.toString());
//            //设置不自动提交，以便于在出现异常的时候数据库回滚**/
//            connection.setAutoCommit(false);
////            logger.info(sql.toString());
//            for (Map<String, Object> data : datas) {
//                for (int k = 0; k < keys.length; k++) {
//                    preparedStatement.setObject(k + 1, data.get(keys[k]));
//                }
//                preparedStatement.addBatch();
//            }
//            int[] arr = preparedStatement.executeBatch();
//            connection.commit();
//            affectRowCount = arr.length;
////            logger.info("成功了插入了" + affectRowCount + "行");
//        } catch (Exception e) {
////            if (connection != null) {
////                connection.rollback();
////            }
//            e.printStackTrace();
//        } finally {
//            if (preparedStatement != null) {
//                preparedStatement.close();
//            }
////            if (connection != null) {
////                connection.close();
////            }
//        }
//        return affectRowCount;
//    }
//
//    public static List<Map<String, Object>> query(String tableName,
//                                           Map<String, Object> whereMap) throws Exception {
//        String whereClause = "";
//        Object[] whereArgs = null;
//        if (whereMap != null && whereMap.size() > 0) {
//            Iterator<String> iterator = whereMap.keySet().iterator();
//            whereArgs = new Object[whereMap.size()];
//            int i = 0;
//            while (iterator.hasNext()) {
//                String key = iterator.next();
//                whereClause += (i == 0 ? "" : " AND ");
//                whereClause += (key + " = ? ");
//                whereArgs[i] = whereMap.get(key);
//                i++;
//            }
//        }
//        return query(tableName, false, null, whereClause, whereArgs, null, null, null, null);
//    }
//
//    public static List<Map<String, Object>> query(String tableName,
//                                           boolean distinct,
//                                           String[] columns,
//                                           String selection,
//                                           Object[] selectionArgs,
//                                           String groupBy,
//                                           String having,
//                                           String orderBy,
//                                           String limit) throws SQLException {
//        String sql = buildQueryString(distinct, tableName, columns, selection, groupBy, having, orderBy, limit);
//        return executeQuery(sql, selectionArgs);
//
//    }
//    /**
//     * 执行查询
//     *
//     * @param sql      要执行的sql语句
//     * @param bindArgs 绑定的参数
//     * @return List<Map < String, Object>>结果集对象
//     * @throws SQLException SQL执行异常
//     */
//    public static List<Map<String, Object>> executeQuery(String sql, Object[] bindArgs) throws SQLException {
//        List<Map<String, Object>> datas = new ArrayList<>();
//        PreparedStatement preparedStatement = null;
//        ResultSet resultSet = null;
//
//        try {
//            String address = "jdbc:clickhouse://127.0.0.1:8123/default";
//            Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
//            Connection connection = DriverManager.getConnection(address);
//
//            preparedStatement = connection.prepareStatement(sql);
//            if (bindArgs != null) {
//                //设置sql占位符中的值
//                for (int i = 0; i < bindArgs.length; i++) {
//                    preparedStatement.setObject(i + 1, bindArgs[i]);
//                }
//            }
//            logger.info(getExecSQL(sql, bindArgs));
//            //执行sql语句，获取结果集
//            resultSet = preparedStatement.executeQuery();
//            datas = getDatas(resultSet);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (resultSet != null) {
//                resultSet.close();
//            }
//            if (preparedStatement != null) {
//                preparedStatement.close();
//            }
//        }
//        return datas;
//    }
//    private  static String getExecSQL(String sql, Object[] bindArgs) {
//        StringBuilder sb = new StringBuilder(sql);
//        if (bindArgs != null && bindArgs.length > 0) {
//            int index = 0;
//            for (Object bindArg : bindArgs) {
//                index = sb.indexOf("?", index);
//                sb.replace(index, index + 1, String.valueOf(bindArg));
//            }
//        }
//        return sb.toString();
//    }
//    /**
//     * 将结果集对象封装成List<Map<String, Object>> 对象
//     *
//     * @param resultSet 结果多想
//     * @return 结果的封装
//     * @throws SQLException s
//     */
//    private static List<Map<String, Object>> getDatas(ResultSet resultSet) throws SQLException {
//        List<Map<String, Object>> datas = new ArrayList<>();
//        //获取结果集的数据结构对象
//        ResultSetMetaData metaData = resultSet.getMetaData();
//        while (resultSet.next()) {
//            Map<String, Object> rowMap = new HashMap<>();
//            for (int i = 1; i <= metaData.getColumnCount(); i++) {
//                rowMap.put(metaData.getColumnName(i), resultSet.getObject(i));
//            }
//            datas.add(rowMap);
//        }
//        logger.info("成功查询到了" + datas.size() + "行数据");
//        for (int i = 0; i < datas.size(); i++) {
//            Map<String, Object> map = datas.get(i);
//            logger.info("第" + (i + 1) + "行：" + map);
//        }
//        return datas;
//    }
//
//    private static String buildQueryString(
//            boolean distinct, String tables, String[] columns, String where,
//            String groupBy, String having, String orderBy, String limit) {
//        if (isEmpty(groupBy) && !isEmpty(having)) {
//            throw new IllegalArgumentException(
//                    "HAVING clauses are only permitted when using a groupBy clause");
//        }
//        if (!isEmpty(limit) && !sLimitPattern.matcher(limit).matches()) {
//            throw new IllegalArgumentException("invalid LIMIT clauses:" + limit);
//        }
//
//        StringBuilder query = new StringBuilder(120);
//
//        query.append("SELECT ");
//        if (distinct) {
//            query.append("DISTINCT ");
//        }
//        if (columns != null && columns.length != 0) {
//            appendColumns(query, columns);
//        } else {
//            query.append(" * ");
//        }
//        query.append("FROM ");
//        query.append(tables);
//        appendClause(query, " WHERE ", where);
//        appendClause(query, " GROUP BY ", groupBy);
//        appendClause(query, " HAVING ", having);
//        appendClause(query, " ORDER BY ", orderBy);
//        appendClause(query, " LIMIT ", limit);
//        return query.toString();
//    }
//    private static final Pattern sLimitPattern =
//            Pattern.compile("\\s*\\d+\\s*(,\\s*\\d+\\s*)?");
//
//
//    private static void appendColumns(StringBuilder s, String[] columns) {
//        int n = columns.length;
//
//        for (int i = 0; i < n; i++) {
//            String column = columns[i];
//
//            if (column != null) {
//                if (i > 0) {
//                    s.append(", ");
//                }
//                s.append(column);
//            }
//        }
//        s.append(' ');
//    }
//
//    /**
//     * addClause
//     *
//     * @param s      the add StringBuilder
//     * @param name   clauseName
//     * @param clause clauseSelection
//     */
//    private static void appendClause(StringBuilder s, String name, String clause) {
//        if (!isEmpty(clause)) {
//            s.append(name);
//            s.append(clause);
//        }
//    }
//
//    /**
//     * Returns true if the string is null or 0-length.
//     *
//     * @param str the string to be examined
//     * @return true if str is null or zero length
//     */
//    private static boolean isEmpty(@Nullable CharSequence str) {
//        if (str == null || str.length() == 0)
//            return true;
//        else
//            return false;
//    }
//
//    public static void exeSql(String sql) {
//        String address = "jdbc:clickhouse://127.0.0.1:8123/default";
//        Connection connection = null;
//        Statement statement = null;
//        ResultSet results = null;
//        try {
//            Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
//            connection = DriverManager.getConnection(address);
//            statement = connection.createStatement();
//            long begin = System.currentTimeMillis();
//            results = statement.executeQuery(sql);
//            long end = System.currentTimeMillis();
//            System.out.println("执行（" + sql + "）耗时：" + (end - begin) + "ms");
//            ResultSetMetaData rsmd = results.getMetaData();
//            List<Map> list = new ArrayList();
//            while (results.next()) {
//                Map map = new HashMap();
//                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
//                    map.put(rsmd.getColumnName(i), results.getString(rsmd.getColumnName(i)));
//                }
//                list.add(map);
//            }
//            for (Map map : list) {
//                System.err.println(map);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {//关闭连接
//            try {
//                if (results != null) {
//                    results.close();
//                }
//                if (statement != null) {
//                    statement.close();
//                }
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//}
