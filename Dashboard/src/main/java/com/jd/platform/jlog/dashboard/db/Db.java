package com.jd.platform.jlog.dashboard.db;

import com.jd.platform.jlog.dashboard.utils.TwoTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * https://blog.csdn.net/linglongxin24/article/details/53769957
 *
 * @author wuweifeng
 * @version 1.0
 * @date 2021-08-23
 */
@Component
public class Db {
    /**
     * 连接池
     */
    @Resource
    private ConnectionPool connectionPool;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 执行更新操作
     *
     * @param tableName 表名
     * @param valueMap  要更改的值
     * @param whereMap  条件
     * @return 影响的行数
     * @throws SQLException SQL异常
     */
    public int update(String tableName, Map<String, Object> valueMap, Map<String, Object> whereMap) throws SQLException {
        //获取数据库插入的Map的键值对的值
        Set<String> keySet = valueMap.keySet();
        Iterator<String> iterator = keySet.iterator();
        //开始拼插入的sql语句**/
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(tableName);
        sql.append(" SET ");

        //要更改的的字段sql，其实就是用key拼起来的
        StringBuilder columnSql = new StringBuilder();
        int i = 0;
        List<Object> objects = new ArrayList<>();
        while (iterator.hasNext()) {
            String key = iterator.next();
            columnSql.append(i == 0 ? "" : ",");
            columnSql.append(key + " = ? ");
            objects.add(valueMap.get(key));
            i++;
        }
        sql.append(columnSql);

        //更新的条件:要更改的的字段sql，其实就是用key拼起来的
        StringBuilder whereSql = new StringBuilder();
        int j = 0;
        if (whereMap != null && whereMap.size() > 0) {
            whereSql.append(" WHERE ");
            iterator = whereMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                whereSql.append(j == 0 ? "" : " AND ");
                whereSql.append(key).append(" = ? ");
                objects.add(whereMap.get(key));
                j++;
            }
            sql.append(whereSql);
        }
        return executeUpdate(sql.toString(), objects.toArray());
    }

    /**
     * 执行删除操作
     *
     * @param tableName 要删除的表名
     * @param whereMap  删除的条件
     * @return 影响的行数
     * @throws SQLException SQL执行异常
     */
    public int delete(String tableName, Map<String, Object> whereMap) throws SQLException {
        //准备删除的sql语句
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ");
        sql.append(tableName);

        //更新的条件:要更改的的字段sql，其实就是用key拼起来的
        StringBuilder whereSql = new StringBuilder();
        Object[] bindArgs = null;
        if (whereMap != null && whereMap.size() > 0) {
            bindArgs = new Object[whereMap.size()];
            whereSql.append(" WHERE ");
            //获取数据库插入的Map的键值对的值
            Set<String> keySet = whereMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                String key = iterator.next();
                whereSql.append(i == 0 ? "" : " AND ");
                whereSql.append(key).append(" = ? ");
                bindArgs[i] = whereMap.get(key);
                i++;
            }
            sql.append(whereSql);
        }
        return executeUpdate(sql.toString(), bindArgs);
    }

    /**
     * 可以执行新增，修改，删除
     *
     * @param sql      sql语句
     * @param bindArgs 绑定参数
     * @return 影响的行数
     * @throws SQLException SQL异常
     */
    private int executeUpdate(String sql, Object[] bindArgs) throws SQLException {
        //影响的行数**/
        int affectRowCount;
        PreparedStatement preparedStatement = null;
        try {
            Connection connection = connectionPool.getConnection();
            //执行SQL预编译**/
            preparedStatement = connection.prepareStatement(sql);
            //设置不自动提交，以便于在出现异常的时候数据库回滚**/
            connection.setAutoCommit(false);
            logger.info(getExecSQL(sql, bindArgs));
            if (bindArgs != null) {
                //绑定参数设置sql占位符中的值
                for (int i = 0; i < bindArgs.length; i++) {
                    preparedStatement.setObject(i + 1, bindArgs[i]);
                }
            }
            //执行sql**/
            affectRowCount = preparedStatement.executeUpdate();
            connection.commit();
            String operate;
            if (sql.toUpperCase().contains("DELETE FROM")) {
                operate = "删除";
            } else if (sql.toUpperCase().contains("INSERT INTO")) {
                operate = "新增";
            } else {
                operate = "修改";
            }
            logger.info("成功" + operate + "了" + affectRowCount + "行");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
        return affectRowCount;
    }

    /**
     * 通过sql查询数据,
     * 慎用，会有sql注入问题
     *
     * @return 查询的数据集合
     * @throws SQLException s
     */
    public List<Map<String, Object>> query(String sql) throws SQLException {
        return executeQuery(sql, null);
    }

    /**
     * 执行sql通过 WhereCause限定查询条件查询
     *
     * @param tableName 表名
     * @param causeList where条件
     * @return List<Map < String, Object>>
     * @throws SQLException s
     */
    public List<Map<String, Object>> query(String tableName, List<WhereCause> causeList, long pageNum) throws Exception {
        return query(tableName, null, causeList, pageNum, false);
    }

    /**
     * 执行sql通过 WhereCause限定查询条件查询
     *
     * @param tableName 表名
     * @param columns   列名
     * @param causeList where条件
     * @return List<Map < String, Object>>
     * @throws SQLException s
     */
    public List<Map<String, Object>> query(String tableName, String[] columns, List<WhereCause> causeList, long pageNum, boolean orderBy) throws Exception {
        TwoTuple<String, Object[]> twoTuple = buildWhereArgs(causeList);
        //Long对象的话[-128,127]是准的
        if (pageNum == 0) {
            pageNum = 1;
        }
        String limit = 20 * (pageNum - 1) + ", 20";

        String orderByStr = null;
        //只有数据量小时，才进行时间倒序
        if (orderBy) {
            orderByStr = "createTime desc ";
        }
        return query(tableName, false, columns, twoTuple.getFirst(), twoTuple.getSecond(), null, null, orderByStr, limit);
    }

    /**
     * 查单条
     *
     * @param tableName 表名
     * @param columns   列名
     * @param causeList where条件
     * @return List<Map < String, Object>>
     * @throws SQLException s
     */
    public List<Map<String, Object>> queryOne(String tableName, String[] columns, List<WhereCause> causeList, long pageNum) throws Exception {
        TwoTuple<String, Object[]> twoTuple = buildWhereArgs(causeList);

        //Long对象的话[-128,127]是准的
        if (pageNum == 0) {
            pageNum = 1;
        }
        String limit = 20 * (pageNum - 1) + ", 20";

        return query(tableName, false, columns, twoTuple.getFirst(), twoTuple.getSecond(), null, null, null, limit);
    }

    /**
     * select count(*) from table_dis where xxxx;
     * 返回值取第一条，Map<String, Object>
     * key为count(),只取value即可，就是数字
     *
     * @param tableName 表名
     * @param causeList where条件
     * @return List<Map < String, Object>>
     * @throws SQLException s
     */
    public List<Map<String, Object>> count(String tableName, List<WhereCause> causeList) throws Exception {
        TwoTuple<String, Object[]> twoTuple = buildWhereArgs(causeList);
        return query(tableName, false, new String[]{"count(*)"}, twoTuple.getFirst(), twoTuple.getSecond(), null, null, null, null);
    }

    private TwoTuple<String, Object[]> buildWhereArgs(List<WhereCause> causeList) {
        TwoTuple<String, Object[]> twoTuple = new TwoTuple<>();
        String whereClause = "";
        Object[] whereArgs = null;
        if (causeList != null && causeList.size() > 0) {
            whereArgs = new Object[causeList.size()];
            int i = 0;
            for (WhereCause whereCause : causeList) {
                String key = whereCause.getKey();
                whereClause += (i == 0 ? "" : " AND ");

                whereClause += (key + operator(whereCause.getOperator()) + "? ");

                whereArgs[i] = whereCause.getValue();
                i++;
            }

        }

        twoTuple.setFirst(whereClause);
        twoTuple.setSecond(whereArgs);
        return twoTuple;
    }


    /**
     * 判断符号
     */
    private String operator(DbOperator dbOperator) {
        if (DbOperator.EQUEL == dbOperator) {
            return " = ";
        } else if (DbOperator.GE == dbOperator) {
            return " >= ";
        } else {
            return " < ";
        }

    }

    /**
     * 执行sql通过 Map<String, Object>限定查询条件查询
     *
     * @param tableName 表名
     * @param whereMap  where条件
     * @return List<Map < String, Object>>
     * @throws SQLException s
     */
    public List<Map<String, Object>> query(String tableName,
                                           Map<String, Object> whereMap) throws Exception {
        String whereClause = "";
        Object[] whereArgs = null;
        if (whereMap != null && whereMap.size() > 0) {
            Iterator<String> iterator = whereMap.keySet().iterator();
            whereArgs = new Object[whereMap.size()];
            int i = 0;
            while (iterator.hasNext()) {
                String key = iterator.next();
                whereClause += (i == 0 ? "" : " AND ");
                whereClause += (key + " = ? ");
                whereArgs[i] = whereMap.get(key);
                i++;
            }
        }
        return query(tableName, false, null, whereClause, whereArgs, null, null, null, null);
    }

    /**
     * 执行sql条件参数绑定形式的查询
     *
     * @param tableName   表名
     * @param whereClause where条件的sql
     * @param whereArgs   where条件中占位符中的值
     * @return List<Map < String, Object>>
     * @throws SQLException s
     */
    public List<Map<String, Object>> query(String tableName,
                                           String whereClause,
                                           String[] whereArgs) throws SQLException {
        return query(tableName, false, null, whereClause, whereArgs, null, null, null, null);
    }

    /**
     * 执行全部结构的sql查询
     *
     * @param tableName     表名
     * @param distinct      去重
     * @param columns       要查询的列名
     * @param selection     where条件
     * @param selectionArgs where条件中占位符中的值
     * @param groupBy       分组
     * @param having        筛选
     * @param orderBy       排序
     * @param limit         分页
     * @return List<Map < String, Object>>
     * @throws SQLException s
     */
    public List<Map<String, Object>> query(String tableName,
                                           boolean distinct,
                                           String[] columns,
                                           String selection,
                                           Object[] selectionArgs,
                                           String groupBy,
                                           String having,
                                           String orderBy,
                                           String limit) throws SQLException {
        String sql = buildQueryString(distinct, tableName, columns, selection, groupBy, having, orderBy, limit);
        return executeQuery(sql, selectionArgs);

    }

    /**
     * 执行查询
     *
     * @param sql      要执行的sql语句
     * @param bindArgs 绑定的参数
     * @return List<Map < String, Object>>结果集对象
     * @throws SQLException SQL执行异常
     */
    public List<Map<String, Object>> executeQuery(String sql, Object[] bindArgs) throws SQLException {
        List<Map<String, Object>> datas = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            Connection connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            if (bindArgs != null) {
                //设置sql占位符中的值
                for (int i = 0; i < bindArgs.length; i++) {
                    preparedStatement.setObject(i + 1, bindArgs[i]);
                }
            }
            logger.info(getExecSQL(sql, bindArgs));
            //执行sql语句，获取结果集
            resultSet = preparedStatement.executeQuery();
            datas = getDatas(resultSet);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Db.executeQuery [error]", e);
//            throw e;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
        return datas;
    }


    /**
     * 将结果集对象封装成List<Map<String, Object>> 对象
     *
     * @param resultSet 结果多想
     * @return 结果的封装
     * @throws SQLException s
     */
    private List<Map<String, Object>> getDatas(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> datas = new ArrayList<>();
        //获取结果集的数据结构对象
        ResultSetMetaData metaData = resultSet.getMetaData();
        while (resultSet.next()) {
            Map<String, Object> rowMap = new HashMap<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                rowMap.put(metaData.getColumnName(i), resultSet.getObject(i));
            }
            datas.add(rowMap);
        }
        logger.info("成功查询到了" + (datas.size() == 1 ? 0 : datas.size()) + "行数据");
//        for (int i = 0; i < datas.size(); i++) {
//            Map<String, Object> map = datas.get(i);
//            logger.info("第" + (i + 1) + "行：" + map);
//        }
        return datas;
    }


    /**
     * Build an SQL query string from the given clauses.
     *
     * @param distinct true if you want each row to be unique, false otherwise.
     * @param tables   The table names to compile the query against.
     * @param columns  A list of which columns to return. Passing null will
     *                 return all columns, which is discouraged to prevent reading
     *                 data from storage that isn't going to be used.
     * @param where    A filter declaring which rows to return, formatted as an SQL
     *                 WHERE clause (excluding the WHERE itself). Passing null will
     *                 return all rows for the given URL.
     * @param groupBy  A filter declaring how to group rows, formatted as an SQL
     *                 GROUP BY clause (excluding the GROUP BY itself). Passing null
     *                 will cause the rows to not be grouped.
     * @param having   A filter declare which row groups to include in the cursor,
     *                 if row grouping is being used, formatted as an SQL HAVING
     *                 clause (excluding the HAVING itself). Passing null will cause
     *                 all row groups to be included, and is required when row
     *                 grouping is not being used.
     * @param orderBy  How to order the rows, formatted as an SQL ORDER BY clause
     *                 (excluding the ORDER BY itself). Passing null will use the
     *                 default sort order, which may be unordered.
     * @param limit    Limits the number of rows returned by the query,
     *                 formatted as LIMIT clause. Passing null denotes no LIMIT clause.
     * @return the SQL query string
     */
    private String buildQueryString(
            boolean distinct, String tables, String[] columns, String where,
            String groupBy, String having, String orderBy, String limit) {
        if (isEmpty(groupBy) && !isEmpty(having)) {
            throw new IllegalArgumentException(
                    "HAVING clauses are only permitted when using a groupBy clause");
        }
        if (!isEmpty(limit) && !sLimitPattern.matcher(limit).matches()) {
            throw new IllegalArgumentException("invalid LIMIT clauses:" + limit);
        }

        StringBuilder query = new StringBuilder(120);

        query.append("SELECT ");
        if (distinct) {
            query.append("DISTINCT ");
        }
        if (columns != null && columns.length != 0) {
            appendColumns(query, columns);
        } else {
            query.append(" * ");
        }
        query.append("FROM ");
        query.append(tables);
        appendClause(query, " PREWHERE ", where);
        appendClause(query, " GROUP BY ", groupBy);
        appendClause(query, " HAVING ", having);
        appendClause(query, " ORDER BY ", orderBy);
        appendClause(query, " LIMIT ", limit);
        return query.toString();
    }

    /**
     * Add the names that are non-null in columns to s, separating
     * them with commas.
     */
    private void appendColumns(StringBuilder s, String[] columns) {
        int n = columns.length;

        for (int i = 0; i < n; i++) {
            String column = columns[i];

            if (column != null) {
                if (i > 0) {
                    s.append(", ");
                }
                s.append(column);
            }
        }
        s.append(' ');
    }

    /**
     * addClause
     *
     * @param s      the add StringBuilder
     * @param name   clauseName
     * @param clause clauseSelection
     */
    private void appendClause(StringBuilder s, String name, String clause) {
        if (!isEmpty(clause)) {
            s.append(name);
            s.append(clause);
        }
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    private boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * the pattern of limit
     */
    private final Pattern sLimitPattern =
            Pattern.compile("\\s*\\d+\\s*(,\\s*\\d+\\s*)?");

    /**
     * After the execution of the complete SQL statement, not necessarily the actual implementation of the SQL statement
     *
     * @param sql      SQL statement
     * @param bindArgs Binding parameters
     * @return Replace? SQL statement executed after the
     */
    private String getExecSQL(String sql, Object[] bindArgs) {
        StringBuilder sb = new StringBuilder(sql);
        if (bindArgs != null && bindArgs.length > 0) {
            int index = 0;
            for (Object bindArg : bindArgs) {
                index = sb.indexOf("?", index);
                sb.replace(index, index + 1, String.valueOf(bindArg));
            }
        }
        return sb.toString();
    }

}
