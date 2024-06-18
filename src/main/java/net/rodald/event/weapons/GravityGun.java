package net.rodald.event.weapons;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
    private boolean mobRightClicked = false;
    private boolean rightClicking = false;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Boolean> forceFieldActive = new HashMap<>();
    private static Entity rightClickedMob;

    public GravityGun(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (rightClicking) {
                    rightClicking = false; // Reset the right-click status
                } else {
                    mobRightClicked = false; // No right-click detected, reset mobRightClicked
                }
            }
        }.runTaskTimer(plugin, 0, 1); // Run every tick
    }



    public static void giveGravityGun(Player player) {
        ItemStack gravityGunItem = new ItemStack(Material.GOLDEN_HORSE_ARMOR);
        ItemMeta meta = gravityGunItem.getItemMeta();
        meta.setDisplayName(GRAVITY_GUN_ITEM_NAME);
        gravityGunItem.setItemMeta(meta);
        player.setItemOnCursor(gravityGunItem);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity.getType() instanceof EntityType) {
            mobRightClicked = true;
            rightClickedMob = entity;
            rightClicking = true;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.GOLDEN_HORSE_ARMOR &&
                item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(GRAVITY_GUN_ITEM_NAME) &&
                event.getAction().toString().contains("RIGHT")) {

            gravityGunTick(player);
            if (rightClickedMob != null) {
                rightClickedMob.setGravity(true);
            }
        } else {
            player.sendMessage(ChatColor.RED + "You stopped rightclicking");
            if (!rightClicking) {
                mobRightClicked = false;
            }
            if (rightClickedMob != null) {
                rightClickedMob.setVelocity(new Vector(0, 0, 0));
                rightClickedMob.setGravity(false);
            }
            rightClickedMob = null;
        }
    }

    private void gravityGunTick(Player player) {
        // Überprüfe, ob ein Mob rechtsgeklickt wurde
        if (mobRightClicked && rightClicking) {
            player.sendMessage(ChatColor.GREEN + "You right-clicked a mob!");

        }
        Location playerPos = player.getLocation();
        playerPos.setY(playerPos.getY() + player.getEyeHeight());

        Vector direction = playerPos.getDirection(); // Richtung, in die der Spieler schaut.
        Vector rangeVector = direction.multiply(6);
        Location targetLocation = playerPos.add(rangeVector);
        if (rightClickedMob != null) {
            Location rightClickedMobPos = rightClickedMob.getLocation();
            Vector gravityPull = targetLocation.toVector().subtract(rightClickedMobPos.toVector()).divide(new Vector(5, 5, 5));
            rightClickedMob.setVelocity(gravityPull);
        }

    }
}
