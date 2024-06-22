package net.rodald.event.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TeamSelector implements Listener {

    private final JavaPlugin plugin;

    public TeamSelector(JavaPlugin plugin) {
        this.plugin = plugin;
        initializeTeams();
    }

    private void initializeTeams() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        if (scoreboard.getTeam("Red") == null) {
            Team redTeam = scoreboard.registerNewTeam("Red");
            redTeam.setDisplayName(ChatColor.RED + "Red Team");
            redTeam.setColor(ChatColor.RED);
            redTeam.setAllowFriendlyFire(false);
        }

        if (scoreboard.getTeam("Blue") == null) {
            Team blueTeam = scoreboard.registerNewTeam("Blue");
            blueTeam.setDisplayName(ChatColor.BLUE + "Blue Team");
            blueTeam.setColor(ChatColor.BLUE);
            blueTeam.setAllowFriendlyFire(false);
        }

        if (scoreboard.getTeam("Green") == null) {
            Team greenTeam = scoreboard.registerNewTeam("Green");
            greenTeam.setDisplayName(ChatColor.DARK_GREEN + "Green Team");
            greenTeam.setColor(ChatColor.DARK_GREEN);
            greenTeam.setAllowFriendlyFire(false);
        }
    }

    public void openInventory(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "Select a Team");

        // Close Item
        ItemStack closeItem = setName(new ItemStack(Material.BARRIER), ChatColor.RED + "Close");

        // Red Team Item
        ItemStack redTeamItem = setName(new ItemStack(Material.RED_CONCRETE), ChatColor.RED + "Red Team");
        ItemMeta redMeta = redTeamItem.getItemMeta();
        redMeta.setLore(getTeamMembersLore("Red"));
        redTeamItem.setItemMeta(redMeta);

        // Blue Team Item
        ItemStack blueTeamItem = setName(new ItemStack(Material.BLUE_CONCRETE), ChatColor.BLUE + "Blue Team");
        ItemMeta blueMeta = blueTeamItem.getItemMeta();
        blueMeta.setLore(getTeamMembersLore("Blue"));
        blueTeamItem.setItemMeta(blueMeta);

        // Green Team Item
        ItemStack greenTeamItem = setName(new ItemStack(Material.GREEN_CONCRETE), ChatColor.DARK_GREEN + "Green Team");
        ItemMeta greenMeta = greenTeamItem.getItemMeta();
        greenMeta.setLore(getTeamMembersLore("Green"));
        greenTeamItem.setItemMeta(greenMeta);

        // Set items in the inventory
        gui.setItem(49, closeItem); // Bottom center
        gui.setItem(20, redTeamItem); // Left middle
        gui.setItem(22, blueTeamItem); // Right middle
        gui.setItem(24, greenTeamItem); // Left middle

        // Open the inventory for the player
        player.openInventory(gui);
    }

    private @Nullable List<String> getTeamMembersLore(String teamName) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Members:");
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);
        if (team != null) {
            for (String entry : team.getEntries()) {
                Player teamMember = Bukkit.getPlayer(entry);
                if (teamMember != null) {
                    lore.add(ChatColor.WHITE + teamMember.getName());
                }
            }
        }
        return lore;
    }

    public static ItemStack setName(ItemStack item, String name) {
        if (item == null || name == null) {
            return null;
        }

        // Get the ItemMeta of the item
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        // Make the name non-italic
        meta.setDisplayName(ChatColor.RESET + name);

        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.DARK_GRAY + "Select a Team")) {
            event.setCancelled(true); // Prevent taking the items

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            String itemName = clickedItem.getItemMeta().getDisplayName();

            if (itemName.equals(ChatColor.RED + "Close")) {
                player.closeInventory();
            } else if (itemName.equals(ChatColor.RED + "Red Team")) {
                joinTeam(player, "Red", ChatColor.RED + "Red Team");
            } else if (itemName.equals(ChatColor.BLUE + "Blue Team")) {
                joinTeam(player, "Blue", ChatColor.BLUE + "Blue Team");
            } else if (itemName.equals(ChatColor.DARK_GREEN + "Green Team")) {
                joinTeam(player, "Green", ChatColor.DARK_GREEN + "Green Team");
            }
        }
    }

    private void joinTeam(Player player, String teamName, String teamDisplayName) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team newTeam = scoreboard.getTeam(teamName);

        if (newTeam == null) {
            player.sendMessage(ChatColor.RED + "Team does not exist.");
            return;
        }

        // Remove player from any other team
        for (Team team : scoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
            }
        }

        // Add player to the new team
        newTeam.addEntry(player.getName());
        player.sendMessage(ChatColor.GOLD + "You joined the " + teamDisplayName + ChatColor.GOLD + ".");
        player.setMaxHealth(8);

        // Update the inventory to show the new team member
        openInventory(player);
    }
}
