package net.rodald.event;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

public final class Event extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        World world = Bukkit.getWorld("world");
        double radius = 2;
        Extractor checker = new Extractor(this, radius);

        assert world != null;
        world.getBlockAt(99, 85, -8).setType(Material.DIAMOND_BLOCK);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("extractor")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length > 0 && args[0].equalsIgnoreCase("place")) {
                    // Hole die Welt, in der sich der Spieler befindet
                    World world = player.getWorld();
                    // Hole die Koordinaten des Spielers
                    int x = player.getLocation().getBlockX();
                    int y = player.getLocation().getBlockY();
                    int z = player.getLocation().getBlockZ();
                    // Setze den Block an den Spielerkoordinaten
                    world.getBlockAt(x, y, z).setType(Material.DIAMOND_BLOCK);
                    TextDisplay text_display = (TextDisplay) world.spawnEntity(player.getLocation().toCenterLocation(), EntityType.TEXT_DISPLAY);
                    text_display.setGravity(false);
                    text_display.setInvulnerable(true);
                    text_display.addScoreboardTag("extractor");
                    text_display.setText(ChatColor.GREEN + "Hold " +
                        ChatColor.YELLOW + "[Sneak]" +
                        ChatColor.GREEN + "\nto extract");
                    text_display.setBillboard(Display.Billboard.CENTER);
                    return true;
                } else if (args.length > 0 && args[0].equalsIgnoreCase("setSpectator")) {
                    GameSpectator.setSpectator(Bukkit.getPlayer(args[1]), Boolean.valueOf(args[2]));
                    return true;
                } else {
                    player.sendMessage("Usage: /extractor place");
                    return false;
                }
            } else {
                sender.sendMessage("This Command can only be executed by a Player.");
                return false;
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
