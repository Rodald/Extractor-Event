package net.rodald.event.weapons;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ForceField implements Listener {

    private final JavaPlugin plugin;
    private static final String FORCE_FIELD_ITEM_NAME = ChatColor.AQUA + "Force Field";
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private int frame = 0;
    private final Map<UUID, Boolean> forceFieldActive = new HashMap<>();
    private final long COOLDOWN_DURATION = 500; // Cooldown-Dauer in Millisekunden (hier 500 Millisekunden)

    public ForceField(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static ItemStack createForceFieldItem() {
        ItemStack forceFieldItem = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta meta = forceFieldItem.getItemMeta();
        meta.setDisplayName(FORCE_FIELD_ITEM_NAME);
        forceFieldItem.setItemMeta(meta);
        return forceFieldItem;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.DIAMOND_HORSE_ARMOR &&
                item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(FORCE_FIELD_ITEM_NAME) &&
                event.getAction().toString().contains("RIGHT")) {

            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            long lastUseTime = cooldowns.getOrDefault(playerId, 0L);

            if (currentTime >= lastUseTime + COOLDOWN_DURATION) {
                // Aktiviere oder deaktiviere das Force Field
                toggleForceField(player);
                cooldowns.put(playerId, currentTime);
            } else {
                player.sendMessage(ChatColor.RED + "Force Field is on cooldown. Please wait.");
            }

            event.setCancelled(true); // Verhindert, dass das Item in die Hand genommen wird
        }
    }

    private void toggleForceField(Player player) {
        UUID playerId = player.getUniqueId();
        boolean isActive = forceFieldActive.getOrDefault(playerId, false);

        if (!isActive) {
            activateForceField(player);
        } else {
            deactivateForceField(player);
        }
    }

    private void activateForceField(Player player) {
        player.sendMessage(ChatColor.RED + "Activated Force Field");
        forceFieldActive.put(player.getUniqueId(), true);

        new BukkitRunnable() {
            Location playerLoc = player.getLocation().clone();
            Vector lastDirection = null;
            int radius = 7;
            double additionalY = -0.1; // Höhe hinzufügen

            @Override
            public void run() {
                if (!forceFieldActive.getOrDefault(player.getUniqueId(), false) || !player.isOnline()) {
                    cancel();
                    forceFieldActive.put(player.getUniqueId(), false);
                    return;
                }

                Location newLoc = player.getLocation().clone();
                Vector direction;



                // Bewege den Spieler in die Richtung, in die er sich bewegt
                // player.setVelocity(player.getVelocity().add(direction));
                // Füge dem Y-Vektor des Spielers Velocity hinzu
                // player.setVelocity(player.getVelocity().setY(player.getVelocity().getY() + additionalY));

                // Abstoßung von festen Blöcken im Radius
                for (double x = -radius; x <= radius; x++) {
                    for (double y = -radius; y <= radius; y++) {
                        for (double z = -radius; z <= radius; z++) {
                            Location blockLoc = playerLoc.clone().add(x, y, z);
                            Block block = blockLoc.getBlock();

                            // Überprüfe, ob der Spieler in der Nähe von festen Blöcken ist
                            if (!block.isEmpty() && block.getType().isSolid()) {
                                // Berechne die Richtung vom Block zum Spieler
                                Vector blockDirection = playerLoc.toVector().subtract(blockLoc.toVector()).normalize();

                                // Bewege den Spieler von den Blöcken weg
                                double forceMultiplier = 0.0002; // Stärke der Abstoßung anpassen
                                double forceHeightMultiplier = .9999999; // Stärke der Abstoßung anpassen
                                player.setVelocity(player.getVelocity().add(blockDirection.multiply(forceMultiplier)));
                                // dont delete its for bouncing
                                Vector velocity = player.getVelocity();
                                velocity.setY(velocity.getY() * forceHeightMultiplier);
                                player.setVelocity(velocity);
                                //player.setVelocity(player.getVelocity().setY(player.getVelocity().getY() * forceHeightMultiplier).normalize());
                            }
                        }
                    }
                }
                double function = Math.pow(Math.sin(frame*.00001)*.0001, 2)-.5;
                // player.setVelocity(player.getVelocity().setY(player.getVelocity().getY()*2 + function).normalize());


                // Überprüfe, ob sich die Position des Spielers geändert hat
                if (lastDirection == null || !newLoc.toVector().equals(playerLoc.toVector())) {
                    // Berechne die Richtung, in die sich der Spieler bewegt
                    direction = newLoc.toVector().subtract(playerLoc.toVector()).normalize();
                    lastDirection = direction;
                } else {
                    direction = lastDirection;
                    direction.setY(0);
                }

                double average = ((player.getVelocity().getX()*2 + player.getVelocity().getZ()*2) / 2)*100;
                // Bewege den Spieler in die Richtung, in die er sich bewegt
                // player.setVelocity(player.getVelocity().add(player.getVelocity().multiply(player.getVelocity())));
                player.setVelocity(player.getVelocity().add(direction).multiply(2*player.getWalkSpeed()));

                // Aktualisiere die Spielerposition für die nächste Iteration
                playerLoc = newLoc;
                frame++;
            }
        }.runTaskTimer(plugin, 0, 0); // Task alle 1 Tick ausführen
    }

    private void deactivateForceField(Player player) {
        player.sendMessage(ChatColor.RED + "Deactivated Force Field");
        forceFieldActive.put(player.getUniqueId(), false);
    }
}
