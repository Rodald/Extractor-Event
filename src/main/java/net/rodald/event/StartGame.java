package net.rodald.event;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.rodald.event.gui.TeamSelector;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
// TODO: Disable minecrafts death msgs on start
// TODO: Turn keepinventory on.

/*
    Games:
    Red vs Green
    Red vs Blue
    Green vs Blue
    Red vs Green
    Green vs Blue
    Red vs Blue

    1st Place vs 2nd Place


    /extractor gameStart
    Team gui opens.
    60s pause

    correct players get teleported to game.
    1st round countdown
    Player cages get removed.

    // TODO: win/lose detection + round timer
 */

public class StartGame {

    private static String[] rounds = {
            "RvG",
            "RvB",
            "GvB",
            "RvG",
            "GvB",
            "RvB",
            "1v2"
    };
    private static JavaPlugin plugin = null;

    public static int getRound() {
        return round;
    }

    public static Boolean gameIsRunning = true;
    private static int round = 1;

    public StartGame(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void startTeamSelectorPhase() {
        Bukkit.getOnlinePlayers().forEach(this::sendClickableMessage);
    }


    public static void startExtractionGame() {

    }

    public static void startRound(int round) {
        teleportPlayers(round);
        startCountdown();
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage("Is round over: " + isRoundOver());
            }
        }.runTaskTimer(plugin, 0, 10);
    }


    private static AtomicReference<Boolean> isRoundOver() {
        AtomicReference<Boolean> roundOver = new AtomicReference<>(true);
        Bukkit.getOnlinePlayers().forEach(player -> {
            Team playerTeam = TeamSelector.getTeam(player);
            if (Arrays.asList(TeamSelector.validTeams).contains(playerTeam) && !player.hasMetadata("game_spectator")) {
                roundOver.set(false);
            };
        });
        return roundOver;
    }


    public static void startCountdown() {
        placeCages();
        new BukkitRunnable() {
            int countdown = 12;

            @Override
            public void run() {
                if (countdown >= 11) {
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("Round " + round, "", 0, 20, 10));
                } else {
                    if (countdown <= 0) {
                        Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "GO!!", "", 0, 20, 10));
                        removeCages();
                        cancel();
                        return;
                    }

                    ChatColor color;
                    switch (countdown) {
                        case 3:
                            color = ChatColor.RED;
                            break;
                        case 2:
                            color = ChatColor.YELLOW;
                            break;
                        case 1:
                            color = ChatColor.GREEN;
                            break;
                        default:
                            color = ChatColor.WHITE;
                            break;
                    }
                    String message = String.valueOf(ChatColor.BOLD) + color + countdown;
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(message, "", 0, 20, 10));
                }
                countdown--;
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public static void teleportPlayers(int round) {
        Bukkit.broadcastMessage(String.valueOf(rounds[round].charAt(0)));
        Team team1 = getTeamByLetter(rounds[round].charAt(0));
        Team team2 = getTeamByLetter(rounds[round].charAt(2));

        Location team1Spawn1 = getSpawnLocation("team1", "spawn1");
        Location team1Spawn2 = getSpawnLocation("team1", "spawn2");
        Location team2Spawn1 = getSpawnLocation("team2", "spawn1");
        Location team2Spawn2 = getSpawnLocation("team2", "spawn2");

        if (team1Spawn1 == null || team1Spawn2 == null) {
            Bukkit.getLogger().severe("Team1 spawns are null!");
            return;
        }

        if (team2Spawn1 == null || team2Spawn2 == null) {
            Bukkit.getLogger().severe("Team2 spawns are null!");
            return;
        }

        balanceTeamPlayers(team1, team1Spawn1, team1Spawn2);
        balanceTeamPlayers(team2, team2Spawn1, team2Spawn2);
    }

    private static Location getSpawnLocation(String teamTag, String spawnTag) {
        return Bukkit.getWorld("world").getEntitiesByClass(ArmorStand.class).stream()
                .filter(as -> as.getScoreboardTags().contains(teamTag) && as.getScoreboardTags().contains(spawnTag))
                .findFirst()
                .orElse(null).getLocation();
    }

    private static Team getTeamByLetter(char team) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        switch (team) {
            case 'R': return scoreboard.getTeam("Red");
            case 'G': return scoreboard.getTeam("Green");
            case 'B': return scoreboard.getTeam("Blue");
            default: return null;
        }
    }
    public static void removeCages() {
        World world = Bukkit.getWorld("world");
        Bukkit.getWorld("world").getEntitiesByClass(ArmorStand.class).stream().filter(as -> as.getScoreboardTags().contains("spawn_tag")).forEach(armorStand -> {
            Location loc = armorStand.getLocation();
            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();
            // Place gold blocks
            for (int dy = 0; dy <= 3; dy++) { // Only for y and y+4
                for (int dx = -2; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 0; dz++) {
                        world.getBlockAt(x + dx, y + dy, z + dz).setType(Material.AIR);
                    }
                }
                for (int dx = -1; dx <= 0; dx++) {
                    world.getBlockAt(x + dx, y + dy, z - 2).setType(Material.AIR);
                    world.getBlockAt(x + dx, y + dy, z + 1).setType(Material.AIR);
                }
            }
        });
    }

    public static void placeCages() {
        World world = Bukkit.getWorld("world");

        world.getEntitiesByClass(ArmorStand.class).stream()
                .filter(as -> as.getScoreboardTags().contains("spawn_tag"))
                .forEach(armorStand -> {
                    Location loc = armorStand.getLocation();
                    int x = loc.getBlockX();
                    int y = loc.getBlockY() - 1;
                    int z = loc.getBlockZ();

                    // Place glass blocks
                    int[][] glassOffsets = {
                            {0, 0, 1}, {-1, 0, 1}, {-2, 0, 0}, {-2, 0, -1},
                            {-1, 0, -2}, {0, 0, -2}, {1, 0, -1}, {1, 0, 0}
                    };

                    for (int i = 0; i < 5; i++) { // Iterate from y to y+4
                        for (int[] offset : glassOffsets) {
                            world.getBlockAt(x + offset[0], y + i, z + offset[2]).setType(Material.GLASS);
                        }
                    }

                    // Place gold blocks
                    for (int i = 0; i <= 4; i += 4) { // Only for y and y+4
                        for (int dx = -2; dx <= 1; dx++) {
                            for (int dz = -1; dz <= 0; dz++) {
                                world.getBlockAt(x + dx, y + i, z + dz).setType(Material.RAW_GOLD_BLOCK);
                            }
                        }
                        for (int dx = -1; dx <= 0; dx++) {
                            world.getBlockAt(x + dx, y + i, z - 2).setType(Material.RAW_GOLD_BLOCK);
                            world.getBlockAt(x + dx, y + i, z + 1).setType(Material.RAW_GOLD_BLOCK);
                        }
                    }
                });
    }

    public void sendClickableMessage(Player player) {
        // Erstelle die Nachricht
        player.sendMessage(ChatColor.RED + "You have 60 seconds to select your team!");
        TextComponent message = new TextComponent("Click here to open the Team GUI!");
        message.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        message.setBold(true);

        // Füge die Klick-Aktion hinzu
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/extractor jointeam"));

        // Sende die Nachricht an den Spieler
        player.spigot().sendMessage(message);

    }

    private static void balanceTeamPlayers(Team team, Location location1, Location location2) {
        List<Player> players = new ArrayList<>();
        for (String entry : team.getEntries()) {
            Player player = Bukkit.getPlayer(entry);
            if (player != null) {
                players.add(player);
            }
        }

        Collections.shuffle(players); // Zufällige Reihenfolge der Spieler

        int mid = players.size() / 2;
        List<Player> group1 = players.subList(0, mid);
        List<Player> group2 = players.subList(mid, players.size());

        for (Player player : group1) {
            float yaw = player.getYaw();
            float pitch = player.getPitch();
            player.teleport(location1.subtract(.5, 0, .5));
            player.setRotation(yaw, pitch);
        }

        for (Player player : group2) {
            float yaw = player.getYaw();
            float pitch = player.getPitch();
            player.teleport(location2.subtract(.5, 0, .5));
            player.setRotation(yaw, pitch);
        }
    }

}
