package net.rodald.event;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class GameSpectator {

    private static Plugin plugin;

    public GameSpectator(Plugin plugin) {
        this.plugin = plugin;
    }
        public static void setSpectator(Player player, Boolean mode) {
            if (mode) {
                player.setMetadata("game_spectator", new FixedMetadataValue(plugin, true));
                player.setGameMode(GameMode.ADVENTURE);
                player.getInventory().clear();
                player.setArrowsInBody(0);
                player.clearActivePotionEffects();
            } else {
                player.removeMetadata("game_spectator", plugin);
                if (StartGame.gameIsRunning) {
                    player.setGameMode(GameMode.ADVENTURE);
                } else {
                    player.setGameMode(GameMode.SURVIVAL);
                }
            }
            player.setAllowFlight(mode);
            player.setInvisible(mode);
            player.setInvulnerable(mode);
            player.setFlying(mode);
        }

        public static boolean getSpectator(Player player) {
            return player.hasMetadata("game_spectator");
        }
}
