package com.starquestminecraft.bungeecord;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class SQBase extends Plugin {

    private static SQBase instance;

    private Configuration config;
    private File file_config;

    public static SQBase getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {

        instance = this;

        file_config = new File(getDataFolder(), "config.yml");

        config = loadConfig();

        StarQuest.initialize(this);

    }

    @Override
    public void onEnable() {

    }

    public Configuration getConfig() {
        return config;
    }

    public void reloadConfig() {

        config = loadConfig();

    }

    private Configuration loadConfig() {

        if(file_config.exists()) {

            try {
                return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file_config);
            }
            catch(IOException ex) {
                getLogger().severe("Error loading '" + file_config.getName() + "': " + ex.getMessage());
            }

        }

        InputStream is = this.getResourceAsStream("config.yml");

        if(is != null) {

            try(InputStreamReader isr = new InputStreamReader(is)) {

                Configuration cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(isr);

                saveConfig(cfg);

                return cfg;

            }
            catch(IOException ex) {
                getLogger().severe("Error loading '" + file_config.getName() + "': " + ex.getMessage());
            }

        }

        return new Configuration();

    }

    public void saveConfig() {
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
            getLogger().severe("Error saving '" + file_config.getName() + "': " + ex.getMessage());
        }

    }

}
