package net.rodald.event.scores;

import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextColor;
import net.rodald.event.GameSpectator;
import net.rodald.event.gui.TeamSelector;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerStatsScoreboard implements Listener {

    private final JavaPlugin plugin;
    private final Map<Player, Integer> playerKills = new HashMap<>();
    private final Map<Player, Integer> playerDamage = new HashMap<>();
    private final Map<Player, Integer> playerExtractions = new HashMap<>();
    private final Map<Team, Integer> teamPoints = new HashMap<>();
    private final String FIREWORK_TAG = "no_damage_firework";

    public PlayerStatsScoreboard(JavaPlugin plugin) {
        this.plugin = plugin;
        resetTeamPoints();
    }

    public void addKill(Player player) {
        playerKills.put(player, getKills(player) + 1);
        Team team = getTeam(player);
        if (team != null) {
            addTeamPoints(team, 8);
        }
    }

    public void addDamage(Player player, int damage) {
        playerDamage.put(player, getDamage(player) + 1);
        Team team = getTeam(player);
        if (team != null) {
            addTeamPoints(team, 4);
        }
    }

    public void addExtraction(Player player) {
        playerExtractions.put(player, getExtractions(player) + 1);
        Team team = getTeam(player);
        if (team != null) {
            addTeamPoints(team, 12);
        }
    }

    public void addTeamPoints(Team team) {
        teamPoints.put(team, getTeamPoints(team) + 1);
    }

    public void addTeamPoints(Team team, int amount) {
        teamPoints.put(team, getTeamPoints(team) + amount);
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

    public int getTeamPoints(Team team) {
        return teamPoints.getOrDefault(team, 0);
    }

    public Map<Team, Integer> getTeamPoints() {
        return teamPoints;
    }

    public void resetTeamPoints() {
        teamPoints.put(Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Red"), 0);
        teamPoints.put(Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Green"), 0);
        teamPoints.put(Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Blue"), 0);
    }

    @EventHandler
    public void onPlayerKill(EntityDeathEvent event) {

// Tests if killer and target are both a player
        if (event.getEntity().getKiller() instanceof Player && event.getEntity() instanceof Player) {
            Player killer = event.getEntity().getKiller();
            Player target = (Player) event.getEntity();
            Team killerTeam = getTeam(killer);
            Team targetTeam = getTeam(target);

            killer.sendMessage(String.valueOf(Arrays.stream(TeamSelector.validTeams).anyMatch(i -> i.equals(killerTeam))));
            killer.sendMessage(String.valueOf(Arrays.stream(TeamSelector.validTeams).anyMatch(i -> i.equals(targetTeam))));

            if (Arrays.stream(TeamSelector.validTeams).anyMatch(i -> i.equals(killerTeam)) && Arrays.stream(TeamSelector.validTeams).anyMatch(i -> i.equals(targetTeam))) {
                World world = killer.getWorld();
                Location location = killer.getLocation();
                Location targetLocation = target.getLocation();

                // returns if target is not a player
                if (!(target instanceof Player)) {
                    return;
                }

                TextColor targetColor = getTeam(target).color();

                // Create and configure firework particle
                Firework firework = (Firework) targetLocation.getWorld().spawn(targetLocation.add(0, 1, 0), Firework.class);
                FireworkMeta meta = firework.getFireworkMeta();

                // Set the color of the firework based on the target's team
                meta.addEffect(FireworkEffect.builder()
                        .withColor(Color.fromRGB(targetColor.red(), targetColor.green(), targetColor.blue())) // Set the firework color
                        .with(FireworkEffect.Type.BALL_LARGE) // Firework type
                        .build());

                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, FIREWORK_TAG), PersistentDataType.BYTE, (byte) 1);
                firework.setFireworkMeta(meta);
                firework.detonate();

                // Spawn wax particles at the killer's location
                world.spawnParticle(Particle.WAX_ON, location.add(0, 1, 0), 200, 0.5, 1, 0.5, 0.1);

                // Display title and broadcast message for the kill
                Team killersTeam = getTeam(killer);

                ChatColor killerChatColor = convertTextColorToChatColor(killersTeam.color());
                ChatColor targetChatColor = convertTextColorToChatColor(targetColor);
                killer.sendTitle("", ChatColor.RED + "\uD83C\uDFF9 " + targetChatColor + target.getName(), 0, 40, 10);
                Bukkit.broadcastMessage(ChatColor.RED + "[\uD83C\uDFF9] " + ChatColor.BOLD + targetChatColor + target.getName() + ChatColor.GRAY + " has been killed by " + killerChatColor + killer.getName());
                killer.sendMessage(ChatColor.GREEN + "+8 points " + ChatColor.DARK_GREEN + "(kill)");

                // Add kill to the killer's stats
                addKill(killer);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Handle arrow damage and health display
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();

            if (projectile instanceof Arrow && projectile.getShooter() instanceof Player) {
                Player damager = (Player) projectile.getShooter();
                Player target = (Player) event.getEntity();

                if (target == damager) {
                    return; // tests if player shoots himself
                }

                // Set damage to 1 heart (2 health points)
                event.setDamage(2);

                TextColor targetColor = getTeam(target).color();
                ChatColor targetChatColor = convertTextColorToChatColor(targetColor);

                // displays enemy health above action bar
                if (target.getHealth() > 2) {
                    damager.sendActionBar(targetChatColor + target.getName() + ChatColor.WHITE + " - " + ChatColor.RED + (int) ((target.getHealth() - 2) / 2) + "❤");
                } else {
                    damager.sendActionBar(targetChatColor + target.getName() + ChatColor.WHITE + " - " + ChatColor.DARK_RED + "☠");
                }

                addDamage(damager, (int) event.getDamage());
                damager.playSound(damager, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
            }
        }

        // Handle firework damage prevention
        if (event.getDamager() instanceof Firework) {
            Firework firework = (Firework) event.getDamager();
            FireworkMeta meta = firework.getFireworkMeta();
            if (meta.getPersistentDataContainer().has(new NamespacedKey(plugin, FIREWORK_TAG), PersistentDataType.BYTE)) {
                event.setCancelled(true); // Prevent the marked firework from causing damage
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (isExtractionConditionMet(player)) {
            addExtraction(player);
            Bukkit.getServer().getPluginManager().callEvent(new PlayerExtractionEvent(player));
        }
    }

    private boolean isExtractionConditionMet(Player player) {
        return player.isSneaking() && player.getLocation().getBlock().getType() == Material.LIGHT;
    }

    private Team getTeam(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        if (scoreboard.getTeam("Red").hasEntity(player)) {
            return Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Red");
        } else if (scoreboard.getTeam("Green").hasEntity(player)) {
            return Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Green");
        } else if (scoreboard.getTeam("Blue").hasEntity(player)) {
            return Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Blue");
        }
        return null;
    }

    private ChatColor convertTextColorToChatColor(TextColor textColor) {
        // Hier ein einfaches Mapping als Beispiel, je nach API musst du dies anpassen
        if (String.valueOf(textColor).equals("red")) {
            return ChatColor.RED;
        } else if (String.valueOf(textColor).equals("blue")) {
            return ChatColor.BLUE;
        } else if (String.valueOf(textColor).equals("dark_green")) {
            return ChatColor.GREEN;
        }
        return ChatColor.WHITE; // Standardfallback, wenn die Farbe nicht übereinstimmt
    }

    @EventHandler
    private void PlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        player.sendMessage("You died :(");
        if (getTeam(player) != null) {
            Team playerTeam = getTeam(player);

            if (Arrays.stream(TeamSelector.validTeams).anyMatch(i -> i.equals(playerTeam))) {
                GameSpectator.setSpectator(player, true);
            };
        }
    }
}
