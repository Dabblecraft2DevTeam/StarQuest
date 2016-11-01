package com.starquestminecraft.bungeecord;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public abstract class SQBungeeCommand<P extends SQBungeePlugin> extends Command {

    protected static final TextComponent TC_MESSAGE_CONSOLE_ONLY = new TextComponent("This command can only be run from console!");

    static {

        TC_MESSAGE_CONSOLE_ONLY.setColor(ChatColor.RED);

    }

    protected final P plugin;

    public SQBungeeCommand(final P plugin, final String name) {

        super(name);

        this.plugin = plugin;

    }

    public SQBungeeCommand(final P plugin, final String name, final String permission, final String... aliases) {

        super(name, permission, aliases);

        this.plugin = plugin;

    }

}
