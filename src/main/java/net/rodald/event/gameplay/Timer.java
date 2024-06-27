package net.rodald.event.gameplay;

import net.rodald.event.GameSpectator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer {

    private static Plugin plugin;
    private static final int gameLength = 150;
    private static int time = gameLength;
    private static Boolean timerReset;

    public Timer(Plugin plugin) {
        Timer.plugin = plugin;
    }

    public static String getTime() {
        return timeToString(time);
    }

    public static void setTime(int time) {
        Timer.time = time;
    }

    public static void startTimer() {
        timerReset = false;
        new BukkitRunnable() {
            @Override
            public void run() {
                decreaseTimer();
                if (time % 60 == 0 && time < gameLength/2 && time != 0 && time / 60 > 1) {
                    // Custom msg if multiple minuteS remain
                    Bukkit.getOnlinePlayers().forEach(player -> {
                            player.sendTitle("", ChatColor.YELLOW + "Round ends in " + time / 60 + " minutes!", 0, 20, 10);
                        });
                } else if (time % 60 == 0 && time < gameLength/2 && time != 0) {
                    // Custom msg if only one minute remains
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.sendTitle("", ChatColor.YELLOW + "Round ends in 1 minute!", 0, 60, 20);
                    });
                }

                if (time == 15) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.sendTitle("", ChatColor.AQUA + "All players are now glowing", 0, 20, 10);
                        if (!GameSpectator.getSpectator(player)) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, -1, 1));
                        }
                    });
                }


                if (time <= 0 || timerReset) {
                    if (time <= 0) {
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.setHealth(0);
                        });
                    }
                    cancel();
                    timerReset = false;
                }

                if (time <= 5 && time != 0) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.sendTitle("", ChatColor.YELLOW + "Round ends in " + (time) + " seconds", 0, 20, 10);
                    });
                }
            }
        }.runTaskTimer(plugin, 0 , 20);
    }

    private static void decreaseTimer() {
        time--;
    }
    public static String timeToString(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static void resetTimer() {
        setTime(gameLength);
        timerReset = true;
    }

    private static void giveArrow(Player player) {
        player.getInventory().addItem(new ItemStack(Material.ARROW));
    }
}
