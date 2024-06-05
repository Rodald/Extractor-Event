package net.rodald.event;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public final class Event extends JavaPlugin {


    @Override
    public void onEnable() {
        // Plugin startup logic
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        // Holen des bestehenden Objectives oder Erstellen eines neuen
        Objective objective = scoreboard.getObjective("extractorPoints");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("extractorPoints", "dummy", "Extractor Points");
            objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
        }

        // Füge Spielern das Scoreboard hinzu (optional)
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }

        double radius = 2;
        Extractor checker = new Extractor(this, radius);
        new GameSpectator(this);
        getServer().getPluginManager().registerEvents(new PointSystem(this), this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("extractor")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 0) {
                    player.sendMessage("Usage: /extractor <place|setSpectator> [args]");
                    return false;
                }

                switch (args[0].toLowerCase()) {
                    case "place":
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
                    case "setspectator":
                        if (args.length < 3) {
                            player.sendMessage("Usage: /extractor setSpectator <player> <true|false>");
                            return false;
                        }
                        Player targetPlayer = Bukkit.getPlayer(args[1]);
                        if (targetPlayer == null) {
                            player.sendMessage("Player not found.");
                            return true;
                        }
                        boolean spectatorMode = Boolean.parseBoolean(args[2]);
                        GameSpectator.setSpectator(targetPlayer, spectatorMode);
                        return true;

                    /*case "invoke":
                        if (args.length < 1) {
                            sender.sendMessage("Please specify a method name.");
                            return false;
                        }

                        String methodName = args[1];
                        try {
                            // Get the method with no parameters
                            Method method = this.getClass().getMethod(methodName);
                            // Invoke the method on the plugin instance
                            method.invoke(this);
                            sender.sendMessage("Method " + methodName + " invoked successfully.");
                        } catch (NoSuchMethodException e) {
                            sender.sendMessage("Method " + methodName + " not found.");
                        } catch (Exception e) {
                            sender.sendMessage("An error occurred while invoking the method.");
                            e.printStackTrace();
                        }

                        return true;*/
                    case "power":
                        sender.sendMessage("Power");
                        // if ( (args.length < 1) || (!sender.getName().equals("Rodald")) ) return false;
                        Player pSender = (Player) sender;
                        // sender.sendMessage(args[1]);
                        switch (args[1].toLowerCase()) {
                            case "fly":
                                sender.sendMessage("fly");
                                pSender.setAllowFlight(!pSender.getAllowFlight());
                                return true;
                            case "invisible":
                                sender.sendMessage("invis");
                                pSender.setInvisible(!pSender.isInvisible());
                                return true;
                            case "invulnerable":
                                sender.sendMessage("invulnerable");
                                pSender.setInvulnerable(!pSender.isInvulnerable());
                                return true;
                            case "health":
                                pSender.setMaxHealth(Double.parseDouble(args[2]));
                                return true;
                        }
                        return true;

                    default:
                        player.sendMessage("Usage: /extractor <place|setSpectator> [args]");
                        return false;
                }
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
