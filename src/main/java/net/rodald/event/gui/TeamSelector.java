package net.rodald.event.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.NameTagVisibility;
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
            redTeam.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
        }

        if (scoreboard.getTeam("Blue") == null) {
            Team blueTeam = scoreboard.registerNewTeam("Blue");
            blueTeam.setDisplayName(ChatColor.BLUE + "Blue Team");
            blueTeam.setColor(ChatColor.BLUE);
            blueTeam.setAllowFriendlyFire(false);
            blueTeam.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
        }

        if (scoreboard.getTeam("Green") == null) {
            Team greenTeam = scoreboard.registerNewTeam("Green");
            greenTeam.setDisplayName(ChatColor.DARK_GREEN + "Green Team");
            greenTeam.setColor(ChatColor.DARK_GREEN);
            greenTeam.setAllowFriendlyFire(false);
            greenTeam.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
        }
    }

    public void openInventory(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "Select a Team");

        ItemStack closeItem = setName(new ItemStack(Material.BARRIER), ChatColor.RED + "Close");

        ItemStack redTeamItem = setName(new ItemStack(Material.RED_CONCRETE), ChatColor.RED + "Red Team");
        ItemMeta redMeta = redTeamItem.getItemMeta();
        redMeta.setLore(getTeamMembersLore("Red"));
        redTeamItem.setItemMeta(redMeta);

        ItemStack blueTeamItem = setName(new ItemStack(Material.BLUE_CONCRETE), ChatColor.BLUE + "Blue Team");
        ItemMeta blueMeta = blueTeamItem.getItemMeta();
        blueMeta.setLore(getTeamMembersLore("Blue"));
        blueTeamItem.setItemMeta(blueMeta);

        ItemStack greenTeamItem = setName(new ItemStack(Material.GREEN_CONCRETE), ChatColor.DARK_GREEN + "Green Team");
        ItemMeta greenMeta = greenTeamItem.getItemMeta();
        greenMeta.setLore(getTeamMembersLore("Green"));
        greenTeamItem.setItemMeta(greenMeta);

        gui.setItem(49, closeItem);
        gui.setItem(20, redTeamItem);
        gui.setItem(22, blueTeamItem);
        gui.setItem(24, greenTeamItem);

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

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(ChatColor.RESET + name);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.DARK_GRAY + "Select a Team")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            String itemName = clickedItem.getItemMeta().getDisplayName();

            if (itemName.equals(ChatColor.RED + "Close")) {
                player.closeInventory();
            } else if (itemName.equals(ChatColor.RED + "Red Team")) {
                joinTeam(player, "Red");
            } else if (itemName.equals(ChatColor.BLUE + "Blue Team")) {
                joinTeam(player, "Blue");
            } else if (itemName.equals(ChatColor.DARK_GREEN + "Green Team")) {
                joinTeam(player, "Green");
            }
        }
    }

    private void joinTeam(Player player, String teamName) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team newTeam = scoreboard.getTeam(teamName);

        if (newTeam == null) {
            player.sendMessage(ChatColor.RED + "Error: Team not found.");
            return;
        }

        for (Team team : scoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
                player.sendMessage(ChatColor.YELLOW + "You have left " + team.getDisplayName() + ChatColor.YELLOW + ".");
            }
        }

        // give player armor
        Color color = Color.fromRGB(newTeam.color().value());

        player.getInventory().setChestplate(colorArmor(new ItemStack(Material.LEATHER_CHESTPLATE), color));
        player.getInventory().setLeggings(colorArmor(new ItemStack(Material.LEATHER_LEGGINGS), color));
        player.getInventory().setBoots(colorArmor(new ItemStack(Material.LEATHER_BOOTS), color));

        newTeam.addEntry(player.getName());
        player.sendMessage(ChatColor.GREEN + "You have joined " + newTeam.getDisplayName() + ChatColor.GREEN + ".");
        player.closeInventory();

        // Debug: Verify the settings are applied
        player.sendMessage(ChatColor.GREEN + "Friendly Fire: " + newTeam.allowFriendlyFire());
        player.sendMessage(ChatColor.GREEN + "NameTagVisibility: " + newTeam.getNameTagVisibility());
    }

    private ItemStack colorArmor(ItemStack armor, Color color) {
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) armor.getItemMeta();
        armorMeta.setColor(color);
        armorMeta.setUnbreakable(true);
        armor.setItemMeta(armorMeta);
        return armor;
    }
}