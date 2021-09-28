package me.hsgamer.villagedefensemythicmobs.hook.boss;

import me.hsgamer.villagedefensemythicmobs.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.mineacademy.boss.api.BossSpawnReason;
import org.mineacademy.boss.api.event.BossPreSpawnEvent;
import plugily.projects.villagedefense.arena.Arena;

import java.util.Optional;

public class BossSpawnListener implements Listener {
    @EventHandler
    public void onBossSpawn(BossPreSpawnEvent event) {
        if (event.getSpawnReason() == BossSpawnReason.CUSTOM) {
            return;
        }
        Entity entity = event.getEntity();
        Location location = entity.getLocation();
        Optional<Arena> optionalArena = Utils.getArena(location);
        if (!optionalArena.isPresent()) {
            return;
        }
        Arena arena = optionalArena.get();
        if (entity instanceof Creature) {
            if (!arena.getEnemies().contains(entity)) {
                arena.getEnemies().add((Creature) entity);
            }
        } else {
            event.setCancelled(true);
        }
    }
}
