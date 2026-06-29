package com.hotiver.dieTogether;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class DieTogether extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new HealthScoreListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
