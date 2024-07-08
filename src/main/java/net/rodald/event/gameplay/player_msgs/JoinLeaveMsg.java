package net.rodald.event.gameplay.player_msgs;

import net.rodald.event.GameSpectator;
import net.rodald.event.gui.TeamSelector;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Random;

public class JoinLeaveMsg implements Listener {

    private static Plugin plugin;


    public JoinLeaveMsg(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        Random random = new Random();

        String[] joinMessages = {
                playerName + " just hopped in to the server",
                "YOOOOO it's " + playerName,
                "What's up " + playerName,
                "Here have a welcome gift " + playerName,
                "Have a fun time " + playerName
        };
        int randomJoinMsg = random.nextInt(joinMessages.length);
        event.setJoinMessage(ChatColor.YELLOW + joinMessages[randomJoinMsg]);
        if (randomJoinMsg == 3) {
            player.getInventory().addItem(new ItemStack(Material.BREAD));
        }
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        Random random = new Random();
        String prefixes[]  = {
                ":(",
                ";(",
                "=(",
                "D:",
                "D;",
                "D=",
                ":[",
                ";[",
                "=[",
                ">:("
        };
        String prefix = prefixes[random.nextInt(prefixes.length)];

        String[] leaveMessages = {
                prefix + playerName + " left the game",
                playerName + " you don't have to rage quit",
                "Hope you had a fun time " + playerName,
                "Yeah fuck off " + playerName,
                "Bye " + playerName + "!",
                "Guys don't be like " + playerName + ". Just stay online"
        };
        int randomLeaveMsg = random.nextInt(leaveMessages.length);
        
        event.setQuitMessage(ChatColor.RED + leaveMessages[randomLeaveMsg]);
    }
}
