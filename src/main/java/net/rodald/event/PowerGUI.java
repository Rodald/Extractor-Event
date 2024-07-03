package net.rodald.event;

import net.md_5.bungee.api.ChatColor;
import net.rodald.event.tests.AnvilGUI;
import net.rodald.event.tests.SignGUI;
import net.rodald.event.weapons.*;
import org.bukkit.Bukkit;
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

public class PowerGUI implements Listener {

    private final JavaPlugin plugin;
    public static int page = 0;
    public PowerGUI(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 54, "Power GUI");

        ItemStack backgroundItem = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        setName(backgroundItem, " ");

        // Hintergrund-Items
        for (int i = 0; i < inventory.getSize() - 9; i++) {
            inventory.setItem(i, backgroundItem);
        }

        loadPage(page, inventory, player);

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
                } else if (clickedItem.getType() == Material.LEVER) {
                    page = 0;
                    loadPage(page, event.getInventory(), player);
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
                } else if (clickedItem.getType() == Material.OAK_SIGN) {
                    page = 1;
                    loadPage(page, event.getInventory(), player);
                } else if (clickedItem.getType() == Material.REDSTONE_BLOCK) {
                    health(player, event);
                } else if (clickedItem.getType() == Material.SUGAR) {
                    speed(player, event);
                } else if (clickedItem.getType() == Material.TNT) {
                    page = 2;
                    loadPage(page, event.getInventory(), player);
                } else if (clickedItem.getType() == Material.BOW) {
                    player.setItemOnCursor(null);
                    TNTBow.giveBow(player);
                } else if (clickedItem.getType() == Material.DIAMOND_HORSE_ARMOR) {
                    player.setItemOnCursor(null);
                    ForceField.giveForceField(player);
                } else if (clickedItem.getType() == Material.FISHING_ROD) {
                    player.setItemOnCursor(null);
                    GrapplingHook.giveGrapplingHook(player);
                } else if (clickedItem.getType() == Material.GOLDEN_HORSE_ARMOR) {
                    player.setItemOnCursor(null);
                    GravityGun.giveGravityGun(player);
                } else if (clickedItem.getType() == Material.NETHER_STAR) {
                    player.setItemOnCursor(null);
                    BlackHoleGenerator.giveBlackHoleGenerator(player);
                } else if (clickedItem.getType() == Material.IRON_HORSE_ARMOR) {
                    player.setItemOnCursor(null);
                    PortalGun.givePortalGun(player);
                }
            }
            // loadPage(page, event.getInventory(), player);
            event.setCancelled(true);
        }
    }

    private static void close(Player player) {
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
            player.setArrowsInBody(0);
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
            SignGUI.openSignGUI(player);
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
            });
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


    private static void loadPage(int page, Inventory inventory, Player player) {
        inventory.close();
        player.openInventory(inventory);
        ItemStack air = new ItemStack(Material.AIR);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, air);
        }
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
        } else if (page == 1) {
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
        } else if (page == 2) {
            ItemStack tntBow = new ItemStack(Material.BOW);
            setName(tntBow, "§CTNT Bow");

            ItemStack forceField = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
            setName(forceField, "§3Force Field");

            ItemStack grapplingHook = new ItemStack(Material.FISHING_ROD);
            grapplingHook.getItemMeta().setUnbreakable(true);
            setName(grapplingHook, "§1Grappling Hook");

            ItemStack portalGun = new ItemStack(Material.IRON_HORSE_ARMOR);
            setName(portalGun, "§6Portal Gun");

            ItemStack gravityGun = new ItemStack(Material.GOLDEN_HORSE_ARMOR);
            setName(gravityGun, "§6Gravity Gun");

            ItemStack blackHoleGenerator = new ItemStack(Material.NETHER_STAR);
            setName(blackHoleGenerator, "§0Black Hole Generator");

            ArrayList<ItemStack> powerItems = new ArrayList<>();
            powerItems.add(tntBow);
            powerItems.add(forceField);
            powerItems.add(grapplingHook);
            powerItems.add(gravityGun);
            powerItems.add(blackHoleGenerator);

            // Platziere die Power-Items in der Mitte des Inventars
            int startingIndex = (inventory.getSize() - powerItems.size() * 2 - 9) / 2 + 1;
            for (int i = 0; i < powerItems.size(); i++) {
                inventory.setItem(startingIndex + 2 * i, powerItems.get(i));
            }
        }

        placePages(inventory);

        ItemStack close = new ItemStack(Material.BARRIER);
        setName(close, "Close");
        inventory.setItem(inventory.getSize() - 5, close);
    }

    public static void placePages(Inventory inventory) {
        ArrayList<ItemStack> pages = new ArrayList<>();

        ItemStack toggles = new ItemStack(Material.LEVER);
        setName(toggles, "Toggles");

        ItemStack fields = new ItemStack(Material.OAK_SIGN);
        setName(fields, "Fields");

        ItemStack weapons = new ItemStack(Material.TNT);
        setName(weapons, "Weapons");

        pages.add(toggles);
        pages.add(fields);
        pages.add(weapons);

        Player player = (Player) inventory.getViewers().get(0);
        int startingPos = inventory.getSize() - 9;
        inventory.setItem(startingPos, new ItemStack(Material.BOW));
        for (int i = 0; i < 9; i++) {
            if (i < pages.size()) {
                inventory.setItem(i + startingPos, pages.get(i));
            } else inventory.setItem(i + startingPos, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

    }
}
