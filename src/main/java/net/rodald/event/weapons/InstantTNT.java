package net.rodald.event.weapons;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InstantTNT implements Listener {

    private final JavaPlugin plugin;
    public static final String TNT_ITEM_NAME = ChatColor.RED + "Instant TNT";
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_DURATION = 1;
    private long lastTNTSpawnTime = 0;

    public InstantTNT(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static void giveCustomTNT(Player player) {
        ItemStack tntItem = new ItemStack(Material.TNT);
        ItemMeta meta = tntItem.getItemMeta();
        meta.setDisplayName(TNT_ITEM_NAME);
        tntItem.setItemMeta(meta);
        player.setItemOnCursor(tntItem);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.TNT &&
                item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(TNT_ITEM_NAME) &&
                event.getAction() == Action.RIGHT_CLICK_BLOCK) { // Check if the action is right-clicking a block

            event.setCancelled(true);

            // Check if the main hand is used
            if (event.getHand() == EquipmentSlot.HAND) {
                long currentTime = System.currentTimeMillis();
                // Check cooldown
                UUID playerId = player.getUniqueId();
                long lastUseTime = cooldowns.getOrDefault(playerId, 0L);

                if (currentTime >= lastUseTime + COOLDOWN_DURATION) {
                    // Place the TNT at the clicked block face location
                    Location location = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();
                    TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(location.add(0.5, 0, 0.5), EntityType.TNT);
                    tnt.setFuseTicks(40);

                    // TODO: There is a bug that the offhand spawn tnt and the mainhand. I really dont bother fixing it the correct way.
                    // TODO: If u want to know the correct way of doing it look it up in the cooking game plugin.
                    // Check if two TNTs are spawned within a short time frame
                    if (currentTime - lastTNTSpawnTime < 50) {
                        tnt.remove(); // Remove the duplicate TNT
                    } else {
                        lastTNTSpawnTime = currentTime;
                    }

                    // Reduce the item count by 1
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        player.getInventory().remove(item);
                    }

                    // Update cooldown
                    cooldowns.put(playerId, currentTime);
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(org.bukkit.event.entity.EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            event.blockList().clear(); // Prevent block damage
            for (Entity entity : event.getEntity().getNearbyEntities(7.5, 7.5, 7.5)) { // Adjust radius as needed
                if (entity instanceof Player) {
                    ((Player) entity).damage(6); // Adjust damage as needed
                }
            }
        }
    }
}
