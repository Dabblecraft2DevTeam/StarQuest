package com.starquestminecraft.bungeecord;

import net.md_5.bungee.api.plugin.Listener;

public abstract class SQBungeeListener<P extends SQBungeePlugin> implements Listener {

    protected final P plugin;

    public SQBungeeListener(final P plugin) {
        this.plugin = plugin;
    }

}
