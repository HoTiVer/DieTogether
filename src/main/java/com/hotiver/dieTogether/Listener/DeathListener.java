package com.hotiver.dieTogether.Listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

public class DeathListener implements Listener {

    private final Plugin plugin;
    private boolean isResetting = false;

    public DeathListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (isResetting) return;

        isResetting = true;
        Player firstToDie = event.getEntity();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(firstToDie.getUniqueId())) {
                continue;
            }
            player.damage(10000.0);
        }
        Bukkit.getScheduler().runTask(plugin, () ->
            isResetting = false
        );
    }

}
