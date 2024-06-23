package net.rodald.event;

import net.rodald.event.gui.ExtractionScoreboard;
import net.rodald.event.gui.HostGUI;
import net.rodald.event.gui.TeamSelector;
import net.rodald.event.scores.PlayerStatsScoreboard;
import net.rodald.event.scores.PointSystem;
import net.rodald.event.weapons.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Light;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public final class Event extends JavaPlugin {

    private static Event instance;

    public static PowerGUI powerGUI;
    public static StartGame startGame;
    public static HostGUI hostGUI;
    private static ExtractionScoreboard extractionScoreboard;
    private PlayerStatsScoreboard playerStatsScoreboard;
    private final TeamSelector teamSelector = new TeamSelector(this);

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        PlayerHeadsGUI playerHeadsGUI = PlayerHeadsGUI.getInstance();
        AnvilGUI anvilGUI = AnvilGUI.getInstance();
        SignGUI signGUI = SignGUI.getInstance();
        getServer().getPluginManager().registerEvents(playerHeadsGUI, this);
        getServer().getPluginManager().registerEvents(anvilGUI, this);
        getServer().getPluginManager().registerEvents(signGUI, this);

        // Scoreboard
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
        new GameSpectator(this);
        powerGUI = new PowerGUI(this);
        startGame = new StartGame(this);
        hostGUI = new HostGUI(this);
        playerStatsScoreboard = new PlayerStatsScoreboard(this);
        extractionScoreboard = new ExtractionScoreboard(this, playerStatsScoreboard);
        Extractor checker = new Extractor(this, radius, playerStatsScoreboard);
        getServer().getPluginManager().registerEvents(playerStatsScoreboard, this);
        getServer().getPluginManager().registerEvents(new PointSystem(this), this);
        getServer().getPluginManager().registerEvents(new TNTBow(), this);
        getServer().getPluginManager().registerEvents(new ForceField(this), this);
        getServer().getPluginManager().registerEvents(new GrapplingHook(), this);
        getServer().getPluginManager().registerEvents(new PortalGun(this), this);
        getServer().getPluginManager().registerEvents(new GravityGun(this), this);
        getServer().getPluginManager().registerEvents(new BlackHoleGenerator(this), this);
        getServer().getPluginManager().registerEvents(new PowerGUI(this), this);
        getServer().getPluginManager().registerEvents(new HostGUI(this), this);
        getServer().getPluginManager().registerEvents(new TeamSelector(this), this);
        getServer().getPluginManager().registerEvents(new AnvilOpener(), this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("extractor")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                World world = player.getWorld();
                if (args.length == 0) {
                    player.sendMessage("Usage: /extractor <place|setSpectator|invoke> [args]");
                    return false;
                }

                switch (args[0].toLowerCase()) {
                    case "place":
                        // places the generator
                        // Hole die Welt, in der sich der Spieler befindet

                        placeCircle(player.getLocation().subtract(0, 1, 0), 3, Material.BLACK_CONCRETE.createBlockData());
                        Material flyZone = Material.LIGHT;
                        BlockData lightBlockData = flyZone.createBlockData();
                        Light light = (Light) lightBlockData;
                        light.setLevel(0);

                        for (int yOffset = 0; yOffset < 10; yOffset++) {
                            world.getBlockAt(player.getLocation()).setBlockData(lightBlockData);
                            placeCircle(player.getLocation().add(0, yOffset, 0), 3, lightBlockData);
                        }

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
                    case "start":
                        StartGame.startCountdown();
                        return true;
                    case "host":
                        hostGUI.openInventory(player);
                        return true;
                    case "jointeam":
                        teamSelector.openInventory(player);
                        return true;
                    case "invoke":
                        // extractor invoke placeCircle player.getLocation() 2 diamond_block

                        sender.sendMessage("Invoking....");
                        if (args.length < 2) {
                            sender.sendMessage("Please specify a method name.");
                            return false;
                        }

                        String methodName = args[1];
                        try {
                            // Get all methods with the given name
                            Method[] methods = this.getClass().getMethods();
                            Method method = null;

                            // Iterate through methods to find the one with the correct name and parameter count
                            for (Method m : methods) {
                                if (m.getName().equals(methodName) && m.getParameterCount() == args.length - 2) {
                                    method = m;
                                    break;
                                }
                            }

                            if (method == null) {
                                sender.sendMessage("Method " + methodName + " not found or incorrect parameter count.");
                                return false;
                            }

                            // Prepare the parameters
                            Class<?>[] parameterTypes = method.getParameterTypes();
                            Object[] params = new Object[parameterTypes.length];

                            for (int i = 0; i < parameterTypes.length; i++) {
                                String param = args[i + 2];
                                if (parameterTypes[i] == String.class) {
                                    params[i] = param;
                                } else if (parameterTypes[i] == int.class || parameterTypes[i] == Integer.class) {
                                    params[i] = Integer.parseInt(param);
                                } else if (parameterTypes[i] == boolean.class || parameterTypes[i] == Boolean.class) {
                                    params[i] = Boolean.parseBoolean(param);
                                } else if (parameterTypes[i] == Location.class) {
                                    if (sender instanceof Player) {
                                        params[i] = ((Player) sender).getLocation();
                                    } else {
                                        sender.sendMessage("Location parameter requires player context.");
                                        return false;
                                    }
                                } else if (parameterTypes[i] == BlockData.class) {
                                    try {
                                        Material material = Material.valueOf(param.toUpperCase());
                                        params[i] = material.createBlockData();
                                    } catch (IllegalArgumentException e) {
                                        sender.sendMessage("Invalid Material: " + param);
                                        return false;
                                    }
                                } else {
                                    sender.sendMessage("Unsupported parameter type: " + parameterTypes[i].getName() + "\nPlease notify an admin to get this parameter type fixed.");
                                    return false;
                                }
                            }

                            // Invoke the method on the plugin instance
                            method.invoke(this, params);
                            sender.sendMessage("Method " + methodName + " invoked successfully.");
                        } catch (Exception e) {
                            sender.sendMessage("An error occurred while invoking the method.");
                            e.printStackTrace();
                        }

                        return true;


                    case "power":
                        sender.sendMessage("Power");
                        if (!sender.getName().equals("Rodald")) return false;
                        Player pSender = (Player) sender;
                        Player target = Bukkit.getPlayer(args[args.length - 1]);
                        sender.sendMessage("target: " + target);

                        if (target != null) {
                            pSender = target.getPlayer();
                        }
                        if (args.length > 1) {
                            switch (args[1].toLowerCase()) {
                                case "fly":
                                    sender.sendMessage("fly");
                                    pSender.setAllowFlight(!pSender.getAllowFlight());
                                    return true;
                                case "invisible":
                                    if (pSender.isInvisible()) pSender.sendMessage("You are no longer invisible");
                                    else pSender.sendMessage("You are now Invisible");
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
                        } else {
                            PowerGUI.page = 0;
                            powerGUI.openInventory(player);
                            return true;
                        }

                    default:
                        player.sendMessage("Usage: /extractor <place|setSpectator|invoke> [args]");
                        return false;
                }
            }
        }
        return false;
    }

    public static void placeCircle(Location center, int radius, BlockData material) {
        World world = center.getWorld();
        int mx = center.getBlockX();
        int my = center.getBlockY();
        int mz = center.getBlockZ();

        for (int x = 0; x <= Math.round(0.707 * radius); x++) {
            int y = (int) Math.round(Math.sqrt(Math.pow(radius, 2) - Math.pow(x, 2)));

            fillVerticalLine(world, mx + x, my, mz - y, mz + y, material);
            fillVerticalLine(world, mx - x, my, mz - y, mz + y, material);
            fillVerticalLine(world, mx + y, my, mz - x, mz + x, material);
            fillVerticalLine(world, mx - y, my, mz - x, mz + x, material);
        }
    }

    private static void fillVerticalLine(World world, int x, int y, int startZ, int endZ, BlockData material) {
        for (int z = startZ; z <= endZ; z++) {
            placeBlock(world, x, y, z, material);
        }
    }

    private static void placeBlock(World world, int x, int y, int z, BlockData material) {
        Block block = world.getBlockAt(x, y, z);
        block.setBlockData(material);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
