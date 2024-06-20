package net.rodald.event.weapons;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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

public class GravityGun implements Listener {

    private final JavaPlugin plugin;
    private static final String GRAVITY_GUN_ITEM_NAME = "§6Gravity Gun";
    private double range = 6;
    private boolean mobRightClicked = false;
    private boolean rightClicking = false;
    private static Entity rightClickedMob;


    public GravityGun(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (rightClicking || mobRightClicked) {
                    rightClicking = false; // Reset the right-click status
                    mobRightClicked = false; // No right-click detected, reset mobRightClicked

                    if (rightClickedMob != null) {
                        rightClickedMob.setGravity(true);
                    }
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
            rightClicking = true;
            rightClickedMob = entity;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack item = event.getItem();

        rightClicking = true;
        if (item != null && item.getType() == Material.GOLDEN_HORSE_ARMOR &&
                item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(GRAVITY_GUN_ITEM_NAME) &&
                event.getAction().toString().contains("RIGHT")) {
            player.sendMessage(ChatColor.GREEN + "You are right clicking");
            gravityGunTick(player);
            player.sendMessage("Target has gravity: " + rightClickedMob.hasGravity());
        } else {
            if (!rightClicking) {
                mobRightClicked = false;
            }
        }
    }

    private void gravityGunTick(Player player) {
        Location playerPos = player.getLocation();
        playerPos.setY(playerPos.getY() + player.getEyeHeight());

        Vector direction = playerPos.getDirection();
        Vector rangeVector = direction.multiply(range);
        Location targetLocation = playerPos.add(rangeVector);


        if (rightClickedMob != null) {
            Location rightClickedMobPos = rightClickedMob.getLocation();
            rightClickedMob.setGravity(false);
            rightClickedMob.setFallDistance(0); // Ensures that entity takes no fall damage after release
            Vector gravityPull = targetLocation.toVector().subtract(rightClickedMobPos.toVector()).divide(new Vector(5, 5, 5));
            rightClickedMob.setVelocity(gravityPull);
        }
    }
}
