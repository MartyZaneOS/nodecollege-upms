package com.nodecollege.cloud.core.sqlite.interceptor;

import com.alibaba.druid.sql.SQLUtils;
import com.nodecollege.cloud.common.model.DbType;
import com.nodecollege.cloud.utils.JdbcUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * mybatis sql转换拦截器
 * 将xml中的mysql语句根据连接的数据库不同,转换成对应的sql
 *
 * @author LC
 * @date 2020/3/29 17:26
 */
//@Intercepts({@Signature(
//        type = StatementHandler.class,
//        method = "prepare",
//        args = {Connection.class, Integer.class}
//)})
public class SqlConverterInterceptor implements Interceptor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String METHOD_PREPARE = "prepare";
    /**
     * oracle独特函数
     */
    @Value("${mybatis.converter.oracleMethod:(to_char|to_date|decode|group_concat|sysdate|nvl|from dual|length|to_number|nvl2|rownumber|over|rank|start with|bwmsys.wm_concat)}")
    private String oracleMethod;

    /**
     * mysql独特函数
     */
    @Value("${mybatis.converter.mysqlMethod:(format|date_format|group_concat|now|ifnull|char_length|if|group_concat|substring|str_to_date)}")
    private String mysqlMethod;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if ("prepare".equals(invocation.getMethod().getName())) {
            StatementHandler handler = (StatementHandler) processTarget(invocation.getTarget());
            MetaObject metaObject = SystemMetaObject.forObject(handler);
            MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
            SqlCommandType sqlCommandType = ms.getSqlCommandType();
            if (SqlCommandType.UPDATE == sqlCommandType || SqlCommandType.SELECT == sqlCommandType) {
                this.sqlConvert(invocation, metaObject);
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private void sqlConvert(Invocation invocation, MetaObject metaObject) throws SQLException {
        String sql = metaObject.getValue("delegate.boundSql.sql").toString();
        Connection connection = (Connection) invocation.getArgs()[0];
        DbType dbType = JdbcUtils.getDbType(connection.getMetaData().getURL());
        this.logger.debug("jdbc type :{},intercept 原始sql :{} ", dbType, sql);
        long start = System.currentTimeMillis();
        switch (dbType) {
            case MYSQL:
                if (Pattern.compile("(?i)\\b" + oracleMethod + "\\b").matcher(sql).find()) {
                    this.logger.debug("包含Oracle特有函数");
                    sql = oracleToMysql(sql);
                    this.logger.info("Oracle特有函数转换 cost:{} ms;更新后sql为 :{}", System.currentTimeMillis() - start, "\n" + SQLUtils.formatMySql(sql.trim()));
                }
            case MARIADB:
                break;
            case ORACLE:
                if (Pattern.compile("(?i)\\b" + mysqlMethod + "\\b").matcher(sql).find()) {
                    this.logger.debug("包含mysql特有函数");
                    sql = mysqlToOracle(sql);
                    this.logger.info("mysql特有函数转换 cost:{} ms;更新后sql为 :{} ", System.currentTimeMillis() - start, "\n" + SQLUtils.formatOracle(sql.trim()));
                }
                break;
            default:
                this.logger.warn("The Database's convert Not Supported!");
        }

        metaObject.setValue("delegate.boundSql.sql", sql);
    }

    /**
     * oracle转Mysql
     *
     * @param sql
     * @return
     */
    private String oracleToMysql(String sql) {
        // ① 替换 `LIMIT` -> `AND ROWNUM &lt;= 1` TODO 【注： 部分包含`ORDER BY` 关键字，需单独处理】
        // 使用 PageHelper.startPage(query.getPageNum(), query.getPageSize()); 进行分页，不要使用硬编码

        // ③ '%'||#{name}||'%'  ->  CONCAT('%', #{name}, '%')    （Oracle中concat函数只能放两个参数）
        // oracle 和 mysql 都可以使用 concat('', '') 两个参数的方式
        // 要求oracle 和mysql使用 两个参数的这种方式

        // ④ `ORACLE_TO_UNIX` -> `UNIX_TIMESTAMP` date类型时间转10位时间戳
        sql = sql.replaceAll("ORACLE_TO_UNIX", "UNIX_TIMESTAMP");

        // ⑥ `IFNULL` -> `NVL`
        sql = sql.replaceAll("NVL", "IFNULL");

        // ⑦ 时间 `str_to_date` -> `to_date`     `date_format` -> `to_char`
        // `%Y-%m-%d`  -> `yyyy-MM-dd`    `%Y-%m` -> `yyyy-MM`
        sql = sql.replaceAll("TO_DATE", "str_to_date");
        sql = sql.replaceAll("TO_DATE", "STR_TO_DATE");
        sql = sql.replaceAll("TO_CHAR", "date_format");
        sql = sql.replaceAll("TO_CHAR", "DATE_FORMAT");

        // 这里注意替换顺序问题，最长的应该放最前面！！！
        sql = sql.replaceAll("yyyy-MM-dd HH24:mi:ss", "%Y-%m-%d %H:%i:%S");
        sql = sql.replaceAll("yyyy-MM-dd HH24:mi:ss", "%Y-%m-%d %H:%i:%s");
        sql = sql.replaceAll("yyyy-MM-dd HH24:mi", "%Y-%m-%d %H:%i");
        sql = sql.replaceAll("yyyy-MM-dd HH24", "%Y-%m-%d %H");
        sql = sql.replaceAll("yyyy-MM-dd HH", "%Y-%m-%d %h");
        sql = sql.replaceAll("yyyy-MM-dd", "%Y-%m-%d");
        sql = sql.replaceAll("yyyy-MM", "%Y-%m");
        sql = sql.replaceAll("yyyy", "%Y");
        sql = sql.replaceAll("HH24", "%H");
        sql = sql.replaceAll("HH24", "%k");

        sql = sql.replaceAll("(SELECT SYSDATE + 8/24 FROM DUAL)", "now\\(\\)");
        sql = sql.replaceAll("(SELECT SYSDATE + 8/24 FROM DUAL)", "NOW\\(\\)");
        return sql;
    }


    /**
     * Mysql 转 sqlLite
     *
     * @param sql
     * @return
     */
    private String mysqlToSqlLite(String sql) {
        // ③ CONCAT('%', #{name}, '%')  ->     '%'||#{name}||'%' （Oracle中concat函数只能放两个参数）
        while (sql.contains("concat(") || sql.contains("CONCAT")) {
            sql = sql.replaceAll("concat", "concat");
            sql = sql.replaceAll("CONCAT", "concat");
            int ci = sql.indexOf("concat(");
            int cidd = sql.indexOf(")", ci);
            String likes = sql.substring(ci + 7, cidd).replaceAll(",", " || ");
            sql = sql.substring(0, ci) + likes + sql.substring(cidd + 1);
        }
        return sql;
    }

    /**
     * mysql 转换成 oracle
     *
     * @param sql
     * @return
     */
    private String mysqlToOracle(String sql) {
        // ① 替换 `LIMIT` -> `AND ROWNUM &lt;= 1` TODO 【注： 部分包含`ORDER BY` 关键字，需单独处理】
        // 使用 PageHelper.startPage(query.getPageNum(), query.getPageSize()); 进行分页，不要使用硬编码

        // ② oracle中不能使用“ ` ”符号
        sql = StringUtils.replace(sql, "`", "");

        // ③ CONCAT('%', #{name}, '%')  ->     '%'||#{name}||'%' （Oracle中concat函数只能放两个参数）
        // oracle 和 mysql 都可以使用 concat('', '') 两个参数的方式
        // 要求oracle 和mysql使用 两个参数的这种方式

        // ④ `UNIX_TIMESTAMP` -> `ORACLE_TO_UNIX` date类型时间转10位时间戳
        sql = sql.replaceAll("UNIX_TIMESTAMP", "ORACLE_TO_UNIX");

        // ⑤ 部分关键字需加上双引号 TODO 【注： 字段名大写，映射的别名需保存原本小写！】 `level -> "LEVEL"`  `user -> "USER"`      `number -> "NUMBER"`  `desc -> "DESC"`
        List<String> keywordList = new ArrayList<>(Arrays.asList("level", "user", "number", "desc"));
        for (String e : keywordList) {
            // StringUtils.swapCase(e) : 大小写互换
            sql = sql.replaceAll(" " + e + " ", " \"" + StringUtils.swapCase(e) + "\" ");
            sql = sql.replaceAll("." + e + " ", "\\.\"" + StringUtils.swapCase(e) + "\" ");
            if (sql.endsWith(e) || sql.endsWith(e + ",")) {
                sql = sql.replaceAll(e, "\"" + StringUtils.swapCase(e) + "\"");
            }
        }
        if (sql.endsWith(" date") || sql.endsWith(" date,") || sql.endsWith(" 'date'") || sql.endsWith(" 'DATE'") || sql.endsWith("DATE")) {
            sql = sql.replaceAll(" date", " \"date\"");
            sql = sql.replaceAll(" date,", " \"date,\"");
            sql = sql.replaceAll(" 'date'", " \"date\"");
            sql = sql.replaceAll(" 'DATE'", " \"date\"");
            sql = sql.replaceAll(" DATE", " \"date\"");
        }
        sql = sql.replaceAll(" date ", " \"date\" ");
        sql = sql.replaceAll(" DATE ", " \"date\" ");

        // ⑥ `IFNULL` -> `NVL`
        sql = sql.replaceAll("IFNULL", "NVL");
        sql = sql.replaceAll("ifnull", "NVL");

        // ⑦ 时间 `str_to_date` -> `to_date`     `date_format` -> `to_char`
        // `%Y-%m-%d`  -> `yyyy-MM-dd`    `%Y-%m` -> `yyyy-MM`
        sql = sql.replaceAll("str_to_date", "TO_DATE");
        sql = sql.replaceAll("STR_TO_DATE", "TO_DATE");
        sql = sql.replaceAll("date_format", "TO_CHAR");
        sql = sql.replaceAll("DATE_FORMAT", "TO_CHAR");

        // 这里注意替换顺序问题，最长的应该放最前面！！！
        sql = sql.replaceAll("%Y-%m-%d %H:%i:%S", "yyyy-MM-dd HH24:mi:ss");
        sql = sql.replaceAll("%Y-%m-%d %H:%i:%s", "yyyy-MM-dd HH24:mi:ss");
        sql = sql.replaceAll("%Y-%m-%d %H:%i", "yyyy-MM-dd HH24:mi");
        sql = sql.replaceAll("%Y-%m-%d %H", "yyyy-MM-dd HH24");
        sql = sql.replaceAll("%Y-%m-%d %h", "yyyy-MM-dd HH");
        sql = sql.replaceAll("%Y-%m-%d", "yyyy-MM-dd");
        sql = sql.replaceAll("%Y-%m", "yyyy-MM");
        sql = sql.replaceAll("%Y", "yyyy");
        sql = sql.replaceAll("%H", "HH24");
        sql = sql.replaceAll("%k", "HH24");

        sql = sql.replaceAll("now\\(\\)", "(SELECT SYSDATE + 8/24 FROM DUAL)");
        sql = sql.replaceAll("NOW\\(\\)", "(SELECT SYSDATE + 8/24 FROM DUAL)");
        return sql;
    }

    private static Object processTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return processTarget(metaObject.getValue("h.target"));
        } else {
            return target;
        }
    }
}
