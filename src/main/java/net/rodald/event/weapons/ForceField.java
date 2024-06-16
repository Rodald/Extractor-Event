package net.rodald.event.weapons;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ForceField implements Listener {

    private final JavaPlugin plugin;
    private static final String FORCE_FIELD_ITEM_NAME = ChatColor.AQUA + "Force Field";
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private int frame = 0;
    private final Map<UUID, Boolean> forceFieldActive = new HashMap<>();
    private final long COOLDOWN_DURATION = 50; // Cooldown-Dauer in Millisekunden (hier 500 Millisekunden)

    public ForceField(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static ItemStack createForceFieldItem() {
        ItemStack forceFieldItem = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta meta = forceFieldItem.getItemMeta();
        meta.setDisplayName(FORCE_FIELD_ITEM_NAME);
        forceFieldItem.setItemMeta(meta);
        return forceFieldItem;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.DIAMOND_HORSE_ARMOR &&
                item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(FORCE_FIELD_ITEM_NAME) &&
                event.getAction().toString().contains("RIGHT")) {

            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            long lastUseTime = cooldowns.getOrDefault(playerId, 0L);

            if (currentTime >= lastUseTime + COOLDOWN_DURATION) {
                // Aktiviere oder deaktiviere das Force Field
                toggleForceField(player);
                cooldowns.put(playerId, currentTime);
            } else {
                player.sendMessage(ChatColor.RED + "Force Field is on cooldown. Please wait.");
            }

            event.setCancelled(true); // Verhindert, dass das Item in die Hand genommen wird
        }
    }

    private void toggleForceField(Player player) {
        UUID playerId = player.getUniqueId();
        boolean isActive = forceFieldActive.getOrDefault(playerId, false);

        if (!isActive) {
            activateForceField(player);
        } else {
            deactivateForceField(player);
        }
    }

    private void activateForceField(Player player) {
        player.sendMessage(ChatColor.RED + "Activated Force Field");
        forceFieldActive.put(player.getUniqueId(), true);

        new BukkitRunnable() {
            Location playerLoc = player.getLocation().clone();
            Vector lastDirection = null;
            int radius = 7;
            double additionalY = -0.1; // Höhe hinzufügen
            double maxYVelocity = 0.6; // Maximale Y-Geschwindigkeit festlegen

            @Override
            public void run() {
                if (!forceFieldActive.getOrDefault(player.getUniqueId(), false) || !player.isOnline()) {
                    cancel();
                    forceFieldActive.put(player.getUniqueId(), false);
                    return;
                }

                Location newLoc = player.getLocation().clone();
                Vector direction = player.getLocation().getDirection().normalize(); // Richtung, in die der Spieler schaut
                Vector currentVelocity = player.getVelocity();

                // Füge dem Y-Vektor des Spielers Velocity hinzu
                currentVelocity.setY(currentVelocity.getY() + additionalY);

                // Begrenze die Y-Geschwindigkeit
                if (currentVelocity.getY() < -maxYVelocity) {
                    player.sendMessage("You are under the maxYVelocity");
                    currentVelocity.setY(currentVelocity.getY() * 0.8);
                } else if (currentVelocity.getY() > maxYVelocity*2) {
                    player.sendMessage("You are over the maxYVelocity");
                    currentVelocity.setY(currentVelocity.getY() * 0.85);
                }

                // Abstoßung von festen Blöcken im Radius
                for (double x = -radius; x <= radius; x++) {
                    for (double y = -radius; y <= radius; y++) {
                        for (double z = -radius; z <= radius; z++) {
                            Location blockLoc = playerLoc.clone().add(x, y, z);
                            Block block = blockLoc.getBlock();

                            // Überprüfe, ob der Spieler in der Nähe von festen Blöcken ist
                            if (!block.isEmpty() && block.getType().isSolid()) {
                                // Berechne die Richtung vom Block zum Spieler
                                Vector blockDirection = playerLoc.toVector().subtract(blockLoc.toVector()).normalize();

                                // Bewege den Spieler von den Blöcken weg
                                double forceMultiplier = 0.0005; // Stärke der Abstoßung anpassen
                                double forceHeightMultiplier = 1; // Stärke der Abstoßung anpassen
                                currentVelocity.add(blockDirection.multiply(forceMultiplier));
                                currentVelocity.setY(currentVelocity.getY() * forceHeightMultiplier);
                            }

                        }
                    }
                }

                if (playerLoc.getBlock().getType() == Material.WATER) {
                    currentVelocity.setY(currentVelocity.getY() + 0.6);
                }
                double walkSpeed;
                if (player.isSprinting()) {
                    walkSpeed = player.getWalkSpeed();
                } else walkSpeed = player.getWalkSpeed()/2;
                // Füge die Bewegungsrichtung hinzu
                currentVelocity.add(direction.multiply(walkSpeed)); // Stärke der Richtung anpassen
                player.setVelocity(currentVelocity);

                // Aktualisiere die Spielerposition für die nächste Iteration
                playerLoc = newLoc;
                frame++;
            }
        }.runTaskTimer(plugin, 0, 1); // Task alle 1 Tick ausführen
    }

    private void deactivateForceField(Player player) {
        player.sendMessage(ChatColor.RED + "Deactivated Force Field");
        forceFieldActive.put(player.getUniqueId(), false);
    }
}
