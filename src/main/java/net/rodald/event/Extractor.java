package net.rodald.event;

import net.rodald.event.scores.PlayerExtractionEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

public class Extractor extends BukkitRunnable {

    private final Plugin plugin;
    private final double radius;

    public Extractor(Plugin plugin, double radius) {
        this.plugin = plugin;
        this.radius = radius;
        startChecking();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Material blockInPlayer = player.getLocation().getBlock().getType();

            if (isPlayerNearExtractor(player, radius) && player.isSneaking()) {
                player.setMetadata("able_to_extract", new FixedMetadataValue(plugin, true));
            } else if (blockInPlayer != Material.LIGHT || !player.isSneaking()) {
                player.removeMetadata("able_to_extract", plugin);
            }
            if (player.hasMetadata("able_to_extract")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 10, 1, true, false, false));
                Bukkit.getPluginManager().callEvent(new PlayerExtractionEvent(player)); // Auslösen des Events
            }
        }
    }

    private boolean isPlayerNearExtractor(Player player, double radius) {
        for (Entity entity : player.getWorld().getEntities()) {
            if (entity instanceof TextDisplay) {
                TextDisplay text_display = (TextDisplay) entity;
                if (player.getLocation().distance(text_display.getLocation()) <= radius * radius) {
                    Material blockInPlayer = player.getLocation().getBlock().getType();
                    BlockState blockState = player.getLocation().getBlock().getState();
                    if (blockInPlayer == Material.LIGHT && blockState.getBlockData().getLightEmission() == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void startChecking() {
        this.runTaskTimer(plugin, 0, 1); // Run the task every tick
    }
}
