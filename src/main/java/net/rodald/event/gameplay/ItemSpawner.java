package net.rodald.event.gameplay;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemSpawner {


    static List<ItemStack> spawnableItems = new ArrayList<ItemStack>();

    public ItemSpawner() {
        setSpawnableItems();
    }

    private static void setSpawnableItems() {
        ItemStack healPotion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta healPotionMeta = (PotionMeta) healPotion.getItemMeta();
        healPotionMeta.setBasePotionType(PotionType.HEALING);
        healPotion.setItemMeta(healPotionMeta);
        spawnableItems.add(healPotion);

        ItemStack speedPotion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta speedPotionMeta = (PotionMeta) speedPotion.getItemMeta();
        speedPotionMeta.setBasePotionType(PotionType.SWIFTNESS);
        speedPotion.setItemMeta(speedPotionMeta);
        spawnableItems.add(speedPotion);

        ItemStack jumpPotion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta jumpPotionMeta = (PotionMeta) jumpPotion.getItemMeta();
        jumpPotionMeta.setBasePotionType(PotionType.LEAPING);
        jumpPotion.setItemMeta(jumpPotionMeta);
        spawnableItems.add(jumpPotion);

        ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
        FireworkMeta fireworkMeta = (FireworkMeta) firework.getItemMeta();

    }
    public static void spawnItems() {
        World world = Bukkit.getWorld("world");

        world.getEntitiesByClass(ArmorStand.class).stream()
                .filter(as -> as.getScoreboardTags().contains("item_spawner"))
                .forEach(armorStand -> {
                    Random random = new Random();
                    int randomItem = random.nextInt(spawnableItems.size());
                    ItemStack item = spawnableItems.get(randomItem);
                    world.dropItem(armorStand.getLocation(), item);
                });
    }
}
