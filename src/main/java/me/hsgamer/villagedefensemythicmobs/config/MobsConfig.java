package me.hsgamer.villagedefensemythicmobs.config;

import com.udojava.evalex.Expression;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.expression.ExpressionUtils;
import me.hsgamer.villagedefensemythicmobs.VillageDefenseMythicMobs;
import me.hsgamer.villagedefensemythicmobs.spawner.MythicSpawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MobsConfig {
    private static final Logger LOGGER = Logger.getLogger(MobsConfig.class.getSimpleName());
    private final VillageDefenseMythicMobs plugin;
    private final BukkitConfig config;
    private final List<MythicSpawner> spawnerList = new ArrayList<>();

    public MobsConfig(VillageDefenseMythicMobs plugin) {
        this.plugin = plugin;
        this.config = new BukkitConfig(plugin, "mobs.yml");
    }

    public void setup() {
        config.setup();
    }

    public void reload() {
        clearSpawners();
        config.reload();
        initSpawners();
    }

    public void initSpawners() {
        config.getKeys(false).forEach(key -> {
            Map<String, Object> values = config.getNormalizedValues(key, false);
            MythicSpawner spawner = getSpawner(key, values);
            if (spawner != null) {
                spawnerList.add(spawner);
                plugin.getParentPlugin().getZombieSpawnerRegistry().getZombieSpawnerSet().add(spawner);
            }
        });
    }

    private MythicSpawner getSpawner(String name, Map<String, Object> values) {
        MythicMob mob = MythicMobs.inst().getAPIHelper().getMythicMob(name);
        if (mob == null) {
            LOGGER.warning(() -> "Cannot load mythic mob named" + name);
            return null;
        }
        int priority = Optional.ofNullable(values.get("priority")).map(String::valueOf).map(Integer::parseInt).orElse(0);
        MythicSpawner spawner = new MythicSpawner(mob, priority);

        Optional.ofNullable(values.get("phase-condition"))
                .map(o -> CollectionUtils.createStringListFromObject(o, true))
                .map(list -> list.stream().map(Expression::new).map(this::applyCustomFunction).collect(Collectors.toList()))
                .ifPresent(spawner::addPhaseConditions);
        Optional.ofNullable(values.get("wave-condition"))
                .map(o -> CollectionUtils.createStringListFromObject(o, true))
                .map(list -> list.stream().map(Expression::new).map(this::applyCustomFunction).collect(Collectors.toList()))
                .ifPresent(spawner::addWaveConditions);
        Optional.ofNullable(values.get("spawn-rate"))
                .map(String::valueOf)
                .map(Expression::new)
                .map(this::applyCustomFunction)
                .ifPresent(spawner::setSpawnRateExpression);
        Optional.ofNullable(values.get("final-amount"))
                .map(String::valueOf)
                .map(Expression::new)
                .map(this::applyCustomFunction)
                .ifPresent(spawner::setFinalAmountExpression);
        Optional.ofNullable(values.get("spawn-weight"))
                .map(String::valueOf)
                .map(Expression::new)
                .map(this::applyCustomFunction)
                .ifPresent(spawner::setSpawnWeightExpression);
        Optional.ofNullable(values.get("level"))
                .map(String::valueOf)
                .map(Expression::new)
                .map(this::applyCustomFunction)
                .ifPresent(spawner::setLevelExpression);

        return spawner;
    }

    public void clearSpawners() {
        spawnerList.forEach(plugin.getParentPlugin().getZombieSpawnerRegistry().getZombieSpawnerSet()::remove);
        spawnerList.clear();
    }

    private Expression applyCustomFunction(Expression expression) {
        ExpressionUtils.applyLazyFunction(expression);
        ExpressionUtils.applyLazyOperator(expression);
        return expression;
    }
}