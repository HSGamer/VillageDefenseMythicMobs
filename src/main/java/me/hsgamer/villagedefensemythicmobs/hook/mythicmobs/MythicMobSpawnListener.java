package me.hsgamer.villagedefensemythicmobs.hook.mythicmobs;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;

import java.util.Objects;
import java.util.Optional;

public class MythicMobSpawnListener implements Listener {

    @EventHandler
    public void onMythicSpawn(MythicMobSpawnEvent event) {
        Entity entity = event.getEntity();
        Location location = event.getLocation();
        Optional<Arena> optionalArena = ArenaRegistry.getArenas().parallelStream().filter(arena -> Objects.equals(location.getWorld(), arena.getStartLocation().getWorld())).findAny();
        if (!optionalArena.isPresent()) {
            return;
        }
        Arena arena = optionalArena.get();
        if (entity instanceof Creature) {
            if (!arena.getEnemies().contains(entity)) {
                arena.getEnemies().add((Creature) entity);
            }
        } else {
            event.setCancelled();
        }
    }
}
