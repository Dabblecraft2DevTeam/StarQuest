package com.starquestminecraft.bungeecord;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public abstract class SQBungeePlugin extends Plugin {

    private Configuration config;
    private File file_config;

    @Override
    public final void onLoad() {

        file_config = new File(getDataFolder(), "config.yml");
        config = loadConfig();

        try {
        load();
        }
        catch(Exception ex) {
            logSevere("Exception loading " + getDescription().getName() + ":", ex);
        }

    }

    @Override
    public final void onEnable() {

        try {
            enable();
        }
        catch(Exception ex) {
            logSevere("Exception enabling " + getDescription().getName() + ":", ex);
        }

    }

    @Override
    public final void onDisable() {

        try {
            disable();
        }
        catch(Exception ex) {
            logSevere("Exception disabling " + getDescription().getName() + ":", ex);
        }

    }

    protected void load() throws Exception {

    }

    protected void enable() throws Exception {

    }

    protected void disable() throws Exception {

    }

    public final Configuration getConfig() {
        return config;
    }

    public final void reloadConfig() {

        config = loadConfig();

    }

    private Configuration loadConfig() {

        if(file_config.exists()) {

            try {
                return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file_config);
            }
            catch(IOException ex) {
                logSevere("Error loading '" + file_config.getName() + "': " + ex.getMessage());
            }

        }

        InputStream is = getResourceAsStream("config.yml");

        if(is != null) {

            try(InputStreamReader isr = new InputStreamReader(is)) {

                Configuration cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(isr);

                saveConfig(cfg);

                return cfg;

            }
            catch(IOException ex) {
                logSevere("Error loading '" + file_config.getName() + "': " + ex.getMessage());
            }

        }

        return new Configuration();

    }

    public final void saveConfig() {
        saveConfig(config);
    }

    private void saveConfig(final Configuration config) {

        try {

            if(!file_config.exists()) {
                file_config.getParentFile().mkdirs();
                file_config.createNewFile();
            }

            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file_config);

        }
        catch(IOException ex) {
            logSevere("Error saving '" + file_config.getName() + "': " + ex.getMessage());
        }

    }

    public void logInfo(final String message) {
        getLogger().log(Level.INFO, message);
    }

    public void logWarning(final String message) {
        getLogger().log(Level.WARNING, message);
    }

    public void logSevere(final String message) {
        getLogger().log(Level.SEVERE, message);
    }

    public void logSevere(final String message, final Throwable throwable) {
        getLogger().log(Level.SEVERE, message, throwable);
    }

}
