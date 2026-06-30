package com.hotiver.dieTogether.Listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private boolean isResetting = false;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (isResetting) return;

        isResetting = true;
        Player firstToDie = event.getEntity();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.equals(firstToDie)){
                player.setGameMode(GameMode.SPECTATOR);
                continue;
            }
            player.setHealth(0.0);
            player.setGameMode(GameMode.SPECTATOR);
        }
        isResetting = false;
    }

}
