package net.rodald.event;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
// TODO: Disable minecrafts death msgs on start
public class StartGame {
    private static JavaPlugin plugin = null;

    public static int getRound() {
        return round;
    }

    private static int round = 1;
    public StartGame(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void startTeamSelectorPhase() {
        Bukkit.getOnlinePlayers().forEach(this::sendClickableMessage);
    }


    public static void startCountdown() {
        placeCages();
        new BukkitRunnable() {
            int countdown = 20;

            @Override
            public void run() {
                if (countdown == 10) {
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("Round " + round, "", 0, 20, 10));
                }
                if (countdown <= 0) {
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "GO!!", "", 0, 20, 10));
                    removeCages();
                    cancel();
                    return;
                }

                ChatColor color;
                switch (countdown) {
                    case 3: color = ChatColor.RED;
                        break;
                    case 2: color = ChatColor.YELLOW;
                        break;
                    case 1: color = ChatColor.GREEN;
                        break;
                    default: color = ChatColor.WHITE;
                        break;
                }
                String message = String.valueOf(ChatColor.BOLD) + color + countdown;
                Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(message, "", 0, 20, 10));
                countdown--;
            }
        }.runTaskTimer(plugin, 0, 20);
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


}
