package net.rodald.event.weapons;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


// TODO: x is not finite error in console whenever player is inside a block
public class ForceField implements Listener {

    private final JavaPlugin plugin;
    private static final String FORCE_FIELD_ITEM_NAME = "§3Force Field";
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Boolean> forceFieldActive = new HashMap<>();
    private final long COOLDOWN_DURATION = 50; // Cooldown in ms

    public ForceField(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static void giveForceField(Player player) {
        ItemStack forceFieldItem = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta meta = forceFieldItem.getItemMeta();
        meta.setDisplayName(FORCE_FIELD_ITEM_NAME);
        forceFieldItem.setItemMeta(meta);
        player.setItemOnCursor(forceFieldItem);
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

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getItemMeta().getDisplayName().equals(FORCE_FIELD_ITEM_NAME)) {
                ItemMeta forceFieldMeta = item.getItemMeta();
                forceFieldMeta.addEnchant(Enchantment.INFINITY, 1, true);
                item.setItemMeta(forceFieldMeta);
            }
        }

        new BukkitRunnable() {
            Location playerLoc = player.getLocation().clone();
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


                Vector currentVelocity = player.getVelocity();
                Vector direction;
                // slows player down when he is sneaking
                if (player.isSneaking()) {
                    direction = player.getLocation().getDirection().normalize().setY(0); // Richtung, in die der Spieler schaut
                } else if (player.isSprinting()) {
                    direction = player.getLocation().getDirection().normalize().multiply(2); // Richtung, in die der Spieler schaut
                } else {
                    direction = new Vector(0, 0, 0);
                }

                // Füge dem Y-Vektor des Spielers Velocity hinzu
                currentVelocity.setY(currentVelocity.getY() + additionalY);

                // Begrenze die Y-Geschwindigkeit
                if (currentVelocity.getY() < -maxYVelocity) {
                    currentVelocity.setY(currentVelocity.getY() * 0.85);
                } else if (currentVelocity.getY() > maxYVelocity*100) {
                    currentVelocity.setY(currentVelocity.getY() * 0.98);
                }

                double forceMultiplier = 0.0005; // Stärke der Abstoßung anpassen
                double forceHeightMultiplier = 1; // Stärke der Abstoßung anpassen

                // Abstoßung von festen Blöcken im Radius
                for (double x = -radius; x <= radius; x++) {
                    for (double y = -radius; y <= radius; y++) {
                        for (double z = -radius; z <= radius; z++) {
                            Location blockLoc = playerLoc.clone().add(x, y, z);
                            Block block = blockLoc.getBlock();

                            // Überprüfe, ob der Spieler in der Nähe von festen Blöcken ist
                            if (!block.isEmpty() && (block.getType().isSolid())) {
                                // Berechne die Richtung vom Block zum Spieler
                                Vector blockDirection = playerLoc.toVector().subtract(blockLoc.toVector()).normalize();

                                // Bewege den Spieler von den Blöcken weg
                                currentVelocity.add(blockDirection.multiply(forceMultiplier));
                                currentVelocity.setY(currentVelocity.getY() * forceHeightMultiplier);
                            }

                            if (!block.isEmpty() && (block.isLiquid() && y <= radius)) {
                                // Berechne die Richtung vom Block zum Spieler
                                Vector blockDirection = playerLoc.toVector().subtract(blockLoc.toVector()).normalize();

                                // Bewege den Spieler von den Blöcken weg
                                currentVelocity.setY(currentVelocity.getY() + forceMultiplier);
                                currentVelocity.setY(currentVelocity.getY() * forceHeightMultiplier);
                            }
                        }
                    }
                }

                if (playerLoc.getBlock().getType() == Material.WATER) {
                    currentVelocity.setY(currentVelocity.getY() + 1);
                }
                double movementSpeed = player.getWalkSpeed() / 2;
                currentVelocity.add(direction.multiply(movementSpeed)); // Stärke der Richtung anpassen
                player.setVelocity(currentVelocity);

                // Aktualisiere die Spielerposition für die nächste Iteration
                playerLoc = newLoc;
            }
        }.runTaskTimer(plugin, 0, 1); // Task alle 1 Tick ausführen
    }

    private void deactivateForceField(Player player) {
        player.sendMessage(ChatColor.RED + "Deactivated Force Field");
        forceFieldActive.put(player.getUniqueId(), false);
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getItemMeta().getDisplayName().equals(FORCE_FIELD_ITEM_NAME)) {
                ItemMeta forceFieldMeta = item.getItemMeta();
                forceFieldMeta.removeEnchant(Enchantment.INFINITY);
                item.setItemMeta(forceFieldMeta);
            }
        }
    }
}
