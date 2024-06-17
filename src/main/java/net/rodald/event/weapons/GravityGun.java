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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GravityGun implements Listener {

    private final JavaPlugin plugin;
    private static final String GRAVITY_GUN_ITEM_NAME = "§6Gravity Gun";
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Boolean> forceFieldActive = new HashMap<>();
    private final long COOLDOWN_DURATION = 50; // Cooldown in ms

    public GravityGun(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static void giveGravityGun(Player player) {
        ItemStack gravityGunItem = new ItemStack(Material.GOLDEN_HORSE_ARMOR);
        ItemMeta meta = gravityGunItem.getItemMeta();
        meta.setDisplayName(GRAVITY_GUN_ITEM_NAME);
        gravityGunItem.setItemMeta(meta);
        player.setItemOnCursor(gravityGunItem);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.GOLDEN_HORSE_ARMOR &&
                item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(GRAVITY_GUN_ITEM_NAME) &&
                event.getAction().toString().contains("RIGHT")) {

            UUID playerId = player.getUniqueId();

            gravityGunTick(Player player);
            event.setCancelled(true); // Verhindert, dass das Item in die Hand genommen wird
        }
    }

    private void gravityGunTick(Player player) {
        player.sendMessage(ChatColor.RED + "Activated Force Field");
            Location playerLoc = player.getLocation().clone();
            int radius = 7;
            double additionalY = -0.1; // Höhe hinzufügen
            double maxYVelocity = 0.6; // Maximale Y-Geschwindigkeit festlegen

                if (!forceFieldActive.getOrDefault(player.getUniqueId(), false) || !player.isOnline()) {
                    forceFieldActive.put(player.getUniqueId(), false);
                    return;
                }

                Location newLoc = player.getLocation().clone();


                Vector currentVelocity = player.getVelocity();
                Vector direction;
                // slows player down when he is sneaking
                direction = player.getLocation().getDirection().normalize(); // Richtung, in die der Spieler schaut


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


                                // Bewege den Spieler von den Blöcken weg
                                currentVelocity.setY(currentVelocity.getY() + forceMultiplier);
                                currentVelocity.setY(currentVelocity.getY() * forceHeightMultiplier);


                if (playerLoc.getBlock().getType() == Material.WATER) {
                    currentVelocity.setY(currentVelocity.getY() + 1);
                }
                double movementSpeed = player.getWalkSpeed() / 2;
                currentVelocity.add(direction.multiply(movementSpeed)); // Stärke der Richtung anpassen
                player.setVelocity(currentVelocity);

                // Aktualisiere die Spielerposition für die nächste Iteration
                playerLoc = newLoc;

    }

    private void deactivateForceField(Player player) {
        player.sendMessage(ChatColor.RED + "Deactivated Force Field");
        forceFieldActive.put(player.getUniqueId(), false);
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getItemMeta().getDisplayName().equals(GRAVITY_GUN_ITEM_NAME)) {
                ItemMeta forceFieldMeta = item.getItemMeta();
                forceFieldMeta.removeEnchant(Enchantment.INFINITY);
                item.setItemMeta(forceFieldMeta);
            }
        }
    }
}
