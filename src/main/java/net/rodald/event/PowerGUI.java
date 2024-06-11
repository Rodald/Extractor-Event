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

    public static int page;
    private static final int MAXPAGES = 1;

    public PowerGUI(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Power GUI");

        ItemStack backgroundItem = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        setName(backgroundItem, " ");

        // Hintergrund-Items
        for (int i = 0; i < inventory.getSize() - 9; i++) {
            inventory.setItem(i, backgroundItem);
        }

        loadPage(0, inventory, player);

        // Platziere die Power-Items in der Mitte des Inventars

        // Setze das Schließen-Item
        ItemStack close = new ItemStack(Material.BARRIER);
        setName(close, "Close");
        inventory.setItem(inventory.getSize() - 5, close);

        // Öffne das Inventar für den Spieler
        // player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Power GUI")) {
            event.setCancelled(true); // Verhindert, dass der Spieler das Item bewegt

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            // Überprüfe, welches Item angeklickt wurde
            if (clickedItem != null) {
                if (clickedItem.getType() == Material.BARRIER) {
                    close(player);
                } else if (clickedItem.getType() == Material.ELYTRA) {
                    fly(player, event);
                } else if (clickedItem.getType() == Material.GLASS) {
                    invisible(player, event);
                } else if (clickedItem.getType() == Material.END_CRYSTAL) {
                    invulnerable(player, event);
                } else if (clickedItem.getType() == Material.COMMAND_BLOCK) {
                    op(player, event);
                } else if (clickedItem.getType() == Material.NAME_TAG) {
                    nameVisible(player, event);
                } else if (clickedItem.getType() == Material.REDSTONE_BLOCK) {
                    health(player, event);
                } else if (clickedItem.getType() == Material.SUGAR) {
                    speed(player, event);
                } else if (clickedItem.getItemMeta().getDisplayName().equals("Next Page")) {
                    page++;
                    loadPage(page, event.getInventory(), player);
                } else if (clickedItem.getItemMeta().getDisplayName().equals("Previous Page")) {
                    page--;
                    loadPage(page, event.getInventory(), player);
                }
            }
        }
    }

    private static void close(Player player) {
        player.sendMessage("You closed!");
        player.closeInventory();
    }

    private static void fly(Player player, InventoryClickEvent event) {
        if (!event.isLeftClick() && !event.isRightClick()) return;

        if (event.isRightClick()) {
            PlayerHeadsGUI playerHeadsGUI = PlayerHeadsGUI.getInstance();
            playerHeadsGUI.openPlayerHeadsGUI(player, selectedPlayer -> {
                selectedPlayer.setAllowFlight(!selectedPlayer.getAllowFlight());
                Event.powerGUI.openInventory(player);
            });
        } else {
            player.setAllowFlight(!player.getAllowFlight());
            Event.powerGUI.openInventory(player);
        }
    }

    private static void invisible(Player player, InventoryClickEvent event) {
        if (!event.isLeftClick() && !event.isRightClick()) return;

        if (event.isRightClick()) {
            PlayerHeadsGUI playerHeadsGUI = PlayerHeadsGUI.getInstance();
            playerHeadsGUI.openPlayerHeadsGUI(player, selectedPlayer -> {
                selectedPlayer.setInvisible(!selectedPlayer.isInvisible());
                Event.powerGUI.openInventory(player);
            });
        } else {
            player.setInvisible(!player.isInvisible());
            Event.powerGUI.openInventory(player);
        }
    }

    private static void invulnerable(Player player, InventoryClickEvent event) {
        if (!event.isLeftClick() && !event.isRightClick()) return;

        if (event.isRightClick()) {
            PlayerHeadsGUI playerHeadsGUI = PlayerHeadsGUI.getInstance();
            playerHeadsGUI.openPlayerHeadsGUI(player, selectedPlayer -> {
                selectedPlayer.setInvulnerable(!selectedPlayer.isInvulnerable());
                Event.powerGUI.openInventory(player);
            });
        } else {
            player.setInvulnerable(!player.isInvulnerable());
            Event.powerGUI.openInventory(player);
        }
    }

    private static void op(Player player, InventoryClickEvent event) {
        if (!event.isLeftClick() && !event.isRightClick()) return;

        if (event.isRightClick()) {
            PlayerHeadsGUI playerHeadsGUI = PlayerHeadsGUI.getInstance();
            playerHeadsGUI.openPlayerHeadsGUI(player, selectedPlayer -> {
                selectedPlayer.setOp(!selectedPlayer.isOp());
                Event.powerGUI.openInventory(player);
            });
        } else {
            player.setOp(!player.isOp());
            Event.powerGUI.openInventory(player);
        }
    }
    private static void nameVisible(Player player, InventoryClickEvent event) {
        if (!event.isLeftClick() && !event.isRightClick()) return;

        if (event.isRightClick()) {
            PlayerHeadsGUI playerHeadsGUI = PlayerHeadsGUI.getInstance();
            playerHeadsGUI.openPlayerHeadsGUI(player, selectedPlayer -> {
                selectedPlayer.setCustomNameVisible(!selectedPlayer.isCustomNameVisible());
                Event.powerGUI.openInventory(player);
            });
        } else {
            player.setCustomNameVisible(!player.isCustomNameVisible());
            Event.powerGUI.openInventory(player);
        }
    }

    private static void health(Player player, InventoryClickEvent event) {
        if (!event.isLeftClick() && !event.isRightClick()) return;

        if (event.isRightClick()) {
            PlayerHeadsGUI playerHeadsGUI = PlayerHeadsGUI.getInstance();
            playerHeadsGUI.openPlayerHeadsGUI(player, selectedPlayer -> {
                selectedPlayer.setOp(!selectedPlayer.isOp());
                Event.powerGUI.openInventory(player);
            });
        } else {
            AnvilGUI.getInstance().openAnvilGUI(player, renamedItemName -> {
                player.sendMessage("The renamed item is now: " + renamedItemName);
                // Weitere Aktionen mit dem umbenannten Namen
            });
            // Event.powerGUI.openInventory(player);
        }
    }

    private static void speed(Player player, InventoryClickEvent event) {
        if (!event.isLeftClick() && !event.isRightClick()) return;

        if (event.isRightClick()) {
            PlayerHeadsGUI playerHeadsGUI = PlayerHeadsGUI.getInstance();
            playerHeadsGUI.openPlayerHeadsGUI(player, selectedPlayer -> {
                selectedPlayer.setOp(!selectedPlayer.isOp());
                Event.powerGUI.openInventory(player);
            });
        } else {
            AnvilGUI.getInstance().openAnvilGUI(player, renamedItemName -> {
                player.sendMessage("The renamed item is now: " + renamedItemName);
                // Weitere Aktionen mit dem umbenannten Namen
            });
            // Event.powerGUI.openInventory(player);
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

        // Makes Name non-italic
        meta.setDisplayName(ChatColor.RESET + name);

        item.setItemMeta(meta);
        return item;
    }


    private void loadPage(int page, Inventory inventory, Player player) {
        ItemStack air = new ItemStack(Material.AIR);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, air);
        }
        ItemStack nextPage = new ItemStack(Material.ARROW);
        setName(nextPage, "Next Page");
        ItemStack previousPage = new ItemStack(Material.ARROW);
        setName(previousPage, "Previous Page");
        ItemStack backgroundItem = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        setName(backgroundItem, " ");

        if (page == 0) {
            ItemStack fly = new ItemStack(Material.ELYTRA);
            setName(fly,"Fly: " + player.getAllowFlight());

            ItemStack invisible = new ItemStack(Material.GLASS);
            setName(invisible, ChatColor.BLUE + "Invisible: " + player.isInvisible());

            ItemStack invulnerable = new ItemStack(Material.END_CRYSTAL);
            setName(invulnerable, ChatColor.DARK_PURPLE + "Invulnerable: " + player.isInvulnerable());

            ItemStack op = new ItemStack(Material.COMMAND_BLOCK);
            setName(op, "Operator: " + player.isOp());

            ItemStack customName = new ItemStack(Material.NAME_TAG);
            setName(customName, ChatColor.AQUA + "CustomNameVisible: " + player.isCustomNameVisible());


            ArrayList<ItemStack> powerItems = new ArrayList<>();
            powerItems.add(fly);
            powerItems.add(invisible);
            powerItems.add(invulnerable);
            powerItems.add(op);
            powerItems.add(customName);

            // Platziere die Power-Items in der Mitte des Inventars
            int startingIndex = (inventory.getSize() - powerItems.size() * 2 - 9) / 2 + 1;
            for (int i = 0; i < powerItems.size(); i++) {
                inventory.setItem(startingIndex + 2*i, powerItems.get(i));
            }

            player.openInventory(inventory);
        } if (page == 1) {
            ItemStack health = new ItemStack(Material.REDSTONE_BLOCK);
            setName(health, ChatColor.RED + "Health: " + player.getHealthScale());


            ItemStack speed = new ItemStack(Material.SUGAR);
            setName(speed,  "Speed: " + player.getWalkSpeed());

            ArrayList<ItemStack> powerItems = new ArrayList<>();
            powerItems.add(health);
            powerItems.add(speed);

            // Platziere die Power-Items in der Mitte des Inventars
            int startingIndex = (inventory.getSize() - powerItems.size() * 2 - 9) / 2 + 1;
            for (int i = 0; i < powerItems.size(); i++) {
                inventory.setItem(startingIndex + 2*i, powerItems.get(i));
            }
        }



        ItemStack close = new ItemStack(Material.BARRIER);
        setName(close, "Close");
        inventory.setItem(inventory.getSize() - 5, close);

        // Page System
        if (page < MAXPAGES) {
            inventory.setItem(inventory.getSize() - 1, nextPage);
        }
        if (page > 0) {
            inventory.setItem(inventory.getSize() - 9, previousPage);
        }
    }
}
