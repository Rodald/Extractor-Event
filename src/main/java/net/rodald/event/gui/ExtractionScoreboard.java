package net.rodald.event.gui;

import net.rodald.event.StartGame;
import net.rodald.event.scores.PlayerStatsScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ExtractionScoreboard {

    private final JavaPlugin plugin;
    private final PlayerStatsScoreboard playerStatsScoreboard;

    public ExtractionScoreboard(JavaPlugin plugin, PlayerStatsScoreboard playerStatsScoreboard) {
        this.plugin = plugin;
        this.playerStatsScoreboard = playerStatsScoreboard;
        updateScoreboard();
    }

    private void updateScoreboard() {
        new BukkitRunnable() {
            @Override
            public void run() {
                String[] leaderboard = {"test"}; // Beispielwert für das Leaderboard
                for (Player player : Bukkit.getOnlinePlayers()) {
                    int kills = playerStatsScoreboard.getKills(player);
                    int damage = playerStatsScoreboard.getDamage(player);
                    int extractions = playerStatsScoreboard.getExtractions(player);
                    showScoreboard(player, StartGame.getRound(), 8, "00:00", 0, leaderboard, kills, damage, extractions);
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public void showScoreboard(Player player, int gameNumber, int totalGames, String timeRemaining, int round, String[] leaderboard, int kills, int damage, int extractions) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("extraction", "dummy", ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "Extraction");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score roundEnd = objective.getScore("⏰ Round ends: " + ChatColor.YELLOW + timeRemaining);
        roundEnd.setScore(10);

        Score roundNumber = objective.getScore("Rounds: " + ChatColor.GREEN + round + "/7");
        roundNumber.setScore(9);

        Score spacer1 = objective.getScore(" ");
        spacer1.setScore(8);

        Score teamLeaderboard = objective.getScore(ChatColor.BOLD + "Team Leaderboard:");
        teamLeaderboard.setScore(7);

        for (int i = 0; i < leaderboard.length; i++) {
            Score teamScore = objective.getScore(leaderboard[i]);
            teamScore.setScore(6 - i);
        }

        Score spacer2 = objective.getScore("  ");
        spacer2.setScore(2);

        Score killsScore = objective.getScore("\uD83D\uDDE1 Kills: " + ChatColor.GOLD + kills);
        killsScore.setScore(1);

        Score damageScore = objective.getScore("\uD83C\uDFF9 Damage: " + ChatColor.RED + damage);
        damageScore.setScore(0);

        Score extractionsScore = objective.getScore("↑ Extractions: " + ChatColor.AQUA + extractions);
        extractionsScore.setScore(-1);

        player.setScoreboard(scoreboard);
    }
}
