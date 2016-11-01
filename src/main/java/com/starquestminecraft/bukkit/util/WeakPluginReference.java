package com.starquestminecraft.bukkit.util;

import java.lang.ref.WeakReference;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class WeakPluginReference {

    private final String name;

    private WeakReference<Plugin> ref_plugin;

    public WeakPluginReference(final String name) {
        this.name = name;
    }

    public <P extends Plugin> P get() {

        Plugin plugin = ref_plugin.get();

        if(plugin == null) {

            plugin = Bukkit.getPluginManager().getPlugin(name);

            if(plugin != null) {
                ref_plugin = new WeakReference<>(plugin);
            }

        }

        return (P)plugin;

    }

    public boolean isEnabled() {

        Plugin plugin = get();

        if(plugin != null) {
            return plugin.isEnabled();
        }

        return false;

    }

    public boolean isLoaded() {
        return(get() != null);
    }

}
