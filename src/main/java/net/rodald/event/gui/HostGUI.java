package net.rodald.event.gui;

import net.rodald.event.StartGame;
import net.rodald.event.scores.PlayerStatsScoreboard;
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

import java.util.ArrayList;
import java.util.List;

public class HostGUI implements Listener {

    private final JavaPlugin plugin;

    public HostGUI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void openInventory(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "Host GUI");

        // Close Item
        ItemStack closeItem = setName(new ItemStack(Material.BARRIER), ChatColor.RED + "Close");

        ItemStack startTeamSelectorPhase = setName(new ItemStack(Material.RED_CONCRETE), "startTeamSelectorPhase");
        ItemStack startCountdown = setName(new ItemStack(Material.SCAFFOLDING), "startCountdown");
        ItemStack teleportPlayers = setName(new ItemStack(Material.ENDER_PEARL), "teleportPlayers");
        ItemStack startRound = setName(new ItemStack(Material.STONE_BUTTON), "startRound");
        ItemStack startExtractionGame = setName(new ItemStack(Material.CROSSBOW), "startExtractionGame");


        // Set items in the inventory
        gui.setItem(0, startExtractionGame);
        gui.setItem(1, startTeamSelectorPhase);
        gui.setItem(2, startCountdown);
        gui.setItem(3, teleportPlayers);
        gui.setItem(4, startRound);
        gui.setItem(26, closeItem); // Bottom right

        // Open the inventory for the player
        player.openInventory(gui);

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

    // Method to handle InventoryClickEvent
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.DARK_GRAY + "Host GUI")) {
            event.setCancelled(true); // Prevent taking the items

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            String itemName = clickedItem.getItemMeta().getDisplayName();

            if (itemName.equals(ChatColor.RED + "Close")) {
                player.closeInventory();
            } else if (itemName.equals("startTeamSelectorPhase")) {
                StartGame.startTeamSelectorPhase();
            } else if (itemName.equals("startCountdown")) {
                StartGame.startCountdown();
            } else if (itemName.equals("teleportPlayers")) {
                StartGame.teleportPlayers(StartGame.getRound());
            } else if (itemName.equals("startRound")) {
                StartGame.startRound(StartGame.getRound());
            } else if (itemName.equals("startExtractionGame")) {
                StartGame.startExtractionGame();
            }
        }
    }

    private void joinTeam(Player player, List<Player> team, String teamName) {
        if (team.contains(player)) {
            player.sendMessage(ChatColor.YELLOW + "You are already in the " + teamName + ".");
        } else {
            team.add(player);
            player.sendMessage(ChatColor.GREEN + "You joined the " + teamName + ".");
        }
        openInventory(player); // Update the inventory to show the new team member
    }


}
