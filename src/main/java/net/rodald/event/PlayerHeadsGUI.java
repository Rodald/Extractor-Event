package net.rodald.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerHeadsGUI implements Listener {

    private static PlayerHeadsGUI instance;
    private final Map<UUID, Consumer<Player>> playerSelectionCallbacks = new HashMap<>();

    private PlayerHeadsGUI() {
        // Private constructor to prevent instantiation
    }

    public static PlayerHeadsGUI getInstance() {
        if (instance == null) {
            instance = new PlayerHeadsGUI();
        }
        return instance;
    }

    public void openPlayerHeadsGUI(Player player, Consumer<Player> onPlayerSelect) {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int inventorySize = ((onlinePlayers / 9) + 1) * 9; // Rounds Players to multiple of 9
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Online Players");

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
            assert meta != null;
            meta.setOwningPlayer(onlinePlayer);
            meta.setDisplayName(onlinePlayer.getName());
            playerHead.setItemMeta(meta);
            inventory.addItem(playerHead);
        }

        playerSelectionCallbacks.put(player.getUniqueId(), onPlayerSelect);
        // player.sendMessage("Callback registered for " + player.getName() + " with UUID " + player.getUniqueId());
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Online Players")) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                SkullMeta meta = (SkullMeta) event.getCurrentItem().getItemMeta();
                if (meta != null && meta.getOwningPlayer() != null) {
                    Player selectedPlayer = meta.getOwningPlayer().getPlayer();
                    Player clicker = (Player) event.getWhoClicked();

                    UUID clickerUUID = clicker.getUniqueId();
                    clicker.sendMessage("Searching callback for UUID: " + clickerUUID);

                    // clicker.sendMessage("Available UUIDs in playerSelectionCallbacks: " + playerSelectionCallbacks.keySet());

                    Consumer<Player> callback = playerSelectionCallbacks.remove(clickerUUID);
                    if (callback != null) {
                        clicker.sendMessage("Callback found, executing...");
                        if (selectedPlayer != null) {
                            callback.accept(selectedPlayer);
                        } else {
                            clicker.sendMessage("Selected player is null!");
                        }
                    } else {
                        clicker.sendMessage("No callback found for this player.");
                    }

                    clicker.closeInventory();
                } else {
                    ((Player) event.getWhoClicked()).sendMessage("Meta or OwningPlayer is null.");
                }
            } else {
                ((Player) event.getWhoClicked()).sendMessage("Clicked item is not a PLAYER_HEAD.");
            }
        }
    }
}
