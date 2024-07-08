package net.rodald.event.gameplay.player_msgs;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class JoinLeaveMsg implements Listener {

    private static Plugin plugin;


    public JoinLeaveMsg(Plugin plugin) {
        this.plugin = plugin;
    }
}
