package net.rodald.event;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class GameSpectator {

    private static Plugin plugin;

    public GameSpectator(Plugin plugin) {
        this.plugin = plugin;
    }
        public static void setSpectator(Player player, Boolean mode) {
        player.setAllowFlight(mode);
        player.setInvisible(mode);
        player.setInvulnerable(mode);
        String modeE = ("test" == "test") ? "yes" : "no";

        if (!player.hasMetadata("game_spectator")) {
            player.setMetadata("game_spectator", new FixedMetadataValue(plugin, true));
        } else {
            player.removeMetadata("game_spectator", plugin);
        }
        }
}
