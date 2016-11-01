package com.starquestminecraft.bukkit.util;

import java.util.List;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

public final class MetadataUtil {

    private MetadataUtil() {

    }

    public static MetadataValue getMetadata(final Metadatable metadatable, final String key, final Plugin plugin) {

        List<MetadataValue> metadata = metadatable.getMetadata(key);

        if(!metadata.isEmpty()) {

            for(MetadataValue value : metadata) {

                if(plugin.equals(value.getOwningPlugin())) {
                    return value;
                }

            }

        }

        return null;

    }

}
