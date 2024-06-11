package net.rodald.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class AnvilOpener implements Listener {
    private static JavaPlugin plugin;

    private final Boolean debugMode = false;

    public static void setPlugin(JavaPlugin pl) {
        plugin = pl;
    }

    public static void openAnvilGUI(Player player) {

        Inventory anvil = Bukkit.createInventory(null, InventoryType.ANVIL, "Enter your text");
        // Erstelle ein Papierstapel, um das Texteingabefeld zu simulieren
        ItemStack paperStack = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = paperStack.getItemMeta();
        paperMeta.setDisplayName("Enter text");
        paperStack.setItemMeta(paperMeta);
        anvil.setItem(0, paperStack);

        player.openInventory(anvil);
    }

    @EventHandler
    public void onAnvilGUIClick(InventoryClickEvent event) {

        event.setCancelled(false);
        Player player = (Player) event.getWhoClicked();
        if (event.getRawSlot() == 0) {
            // Das Texteingabefeld wurde geklickt
            if (debugMode) player.sendMessage("You clicked the text input field.");
        }
    }

    @EventHandler
    public void onAnvilGUIClose(InventoryCloseEvent event) {

        Player player = (Player) event.getPlayer();
        if (debugMode) player.sendMessage("Anvil GUI closed.");
    }

    @EventHandler
    public void AnvilTakeResultEvent(InventoryClickEvent event) {
        event.setCancelled(false);
        if (event.getInventory().getType() == InventoryType.ANVIL) {
        Player player = (Player) event.getWhoClicked();
        ItemStack resultItem = event.getCurrentItem();
        assert resultItem != null;
            if (debugMode) player.sendMessage("Du hast ein Item aus dem Amboss genommen: " + resultItem.getType());
        }
    }

    @EventHandler
    public void AnvilUpdateResultEvent(InventoryClickEvent event) {
        event.setCancelled(false);
        if (event.getRawSlot() == 0 || event.getRawSlot() == 1) { // Überprüfen, ob einer der Eingabe-Slots angeklickt wurde
            Player player = (Player) event.getWhoClicked();
            ItemStack inputItem1 = event.getInventory().getItem(0); // Erster Eingabe-Slot
            ItemStack inputItem2 = event.getInventory().getItem(1); // Zweiter Eingabe-Slot
            if (inputItem1 != null && inputItem2 != null) {
                if (debugMode) player.sendMessage("Die Eingabe wurde aktualisiert. Neue Kombination: " + inputItem1.getType() + " + " + inputItem2.getType());
            }
        }
    }

    @EventHandler
    public void PrepareAnvilEvent(PrepareAnvilEvent event) {

        Player player = (Player) event.getInventory().getViewers().get(0);
        if (debugMode) player.sendMessage("PrepareAnvilEvent");
    }
}
