package me.hsgamer.villagedefensemythicmobs.hook.mythicmobs;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import io.lumine.xikage.mythicmobs.mobs.entities.SpawnReason;
import me.hsgamer.villagedefensemythicmobs.config.SpawnerData;
import me.hsgamer.villagedefensemythicmobs.spawner.AbstractMythicSpawner;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import plugily.projects.villagedefense.arena.Arena;

public class MythicMobSpawner extends AbstractMythicSpawner {

    public MythicMobSpawner(SpawnerData spawnerData) {
        super(spawnerData);
    }

    private MythicMob getMythicMob() {
        return MythicMobs.inst().getAPIHelper().getMythicMob(getMobName());
    }

    private boolean spawn(MythicMob mythicMob, Location location, Arena arena, double level) {
        ActiveMob mob = mythicMob.spawn(BukkitAdapter.adapt(location), level, SpawnReason.OTHER, entity -> {
            if (entity instanceof Creature) {
                arena.getEnemies().add((Creature) entity);
            }
        });
        Entity entity = mob.getEntity().getBukkitEntity();
        if (entity instanceof Creature) {
            return true;
        } else {
            LOGGER.warning(() -> "Cannot spawn " + getMobName() + " as the mob is not Creature");
            return false;
        }
    }

    @Override
    protected boolean spawn(Location location, Arena arena, double level) {
        MythicMob mythicMob = getMythicMob();
        if (mythicMob == null) {
            LOGGER.warning(() -> "Invalid mythic mob named " + getMobName() + " on spawner " + getSpawnerName());
            return false;
        }
        return spawn(mythicMob, location, arena, level);
    }
}
