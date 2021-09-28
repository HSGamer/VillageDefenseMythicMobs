package me.hsgamer.villagedefensemythicmobs.spawner;

import me.hsgamer.villagedefensemythicmobs.config.SpawnerData;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import plugily.projects.villagedefense.arena.Arena;

import java.util.Optional;

public abstract class SimpleMythicSpawner extends AbstractMythicSpawner {
    protected SimpleMythicSpawner(SpawnerData spawnerData) {
        super(spawnerData);
    }

    protected abstract Optional<Creature> createCreature(Location location, Arena arena, double level);

    @Override
    protected final boolean spawn(Location location, Arena arena, double level) {
        return this.createCreature(location, arena, level)
                .map(creature -> {
                    arena.getEnemies().add(creature);
                    return true;
                })
                .orElse(false);
    }
}
