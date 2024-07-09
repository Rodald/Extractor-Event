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

import java.util.Random;

public class JoinLeaveMsg implements Listener {



    public JoinLeaveMsg() {}

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName;

        if (TeamSelector.getTeam(player) != null) {
            playerName = TeamSelector.getTeam(player).getColor() + player.getName() + ChatColor.YELLOW;
        } else {
            playerName = player.getName();
        }

        Random random = new Random();

        String[] joinMessages = {
                playerName + " just hopped into the server",
                "YOOOOO it's " + playerName,
                "What's up " + playerName,
                "You are my favourite player. Have a welcome gift " + playerName,
                "Have a fun time " + playerName,
                "print(\"Goodbye " + playerName + "!\")",
                "Welcome, " + playerName + "! May your packets always reach their destination.",
                "Catch you later, " + playerName + "! Don't let the zombies byte!",
                "A wild " + playerName + " appeared",
                "Yay you made it " + playerName,
                "Everyone welcome " + playerName,
                playerName + " just slid into the server",
                playerName + " just showed up,",
                playerName + " joined the party",
                "Good to see you, " + playerName
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
        String playerName;

        if (TeamSelector.getTeam(player) != null) {
            playerName = TeamSelector.getTeam(player).getColor() + player.getName() + ChatColor.RED;
        } else {
            playerName = player.getName();
        }

        Random random = new Random();
        String[] prefixes = {
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
                "Guys don't be like " + playerName + ". Just stay online",
                "Catch you later, " + playerName + "! Exception: UserDisconnected",
                "404 player " + playerName + " not found",
                "Goodbye, " + playerName + "! OutOfMemoryError: No room for losers.",
                "See you, " + playerName + "! NullPointerException: Players Skill Value Not Set.",
                "Farewell, " + playerName + "! Infinite Loop: Running away from challenges.",
                playerName  + "s wifi disconnected",
                playerName + "s cat ran over their keyboard",
                playerName + " got an null exception"
        };
        int randomLeaveMsg = random.nextInt(leaveMessages.length);
        
        event.setQuitMessage(ChatColor.RED + leaveMessages[randomLeaveMsg]);
    }
}
