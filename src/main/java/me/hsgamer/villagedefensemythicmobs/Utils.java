package me.hsgamer.villagedefensemythicmobs;

import org.bukkit.Location;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;

import java.util.Objects;
import java.util.Optional;

public final class Utils {
    private Utils() {
        // EMPTY
    }

    public static Optional<Arena> getArena(Location location) {
        return ArenaRegistry.getArenas().parallelStream().filter(arena -> Objects.equals(location.getWorld(), arena.getStartLocation().getWorld())).findAny();
    }
}
