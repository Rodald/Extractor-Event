package net.rodald.event;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameSpectator {
    public static void setSpectator(Player player, Boolean mode) {
        player.setAllowFlight(mode);
        player.setInvisible(mode);
        player.setInvulnerable(mode);
    }
}
