package net.rodald.event.scores;

import net.rodald.event.Event;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;


public class PointSystem implements Listener {
    Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();


    private final Event gamePoints;

    public PointSystem(Event gamePoints) {
        this.gamePoints = gamePoints;
    }
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Zombie && event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();

            if (projectile instanceof Arrow && projectile.getShooter() instanceof Player) {
                Player damager = (Player) projectile.getShooter();
                Player target = (Player) event.getEntity();
                // test if player shots himself
                if (target != damager) {
                    damager.sendMessage("You shot a player");
                    addPoints("extractorPoints", damager, 1);
                    damager.playSound(damager, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                    damager.sendMessage("Your current Score is: " + getPoints("extractorPoints", damager));
                }
            }
        }
    }

    public void addPoints(String objectiveName, Player player, int points) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);

        if (objective == null) {
            Bukkit.getLogger().warning("Objective '" + objectiveName + "' does not exist in the scoreboard.");
            return; // Beende die Methode, wenn das Objective nicht existiert
        }

        Score score = objective.getScore(player.getName());
        int newScore = score.getScore() + points;
        score.setScore(newScore);
    }

    public int getPoints(String objectiveName, Player player) {
        Objective objective = scoreboard.getObjective(objectiveName);
        return objective.getScore(player.getName()).getScore();
    }
    public void setPoints(String objectiveName, Player player, int points) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);

        if (objective == null) {
            Bukkit.getLogger().warning("Objective '" + objectiveName + "' does not exist in the scoreboard.");
            return; // Beende die Methode, wenn das Objective nicht existiert
        }

        Score score = objective.getScore(player.getName());
        score.setScore(points);
    }

    public void removePoints(String objectiveName, Player player, int points) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);

        if (objective == null) {
            Bukkit.getLogger().warning("Objective '" + objectiveName + "' does not exist in the scoreboard.");
            return; // Beende die Methode, wenn das Objective nicht existiert
        }

        Score score = objective.getScore(player.getName());
        int newScore = score.getScore() - points;
        score.setScore(newScore);
    }

    public void resetPoints(String objectiveName, Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);

        if (objective == null) {
            Bukkit.getLogger().warning("Objective '" + objectiveName + "' does not exist in the scoreboard.");
            return; // Beende die Methode, wenn das Objective nicht existiert
        }

        Score score = objective.getScore(player.getName());
        score.setScore(0);
    }

    public void resetAllPoints(String objectiveName) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);

        if (objective == null) {
            Bukkit.getLogger().warning("Objective '" + objectiveName + "' does not exist in the scoreboard.");
            return; // Beende die Methode, wenn das Objective nicht existiert
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Score score = objective.getScore(player.getName());
            score.setScore(0);
        }

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            Score score = objective.getScore(player.getName());
            score.setScore(0);
        }
    }
}
