package net.rodald.event;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

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
        setName(fly, "Fly: " + player.getAllowFlight());

        ItemStack invisible = new ItemStack(Material.GLASS);
        setName(invisible, "Invisible: " + player.isInvisible());

        ItemStack invulnerable = new ItemStack(Material.END_CRYSTAL);
        setName(invulnerable, "Invulnerable: " + player.isInvulnerable());

        ItemStack op = new ItemStack(Material.COMMAND_BLOCK);
        setName(op, "Operator: " + player.isOp());

        ItemStack backgroundItem = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        setName(backgroundItem, " ");

        ArrayList<ItemStack> powerItems = new ArrayList<>();
        powerItems.add(fly);
        powerItems.add(invisible);
        powerItems.add(invulnerable);
        powerItems.add(op);

        // Hintergrund-Items
        for (int i = 0; i < inventory.getSize() - 9; i++) {
            inventory.setItem(i, backgroundItem);
        }

        // Platziere die Power-Items in der Mitte des Inventars
        int startingIndex = (inventory.getSize() - powerItems.size() * 2 - 9) / 2 + 1;
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
            if (clickedItem != null && clickedItem.getType() == Material.BARRIER) {
                close(player);
            } else if (clickedItem != null && clickedItem.getType() == Material.ELYTRA) {
                fly(player, event);
            } else if (clickedItem != null && clickedItem.getType() == Material.GLASS) {
                player.sendMessage("You clicked on INVISIBLE!");
                player.setInvisible(!player.isInvisible());
                Event.powerGUI.openInventory(player);
            } else if (clickedItem != null && clickedItem.getType() == Material.END_CRYSTAL) {
                player.sendMessage("You clicked on INVULNERABLE!");
                player.setInvulnerable(!player.isInvulnerable());
                Event.powerGUI.openInventory(player);
            } else if (clickedItem != null && clickedItem.getType() == Material.COMMAND_BLOCK) {
                player.sendMessage("You clicked on OP!");
                player.setOp(!player.isOp());
                Event.powerGUI.openInventory(player);
            }
        }
    }

    private static void close(Player player) {
        player.sendMessage("You closed!");
        player.closeInventory();
    }

    private static void fly(Player player, InventoryClickEvent event) {
        if (event.isLeftClick()) {
            player.sendMessage("You clicked LEFT CLICK on FLY!");
            player.setAllowFlight(!player.getAllowFlight());
            Event.powerGUI.openInventory(player);
        } else if (event.isRightClick()) {
            PlayerHeadsGUI playerHeadsGUI = PlayerHeadsGUI.getInstance();
            playerHeadsGUI.openPlayerHeadsGUI(player, selectedPlayer -> {
                player.sendMessage("You selected: " + selectedPlayer.getName());
            });
        }
    }



    public void openAnvilGui(Player player) {
        Inventory anvil = Bukkit.createInventory(null, InventoryType.ANVIL, "Enter your text");

        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        meta.setDisplayName("Type here");
        paper.setItemMeta(meta);

        anvil.setItem(0, paper); // Setzt das Papier in den ersten Slot des Ambosses

        player.openInventory(anvil); // Öffnet das Anvil GUI für den Spieler
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

        // Makes Name non-italic
        meta.setDisplayName(ChatColor.RESET + name);

        item.setItemMeta(meta);
        return item;
    }
}
