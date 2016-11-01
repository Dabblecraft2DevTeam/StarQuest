package com.starquestminecraft.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class SQBase extends JavaPlugin {

    private static SQBase instance;

    public static SQBase getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {

        instance = this;

        saveDefaultConfig();

        StarQuest.initialize(this);

    }

    @Override
    public void onEnable() {

        StarQuest.setupVault(this);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onServiceRegister(final ServiceRegisterEvent event) {

        onServiceChange(event.getProvider().getService());

    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onServiceRegister(final ServiceUnregisterEvent event) {

        onServiceChange(event.getProvider().getService());

    }

    private void onServiceChange(final Class<?> service) {

        if(service.equals(Chat.class)) {
            StarQuest.setupVaultChat(this);
        }
        else if(service.equals(Economy.class)) {
            StarQuest.setupVaultEconomy(this);
        }
        else if(service.equals(Permission.class)) {
            StarQuest.setupVaultPermission(this);
        }

    }

}
