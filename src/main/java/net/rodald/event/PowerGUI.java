package net.rodald.event;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;

public class PowerGUI implements Listener {

    private final JavaPlugin plugin;

    public PowerGUI(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Power GUI");

        // Füge Items zum Inventar hinzu
        ItemStack fly = new ItemStack(Material.ELYTRA);
        setName(fly, "Fly: " + String.valueOf(player.getAllowFlight()));
        ItemStack invisible = new ItemStack(Material.GLASS);
        setName(invisible, "Invisible: " + String.valueOf(player.isInvisible()));
        ItemStack invulnerable = new ItemStack(Material.END_CRYSTAL);
        setName(invulnerable, "Invulnerable: " + String.valueOf(player.isInvulnerable()));

        ItemStack item4 = new ItemStack(Material.REDSTONE);
        ItemStack item5 = new ItemStack(Material.LAPIS_LAZULI);
        ItemStack backgroundItem = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        setName(backgroundItem, " ");

        // ItemStack[] powerItems = new ItemStack[powerItems.size()];
        ArrayList<ItemStack> powerItems = new ArrayList<>();
        powerItems.add(fly);
        powerItems.add(item2);
        powerItems.add(item3);
        powerItems.add(item4);
        powerItems.add(item5);


        // Background Items
        for (int i = 0; i < inventory.getSize() - 9; i++) {
            inventory.setItem(i, backgroundItem);
        }

        // Platziere die Power-Items in der Mitte des Inventars
        int startingIndex = (int) Math.ceil((inventory.getSize() - powerItems.size()*2 - 9) / 2) + 1;
        int skips = 0;
        for (int i = 0; i < powerItems.size(); i++) {
            inventory.setItem(startingIndex + i + skips, powerItems.get(i));
            skips++;
        }

        // Setze das Schließen-Item
        ItemStack close = new ItemStack(Material.BARRIER);
        setName(close, "Close");
        inventory.setItem(inventory.getSize() - 5, close);

        // Öffne das Inventar für den Spieler
        player.openInventory(inventory);
    }



    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Power GUI")) {
            event.setCancelled(true); // Verhindert, dass der Spieler das Item bewegt

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            // Überprüfe, welches Item angeklickt wurde
            if (clickedItem != null && clickedItem.getType() == Material.DIAMOND) {
                player.sendMessage("You clicked on a diamond!");
                player.closeInventory();
            } else if (clickedItem != null && clickedItem.getType() == Material.EMERALD) {
                player.sendMessage("You clicked on an emerald!");
                player.closeInventory();
            }
        }
    }

    public static ItemStack setName(ItemStack item, String name) {
        if (item == null || name == null) {
            return null;
        }

        // Hole das ItemMeta des Gegenstands
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        // Makes Name non italic
        meta.setDisplayName(ChatColor.RESET + name);

        item.setItemMeta(meta);
        return item;
    }
}
