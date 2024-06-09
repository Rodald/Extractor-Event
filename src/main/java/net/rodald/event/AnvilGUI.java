package net.rodald.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class AnvilGUI implements Listener {

    private static AnvilGUI instance;
    private final Map<UUID, Consumer<String>> renameCallbacks = new HashMap<>();

    private AnvilGUI() {
        // Private constructor to prevent instantiation
    }

    public static AnvilGUI getInstance() {
        if (instance == null) {
            instance = new AnvilGUI();
        }
        return instance;
    }

    public void openAnvilGUI(Player player, Consumer<String> onRename) {
        Inventory anvil = Bukkit.createInventory(null, InventoryType.ANVIL, "Enter your text");

        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        assert meta != null;
        meta.setDisplayName("Type here");
        paper.setItemMeta(meta);

        anvil.setItem(0, paper); // Setzt das Papier in den ersten Slot des Ambosses

        player.sendMessage("Slot 0: ");
        player.sendMessage(String.valueOf(anvil.getItem(0)));

        player.sendMessage("Slot 2: ");
        player.sendMessage(String.valueOf(anvil.getItem(2)));

        renameCallbacks.put(player.getUniqueId(), onRename);
        player.openInventory(anvil);
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        Player player = (Player) event.getInventory().getViewers().get(0);
        if (event.getInventory().getViewers().isEmpty()) return;
        UUID playerUUID = player.getUniqueId();

        ItemStack resultItem = event.getResult();
        if (resultItem != null) {
            ItemMeta meta = resultItem.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String renamedItemName = meta.getDisplayName();
                Consumer<String> callback = renameCallbacks.remove(playerUUID);
                if (callback != null) {
                    callback.accept(renamedItemName);
                }
            }
        }
    }


    @EventHandler
    public void AnvilTakeResultEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Enter your text")) {
            if (event.getInventory().getType() == InventoryType.ANVIL) {
                if (event.getRawSlot() == 2) { // Überprüfen, ob der Spieler das Ergebnis-Slot angeklickt hat
                    Player player = (Player) event.getWhoClicked();
                    ItemStack resultItem = event.getCurrentItem();
                    if (resultItem != null) {
                        player.sendMessage("Du hast ein Item aus dem Amboss genommen: " + resultItem.getType());
                    }
                }
            }
        }
    }

    @EventHandler
    public void AnvilUpdateResultEvent(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.ANVIL) {
            if (event.getRawSlot() == 0 || event.getRawSlot() == 1) { // Überprüfen, ob einer der Eingabe-Slots angeklickt wurde
                Player player = (Player) event.getWhoClicked();
                ItemStack inputItem1 = event.getInventory().getItem(0); // Erster Eingabe-Slot
                ItemStack inputItem2 = event.getInventory().getItem(1); // Zweiter Eingabe-Slot
                if (inputItem1 != null && inputItem2 != null) {
                    player.sendMessage("Die Eingabe wurde aktualisiert. Neue Kombination: " + inputItem1.getType() + " + " + inputItem2.getType());
                }
            }
        }
    }
}
