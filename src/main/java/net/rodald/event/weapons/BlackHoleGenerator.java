package net.rodald.event.weapons;

import org.bukkit.*;
import org.bukkit.entity.Entity;
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

public class BlackHoleGenerator implements Listener {

    private final JavaPlugin plugin;
    private static final String BLACK_HOLE_ITEM_NAME = "§0Black Hole Generator";
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_DURATION = 10000; // 10 seconds cooldown

    public BlackHoleGenerator(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static void giveBlackHoleGenerator(Player player) {
        ItemStack blackHole = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = blackHole.getItemMeta();
        meta.setDisplayName(BLACK_HOLE_ITEM_NAME);
        blackHole.setItemMeta(meta);
        player.setItemOnCursor(blackHole);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.NETHER_STAR &&
                item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(BLACK_HOLE_ITEM_NAME) &&
                event.getAction().toString().contains("RIGHT_CLICK")) {

            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            long lastUseTime = cooldowns.getOrDefault(playerId, 0L);

            if (currentTime >= lastUseTime + COOLDOWN_DURATION) {
                createBlackHole(player.getLocation());
                cooldowns.put(playerId, currentTime);
            } else {
                player.sendMessage(ChatColor.RED + "Black Hole Generator is on cooldown. Please wait.");
            }

            event.setCancelled(true); // Verhindert, dass das Item in die Hand genommen wird
        }
    }

    private void createBlackHole(Location location) {
        World world = location.getWorld();
        world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);

        new BukkitRunnable() {
            int duration = 100; // Duration of the black hole in ticks (5 seconds)
            double radius = 10.0; // Radius of the black hole effect

            @Override
            public void run() {
                if (duration-- <= 0) {
                    cancel();
                    return;
                }

                // Display particle effects
                world.spawnParticle(Particle.PORTAL, location, 100, radius / 2, radius / 2, radius / 2, 0.1);

                // Attract nearby entities
                for (Entity entity : world.getNearbyEntities(location, radius, radius, radius)) {
                    if (entity instanceof Player && ((Player) entity).getUniqueId().equals(location.getWorld().getSpawnLocation().getBlock().getLocation().getWorld().getName())) {
                        continue;
                    }
                    Vector direction = location.toVector().subtract(entity.getLocation().toVector()).normalize();
                    entity.setVelocity(direction.multiply(0.2)); // Adjust the strength of the attraction
                }
            }
        }.runTaskTimer(plugin, 0, 1); // Task alle 1 Tick ausführen
    }
}
