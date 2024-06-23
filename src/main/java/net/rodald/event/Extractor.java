package net.rodald.event;

import net.kyori.adventure.text.format.TextColor;
import net.rodald.event.scores.PlayerExtractionEvent;
import net.rodald.event.scores.PlayerStatsScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
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
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Text;

public class Extractor extends BukkitRunnable {

    private final Plugin plugin;
    private final double radius;
    private PlayerStatsScoreboard playerStatsScoreboard;

    public Extractor(Plugin plugin, double radius, PlayerStatsScoreboard playerStatsScoreboard) {
        this.plugin = plugin;
        this.radius = radius;
        this.playerStatsScoreboard = playerStatsScoreboard;
        startChecking();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasMetadata("game_spectator")) { continue; }

            Material blockInPlayer = player.getLocation().getBlock().getType();

            if (isPlayerNearExtractor(player, radius) && player.isSneaking()) {
                player.setMetadata("able_to_extract", new FixedMetadataValue(plugin, true));
            } else if (blockInPlayer != Material.LIGHT || !player.isSneaking()) {
                player.removeMetadata("able_to_extract", plugin);
            }

            if (player.hasMetadata("able_to_extract")) {
                TextDisplay nearestTextDisplay = player.getWorld().getEntities().stream()

                        .filter(entity -> entity instanceof TextDisplay && !entity.equals(player))
                        .map(entity -> (TextDisplay) entity)
                        .min((entity1, entity2) -> Double.compare(player.getLocation().distance(entity1.getLocation()), player.getLocation().distance(entity2.getLocation())))
                        .orElse(null);


                if (player.getLocation().distance(nearestTextDisplay.getLocation()) >= 9) {
                    player.sendMessage(String.valueOf(player.getLocation().distance(nearestTextDisplay.getLocation())));
                    GameSpectator.setSpectator(player, true);

                    Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

                    TextColor color = scoreboard.getEntityTeam(player).displayName().children().get(0).style().color();
                    player.sendMessage(ChatColor.GREEN + "+12 points " + ChatColor.DARK_GREEN + "(Extraction)");
                    Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "[↑] " + convertTextColorToChatColor(color) + player.getName()  + ChatColor.GRAY + " extracted!");
                    playerStatsScoreboard.addExtraction(player);

                }
                    player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 10, 3, true, false, false));
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

    private ChatColor convertTextColorToChatColor(TextColor textColor) {
        // Hier ein einfaches Mapping als Beispiel, je nach API musst du dies anpassen
        if (String.valueOf(textColor).equals("red")) {
            return ChatColor.RED;
        } else if (String.valueOf(textColor).equals("blue")) {
            return ChatColor.BLUE;
        } else if (String.valueOf(textColor).equals("green")) {
            return ChatColor.GREEN;
        }
        return ChatColor.WHITE; // Standardfallback, wenn die Farbe nicht übereinstimmt
    }
}
