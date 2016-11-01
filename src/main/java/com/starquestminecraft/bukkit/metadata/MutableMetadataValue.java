package com.starquestminecraft.bukkit.metadata;

import org.bukkit.metadata.MetadataValueAdapter;
import org.bukkit.plugin.Plugin;

public class MutableMetadataValue extends MetadataValueAdapter {

    private Object value;

    public MutableMetadataValue(final Plugin plugin, final Object value) {

        super(plugin);

        this.value = value;

    }

    @Override
    public Object value() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    @Override
    public void invalidate() {

    }

}
