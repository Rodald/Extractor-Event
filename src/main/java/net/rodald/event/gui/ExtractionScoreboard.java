package net.rodald.event.gui;

import net.kyori.adventure.text.format.TextColor;
import net.rodald.event.StartGame;
import net.rodald.event.gameplay.Timer;
import net.rodald.event.scores.PlayerStatsScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExtractionScoreboard {

    public static Boolean enableScoreboard = true;

    private final JavaPlugin plugin;
    private final PlayerStatsScoreboard playerStatsScoreboard;

    public ExtractionScoreboard(JavaPlugin plugin, PlayerStatsScoreboard playerStatsScoreboard) {
        this.plugin = plugin;
        this.playerStatsScoreboard = playerStatsScoreboard;
        updateScoreboard();
    }



    private void updateScoreboard() {
        if (enableScoreboard) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    // sort team after point score
                    List<Map.Entry<Team, Integer>> sortedTeams = playerStatsScoreboard.getTeamPoints().entrySet().stream()
                            .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                            .collect(Collectors.toList());

                    // leaderboard
                    String[] leaderboard = new String[sortedTeams.size()];
                    for (int i = 0; i < sortedTeams.size(); i++) {
                        Map.Entry<Team, Integer> entry = sortedTeams.get(i);
                        if (entry != null && entry.getKey() != null) {
                            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

                            TextColor color = scoreboard.getTeam(entry.getKey().getName()).color();
                            leaderboard[i] = ChatColor.YELLOW + String.valueOf(i + 1) + "." + ChatColor.WHITE + " - " + convertTextColorToChatColor(color) + entry.getKey().getName();

                            switch (entry.getKey().getName()) {
                                case "Red":
                                    leaderboard[i] = leaderboard[i] + "    ";
                                    break;
                                case "Green":
                                    leaderboard[i] = leaderboard[i] + " ";
                                    break;
                                case "Blue":
                                    leaderboard[i] = leaderboard[i] + "  " + ChatColor.BOLD + " ";
                                    break;
                            }
                            leaderboard[i] = leaderboard[i]  + ChatColor.RESET + ChatColor.GRAY + "(" + entry.getValue() + "pts)";
                        }
                    }

                    // updates scoreboard
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        int kills = playerStatsScoreboard.getKills(player);
                        int damage = playerStatsScoreboard.getDamage(player);
                        int extractions = playerStatsScoreboard.getExtractions(player);

                        showScoreboard(player, Timer.getTime(), StartGame.getRound(), leaderboard, kills, damage, extractions);
                    }
                }
            }.runTaskTimer(plugin, 0, 10);
        }
    }

    public void showScoreboard(Player player, String timeRemaining, int round, String[] leaderboard, int kills, int damage, int extractions) {
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

        Score teamLeaderboard = objective.getScore(ChatColor.BOLD + "Leaderboard:");
        teamLeaderboard.setScore(7);

        for (int i = 0; i < leaderboard.length; i++) {
            Score teamScore = objective.getScore(leaderboard[i]);
            teamScore.setScore(6 - i);
        }

        Score spacer2 = objective.getScore("  ");
        spacer2.setScore(3);

        Score killsScore = objective.getScore("\uD83D\uDDE1 Kills: " + ChatColor.GOLD + kills);
        killsScore.setScore(2);

        Score damageScore = objective.getScore("\uD83C\uDFF9 Damage: " + ChatColor.RED + damage);
        damageScore.setScore(1);

        Score extractionsScore = objective.getScore("↑ Extractions: " + ChatColor.AQUA + extractions);
        extractionsScore.setScore(0);

        player.setScoreboard(scoreboard);
    }

    private ChatColor convertTextColorToChatColor(TextColor textColor) {
        if (String.valueOf(textColor).equals("red")) {
            return ChatColor.RED;
        } else if (String.valueOf(textColor).equals("blue")) {
            return ChatColor.BLUE;
        } else if (String.valueOf(textColor).equals("dark_green")) {
            return ChatColor.GREEN;
        }
        return ChatColor.WHITE;
    }
}
