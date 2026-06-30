package com.hotiver.dieTogether.Listener;

import com.hotiver.dieTogether.common.Lang;
import com.hotiver.dieTogether.common.MessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DamageListener implements Listener {

    private final MessageManager messageManager;
    private final Logger logger;

    public DamageListener(MessageManager messageManager, Logger logger) {
        this.messageManager = messageManager;
        this.logger = logger;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.isApplicable(EntityDamageEvent.DamageModifier.BLOCKING) &&
                    event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) < 0)
                return;
            informAllPlayers(event, player);
        }
    }

    private void informAllPlayers(EntityDamageEvent event, Player player) {
        String formattedDamage = String.format("%.1f", event.getDamage());

        Component sourceComponent = getDamageSource(event, player);

        loggDamageReceiving(player, formattedDamage, sourceComponent);

        Map<String, Component> messageCache = new HashMap<>();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            String rawLocale = onlinePlayer.getLocale();
            String playerLang = (rawLocale != null && rawLocale.length() >= 2)
                    ? rawLocale.substring(0, 2).toUpperCase()
                    : "EN";

            Component message = messageCache.computeIfAbsent(playerLang, lang ->
                    Component.text("")
                    .append(Component.text(player.getName(), NamedTextColor.RED))
                    .append(Component.text(getMessageFromManager("damage_receive", lang)))
                    .append(Component.text(formattedDamage, NamedTextColor.GOLD))
                    .append(Component.text(getMessageFromManager("damage", lang)))
                    .append(Component.text(getMessageFromManager("damage_reason", lang)))
                    .append(sourceComponent.color(NamedTextColor.GREEN))
            );

            onlinePlayer.sendMessage(message);
        }
    }

    private Component getDamageSource(EntityDamageEvent event, Player player) {
        if (event instanceof EntityDamageByEntityEvent targetEvent) {
            return Component.translatable(targetEvent.getDamager().getType().translationKey());
        }

        String translationKey = getNotFromEntityDamageSourceKey(event.getCause());

        if (translationKey.startsWith("death.")) {
            return Component.translatable(translationKey, Component.text(player.getName()));
        }

        return Component.translatable(translationKey);
    }

    private String getMessageFromManager(String key, String lang) {
        Lang enumLang;
        try {
            enumLang = Lang.valueOf(lang);
        } catch (IllegalArgumentException e) {
            enumLang = Lang.EN;
        }
        return messageManager.getMessageString(key, enumLang);
    }

    private void loggDamageReceiving(Player player, String formattedDamage,
                                     Component sourceComponent) {
        Component logMessage = Component.text("")
                .append(Component.text(player.getName(), NamedTextColor.RED))
                .append(Component.text(getMessageFromManager("damage_receive", "EN")))
                .append(Component.text(formattedDamage, NamedTextColor.GOLD))
                .append(Component.text(getMessageFromManager("damage", "EN")))
                .append(Component.text(getMessageFromManager("damage_reason", "EN")))
                .append(sourceComponent.color(NamedTextColor.GREEN));

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
            default -> cause.name().toLowerCase().replace("_", " ");
        };
    }
}
