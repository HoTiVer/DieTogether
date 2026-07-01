package com.hotiver.dieTogether;

import com.hotiver.dieTogether.Listener.DamageListener;
import com.hotiver.dieTogether.Listener.DeathListener;
import com.hotiver.dieTogether.Listener.HealthScoreListener;
import com.hotiver.dieTogether.common.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class DieTogether extends JavaPlugin {

    private MessageManager messageManager;
    private Logger logger;

    @Override
    public void onEnable() {

        this.saveDefaultConfig();

        this.messageManager = new MessageManager(this);
        this.logger = this.getLogger();

        Bukkit.getPluginManager().registerEvents(new DamageListener(messageManager, logger, this), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new HealthScoreListener(), this);

        logger.log(Level.INFO, "Die Together has been enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
