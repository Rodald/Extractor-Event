package net.rodald.event.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class MovableBlock implements Listener {

    private final JavaPlugin plugin;
    private static final String MOVABLE_BLOCK_ITEM_NAME = "§6Movable Block";
    private Shulker shulker;
    private BlockDisplay blockDisplay;
    private Slime slime;

    public MovableBlock(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void spawnMovableBlock(Location location) {
        // Spawne den Shulker
        shulker = (Shulker) location.getWorld().spawnEntity(location, EntityType.SHULKER);
        shulker.setAI(false);
        shulker.setInvulnerable(true);
        shulker.setCustomName(MOVABLE_BLOCK_ITEM_NAME);
        shulker.setCustomNameVisible(false);
        shulker.setSilent(true);


        slime = (Slime) location.getWorld().spawnEntity(location, EntityType.SLIME);
        slime.setSize(2);
        slime.setAI(false);

        // Spawne den FallingBlock
        blockDisplay = (BlockDisplay) location.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);

        blockDisplay.setBlock(Material.IRON_BLOCK.createBlockData());


        // Synchronisiere den FallingBlock mit dem Shulker
        new BukkitRunnable() {
            @Override
            public void run() {
                if (shulker.isDead() || blockDisplay.isDead() || slime.isDead()) {
                    cancel();
                } else {
                    // blockDisplay.teleport(slime.getLocation().add(0, -0.5, 0));
                    shulker.teleport(blockDisplay.getLocation().add(0, -0.5, 0));
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Shulker) {
            Shulker clickedShulker = (Shulker) event.getRightClicked();
            if (MOVABLE_BLOCK_ITEM_NAME.equals(clickedShulker.getCustomName())) {
                // Block bewegen
                Location playerLocation = player.getLocation();
                Vector direction = playerLocation.getDirection().multiply(2);
                Location newLocation = clickedShulker.getLocation().add(direction);

                // Shulker teleportieren
                clickedShulker.teleport(newLocation);
            }
        }
    }
}
