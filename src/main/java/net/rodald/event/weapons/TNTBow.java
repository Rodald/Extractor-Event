package net.rodald.event.weapons;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TNTBow implements Listener {
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                if (player.getInventory().getItemInMainHand().getType() == Material.BOW
                        && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§cTNT Bow")) {

                    Location hitLocation = arrow.getLocation();
                    TNTPrimed tnt = hitLocation.getWorld().spawn(hitLocation, TNTPrimed.class);
                    tnt.setFuseTicks(0);
                    arrow.remove();
                }
            }
        }
    }

    public static void giveBow(Player player) {
        ItemStack tntBow = new ItemStack(Material.BOW);
        ItemMeta meta = tntBow.getItemMeta();
        meta.setDisplayName("§cTNT Bow");
        tntBow.setItemMeta(meta);
        player.setItemOnCursor(tntBow);
    }

}
