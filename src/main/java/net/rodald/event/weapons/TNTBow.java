package net.rodald.event.weapons;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class TNTBow implements Listener {
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        // Überprüfen, ob das Projektil ein Pfeil ist
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            // Überprüfen, ob der Schütze ein Spieler ist
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                // Überprüfen, ob der Spieler einen Bogen verwendet hat und ob es der spezielle Bogen ist
                if (player.getInventory().getItemInMainHand().getType() == Material.BOW
                        && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§6TNT Bogen")) {

                    // Den Standort des Treffers ermitteln
                    Location hitLocation = arrow.getLocation();
                    // Gezündetes TNT am Trefferstandort spawnen
                    TNTPrimed tnt = hitLocation.getWorld().spawn(hitLocation, TNTPrimed.class);
                    tnt.setFuseTicks(60); // Zündzeit des TNTs festlegen (optional)
                    // Den Pfeil nach dem Treffer entfernen
                    arrow.remove();
                }
            }
        }
    }

}
