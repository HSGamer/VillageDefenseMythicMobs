package me.hsgamer.villagedefensemythicmobs.hook.boss;

import me.hsgamer.villagedefensemythicmobs.config.SpawnerData;
import me.hsgamer.villagedefensemythicmobs.spawner.AbstractMythicSpawner;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.mineacademy.boss.api.Boss;
import org.mineacademy.boss.api.BossAPI;
import org.mineacademy.boss.api.BossSpawnReason;
import org.mineacademy.boss.api.SpawnedBoss;
import plugily.projects.villagedefense.arena.Arena;

public class BossSpawner extends AbstractMythicSpawner {
    public BossSpawner(SpawnerData spawnerData) {
        super(spawnerData);
    }

    @Override
    protected boolean spawn(Location location, Arena arena, double level) {
        Boss boss = BossAPI.getBoss(getMobName());
        if (boss == null) {
            LOGGER.warning("Invalid boss named " + getMobName() + " for spawner " + getSpawnerName());
            return false;
        }
        Class<? extends Entity> entityClass = boss.getType().getEntityClass();
        if (entityClass == null || !Creature.class.isAssignableFrom(entityClass)) {
            LOGGER.warning(() -> "Cannot spawn " + getMobName() + " as the mob is not Creature");
            return false;
        }
        SpawnedBoss spawnedBoss = boss.spawn(location, BossSpawnReason.CUSTOM);
        Creature creature = (Creature) spawnedBoss.getEntity();
        arena.getEnemies().add(creature);
        return true;
    }
}
