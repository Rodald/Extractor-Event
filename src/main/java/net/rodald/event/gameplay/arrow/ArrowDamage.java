package net.rodald.event.gameplay.arrow;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ArrowDamage implements Listener {

    private final Set<Arrow> trackedArrows = new HashSet<>();

    public ArrowDamage(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Team playerTeam = getTeam(player);

            Team[] validTeams = {
                    Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Red"),
                    Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Green"),
                    Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Blue")
            };

            if (playerTeam != null && Arrays.stream(validTeams).anyMatch(i -> i.equals(playerTeam))) {
                if (event.getProjectile() instanceof Arrow) {
                    Arrow arrow = (Arrow) event.getProjectile();
                    trackedArrows.add(arrow);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if (trackedArrows.contains(arrow)) {
                if (event.getEntity() instanceof Player) {
                    // Formula: wantedDamage = unknownDamage*(1-(min(20, max(6/5, 6-(4*unknownDamage)/8)))/25)

                    // set damage to make player do exactly 1 heart of damage even if player wears armor
                    event.setDamage(2.470910553583);
                    trackedArrows.remove(arrow);
                    arrow.remove(); // Remove the arrow after applying custom damage
                }
            }
        }
    }

    private Team getTeam(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        for (Team team : scoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                return team;
            }
        }
        return null;
    }
}
