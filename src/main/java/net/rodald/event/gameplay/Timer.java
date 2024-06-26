package net.rodald.event.gameplay;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer {

    private static Plugin plugin;
    private static final int gameLength = 150;
    private static int time = gameLength;

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
        new BukkitRunnable() {
            @Override
            public void run() {
                if (time > 0) {
                    decreaseTimer();
                } else {
                    cancel();
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
    }
}
