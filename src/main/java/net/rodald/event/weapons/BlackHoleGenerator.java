package net.rodald.event.weapons;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlackHoleGenerator implements Listener {

    private final JavaPlugin plugin;
    private static final String BLACK_HOLE_ITEM_NAME = ChatColor.BLACK + "Black Hole Generator";
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_DURATION = 1000;
    private Player shooter;

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
                item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(BLACK_HOLE_ITEM_NAME) &&
                event.getAction().toString().contains("RIGHT_CLICK")) {

            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            long lastUseTime = cooldowns.getOrDefault(playerId, 0L);

            if (currentTime >= lastUseTime + COOLDOWN_DURATION) {
                // Shoot a snowball as a placeholder for the black hole
                Snowball snowball = player.launchProjectile(Snowball.class);
                shooter = player;
                snowball.setCustomName("BlackHole");
                cooldowns.put(playerId, currentTime);
            } else {
                player.sendMessage(ChatColor.RED + "Black Hole Generator is on cooldown. Please wait.");
            }

            event.setCancelled(true); // Prevents the item from being used
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball) {
            Snowball snowball = (Snowball) event.getEntity();
            if ("BlackHole".equals(snowball.getCustomName())) {
                createBlackHole(snowball.getLocation());
            }
        }
    }

    private void createBlackHole(Location location) {
        World world = location.getWorld();
        world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);

        new BukkitRunnable() {
            int duration = 400; // Duration of the black hole in ticks (20 seconds)
            double radius = 7.5; // Radius of the black hole effect

            @Override
            public void run() {
                if (duration-- <= 0) {
                    cancel();
                    return;
                }

                // Display particle effects
                world.spawnParticle(Particle.DUST, location, 500, radius / 3, radius / 3, radius / 3, 0, new Particle.DustOptions(Color.BLACK, 3));

                // Attract nearby entities
                for (Entity entity : world.getNearbyEntities(location, radius, radius, radius)) {
                    if (entity instanceof Player) {
                        if (entity == shooter) continue;  // makes sure that shooter doesnt take damage
                        Player player = (Player) entity;
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1, true, false));
                    }
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        livingEntity.damage(1.25, shooter);
                    }
                    Vector direction = location.toVector().subtract(entity.getLocation().toVector()).normalize();
                    entity.setVelocity(entity.getVelocity().add(direction.multiply(0.1))); // Adjust the strength of the attraction
                }
            }
        }.runTaskTimer(plugin, 0, 1); // Task every tick
    }
}
