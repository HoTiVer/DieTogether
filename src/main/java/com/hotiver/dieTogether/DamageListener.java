package com.hotiver.dieTogether;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.isApplicable(EntityDamageEvent.DamageModifier.BLOCKING) &&
                    e.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) < 0)
                return;
            informAllPlayers(e);
        }
    }

    private void informAllPlayers(EntityDamageEvent e) {
        String formattedDamage = String.format("%.1f", e.getDamage());

        String sourceName = getDamageSource(e);

        Component message = Component.text("")
                .append(Component.text(e.getEntity().getName(), NamedTextColor.RED))
                .append(Component.text(" receives "))
                .append(Component.text(formattedDamage, NamedTextColor.GOLD))
                .append(Component.text(" damage from "))
                .append(Component.text(sourceName, NamedTextColor.GREEN));

        Bukkit.broadcast(message);
    }

    private String getDamageSource(EntityDamageEvent e) {

        if (e instanceof EntityDamageByEntityEvent) {
            return ((EntityDamageByEntityEvent) e).getDamager().getName();
        }

        return e.getCause().name().toLowerCase().replace("_", " ");
    }

}
