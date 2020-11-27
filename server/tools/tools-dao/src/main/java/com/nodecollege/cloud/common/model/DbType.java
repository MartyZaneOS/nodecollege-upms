package com.nodecollege.cloud.common.model;

public enum DbType {
    MYSQL("mysql", "%s LIKE CONCAT('%%',#{%s},'%%')", "MySql数据库"),
    MARIADB("mariadb", "%s LIKE CONCAT('%%',#{%s},'%%')", "MariaDB数据库"),
    ORACLE("oracle", "%s LIKE CONCAT(CONCAT('%%',#{%s}),'%%')", "Oracle数据库"),
    DB2("db2", "%s LIKE CONCAT(CONCAT('%%',#{%s}),'%%')", "DB2数据库"),
    H2("h2", "%s LIKE CONCAT('%%',#{%s},'%%')", "H2数据库"),
    HSQL("hsql", "%s LIKE CONCAT('%%',#{%s},'%%')", "HSQL数据库"),
    SQLITE("sqlite", "%s #{%s}||'%')", "SQLite数据库"),
    POSTGRE_SQL("postgresql", "%s LIKE CONCAT('%%',#{%s},'%%')", "Postgre数据库"),
    SQL_SERVER2005("sqlserver2005", "%s LIKE '%%'+#{%s}+'%%'", "SQLServer2005数据库"),
    SQL_SERVER("sqlserver", "%s LIKE '%%'+#{%s}+'%%'", "SQLServer数据库"),
    DM("dm", (String) null, "达梦数据库"),
    OTHER("other", (String) null, "其他数据库");

    private final String db;
    private final String like;
    private final String desc;

    DbType(String db, String like, String desc) {
        this.db = db;
        this.like = like;
        this.desc = desc;
    }

    public static DbType getDbType(String dbType) {
        DbType[] dts = values();
        DbType[] var2 = dts;
        int var3 = dts.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            DbType dt = var2[var4];
            if (dt.getDb().equalsIgnoreCase(dbType)) {
                return dt;
            }
        }

        return OTHER;
    }

    public String getDb() {
        return this.db;
    }

    public String getLike() {
        return this.like;
    }

    public String getDesc() {
        return this.desc;
    }
}