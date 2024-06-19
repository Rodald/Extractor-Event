package net.rodald.event.weapons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class GrapplingHook implements Listener {

    private static final String GRAPPLING_HOOK_NAME = "§1Grappling Hook";

    public static void giveGrapplingHook(Player player) {
        ItemStack grapplingHook = new ItemStack(Material.FISHING_ROD);
        ItemMeta meta = grapplingHook.getItemMeta();
        meta.setDisplayName(GRAPPLING_HOOK_NAME);
        grapplingHook.setItemMeta(meta);
        player.setItemOnCursor(grapplingHook);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && item.getType() == Material.FISHING_ROD &&
                item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(GRAPPLING_HOOK_NAME)) {

            if (event.getState() == PlayerFishEvent.State.REEL_IN || event.getState() == PlayerFishEvent.State.IN_GROUND) {
                FishHook hook = event.getHook();
                if (hook.getHookedEntity() == null || hook.getHookedEntity().getType() == EntityType.PLAYER) {
                    // Berechne die Richtung vom Spieler zum Haken
                    Vector hookLocation = hook.getLocation().toVector();
                    Vector playerLocation = player.getLocation().toVector();
                    Vector direction = hookLocation.subtract(playerLocation).normalize();

                    // Setze die Geschwindigkeit des Spielers in Richtung des Hakens
                    double distance = player.getLocation().distance(hook.getLocation());
                    player.setVelocity(direction.multiply(distance * 0.3)); // Stärke des Ziehens anpassen
                }
            }
        }
    }
}
