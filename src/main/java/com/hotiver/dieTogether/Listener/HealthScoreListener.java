package com.hotiver.dieTogether.Listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;

import java.util.Objects;

public class HealthScoreListener implements Listener {

    private final Scoreboard scoreboard;
    private final Objective objective;

    public HealthScoreListener() {

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.scoreboard = manager.getNewScoreboard();

        this.objective = this.scoreboard.registerNewObjective(
                "HealthScore",
                "dummy",
                Component.text("Health", NamedTextColor.RED)
        );
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setScoreboard(scoreboard);

        updatePlayerHealth(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        scoreboard.resetScores(player.getName());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            Bukkit.getScheduler().runTask(Objects.requireNonNull(
                    Bukkit.getPluginManager().getPlugin("DieTogether")), () -> {
                updatePlayerHealth(player);
            });
        }
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player player) {
            Bukkit.getScheduler().runTask(Objects.requireNonNull(
                    Bukkit.getPluginManager().getPlugin("DieTogether")), () -> {
                updatePlayerHealth(player);
            });
        }
    }

    private void updatePlayerHealth(Player player) {
        int health = (int) Math.round(player.getHealth());

        Score score = objective.getScore(player.getName());
        score.setScore(health);
    }

}
