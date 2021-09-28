package me.hsgamer.villagedefensemythicmobs.hook.vanilla;

import me.hsgamer.villagedefensemythicmobs.config.SpawnerData;
import me.hsgamer.villagedefensemythicmobs.spawner.SimpleMythicSpawner;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.creatures.CreatureUtils;

import java.util.Objects;
import java.util.Optional;

public class VanillaSpawner extends SimpleMythicSpawner {
    private final EntityType entityType;
    private final boolean applyEffects;
    private final boolean applyHolidayEffects;

    public VanillaSpawner(SpawnerData spawnerData) {
        super(spawnerData);
        this.entityType = Optional.ofNullable(getMobName()).map(name -> {
            try {
                return EntityType.valueOf(name);
            } catch (Exception e) {
                LOGGER.warning(() -> "Invalid entity type named " + name);
                return null;
            }
        }).orElse(null);
        this.applyEffects = Optional.ofNullable(getOptions().get("apply-effects"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(false);
        this.applyHolidayEffects = Optional.ofNullable(getOptions().get("apply-holiday-effects"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    @Override
    protected Optional<Creature> createCreature(Location location, Arena arena, double level) {
        if (entityType == null) {
            return Optional.empty();
        }
        Class<? extends Entity> entityClass = entityType.getEntityClass();
        if (entityClass == null || !Creature.class.isAssignableFrom(entityClass)) {
            LOGGER.warning(() -> "Cannot spawn " + getMobName() + " as the mob is not Creature");
            return Optional.empty();
        }
        Creature creature = (Creature) Objects.requireNonNull(location.getWorld()).spawnEntity(location, entityType);
        if (applyEffects) {
            CreatureUtils.applyAttributes(creature, arena);
        }
        if (applyHolidayEffects) {
            getInstance().getParentPlugin().getHolidayManager().applyHolidayCreatureEffects(creature);
        }
        return Optional.of(creature);
    }
}
