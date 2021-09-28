package me.hsgamer.villagedefensemythicmobs.hook.boss;

import me.hsgamer.villagedefensemythicmobs.config.SpawnerData;
import me.hsgamer.villagedefensemythicmobs.spawner.SimpleMythicSpawner;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.mineacademy.boss.api.Boss;
import org.mineacademy.boss.api.BossAPI;
import org.mineacademy.boss.api.BossSpawnReason;
import org.mineacademy.boss.api.SpawnedBoss;
import plugily.projects.villagedefense.arena.Arena;

import java.util.Optional;

public class BossSpawner extends SimpleMythicSpawner {
    public BossSpawner(SpawnerData spawnerData) {
        super(spawnerData);
    }

    @Override
    protected Optional<Creature> createCreature(Location location, Arena arena, double level) {
        Boss boss = BossAPI.getBoss(getMobName());
        if (boss == null) {
            LOGGER.warning("Invalid boss named " + getMobName() + " for spawner " + getSpawnerName());
            return Optional.empty();
        }
        Class<? extends Entity> entityClass = boss.getType().getEntityClass();
        if (entityClass == null || !Creature.class.isAssignableFrom(entityClass)) {
            LOGGER.warning(() -> "Cannot spawn " + getMobName() + " as the mob is not Creature");
            return Optional.empty();
        }
        SpawnedBoss spawnedBoss = boss.spawn(location, BossSpawnReason.CUSTOM);
        Creature creature = (Creature) spawnedBoss.getEntity();
        return Optional.of(creature);
    }
}
