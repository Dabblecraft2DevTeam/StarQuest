package com.starquestminecraft.bungeecord.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariDatabase {

    public static HikariDataSource create(final String hostname, final String username, final String password, final String database) {

        HikariConfig dbconfig = new HikariConfig();

        dbconfig.setJdbcUrl("jdbc:mysql://" + hostname);
        dbconfig.setUsername(username);
        dbconfig.setPassword(password);
        dbconfig.setCatalog(database);

        dbconfig.setConnectionTimeout(10000);
        dbconfig.setIdleTimeout(0);
        dbconfig.setLeakDetectionThreshold(120000);
        dbconfig.setMaxLifetime(0);
        dbconfig.setMaximumPoolSize(16);
        dbconfig.setMinimumIdle(1);

        dbconfig.addDataSourceProperty("autoReconnect", "false");
        dbconfig.addDataSourceProperty("cachePrepStmts", "true");
        dbconfig.addDataSourceProperty("prepStmtCacheSize", "384");
        dbconfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dbconfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        dbconfig.addDataSourceProperty("useServerPrepStmts", "true");
        dbconfig.addDataSourceProperty("zeroDateTimeBehavior", "convertToNull");

        HikariDataSource source = new HikariDataSource(dbconfig);

        if(source.getIdleTimeout() == 0) {
            try(Connection con = source.getConnection()) {
                try(Statement s = con.createStatement()) {
                    try(ResultSet rs = s.executeQuery("SHOW VARIABLES LIKE 'wait_timeout'")) {
                        if(rs.next()) {
                            long timeout = Math.max(0, (rs.getInt(2) - 60) * 1000);
                            dbconfig.setIdleTimeout(timeout);
                            source.setIdleTimeout(timeout);
                        }
                    }
                }
            }
            catch(Exception ex) {

            }
        }

        return source;

    }

}
