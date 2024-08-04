package net.rodald.event.weapons;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PortalGun implements Listener {
    private final JavaPlugin plugin;
    private static final String PORTAL_GUN_NAME = "§6Portal Gun";
    private Location portalA = null;
    private Location portalB = null;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_DURATION = 100;

    public PortalGun(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static void givePortalGun(Player player) {
        ItemStack portalGun = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = portalGun.getItemMeta();
        meta.setDisplayName(PORTAL_GUN_NAME);
        portalGun.setItemMeta(meta);
        player.setItemOnCursor(portalGun);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.IRON_HORSE_ARMOR &&
                item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(PORTAL_GUN_NAME)) {

            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            long lastUseTime = cooldowns.getOrDefault(playerId, 0L);

            if (currentTime >= lastUseTime + COOLDOWN_DURATION) {
                if (event.getAction().toString().contains("LEFT_CLICK")) {
                    createPortalA(player);
                } else if (event.getAction().toString().contains("RIGHT_CLICK")) {
                    createPortalB(player);
                }

                float yaw = player.getYaw();
                float pitch = player.getPitch();
                if (portalA != null && portalB != null) {
                    if (player.getLocation().distance(portalA) < 2) {
                        player.teleport(portalB);

                        player.setRotation(yaw, pitch);
                    } else if (player.getLocation().distance(portalB) < 2) {
                        player.teleport(portalA);
                        player.setRotation(yaw, pitch);
                    }
                }

                cooldowns.put(playerId, currentTime);
            } else {
                player.sendMessage(ChatColor.RED + "Portal Gun is on cooldown. Please wait.");
            }

            event.setCancelled(true);
        }
    }

    private void createPortalA(Player player) {
        if (portalA != null) {
            // Entferne das alte Portal A
            player.getWorld().spawnParticle(Particle.EXPLOSION, portalA, 50, 0.5, 0.5, 0.5, 0.1);
        }
        portalA = player.getTargetBlockExact(50).getLocation();
        player.getWorld().spawnParticle(Particle.DUST, portalA, 500, 0.5, 1, 0.5, new Particle.DustOptions(org.bukkit.Color.BLUE, 1)); // Blaue Partikel
        spawnPortalParticles(portalA, Particle.DUST, org.bukkit.Color.BLUE);
    }

    private void createPortalB(Player player) {
        if (portalB != null) {
            // kills old portal
            player.getWorld().spawnParticle(Particle.EXPLOSION, portalB, 50, 0.5, 0.5, 0.5, 0.1);
        }
        portalB = player.getTargetBlockExact(50).getLocation();
        player.getWorld().spawnParticle(Particle.DUST, portalB, 500, 0.5, 1, 0.5, new Particle.DustOptions(org.bukkit.Color.ORANGE, 1)); // Orange Partikel
        spawnPortalParticles(portalB, Particle.DUST, org.bukkit.Color.ORANGE);
    }

    private void spawnPortalParticles(Location location, Particle particle, org.bukkit.Color color) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if ((location.equals(portalA) || location.equals(portalB))) {
                    location.getWorld().spawnParticle(particle, location, 500, 0.5, 1, 0.5, 0.1, new Particle.DustOptions(color, 1));
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 2);
    }
}
