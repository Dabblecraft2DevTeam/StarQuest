package com.starquestminecraft.bukkit;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import com.starquestminecraft.bukkit.database.HikariDatabase;

public class StarQuest {

    private static DataSource database;
    private static Chat vault_chat;
    private static Economy vault_economy;
    private static Permission vault_permission;

    private StarQuest() {

    }

    static void initialize(final SQBase plugin) {

        setupDatabase(plugin);

    }

    static void setupDatabase(final SQBase plugin) {

        String hostname = plugin.getConfig().getString("database.hostname", "localhost");
        String username = plugin.getConfig().getString("database.username", "minecraft");
        String password = plugin.getConfig().getString("database.password", "");
        String dbname = plugin.getConfig().getString("database.database", plugin.getServer().getServerName());

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

    static void setupVault(final SQBase plugin) {

        if(plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }

        setupVaultChat(plugin);
        setupVaultEconomy(plugin);
        setupVaultPermission(plugin);

    }

    static void setupVaultChat(final SQBase plugin) {

        vault_chat = setupService(plugin, Chat.class, "Vault Chat", vault_chat);

    }

    static void setupVaultEconomy(final SQBase plugin) {

        vault_economy = setupService(plugin, Economy.class, "Vault Economy", vault_economy);

    }

    static void setupVaultPermission(final SQBase plugin) {

        vault_permission = setupService(plugin, Permission.class, "Vault Permission", vault_permission);

    }

    public static Connection getDatabaseConnection() throws SQLException {

        if(database == null) {
            throw new SQLException("DataSource not initialized!");
        }

        return database.getConnection();

    }

    public static Chat getVaultChat() {
        return vault_chat;
    }

    public static Economy getVaultEconomy() {
        return vault_economy;
    }

    public static Permission getVaultPermission() {
        return vault_permission;
    }

    private static <T> T setupService(final SQBase plugin, final Class<T> clazz, final String prefix, final T current) {

        RegisteredServiceProvider<T> provider = plugin.getServer().getServicesManager().getRegistration(clazz);
        T service = null;

        if(provider != null) {
            service = provider.getProvider();
        }

        if(current == service) {
            return current;
        }

        if(service != null) {

            String provider_class = service.getClass().getCanonicalName();
            String provider_plugin = provider.getPlugin().getDescription().getFullName();

            plugin.getLogger().info("[" + prefix + "] Using " + provider_class + " provided by " + provider_plugin);

        }
        else {
            plugin.getLogger().info("[" + prefix +"] No provider found!");
        }

        return service;

    }

}
