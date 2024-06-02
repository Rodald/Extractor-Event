package net.rodald.event;

import org.bukkit.entity.Player;

public class GameSpectator {
    public static void setSpectator(Player player, Boolean mode) {
        player.setAllowFlight(mode);
        player.setInvisible(mode);
        player.setInvulnerable(mode);
    }
}
