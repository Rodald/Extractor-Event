package net.rodald.event.scores;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PlayerStatsScoreboard implements Listener {

    private final JavaPlugin plugin;
    private final Map<Player, Integer> playerKills = new HashMap<>();
    private final Map<Player, Integer> playerDamage = new HashMap<>();
    private final Map<Player, Integer> playerExtractions = new HashMap<>();

    public PlayerStatsScoreboard(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void addKill(Player player) {
        playerKills.put(player, getKills(player) + 1);
    }

    public void addDamage(Player player, int damage) {
        playerDamage.put(player, getDamage(player) + damage);
    }

    public void addExtraction(Player player) {
        playerExtractions.put(player, getExtractions(player) + 1);
    }

    public int getKills(Player player) {
        return playerKills.getOrDefault(player, 0);
    }

    public int getDamage(Player player) {
        return playerDamage.getOrDefault(player, 0);
    }

    public int getExtractions(Player player) {
        return playerExtractions.getOrDefault(player, 0);
    }

    @EventHandler
    public void onPlayerKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player) {
            Player killer = event.getEntity().getKiller();
            addKill(killer);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            addDamage(damager, (int) event.getDamage());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Logik zum Überprüfen einer tatsächlichen Extraktion
        Player player = event.getPlayer();
        if (isExtractionConditionMet(player)) {
            addExtraction(player);
            Bukkit.getServer().getPluginManager().callEvent(new PlayerExtractionEvent(player));
        }
    }

    private boolean isExtractionConditionMet(Player player) {
        // Überprüfe hier, ob der Spieler tatsächlich extrahiert (z.B. ob er eine bestimmte Aktion durchführt)
        // Dies ist nur ein Beispiel, du musst es an deine Extraktionslogik anpassen
        return player.isSneaking() && player.getLocation().getBlock().getType() == Material.LIGHT;
    }
}
