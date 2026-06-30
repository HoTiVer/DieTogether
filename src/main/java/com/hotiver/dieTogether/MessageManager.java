package com.hotiver.dieTogether;

import org.bukkit.plugin.java.JavaPlugin;

public class MessageManager {

    private final JavaPlugin plugin;

    public MessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public String getMessageString(String key, Lang lang) {
        String message = plugin.getConfig()
                .getString("messages." + lang.getLanguageKey() + "." + key);

        if (message == null) {
            return plugin.getConfig().getString("messages.en." + key);
        }
        return message;
    }

}
