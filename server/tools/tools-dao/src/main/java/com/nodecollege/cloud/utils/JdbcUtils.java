package com.nodecollege.cloud.utils;

import com.nodecollege.cloud.common.model.DbType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcUtils {
    private static final Logger logger = LoggerFactory.getLogger(JdbcUtils.class);

    public static DbType getDbType(String jdbcUrl) {
        if (StringUtils.isEmpty(jdbcUrl)) throw new RuntimeException("数据库连接地址不能为空！");
        if (jdbcUrl.startsWith("jdbc:mysql:") || jdbcUrl.startsWith("jdbc:cobar:") || jdbcUrl.startsWith("jdbc:log4jdbc:mysql:")) {
            return DbType.MYSQL;
        }
        if (jdbcUrl.startsWith("jdbc:sqlite:")) return DbType.SQLITE;
        if (jdbcUrl.startsWith("jdbc:oracle:") || jdbcUrl.startsWith("jdbc:log4jdbc:oracle:")) return DbType.ORACLE;
        if (jdbcUrl.startsWith("jdbc:sqlserver:") || jdbcUrl.startsWith("jdbc:microsoft:")) {
            return DbType.SQL_SERVER2005;
        }
        if (jdbcUrl.startsWith("jdbc:mariadb:")) return DbType.MARIADB;
        if (jdbcUrl.startsWith("jdbc:sqlserver2012:")) return DbType.SQL_SERVER;
        if (jdbcUrl.startsWith("jdbc:db2:")) return DbType.DB2;
        if (jdbcUrl.startsWith("jdbc:hsqldb:") || jdbcUrl.startsWith("jdbc:log4jdbc:hsqldb:")) return DbType.HSQL;
        if (jdbcUrl.startsWith("jdbc:h2:") || jdbcUrl.startsWith("jdbc:log4jdbc:h2:")) return DbType.H2;
        if (jdbcUrl.startsWith("jdbc:dm:") || jdbcUrl.startsWith("jdbc:log4jdbc:dm:")) return DbType.DM;

        if (jdbcUrl.startsWith("jdbc:postgresql:") || jdbcUrl.startsWith("jdbc:log4jdbc:postgresql:")) {
            return DbType.POSTGRE_SQL;
        }
        logger.warn("The jdbcUrl is " + jdbcUrl + ", Mybatis Plus Cannot Read Database type or The Database's Not Supported!");
        return DbType.OTHER;
    }

    public static void main(String[] args) {
        String sql = "jdbc:sqlite:D:/LC/nodecollege-upms/upms/db/sqlitedb3";
        System.out.println(JdbcUtils.getDbType(sql));
    }
}
