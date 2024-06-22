package net.rodald.event.gui;

import net.rodald.event.StartGame;
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
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "Select a Team");

        // Close Item
        ItemStack closeItem = setName(new ItemStack(Material.BARRIER), ChatColor.RED + "Close");

        ItemStack startTeamSelectorPhase = setName(new ItemStack(Material.RED_CONCRETE), "startTeamSelectorPhase");
        ItemStack startCountdown = setName(new ItemStack(Material.SCAFFOLDING), "startCountdown");


        // Set items in the inventory
        gui.setItem(1, startTeamSelectorPhase);
        gui.setItem(2, startCountdown);
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
        if (event.getView().getTitle().equals(ChatColor.DARK_GRAY + "Select a Team")) {
            event.setCancelled(true); // Prevent taking the items

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            String itemName = clickedItem.getItemMeta().getDisplayName();
            StartGame startGame = new StartGame(plugin);

            if (itemName.equals(ChatColor.RED + "Close")) {
                player.closeInventory();
            } else if (itemName.equals("startTeamSelectorPhase")) {
                startGame.startTeamSelectorPhase();
            } else if (itemName.equals("startCountdown")) {
                StartGame.startCountdown();
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
