package net.rodald.event.tests;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;



/*
 *  Failed Attempt to make a Sign GUI:
 *  [20:22:24 WARN]: Player Rodald just tried to change non-editable sign
 */
public class SignGUI implements Listener {

    private static SignGUI instance;
    private final Map<UUID, Consumer<String[]>> signTextCallbacks = new HashMap<>();

    private static Sign sign;

    private SignGUI() {
    }

    public static SignGUI getInstance() {
        if (instance == null) {
            instance = new SignGUI();
        }
        return instance;
    }

    public static void openSignGUI(Player player) {
        // Create a temporary sign block
        Block tempSignBlock = player.getLocation().getBlock();
        tempSignBlock.setType(Material.OAK_SIGN);

        sign = (Sign) tempSignBlock.getState();
        sign.setEditable(true);
        tempSignBlock.setBlockData(sign.getBlockData());
        // Clear previous text
        for (int i = 0; i < 4; i++) {
            sign.setLine(i, "");
        }

        // Set up the sign for text entry
        sign.setEditable(true);
        sign.setLine(0, "Enter your text:");
        player.sendSignChange(sign.getLocation(), sign.getLines()); // Display the sign to the player
        player.openSign(sign);
    }

        // Remove the temporary sign block after a short delay to ensure it's opened properly
        // Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(SignGUI.class), () -> tempSignBlock.setType(Material.AIR), 20L);

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        player.sendMessage(event.getLine(0));
        player.sendMessage(event.getLines());
    }
}
