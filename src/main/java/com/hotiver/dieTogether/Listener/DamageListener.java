package com.hotiver.dieTogether.Listener;

import com.hotiver.dieTogether.common.Lang;
import com.hotiver.dieTogether.common.MessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class DamageListener implements Listener {

    private final Plugin plugin;
    private final MessageManager messageManager;
    private final Logger logger;

    public DamageListener(MessageManager messageManager, Logger logger, Plugin plugin) {
        this.messageManager = messageManager;
        this.logger = logger;
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            informAllPlayers(event, player);
        }
    }

    private void informAllPlayers(EntityDamageEvent event, Player player) {
        double playerOldHealth = player.getHealth();

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!player.isOnline()) return;

            double damageTaken = playerOldHealth - player.getHealth();

            if (damageTaken <= 0) return;

            String formattedDamage = String.format(Locale.ROOT,"%.1f", damageTaken);

            loggDamageReceiving(player, formattedDamage, event);

            broadCastMessage(player, formattedDamage, event);
        });
    }

    private void broadCastMessage(Player victim,
                                  String formattedDamage,
                                  EntityDamageEvent event) {
        Map<String, Component> messageCache = new HashMap<>();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String rawLocale = onlinePlayer.getLocale();
            String playerLang = (rawLocale != null && rawLocale.length() >= 2)
                    ? rawLocale.substring(0, 2).toUpperCase()
                    : "EN";

            Component message = messageCache.computeIfAbsent(playerLang, lang -> {
                Component sourceComponent = getDamageSource(event, victim, lang);

                return buildDamageMessageComponent(victim, formattedDamage, sourceComponent, lang);
            });

            onlinePlayer.sendMessage(message);
        }

    }

    private Component getDamageSource(EntityDamageEvent event, Player player, String langCode) {
        if (event instanceof EntityDamageByEntityEvent targetEvent) {

            Entity damager = targetEvent.getDamager();

            if (damager instanceof Projectile projectile
                    && projectile.getShooter() instanceof Entity shooter) {
                return Component.translatable(shooter.getType().translationKey());
            }
            return Component.translatable(damager.getType().translationKey());
        }

        String translationKey = getNotFromEntityDamageSourceKey(event.getCause());

        return Component.text(getMessageFromManager(translationKey, langCode));
    }

    private Component buildDamageMessageComponent(Player victim,
                                                  String formattedDamage,
                                                  Component sourceComponent,
                                                  String lang) {
        return Component.text("")
                .append(Component.text(victim.getName(), NamedTextColor.RED))
                .append(Component.text(getMessageFromManager("damage_receive", lang)))
                .append(Component.text(formattedDamage, NamedTextColor.GOLD))
                .append(Component.text(getMessageFromManager("damage", lang)))
                .append(Component.text(getMessageFromManager("damage_reason", lang)))
                .append(sourceComponent.color(NamedTextColor.GREEN));
    }

    private void loggDamageReceiving(Player player,
                                     String formattedDamage,
                                     EntityDamageEvent event) {

        Component sourceComponent = getDamageSource(event, player, "EN");

        Component logMessage = buildDamageMessageComponent(player,
                formattedDamage,
                sourceComponent,
                "en");

        String plainText = PlainTextComponentSerializer.plainText().serialize(logMessage);
        logger.info(plainText);
    }

    private String getNotFromEntityDamageSourceKey(EntityDamageEvent.DamageCause cause) {
        return switch (cause) {
            case FALL -> "death.attack.fall";
            case LAVA -> "death.attack.lava";
            case FIRE, FIRE_TICK -> "death.attack.inFire";
            case DROWNING -> "death.attack.drown";
            case STARVATION -> "death.attack.starve";
            case VOID -> "death.attack.outOfWorld";
            case FREEZE -> "death.attack.freeze";
            case MAGIC, POISON -> "death.attack.magic";
            case CRAMMING -> "death.attack.cramming";
            case WITHER -> "death.attack.wither";
            case SUFFOCATION -> "death.attack.inWall";
            case CONTACT -> "death.attack.contact";
            default -> "unknown-source";
        };
    }

    private String getMessageFromManager(String key, String langCode) {
        Lang enumLang = Lang.fromString(langCode);

        return messageManager.getMessageString(key, enumLang);
    }
}
