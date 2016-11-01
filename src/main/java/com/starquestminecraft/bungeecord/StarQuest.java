package com.starquestminecraft.bungeecord;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.starquestminecraft.bungeecord.database.HikariDatabase;

public class StarQuest {

    private static DataSource database;

    private StarQuest() {

    }

    static void initialize(final SQBase plugin) {

        setupDatabase(plugin);

    }

    static void setupDatabase(final SQBase plugin) {

        String hostname = plugin.getConfig().getString("database.hostname", "localhost");
        String username = plugin.getConfig().getString("database.username", "minecraft");
        String password = plugin.getConfig().getString("database.password", "");
        String dbname = plugin.getConfig().getString("database.database", "bungeecord");

        if((database != null) || (database instanceof AutoCloseable)) {
            try {
                ((AutoCloseable)database).close();
            }
            catch(Exception ex) {

            }
        }

        database = HikariDatabase.create(hostname, username, password, dbname);

        plugin.getLogger().info("[Database] Using " + username + "@" + hostname + " password: " + (password.isEmpty() ? "NO" : "YES") + ", database: " + dbname);

    }

    public static Connection getDatabaseConnection() throws SQLException {

        if(database == null) {
            throw new SQLException("DataSource not initialized!");
        }

        return database.getConnection();

    }

}
